package mom.wii.itemcustomization.dialog;

import com.google.common.primitives.Floats;
import com.mojang.serialization.DataResult;
import mom.wii.itemcustomization.template.SmithingTemplate;
import mom.wii.itemcustomization.template.settings.CustomModelDataSettings;
import mom.wii.itemcustomization.template.settings.equipment.CameraOverlaySettings;
import mom.wii.itemcustomization.template.settings.equipment.EquipmentModelSettings;
import mom.wii.itemcustomization.template.settings.equipment.EquipmentSettings;
import mom.wii.itemcustomization.template.settings.ItemModelSettings;
import mom.wii.itemcustomization.template.settings.music_and_sounds.InstrumentSettings;
import mom.wii.itemcustomization.template.settings.music_and_sounds.JukeboxSongSettings;
import mom.wii.itemcustomization.template.settings.music_and_sounds.MusicAndSoundsSettings;
import mom.wii.itemcustomization.template.settings.music_and_sounds.NoteBlockSoundSettings;
import mom.wii.itemcustomization.template.settings.tooltip.HiddenComponentSettings;
import mom.wii.itemcustomization.template.settings.tooltip.TooltipSettings;
import mom.wii.itemcustomization.template.settings.tooltip.TooltipStyleSettings;
import mom.wii.itemcustomization.util.IdentifierIndex;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static mom.wii.itemcustomization.ItemCustomization.*;
import static mom.wii.itemcustomization.template.SmithingTemplate.isItemCustomizationSmithingTemplate;

public class Dialogs {
    public static final ItemStack SEARCH_ICON;

    static {
        ItemStack searchIcon = new ItemStack(Items.EGG);
        searchIcon.applyComponents(DataComponentMap.builder().set(DataComponents.ITEM_MODEL, Identifier.parse("igalaxy_item_customization:search_icon")).build());
        SEARCH_ICON = searchIcon;
    }

