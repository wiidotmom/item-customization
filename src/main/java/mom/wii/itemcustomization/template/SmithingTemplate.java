package mom.wii.itemcustomization.template;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static mom.wii.itemcustomization.dialog.DialogManager.simpleTranslatableMenuButton;

public class SmithingTemplate {
    private static final ItemStack PREVIEW_SLOT_ITEMSTACK;
    private static final List<DialogActionButtonData> ROOT_MENU_BUTTONS = List.of(
            simpleTranslatableMenuButton("item_model.external_title", "Item Model...", "item_model"),
            simpleTranslatableMenuButton("equipment.external_title", "Equipment...", "equipment"),
            simpleTranslatableMenuButton("custom_model_data.external_title", "Custom Model Data...", "custom_model_data"),
            simpleTranslatableMenuButton("tooltip.external_title", "Tooltip...", "tooltip"),
            simpleTranslatableMenuButton("music_and_sounds.external_title", "Music & Sounds...", "music_and_sounds")
    );
    public static final HashMap<String, Integer> COST_MAP = new HashMap<>() {{
        put("item_model", 1);
        put("equipment_model", 6);
        put("camera_overlay", 2);
    }};
    private ItemStack itemStack;
    public static final Item ingredient;


    static {
        ItemStack egg = new ItemStack(Items.EGG);
        egg.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ITEM_MODEL, Identifier.of(ItemCustomization.MOD_ID, "preview_slot")).build());
        PREVIEW_SLOT_ITEMSTACK = egg;
        ingredient = Registries.ITEM.get(Identifier.of(ItemCustomization.CONFIG.smithingIngredient));
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

    public static boolean isItemCustomizationSmithingTemplate(ItemStack itemStack) {
        return itemStack.hasChangedComponent(DataComponentTypes.CUSTOM_DATA) && Objects.requireNonNull(itemStack.get(DataComponentTypes.CUSTOM_DATA)).contains("igalaxy_item_customization:is_customization_template");
    }

    public void openDialog(ServerPlayerEntity player) {
        ItemStack previewItem = new ItemStack(Items.PAPER);
        previewItem.set(DataComponentTypes.ITEM_NAME, Text.translatableWithFallback("gui.igalaxy_item_customization.preview_item", "Preview Item"));
        this.applySettings(previewItem);

        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                    Text.translatableWithFallback("gui.igalaxy_item_customization.root.title", "Item Customization"),
                    Optional.empty(),
                    true,
                    true,
                    AfterAction.WAIT_FOR_RESPONSE,
                    List.of(
                            new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.preview", "Preview"), 200),
                            new ItemDialogBody(PREVIEW_SLOT_ITEMSTACK, Optional.empty(), false, false, 16, 1),
                            new ItemDialogBody(previewItem, Optional.empty(), false, true, 16, 24),
                            this.getCostDialogBody(),
                            new PlainMessageDialogBody(Text.translatable("options.title"), 200)
                    ),
                    List.of()
                ),
                ROOT_MENU_BUTTONS,
                Optional.empty(),
                2
        );

        player.openDialog(RegistryEntry.of(dialog));
    }

    public List<Text> getTooltip() {
        ArrayList<Text> tooltip = new ArrayList<>(List.of(
                Text.translatable("item.minecraft.smithing_template").styled(style -> style.withColor(Formatting.GRAY).withItalic(false)),
                Text.empty(),
                Text.translatable("item.minecraft.smithing_template.applies_to").styled(style -> style.withColor(Formatting.GRAY).withItalic(false)),
                Text.literal(" Any").styled(style -> style.withItalic(false).withColor(Formatting.BLUE)),
                Text.translatable("item.minecraft.smithing_template.ingredients").styled(style -> style.withItalic(false).withColor(Formatting.GRAY)),
                Text.literal(" ").append(Text.translatable(this.ingredient.getTranslationKey()).styled(style -> style.withItalic(false).withColor(Formatting.BLUE)))

        ));
        if (this.hasSettings()) {
            tooltip.add(Text.translatable("potion.whenDrank").styled(style -> style.withItalic(false).withColor(Formatting.GRAY)));
            if (this.hasSetting("item_model")) {
                String itemModel = ((NbtString) this.getSetting("item_model")).value();
                tooltip.add(Text.literal(" Item Model").styled(style -> style.withColor(Formatting.GOLD).withItalic(false)));
                tooltip.add(Text.literal("  " + itemModel).styled(style -> style.withItalic(false).withColor(Formatting.DARK_GRAY)));
            }
            if (this.hasSetting("equipment_model")) {
                String equipmentModel = ((NbtString) this.getSetting("equipment_model")).value();
                tooltip.add(Text.literal(" Equipment Model").styled(style -> style.withColor(Formatting.GOLD).withItalic(false)));
                tooltip.add(Text.literal("  " + equipmentModel).styled(style -> style.withItalic(false).withColor(Formatting.DARK_GRAY)));
            }
            if (this.hasSetting("camera_overlay")) {
                String cameraOverlay = ((NbtString) this.getSetting("camera_overlay")).value();
                tooltip.add(Text.literal(" Camera Overlay").styled(style -> style.withColor(Formatting.GOLD).withItalic(false)));
                tooltip.add(Text.literal("  " + cameraOverlay).styled(style -> style.withItalic(false).withColor(Formatting.DARK_GRAY)));
            }
        }
        return tooltip;
    }

    public boolean hasSettings() {
        if (this.itemStack.hasChangedComponent(DataComponentTypes.CUSTOM_DATA)) {
            return this.itemStack.get(DataComponentTypes.CUSTOM_DATA).contains("igalaxy_item_customization:settings");
        }
        return false;
    }

    public boolean hasSetting(String key) {
        if (this.itemStack.hasChangedComponent(DataComponentTypes.CUSTOM_DATA)) {
            if (this.itemStack.get(DataComponentTypes.CUSTOM_DATA).contains("igalaxy_item_customization:settings")) {
                NbtCompound customData = this.itemStack.get(DataComponentTypes.CUSTOM_DATA).copyNbt();
                return customData.getCompound("igalaxy_item_customization:settings").isPresent() &&
                        customData.getCompound("igalaxy_item_customization:settings").get().contains(key);
            }
        }
        return false;
    }

    public @Nullable NbtElement getSetting(String key) {
        if (hasSetting(key)) {
            NbtCompound customData = this.itemStack.get(DataComponentTypes.CUSTOM_DATA).copyNbt();
            NbtCompound settings = customData.getCompound("igalaxy_item_customization:settings").get();
            return settings.get(key);
        }
        return null;
    }

    public @Nullable NbtElement getSettingOrElse(String key, Supplier<NbtElement> defaultValue) {
        NbtElement setting = this.getSetting(key);
        if (setting == null) {
            return defaultValue.get();
        }
        return setting;
    }

    public NbtComponent setSetting(String key, NbtElement value) {
        if (this.itemStack.hasChangedComponent(DataComponentTypes.CUSTOM_DATA)) {
            NbtComponent customData = this.itemStack.get(DataComponentTypes.CUSTOM_DATA);
            NbtCompound newCustomData = customData.copyNbt();
            if (this.itemStack.get(DataComponentTypes.CUSTOM_DATA).contains("igalaxy_item_customization:settings")) {
                NbtCompound settings = newCustomData.getCompound("igalaxy_item_customization:settings").get();
                settings.put(key, value);
                newCustomData.put("igalaxy_item_customization:settings", settings);
            } else {
                NbtCompound settings = new NbtCompound();
                settings.put(key, value);
                newCustomData.put("igalaxy_item_customization:settings", settings);
            }
            return this.itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(newCustomData));
        } else {
            NbtCompound customData = new NbtCompound();
            NbtCompound settings = new NbtCompound();
            settings.put(key, value);
            customData.put("igalaxy_item_customization:settings", settings);
            return this.itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(customData));
        }
    }

    public void resetSettings() {
        if (itemStack.hasChangedComponent(DataComponentTypes.CUSTOM_DATA)) {
            if (itemStack.get(DataComponentTypes.CUSTOM_DATA).contains("igalaxy_item_customization:settings")) {
                NbtCompound newCustomData = itemStack.get(DataComponentTypes.CUSTOM_DATA).copyNbt();
                newCustomData.remove("igalaxy_item_customization:settings");
                itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(newCustomData));
            }
        }
    }

    public void applySettings(ItemStack stack) {
        if (this.hasSetting("item_model")) {
            Identifier id = Identifier.of(((NbtString) this.getSetting("item_model")).value());
            stack.set(DataComponentTypes.ITEM_MODEL, id);
        }
        if (this.hasSetting("equipment_model")) {
            Identifier id = Identifier.of(((NbtString) this.getSetting("equipment_model")).value());
            if (stack.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE)) {
                EquippableComponent ec = stack.get(DataComponentTypes.EQUIPPABLE);
                RegistryKey<EquipmentAsset> equipmentAsset = RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), id);
                EquippableComponent newEquippableComponent = new EquippableComponent(
                        ec.slot(), ec.equipSound(),
                        Optional.of(equipmentAsset),
                        ec.cameraOverlay(), ec.allowedEntities(), ec.dispensable(), ec.swappable(), ec.damageOnHurt(), ec.equipOnInteract(), ec.canBeSheared(), ec.shearingSound()
                );
                stack.set(DataComponentTypes.EQUIPPABLE, newEquippableComponent);
            }
        }
        if (this.hasSetting("camera_overlay")) {
            Identifier id = Identifier.of(((NbtString) this.getSetting("camera_overlay")).value());
            if (stack.getDefaultComponents().contains(DataComponentTypes.EQUIPPABLE) && stack.getDefaultComponents().get(DataComponentTypes.EQUIPPABLE).slot().equals(EquipmentSlot.HEAD)) {
                EquippableComponent ec = stack.get(DataComponentTypes.EQUIPPABLE);
                EquippableComponent newEquippableComponent = new EquippableComponent(
                        ec.slot(), ec.equipSound(), ec.assetId(),
                        Optional.of(id),
                        ec.allowedEntities(), ec.dispensable(), ec.swappable(), ec.damageOnHurt(), ec.equipOnInteract(), ec.canBeSheared(), ec.shearingSound()
                );
                stack.set(DataComponentTypes.EQUIPPABLE, newEquippableComponent);
            }
        }
        if (this.hasSetting("custom_model_data")) {
            NbtCompound data = (NbtCompound) this.getSetting("custom_model_data");
            List<Float> floats = data.getListOrEmpty("floats").stream().map(x -> x.asFloat().orElseThrow()).toList();
            List<Boolean> flags = data.getListOrEmpty("flags").stream().map(x -> x.asBoolean().orElseThrow()).toList();
            List<String> strings = data.getListOrEmpty("strings").stream().map(x -> x.asString().orElseThrow()).toList();
            List<Integer> colors = data.getListOrEmpty("colors").stream().map(x -> x.asInt().orElseThrow()).toList();
            CustomModelDataComponent customModelDataComponent = new CustomModelDataComponent(floats, flags, strings, colors);
            stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, customModelDataComponent);
        }
    }

    public int getCost() {
        AtomicInteger cost = new AtomicInteger();
        COST_MAP.forEach((key, value) -> {
            if (this.hasSetting(key))
                cost.addAndGet(value);
        });
        return cost.get();
    }

    public boolean canApplyToStack(ItemStack stack) {
        boolean canApply = true;
        ComponentMap def = stack.getDefaultComponents();
        if (this.hasSetting("equipment_model") && !def.contains(DataComponentTypes.EQUIPPABLE))
            canApply = false;
        if (
                this.hasSetting("camera_overlay") &&
                        (!def.contains(DataComponentTypes.EQUIPPABLE) || (def.contains(DataComponentTypes.EQUIPPABLE) && !def.get(DataComponentTypes.EQUIPPABLE).slot().equals(EquipmentSlot.HEAD)))
        )
            canApply = false;
        return canApply;
    }

    private DialogBody getCostDialogBody() {
        if (this.getCost() > 0) {
            return new ItemDialogBody(
                    new ItemStack(ingredient, this.getCost()),
                    Optional.of(new PlainMessageDialogBody(
                            Text.translatableWithFallback("gui.igalaxy_item_customization.to_apply", " to apply"), 200
                    )),
                    true, true, 16, 16
            );
        }
        return new PlainMessageDialogBody(
                Text.empty(),
                200
        );
    }
}
