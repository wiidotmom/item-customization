package mom.wii.itemcustomization.template;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

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
        return List.of(
                Text.of("Smithing Template").copy().styled(style -> style.withColor(Formatting.GRAY).withItalic(false)),
                Text.empty(),
                Text.of("Applies to:").copy().styled(style -> style.withColor(Formatting.GRAY).withItalic(false)),
                Text.of(" Any").copy().styled(style -> style.withItalic(false).withColor(Formatting.BLUE)),
                Text.of("Ingredients:").copy().styled(style -> style.withItalic(false).withColor(Formatting.GRAY)),
                Text.of(" ").copy().append(Text.translatable(this.ingredient.getTranslationKey()).styled(style -> style.withItalic(false).withColor(Formatting.BLUE)))
        );
    }

    private int getCost() {
        return 0;
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
