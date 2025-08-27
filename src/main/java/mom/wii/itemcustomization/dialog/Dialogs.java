package mom.wii.itemcustomization.dialog;

import com.google.common.primitives.Floats;
import com.mojang.serialization.DataResult;
import mom.wii.itemcustomization.template.SmithingTemplate;
import mom.wii.itemcustomization.template.settings.CustomModelDataSettings;
import mom.wii.itemcustomization.template.settings.equipment.CameraOverlaySettings;
import mom.wii.itemcustomization.template.settings.equipment.EquipmentModelSettings;
import mom.wii.itemcustomization.template.settings.equipment.EquipmentSettings;
import mom.wii.itemcustomization.template.settings.ItemModelSettings;
import mom.wii.itemcustomization.template.settings.music_and_sounds.MusicAndSoundsSettings;
import mom.wii.itemcustomization.template.settings.tooltip.HiddenComponentSettings;
import mom.wii.itemcustomization.template.settings.tooltip.TooltipSettings;
import mom.wii.itemcustomization.template.settings.tooltip.TooltipStyleSettings;
import mom.wii.itemcustomization.util.IdentifierIndex;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedHashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static mom.wii.itemcustomization.ItemCustomization.*;
import static mom.wii.itemcustomization.template.SmithingTemplate.isItemCustomizationSmithingTemplate;

public class Dialogs {
    public static final ItemStack SEARCH_ICON;