    private static void registerIndexRootAction(String id, IdentifierIndex index, Consumer<ServerPlayer> openRootDialog, String errorMessage) {
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, id),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem()) && !index.isEmpty()) {
                        openRootDialog.accept(player);
                        return;
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.nullToEmpty(errorMessage))
                            )
                    );
                }
        );
    }

    private static void registerIndexNamespaceAction(String id, IdentifierIndex index, BiConsumer<ServerPlayer, String> openDialogForNamespace) {
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, id + "/namespace"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof StringTag) {
                            String namespace = ((StringTag) packet.payload().get()).value();
                            if (index.containsNamespace(namespace)) {
                                openDialogForNamespace.accept(player, namespace);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.nullToEmpty("Invalid namespace selected"))
                            )
                    );
                }
        );
    }

    @FunctionalInterface
    interface IndexBrowseDialog {
        void accept(ServerPlayer player, String namespace, String path);
    }

    private static void registerIndexBrowseAction(String id, IdentifierIndex index, IndexBrowseDialog indexBrowseDialog) {
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, id + "/browse"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem()) &&
                            packet.payload().isPresent() &&
                            packet.payload().get() instanceof StringTag
                    ) {
                        StringTag payload = (StringTag) packet.payload().get();
                        DataResult<Identifier> validated = Identifier.read(payload.value());
                        if (validated.isSuccess()) {
                            Identifier entry = validated.getOrThrow();
                            if (index.isValidPath(entry)) {
                                if (entry.getPath().isEmpty() || entry.getPath().endsWith("/")) {
                                    indexBrowseDialog.accept(player, entry.getNamespace(), entry.getPath());
                                    return;
                                }
                            }
                        }
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.literal("Not a browsable path"))
                            )
                    );
                }
        );
    }

    private static void registerIndexSetAction(String id, IdentifierIndex index, Function<Identifier, String> entryToValue, String errorMessage) {
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, id + "/set"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem()) &&
                            packet.payload().isPresent() &&
                            packet.payload().get() instanceof StringTag
                    ) {
                        StringTag payload = (StringTag) packet.payload().get();
                        DataResult<Identifier> validated = Identifier.read(payload.value());
                        if (validated.isSuccess()) {
                            Identifier entry = validated.getOrThrow();
                            if (index.identifiers.stream().anyMatch(entry::equals)) {
                                SmithingTemplate template = SmithingTemplate.from(player.getMainHandItem());
                                template.setSetting(id, StringTag.valueOf(entryToValue.apply(entry)));
                                template.openDialog(player);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.nullToEmpty(errorMessage))
                            )
                    );
                }
        );
    }

    public static void register() {
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "root"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        SmithingTemplate.from(player.getMainHandItem()).openDialog(player);
                    }
                }
        );

        registerIndexRootAction("item_model", ITEMS_MODEL_INDEX, ItemModelSettings::openRootDialog, "No usable item models present in resource pack");
        registerIndexNamespaceAction("item_model", ITEMS_MODEL_INDEX, ItemModelSettings::openDialogForNamespace);
        registerIndexBrowseAction("item_model", ITEMS_MODEL_INDEX, ItemModelSettings::openDialogForNamespaceAndPath);
        registerIndexSetAction("item_model", ITEMS_MODEL_INDEX, Identifier::toString, "Invalid item model selected");

        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "equipment"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        EquipmentSettings.openRootDialog(player);
                    }
                }
        );

        registerIndexRootAction("equipment_model", EQUIPMENT_MODEL_INDEX, EquipmentModelSettings::openRootDialog, "No usable equipment models present in resource pack");
        registerIndexNamespaceAction("equipment_model", EQUIPMENT_MODEL_INDEX, EquipmentModelSettings::openDialogForNamespace);
        registerIndexBrowseAction("equipment_model", EQUIPMENT_MODEL_INDEX, EquipmentModelSettings::openDialogForNamespaceAndPath);
        registerIndexSetAction("equipment_model", EQUIPMENT_MODEL_INDEX, Identifier::toString, "Invalid equipment model selected");

        registerIndexRootAction("camera_overlay", CAMERA_OVERLAY_INDEX, CameraOverlaySettings::openRootDialog, "No usable misc textures present in resource pack");
        registerIndexNamespaceAction("camera_overlay", CAMERA_OVERLAY_INDEX, CameraOverlaySettings::openDialogForNamespace);
        registerIndexBrowseAction("camera_overlay", CAMERA_OVERLAY_INDEX, CameraOverlaySettings::openDialogForNamespaceAndPath);
        registerIndexSetAction("camera_overlay", CAMERA_OVERLAY_INDEX, id -> id.getNamespace() + ":misc/" + id.getPath(), "Invalid camera overlay texture selected");


        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "custom_model_data"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandItem();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        CustomModelDataSettings.openRootDialog(player, SmithingTemplate.from(stack));
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "custom_model_data/float"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        CustomModelDataSettings.openAddNewDialog(player, "float", "Float", false);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "custom_model_data/float/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandItem();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof CompoundTag) {
                            CompoundTag payload = (CompoundTag) packet.payload().get();
                            if (payload.contains("float") && payload.getString("float").isPresent()) {
                                Float f = Floats.tryParse(payload.getString("float").get());
                                if (f != null) {
                                    SmithingTemplate template = SmithingTemplate.from(stack);
                                    CompoundTag newCustomModelData = ((CompoundTag) template.getSettingOrElse("custom_model_data", CompoundTag::new)).copy();
                                    ListTag floats = newCustomModelData.getListOrEmpty("floats");
                                    floats.add(FloatTag.valueOf(f));
                                    newCustomModelData.put("floats", floats);
                                    template.setSetting("custom_model_data", newCustomModelData);
                                    CustomModelDataSettings.openRootDialog(player, template);
                                    return;
                                }
                            }
                        }
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.nullToEmpty("Invalid float"))
                            )
                    );
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "custom_model_data/flag"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        CustomModelDataSettings.openAddNewFlagDialog(player);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "custom_model_data/flag/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandItem();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof StringTag) {
                            boolean f = Boolean.parseBoolean(((StringTag) packet.payload().get()).value());
                            SmithingTemplate template = SmithingTemplate.from(stack);
                            CompoundTag newCustomModelData = ((CompoundTag) template.getSettingOrElse("custom_model_data", CompoundTag::new)).copy();
                            ListTag flags = newCustomModelData.getListOrEmpty("flags");
                            flags.add(ByteTag.valueOf(f));
                            newCustomModelData.put("flags", flags);
                            template.setSetting("custom_model_data", newCustomModelData);
                            CustomModelDataSettings.openRootDialog(player, template);
                            return;
                        }
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.nullToEmpty("Invalid flag"))
                            )
                    );
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "custom_model_data/string"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        CustomModelDataSettings.openAddNewDialog(player, "string", "String", true);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "custom_model_data/string/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandItem();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof CompoundTag) {
                            CompoundTag payload = (CompoundTag) packet.payload().get();
                            if (payload.contains("string") && payload.getString("string").isPresent()) {
                                String s = payload.getString("string").get();
                                SmithingTemplate template = SmithingTemplate.from(stack);
                                CompoundTag newCustomModelData = ((CompoundTag) template.getSettingOrElse("custom_model_data", CompoundTag::new)).copy();
                                ListTag strings = newCustomModelData.getListOrEmpty("strings");
                                strings.add(StringTag.valueOf(s));
                                newCustomModelData.put("strings", strings);
                                template.setSetting("custom_model_data", newCustomModelData);
                                CustomModelDataSettings.openRootDialog(player, template);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.nullToEmpty("Invalid string"))
                            )
                    );
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "custom_model_data/color"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        CustomModelDataSettings.openAddNewDialog(player, "color", "Color (Decimal)", false);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "custom_model_data/color/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandItem();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof CompoundTag) {
                            CompoundTag payload = (CompoundTag) packet.payload().get();
                            if (payload.contains("color") && payload.getString("color").isPresent()) {
                                int c = Integer.parseInt(payload.getString("color").get());
                                SmithingTemplate template = SmithingTemplate.from(stack);
                                CompoundTag newCustomModelData = ((CompoundTag) template.getSettingOrElse("custom_model_data", CompoundTag::new)).copy();
                                ListTag colors = newCustomModelData.getListOrEmpty("colors");
                                colors.add(IntTag.valueOf(c));
                                newCustomModelData.put("colors", colors);
                                template.setSetting("custom_model_data", newCustomModelData);
                                CustomModelDataSettings.openRootDialog(player, template);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.nullToEmpty("Invalid color"))
                            )
                    );
                }
        );

        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "tooltip"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        TooltipSettings.openRootDialog(player);
                    }
                }
        );

        registerIndexRootAction("tooltip_style", TOOLTIP_STYLE_INDEX, TooltipStyleSettings::openRootDialog, "No usable tooltip styles present in resource pack");
        registerIndexNamespaceAction("tooltip_style", TOOLTIP_STYLE_INDEX, TooltipStyleSettings::openDialogForNamespace);
        registerIndexBrowseAction("tooltip_style", TOOLTIP_STYLE_INDEX, TooltipStyleSettings::openDialogForNamespaceAndPath);
        registerIndexSetAction("tooltip_style", TOOLTIP_STYLE_INDEX, Identifier::toString, "Invalid tooltip style");

        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "hidden_components"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandItem();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        HiddenComponentSettings.openRootDialog(player, SmithingTemplate.from(stack));
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "hidden_components/add_component"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        HiddenComponentSettings.openAddComponentDialog(player);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "hidden_components/add_component/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandItem();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof CompoundTag) {
                            CompoundTag payload = (CompoundTag) packet.payload().get();
                            if (payload.getString("component").isPresent()) {
                                SmithingTemplate template = SmithingTemplate.from(stack);
                                String c = payload.getString("component").get().toLowerCase();
                                DataResult<Identifier> validated = Identifier.read(c);
                                if (validated.isSuccess()) {
                                    if (BuiltInRegistries.DATA_COMPONENT_TYPE.containsKey(validated.getOrThrow())) {
                                        ListTag hidden = ((ListTag) template.getSettingOrElse("hidden_components", ListTag::new)).copy();
                                        if (!c.startsWith("minecraft:") && !c.contains(":"))
                                            c = "minecraft:" + c;
                                        final String component = c;
                                        if (hidden.stream().noneMatch(x -> x.asString().get().equals(component))) {
                                            hidden.add(StringTag.valueOf(component));
                                            template.setSetting("hidden_components", hidden);
                                            HiddenComponentSettings.openRootDialog(player, template);
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.nullToEmpty("Invalid component"))
                            )
                    );
                }
        );

        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "music_and_sounds"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandItem())) {
                        MusicAndSoundsSettings.openRootDialog(player);
                    }
                }
        );

        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "note_block_sound"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandItem();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        NoteBlockSoundSettings.openRootDialog(player, SmithingTemplate.from(stack));
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "note_block_sound/set"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandItem();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof CompoundTag) {
                            CompoundTag payload = (CompoundTag) packet.payload().get();
                            if (payload.getString("note_block_sound").isPresent()) {
                                String s = payload.getString("note_block_sound").get().toLowerCase();
                                DataResult<Identifier> validated = Identifier.read(s);
                                if (validated.isSuccess()) {
                                    Identifier sound = validated.getOrThrow();
                                    SmithingTemplate template = SmithingTemplate.from(stack);
                                    template.setSetting("note_block_sound", StringTag.valueOf(sound.toString()));
                                    return;
                                }
                            }
                        }
                    }
                    player.openDialog(
                            Holder.direct(
                                    DialogManager.simpleNoticeDialog(Component.nullToEmpty("Invalid note block sound"))
                            )
                    );
                }
        );

        registerIndexRootAction("jukebox_song", JUKEBOX_SONG_INDEX, JukeboxSongSettings::openRootDialog, "No usable jukebox songs present in data pack");
        registerIndexNamespaceAction("jukebox_song", JUKEBOX_SONG_INDEX, JukeboxSongSettings::openDialogForNamespace);
        registerIndexBrowseAction("jukebox_song", JUKEBOX_SONG_INDEX, JukeboxSongSettings::openDialogForNamespaceAndPath);
        registerIndexSetAction("jukebox_song", JUKEBOX_SONG_INDEX, Identifier::toString, "Invalid jukebox song");

        registerIndexRootAction("instrument", INSTRUMENT_INDEX, InstrumentSettings::openRootDialog, "No usable instruments present in data pack");
        registerIndexNamespaceAction("instrument", INSTRUMENT_INDEX, InstrumentSettings::openDialogForNamespace);
        registerIndexBrowseAction("instrument", INSTRUMENT_INDEX, InstrumentSettings::openDialogForNamespaceAndPath);
        registerIndexSetAction("instrument",  INSTRUMENT_INDEX, Identifier::toString, "Invalid instrument");
    }
}
