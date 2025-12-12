package mom.wii.itemcustomization.template;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.EitherHolder;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import static mom.wii.itemcustomization.dialog.DialogManager.simpleTranslatableMenuButton;

public class SmithingTemplate {
    private static final ItemStack PREVIEW_SLOT_ITEMSTACK;
    public static final HashMap<String, Integer> COST_MAP = new HashMap<>() {{
        put("item_model", 1);
        put("equipment_model", 6);
        put("camera_overlay", 2);
        put("tooltip_style", 2);
        put("note_block_sound", 1);
        put("jukebox_song", 6);
        put("instrument", 6);
    }};
    public static final LinkedHashMap<String, Tuple<String, Function<Tag, String>>> TOOLTIPS = new LinkedHashMap<>() {{
        put("item_model", new Tuple<>("Item Model", (e) -> e.asString().get()));
        put("equipment_model", new Tuple<>("Equipment Model", (e) ->  e.asString().get()));
        put("camera_overlay", new Tuple<>("Camera Overlay", (e) ->  e.asString().get()));
        put("custom_model_data", new Tuple<>("Custom Model Data", (e) -> {
            StringTagVisitor writer = new StringTagVisitor();
            writer.visitCompound(e.asCompound().get());
            return writer.build();
        }));
        put("tooltip_style", new Tuple<>("Tooltip Style",  (e) -> e.asString().get()));
        put("hidden_components", new Tuple<>("Hidden Components", (e) -> {
            StringTagVisitor writer = new StringTagVisitor();
            writer.visitList(e.asList().get());
            return writer.build();
        }));
        put("note_block_sound", new Tuple<>("Note Block Sound", (e) -> e.asString().get()));
        put("jukebox_song", new  Tuple<>("Jukebox Song", (e) -> e.asString().get()));
        put("instrument", new Tuple<>("Instrument", (e) -> e.asString().get()));
    }};
    public static final HashMap<String, BiPredicate<SmithingTemplate, ItemStack>> CAN_APPLY_PREDICATES = new HashMap<>() {{
       put("equipment_model", (t, i) -> !i.getPrototype().has(DataComponents.EQUIPPABLE));
       put("camera_overlay", (t, i) -> {
           DataComponentMap d = i.getPrototype();
           return (!d.has(DataComponents.EQUIPPABLE) || (d.has(DataComponents.EQUIPPABLE) && !d.get(DataComponents.EQUIPPABLE).slot().equals(EquipmentSlot.HEAD)));
       });
       put("note_block_sound", (t, i) -> !(i.getItem() instanceof PlayerHeadItem));
       put("jukebox_song", (t, i) -> !i.getPrototype().has(DataComponents.JUKEBOX_PLAYABLE));
       put("instrument", (t, i) -> !i.getPrototype().has(DataComponents.INSTRUMENT));
    }};
    public ItemStack itemStack;
    public static final Item ingredient;


