package mom.wii.itemcustomization.template.settings.tooltip;

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

import static mom.wii.itemcustomization.dialog.DialogManager.simpleTranslatableMenuButton;

public class TooltipSettings {
    public static void openRootDialog(ServerPlayerEntity player) {
        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.tooltip.title", "Tooltip"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of()
                ),
                List.of(
                        simpleTranslatableMenuButton("tooltip_style.external_title", "Tooltip Style...", "tooltip_style"),
                        simpleTranslatableMenuButton("hidden_components.external_title", "Hidden Components...", "hidden_components")
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
