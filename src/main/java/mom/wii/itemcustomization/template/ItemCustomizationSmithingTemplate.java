package mom.wii.itemcustomization.template;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ItemCustomizationSmithingTemplate {
    private static final ItemStack PREVIEW_SLOT_ITEMSTACK;
    private static final List<DialogActionButtonData> ROOT_MENU_BUTTONS = List.of(
            simpleMenuButton("item_model.external_title", "Item Model...", "item_model"),
            simpleMenuButton("equipment_model.external_title", "Equipment Model...", "equipment_model"),
            simpleMenuButton("music_and_sounds.external_title", "Music & Sounds...", "music_and_sounds"),
            simpleMenuButton("tooltip.external_title", "Tooltip...", "tooltip")
    );
    private ItemStack itemStack;

    static {
        ItemStack egg = new ItemStack(Items.EGG);
        egg.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ITEM_MODEL, Identifier.of(ItemCustomization.MOD_ID, "preview_slot")).build());
        PREVIEW_SLOT_ITEMSTACK = egg;
    }

    private ItemCustomizationSmithingTemplate(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    private static DialogActionButtonData simpleMenuButton(String translation, String fallback, String action) {
        return new DialogActionButtonData(
                new DialogButtonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization." + translation, fallback),
                        125
                ),
                Optional.of(new SimpleDialogAction(
                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, action), Optional.empty())
                ))
        );
    }

    public static @Nullable ItemCustomizationSmithingTemplate from(ItemStack itemStack) {
        if (isItemCustomizationSmithingTemplate(itemStack)) {
            return new ItemCustomizationSmithingTemplate(itemStack);
        }
        return null;
    }

    public static boolean isItemCustomizationSmithingTemplate(ItemStack itemStack) {
        return itemStack.hasChangedComponent(DataComponentTypes.CUSTOM_DATA) && Objects.requireNonNull(itemStack.get(DataComponentTypes.CUSTOM_DATA)).contains("igalaxy_item_customization:is_customization_template");
    }

    public void showRootDialog(ServerPlayerEntity player) {
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
                            new ItemDialogBody(
                                    new ItemStack(Items.RESIN_CLUMP, this.getCost()),
                                    Optional.of(new PlainMessageDialogBody(
                                            Text.translatableWithFallback("gui.igalaxy_item_customization.to_apply", " to apply"), 200
                                    )),
                                    false, true, 16, 16),
                            new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.settings", "Settings"), 200)
                    ),
                    List.of()
                ),
                ROOT_MENU_BUTTONS,
                Optional.empty(),
                2
        );

        player.networkHandler.sendPacket(new ShowDialogS2CPacket(RegistryEntry.of(dialog)));
    }

    private int getCost() {
        return 1;
    }
}