    static {
        ItemStack searchIcon = new ItemStack(Items.EGG);
        searchIcon.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ITEM_MODEL, Identifier.of("igalaxy_item_customization:search_icon")).build());
        SEARCH_ICON = searchIcon;
    }

    private static void registerIndexRootAction(String id, IdentifierIndex index, Consumer<ServerPlayerEntity> openRootDialog, String errorMessage) {
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, id),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack()) && !index.isEmpty()) {
                        openRootDialog.accept(player);
                        return;
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of(errorMessage))
                            )
                    );
                }
        );
    }

    private static void registerIndexNamespaceAction(String id, IdentifierIndex index, BiConsumer<ServerPlayerEntity, String> openDialogForNamespace) {
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, id + "/namespace"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof NbtString) {
                            String namespace = ((NbtString) packet.payload().get()).value();
                            if (index.namespaces.contains(namespace)) {
                                openDialogForNamespace.accept(player, namespace);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of("Invalid namespace selected"))
                            )
                    );
                }
        );
    }

    private static void registerIndexSetAction(String id, IdentifierIndex index, Function<Identifier, String> entryToValue, String errorMessage) {
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, id + "/set"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack()) &&
                            packet.payload().isPresent() &&
                            packet.payload().get() instanceof NbtString
                    ) {
                        NbtString payload = (NbtString) packet.payload().get();
                        DataResult<Identifier> validated = Identifier.validate(payload.value());
                        if (validated.isSuccess()) {
                            Identifier entry = validated.getOrThrow();
                            if (index.identifiers.stream().anyMatch(entry::equals)) {
                                SmithingTemplate template = SmithingTemplate.from(player.getMainHandStack());
                                template.setSetting(id, NbtString.of(entryToValue.apply(entry)));
                                template.openDialog(player);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of(errorMessage))
                            )
                    );
                }
        );
    }

    public static void register() {
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "root"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        SmithingTemplate.from(player.getMainHandStack()).openDialog(player);
                    }
                }
        );

        registerIndexRootAction("item_model", ITEM_MODEL_INDEX, ItemModelSettings::openRootDialog, "No usable item models present in resource pack");
        registerIndexNamespaceAction("item_model", ITEM_MODEL_INDEX, ItemModelSettings::openDialogForNamespace);
        registerIndexSetAction("item_model", ITEM_MODEL_INDEX, Identifier::toString, "Invalid item model selected");

        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "equipment"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        EquipmentSettings.openRootDialog(player);
                    }
                }
        );

        registerIndexRootAction("equipment_model", EQUIPMENT_MODEL_INDEX, EquipmentModelSettings::openRootDialog, "No usable equipment models present in resource pack");
        registerIndexNamespaceAction("equipment_model", EQUIPMENT_MODEL_INDEX, EquipmentModelSettings::openDialogForNamespace);
        registerIndexSetAction("equipment_model", EQUIPMENT_MODEL_INDEX, Identifier::toString, "Invalid equipment model selected");

        registerIndexRootAction("camera_overlay", CAMERA_OVERLAY_INDEX, CameraOverlaySettings::openRootDialog, "No usable misc textures present in resource pack");
        registerIndexNamespaceAction("camera_overlay", CAMERA_OVERLAY_INDEX, CameraOverlaySettings::openDialogForNamespace);
        registerIndexSetAction("camera_overlay", CAMERA_OVERLAY_INDEX, id -> id.getNamespace() + ":misc/" + id.getPath(), "Invalid camera overlay texture selected");


        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "custom_model_data"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandStack();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        CustomModelDataSettings.openRootDialog(player, SmithingTemplate.from(stack));
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "custom_model_data/float"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        CustomModelDataSettings.openAddNewDialog(player, "float", "Float", false);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "custom_model_data/float/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandStack();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof NbtCompound) {
                            NbtCompound payload = (NbtCompound) packet.payload().get();
                            if (payload.contains("float") && payload.getString("float").isPresent()) {
                                Float f = Floats.tryParse(payload.getString("float").get());
                                if (f != null) {
                                    SmithingTemplate template = SmithingTemplate.from(stack);
                                    NbtCompound newCustomModelData = ((NbtCompound) template.getSettingOrElse("custom_model_data", NbtCompound::new)).copy();
                                    NbtList floats = newCustomModelData.getListOrEmpty("floats");
                                    floats.add(NbtFloat.of(f));
                                    newCustomModelData.put("floats", floats);
                                    template.setSetting("custom_model_data", newCustomModelData);
                                    CustomModelDataSettings.openRootDialog(player, template);
                                    return;
                                }
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of("Invalid float"))
                            )
                    );
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "custom_model_data/flag"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        CustomModelDataSettings.openAddNewFlagDialog(player);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "custom_model_data/flag/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandStack();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof NbtString) {
                            boolean f = Boolean.parseBoolean(((NbtString) packet.payload().get()).value());
                            SmithingTemplate template = SmithingTemplate.from(stack);
                            NbtCompound newCustomModelData = ((NbtCompound) template.getSettingOrElse("custom_model_data", NbtCompound::new)).copy();
                            NbtList flags = newCustomModelData.getListOrEmpty("flags");
                            flags.add(NbtByte.of(f));
                            newCustomModelData.put("flags", flags);
                            template.setSetting("custom_model_data", newCustomModelData);
                            CustomModelDataSettings.openRootDialog(player, template);
                            return;
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of("Invalid flag"))
                            )
                    );
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "custom_model_data/string"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        CustomModelDataSettings.openAddNewDialog(player, "string", "String", true);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "custom_model_data/string/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandStack();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof NbtCompound) {
                            NbtCompound payload = (NbtCompound) packet.payload().get();
                            if (payload.contains("string") && payload.getString("string").isPresent()) {
                                String s = payload.getString("string").get();
                                SmithingTemplate template = SmithingTemplate.from(stack);
                                NbtCompound newCustomModelData = ((NbtCompound) template.getSettingOrElse("custom_model_data", NbtCompound::new)).copy();
                                NbtList strings = newCustomModelData.getListOrEmpty("strings");
                                strings.add(NbtString.of(s));
                                newCustomModelData.put("strings", strings);
                                template.setSetting("custom_model_data", newCustomModelData);
                                CustomModelDataSettings.openRootDialog(player, template);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of("Invalid string"))
                            )
                    );
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "custom_model_data/color"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        CustomModelDataSettings.openAddNewDialog(player, "color", "Color (Decimal)", false);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "custom_model_data/color/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandStack();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof NbtCompound) {
                            NbtCompound payload = (NbtCompound) packet.payload().get();
                            if (payload.contains("color") && payload.getString("color").isPresent()) {
                                int c = Integer.parseInt(payload.getString("color").get());
                                SmithingTemplate template = SmithingTemplate.from(stack);
                                NbtCompound newCustomModelData = ((NbtCompound) template.getSettingOrElse("custom_model_data", NbtCompound::new)).copy();
                                NbtList colors = newCustomModelData.getListOrEmpty("colors");
                                colors.add(NbtInt.of(c));
                                newCustomModelData.put("colors", colors);
                                template.setSetting("custom_model_data", newCustomModelData);
                                CustomModelDataSettings.openRootDialog(player, template);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of("Invalid color"))
                            )
                    );
                }
        );

        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "tooltip"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        TooltipSettings.openRootDialog(player);
                    }
                }
        );

        registerIndexRootAction("tooltip_style", TOOLTIP_STYLE_INDEX, TooltipStyleSettings::openRootDialog, "No useable tooltip styles present in resource pack");
        registerIndexNamespaceAction("tooltip_style", TOOLTIP_STYLE_INDEX, TooltipStyleSettings::openDialogForNamespace);
        registerIndexSetAction("tooltip_style", TOOLTIP_STYLE_INDEX, Identifier::toString, "Invalid tooltip style");

        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "hidden_components"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandStack();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        HiddenComponentSettings.openRootDialog(player, SmithingTemplate.from(stack));
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "hidden_components/add_component"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        HiddenComponentSettings.openAddComponentDialog(player);
                    }
                }
        );
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "hidden_components/add_component/add"),
                (packet, player) -> {
                    ItemStack stack = player.getMainHandStack();
                    if (isItemCustomizationSmithingTemplate(stack)) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof NbtCompound) {
                            NbtCompound payload = (NbtCompound) packet.payload().get();
                            if (payload.getString("component").isPresent()) {
                                SmithingTemplate template = SmithingTemplate.from(stack);
                                String c = payload.getString("component").get().toLowerCase();
                                DataResult<Identifier> validated = Identifier.validate(c);
                                if (validated.isSuccess()) {
                                    if (Registries.DATA_COMPONENT_TYPE.containsId(validated.getOrThrow())) {
                                        TooltipDisplayComponent oldComponent = stack.getOrDefault(
                                                DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(
                                                        false,
                                                        new LinkedHashSet<>()
                                                )
                                        );
                                        LinkedHashSet<ComponentType<?>> set = new LinkedHashSet(oldComponent.hiddenComponents());
                                        ComponentType<?> componentType = Registries.DATA_COMPONENT_TYPE.get(validated.getOrThrow());
                                        set.add(componentType);
                                        stack.set(DataComponentTypes.TOOLTIP_DISPLAY, new TooltipDisplayComponent(
                                                oldComponent.hideTooltip(),
                                                set
                                        ));
                                        HiddenComponentSettings.openRootDialog(player, template);
                                    }
                                }
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of("Invalid component"))
                            )
                    );
                }
        );

        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "music_and_sounds"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        MusicAndSoundsSettings.openRootDialog(player);
                    }
                }
        );
    }
}
