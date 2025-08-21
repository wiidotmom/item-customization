package mom.wii.itemcustomization.template.settings.equipment;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

import static mom.wii.itemcustomization.dialog.DialogManager.translatableMenuButtonWithTooltip;

public class EquipmentSettings {
    public static void openRootDialog(ServerPlayerEntity player) {
        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.equipment.title", "Equipment"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of()
                ),
                List.of(
                        translatableMenuButtonWithTooltip("equipment_model.external_title", "Equipment Model...", "equipment_model.tooltip", "Only applies to equippable items (any slot), such as armor, Saddles/Harnesses, or Carved Pumpkins", "equipment_model"),
                        translatableMenuButtonWithTooltip("camera_overlay.external_title", "Camera Overlay...", "camera_overlay.tooltip", "Only applies to head-equippable items, such as Helmets, Mob/Player Heads, or Carved Pumpkins", "camera_overlay")
                ),
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "root"), Optional.empty())
                                ))
                        )
                ),
                1
        );

        player.openDialog(RegistryEntry.of(dialog));
    }
}
