package mom.wii.itemcustomization.template;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static mom.wii.itemcustomization.dialog.DialogManager.simpleTranslatableMenuButton;
import static mom.wii.itemcustomization.dialog.DialogManager.translatableMenuButtonWithTooltip;

public class SmithingTemplate {
    private static final ItemStack PREVIEW_SLOT_ITEMSTACK;
    private static final List<DialogActionButtonData> ROOT_MENU_BUTTONS = List.of(
            simpleTranslatableMenuButton("item_model.external_title", "Item Model...", "item_model"),
            translatableMenuButtonWithTooltip(
                    "equipment.external_title", "Equipment...",
                    "equipment.tooltip", "Only applies to already equippable items, such as armor or Carved Pumpkins",
                    "equipment"
            ),
            simpleTranslatableMenuButton("custom_model_data.external_title", "Custom Model Data...", "custom_model_data"),
            simpleTranslatableMenuButton("tooltip.external_title", "Tooltip...", "tooltip"),
            simpleTranslatableMenuButton("music_and_sounds.external_title", "Music & Sounds...", "music_and_sounds"),
            simpleTranslatableMenuButton("preview_settings.external_title", "Preview Settings...", "preview_settings")
    );
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
                tooltip.add(
                        Text.literal(" ")
                                .append(Text.literal(itemModel).styled(style -> style.withItalic(true).withColor(Formatting.YELLOW)))
                                .append(Text.literal(" Item Model").styled(style -> style.withColor(Formatting.YELLOW).withItalic(false)))
                );
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
    }

    private int getCost() {
        int cost = 0;
        if (this.hasSetting("item_model"))
            cost++;
        return cost;
    }

    private DialogBody getCostDialogBody() {
        if (this.getCost() > 0) {
            return new ItemDialogBody(
                    new ItemStack(ingredient, this.getCost()),
                    Optional.of(new PlainMessageDialogBody(
                            Text.translatableWithFallback("gui.igalaxy_item_customization.to_apply", " to apply"), 200
                    )),
                    false, true, 16, 16
            );
        }
        return new PlainMessageDialogBody(
                Text.empty(),
                200
        );
    }
}