    static {
        ItemStack egg = new ItemStack(Items.EGG);
        egg.applyComponents(DataComponentMap.builder().set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "preview_slot")).build());
        PREVIEW_SLOT_ITEMSTACK = egg;
        ingredient = BuiltInRegistries.ITEM.getValue(Identifier.parse(ItemCustomization.CONFIG.smithingIngredient));
    }

    private SmithingTemplate(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static @Nullable SmithingTemplate from(ItemStack itemStack) {
        if (isItemCustomizationSmithingTemplate(itemStack)) {
            return new SmithingTemplate(itemStack);
        }
        return null;
    }

    public static SmithingTemplate virtual() {
        ItemStack virtual = new ItemStack(Items.COMMAND_BLOCK);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("igalaxy_item_customization:is_customization_template", true);
        virtual.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return new SmithingTemplate(virtual);
    }

    public static boolean isItemCustomizationSmithingTemplate(ItemStack itemStack, PacketContext context) {
        return isItemCustomizationSmithingTemplate(itemStack);
    }

    public static boolean isItemCustomizationSmithingTemplate(ItemStack itemStack) {
        return itemStack.hasNonDefault(DataComponents.CUSTOM_DATA) && Objects.requireNonNull(itemStack.get(DataComponents.CUSTOM_DATA)).copyTag().contains("igalaxy_item_customization:is_customization_template");
    }

    public void openDialog(ServerPlayer player) {
        ItemStack previewItem = new ItemStack(Items.PAPER);
        previewItem.set(DataComponents.ITEM_NAME, Component.translatableWithFallback("gui.igalaxy_item_customization.preview_item", "Preview Item"));
        this.applySettings(previewItem, player.level());

        MultiActionDialog dialog = new MultiActionDialog(
                new CommonDialogData(
                    Component.translatableWithFallback("gui.igalaxy_item_customization.root.title", "Item Customization"),
                    Optional.empty(),
                    true,
                    true,
                    DialogAction.WAIT_FOR_RESPONSE,
                    List.of(
                            new PlainMessage(Component.translatableWithFallback("gui.igalaxy_item_customization.preview", "Preview"), 200),
                            new ItemBody(PREVIEW_SLOT_ITEMSTACK, Optional.empty(), false, false, 16, 1),
                            new ItemBody(previewItem, Optional.empty(), false, true, 16, 24),
                            this.getCostDialogBody(),
                            new PlainMessage(Component.translatable("options.title"), 200)
                    ),
                    List.of()
                ),
                List.of(
                        simpleTranslatableMenuButton("item_model.external_title", "Item Model...", "item_model"),
                        simpleTranslatableMenuButton("equipment.external_title", "Equipment...", "equipment"),
                        simpleTranslatableMenuButton("custom_model_data.external_title", "Custom Model Data...", "custom_model_data"),
                        simpleTranslatableMenuButton("tooltip.external_title", "Tooltip...", "tooltip"),
                        simpleTranslatableMenuButton("music_and_sounds.external_title", "Music & Sounds...", "music_and_sounds")
                ),
                Optional.empty(),
                2
        );

        player.openDialog(Holder.direct(dialog));
    }

    private static void addToTooltip(ArrayList<Component> tooltip, String title, String value) {
        tooltip.add(Component.literal(" " + title).withStyle(style -> style.withColor(ChatFormatting.GOLD).withItalic(false)));
        tooltip.add(Component.literal("  " + value).withStyle(style -> style.withItalic(false).withColor(ChatFormatting.DARK_GRAY)));
    }

    public List<Component> getTooltip() {
        ArrayList<Component> tooltip = new ArrayList<>(List.of(
                Component.translatable("item.minecraft.smithing_template").withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false)),
                Component.empty(),
                Component.translatable("item.minecraft.smithing_template.applies_to").withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false)),
                Component.literal(" Any").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.BLUE)),
                Component.translatable("item.minecraft.smithing_template.ingredients").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.GRAY)),
                Component.literal(" ").append(Component.translatable(this.ingredient.getDescriptionId()).withStyle(style -> style.withItalic(false).withColor(ChatFormatting.BLUE)))

        ));
        if (this.hasSettings()) {
            tooltip.add(Component.translatable("potion.whenDrank").withStyle(style -> style.withItalic(false).withColor(ChatFormatting.GRAY)));
            TOOLTIPS.forEach((id, p) -> {
                if (this.hasSetting(id)) {
                    Tag e = this.getSetting(id);
                    addToTooltip(tooltip, p.getA(), p.getB().apply(e));
                }
            });
        }
        return tooltip;
    }

    public boolean hasSettings() {
        if (this.itemStack.hasNonDefault(DataComponents.CUSTOM_DATA)) {
            return this.itemStack.get(DataComponents.CUSTOM_DATA).copyTag().contains("igalaxy_item_customization:settings");
        }
        return false;
    }

    public boolean hasSetting(String key) {
        if (this.itemStack.hasNonDefault(DataComponents.CUSTOM_DATA)) {
            if (this.itemStack.get(DataComponents.CUSTOM_DATA).copyTag().contains("igalaxy_item_customization:settings")) {
                CompoundTag customData = this.itemStack.get(DataComponents.CUSTOM_DATA).copyTag();
                return customData.getCompound("igalaxy_item_customization:settings").isPresent() &&
                        customData.getCompound("igalaxy_item_customization:settings").get().contains(key);
            }
        }
        return false;
    }

    public @Nullable Tag getSetting(String key) {
        if (hasSetting(key)) {
            CompoundTag customData = this.itemStack.get(DataComponents.CUSTOM_DATA).copyTag();
            CompoundTag settings = customData.getCompound("igalaxy_item_customization:settings").get();
            return settings.get(key);
        }
        return null;
    }

    public @Nullable Tag getSettingOrElse(String key, Supplier<Tag> defaultValue) {
        Tag setting = this.getSetting(key);
        if (setting == null) {
            return defaultValue.get();
        }
        return setting;
    }

    public CustomData setSetting(String key, Tag value) {
        if (this.itemStack.hasNonDefault(DataComponents.CUSTOM_DATA)) {
            CustomData customData = this.itemStack.get(DataComponents.CUSTOM_DATA);
            CompoundTag newCustomData = customData.copyTag();
            if (this.itemStack.get(DataComponents.CUSTOM_DATA).copyTag().contains("igalaxy_item_customization:settings")) {
                CompoundTag settings = newCustomData.getCompound("igalaxy_item_customization:settings").get();
                settings.put(key, value);
                newCustomData.put("igalaxy_item_customization:settings", settings);
            } else {
                CompoundTag settings = new CompoundTag();
                settings.put(key, value);
                newCustomData.put("igalaxy_item_customization:settings", settings);
            }
            return this.itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(newCustomData));
        } else {
            CompoundTag customData = new CompoundTag();
            CompoundTag settings = new CompoundTag();
            settings.put(key, value);
            customData.put("igalaxy_item_customization:settings", settings);
            return this.itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(customData));
        }
    }

    public void resetSettings() {
        if (itemStack.hasNonDefault(DataComponents.CUSTOM_DATA)) {
            if (itemStack.get(DataComponents.CUSTOM_DATA).copyTag().contains("igalaxy_item_customization:settings")) {
                CompoundTag newCustomData = itemStack.get(DataComponents.CUSTOM_DATA).copyTag();
                newCustomData.remove("igalaxy_item_customization:settings");
                itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(newCustomData));
            }
        }
    }

    public void applySettings(ItemStack stack, Level world) {
        if (this.hasSetting("item_model")) {
            Identifier id = Identifier.parse(((StringTag) this.getSetting("item_model")).value());
            stack.set(DataComponents.ITEM_MODEL, id);

            if (ItemCustomization.CONFIG.overrideHeadEquipmentModels && stack.getComponents().has(DataComponents.EQUIPPABLE)) {
                Equippable ec = stack.get(DataComponents.EQUIPPABLE);
                if (ec.slot().equals(EquipmentSlot.HEAD)) {
                    Equippable newEquippableComponent = new Equippable(
                            ec.slot(), ec.equipSound(), Optional.empty(), ec.cameraOverlay(), ec.allowedEntities(), ec.dispensable(), ec.swappable(), ec.damageOnHurt(), ec.equipOnInteract(), ec.canBeSheared(), ec.shearingSound()
                    );
                    stack.set(DataComponents.EQUIPPABLE, newEquippableComponent);
                }
            }
        }
        if (this.hasSetting("equipment_model")) {
            Identifier id = Identifier.parse(((StringTag) this.getSetting("equipment_model")).value());
            if (stack.getPrototype().has(DataComponents.EQUIPPABLE)) {
                Equippable ec = stack.get(DataComponents.EQUIPPABLE);
                ResourceKey<EquipmentAsset> equipmentAsset = ResourceKey.create(ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset")), id);
                Equippable newEquippableComponent = new Equippable(
                        ec.slot(), ec.equipSound(),
                        Optional.of(equipmentAsset),
                        ec.cameraOverlay(), ec.allowedEntities(), ec.dispensable(), ec.swappable(), ec.damageOnHurt(), ec.equipOnInteract(), ec.canBeSheared(), ec.shearingSound()
                );
                stack.set(DataComponents.EQUIPPABLE, newEquippableComponent);
            }
        }
        if (this.hasSetting("camera_overlay")) {
            Identifier id = Identifier.parse(((StringTag) this.getSetting("camera_overlay")).value());
            if (stack.getPrototype().has(DataComponents.EQUIPPABLE) && stack.getPrototype().get(DataComponents.EQUIPPABLE).slot().equals(EquipmentSlot.HEAD)) {
                Equippable ec = stack.get(DataComponents.EQUIPPABLE);
                Equippable newEquippableComponent = new Equippable(
                        ec.slot(), ec.equipSound(), ec.assetId(),
                        Optional.of(id),
                        ec.allowedEntities(), ec.dispensable(), ec.swappable(), ec.damageOnHurt(), ec.equipOnInteract(), ec.canBeSheared(), ec.shearingSound()
                );
                stack.set(DataComponents.EQUIPPABLE, newEquippableComponent);

                if (stack.getComponents().has(DataComponents.ATTRIBUTE_MODIFIERS) && ItemCustomization.CONFIG.customizedHeadVisibleOnPlayerLocatorBar) {
                    ItemAttributeModifiers attributeModifiersComponent = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
                    ItemAttributeModifiers newAttributeModifiersComponent = new ItemAttributeModifiers(
                            attributeModifiersComponent.modifiers().stream().filter(x -> !x.modifier().is(Identifier.parse("minecraft:waypoint_transmit_range_hide"))).toList()
                    );
                    if (!newAttributeModifiersComponent.modifiers().isEmpty())
                        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, newAttributeModifiersComponent);
                    else
                        stack.remove(DataComponents.ATTRIBUTE_MODIFIERS);
                }
            }
        }
        if (this.hasSetting("custom_model_data")) {
            CompoundTag data = (CompoundTag) this.getSetting("custom_model_data");
            List<Float> floats = data.getListOrEmpty("floats").stream().map(x -> x.asFloat().orElseThrow()).toList();
            List<Boolean> flags = data.getListOrEmpty("flags").stream().map(x -> x.asBoolean().orElseThrow()).toList();
            List<String> strings = data.getListOrEmpty("strings").stream().map(x -> x.asString().orElseThrow()).toList();
            List<Integer> colors = data.getListOrEmpty("colors").stream().map(x -> x.asInt().orElseThrow()).toList();
            CustomModelData customModelDataComponent = new CustomModelData(floats, flags, strings, colors);
            stack.set(DataComponents.CUSTOM_MODEL_DATA, customModelDataComponent);
        }
        if (this.hasSetting("tooltip_style")) {
            String style = ((StringTag) this.getSetting("tooltip_style")).value();
            stack.set(DataComponents.TOOLTIP_STYLE, Identifier.parse(style));
        }
        if (this.hasSetting("note_block_sound")) {
            String s = ((StringTag) this.getSetting("note_block_sound")).value();
            stack.set(DataComponents.NOTE_BLOCK_SOUND, Identifier.parse(s));
        }
        if (this.hasSetting("jukebox_song")) {
            String s = ((StringTag) this.getSetting("jukebox_song")).value();
            Holder.Reference<JukeboxSong> song = world.registryAccess().lookupOrThrow(Registries.JUKEBOX_SONG).get(Identifier.parse(s)).get();
            stack.set(DataComponents.JUKEBOX_PLAYABLE, new JukeboxPlayable(new EitherHolder<>(song)));
        }
        if (this.hasSetting("instrument")) {
            String i = ((StringTag) this.getSetting("instrument")).value();
            Holder.Reference<Instrument> instrument = world.registryAccess().lookupOrThrow(Registries.INSTRUMENT).get(Identifier.parse(i)).get();
            stack.set(DataComponents.INSTRUMENT, new InstrumentComponent(Holder.direct(instrument.value())));
        }
        if (this.hasSetting("hidden_components")) {
            ListTag list = (ListTag) this.getSetting("hidden_components");
            LinkedHashSet<DataComponentType<?>> hidden = new LinkedHashSet<>(
                    list.stream()
                            .filter(x -> BuiltInRegistries.DATA_COMPONENT_TYPE.containsKey(Identifier.parse(x.asString().get())))
                            .map(x -> BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.parse(x.asString().get())))
                            .filter(x -> stack.getComponents().has(x))
                            .toList()
            );
            stack.set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(false, hidden));
        }
    }

    public int getCost() {
        if (!this.hasSettings()) return 0;
        AtomicInteger cost = new AtomicInteger();
        COST_MAP.forEach((key, value) -> {
            if (this.hasSetting(key))
                cost.addAndGet(value);
        });
        return Math.max(cost.get(), 1);
    }

    public boolean canApplyToStack(ItemStack stack) {
        return CAN_APPLY_PREDICATES.entrySet().stream().noneMatch(x -> this.hasSetting(x.getKey()) && x.getValue().test(this, stack));
    }

    private DialogBody getCostDialogBody() {
        if (this.getCost() > 0) {
            return new ItemBody(
                    new ItemStack(ingredient, this.getCost()),
                    Optional.of(new PlainMessage(
                            Component.translatableWithFallback("gui.igalaxy_item_customization.to_apply", "to apply"), 50
                    )),
                    true, true, 16, 16
            );
        }
        return new PlainMessage(
                Component.empty(),
                200
        );
    }
}
