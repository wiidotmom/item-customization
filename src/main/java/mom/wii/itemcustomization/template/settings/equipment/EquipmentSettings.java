package mom.wii.itemcustomization.template.settings.equipment;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import java.util.Optional;

import static mom.wii.itemcustomization.dialog.DialogManager.translatableMenuButtonWithTooltip;

public class EquipmentSettings {
    public static void openRootDialog(ServerPlayer player) {
        MultiActionDialog dialog = new MultiActionDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.equipment.title", "Equipment"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of()
                ),
                List.of(
                        translatableMenuButtonWithTooltip("equipment_model.external_title", "Equipment Model...", "equipment_model.tooltip", "Only applies to equippable items (any slot), such as armor, Saddles/Harnesses, or Carved Pumpkins", "equipment_model"),
                        translatableMenuButtonWithTooltip("camera_overlay.external_title", "Camera Overlay...", "camera_overlay.tooltip", "Only applies to head-equippable items, such as Helmets, Mob/Player Heads, or Carved Pumpkins", "camera_overlay")
                ),
                Optional.of(
                        new ActionButton(
                                new CommonButtonData(Component.translatable("gui.back"), 200),
                                Optional.of(new StaticAction(
                                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "root"), Optional.empty())
                                ))
                        )
                ),
                1
        );

        player.openDialog(Holder.direct(dialog));
    }
}
