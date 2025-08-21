package mom.wii.itemcustomization.template.settings.tooltip;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mom.wii.itemcustomization.dialog.Dialogs.SEARCH_ICON;

public class TooltipStyleSettings {
    public static void openRootDialog(ServerPlayerEntity player) {
        ArrayList<DialogActionButtonData> buttons = new ArrayList<>();
        ItemCustomization.TOOLTIP_STYLE_INDEX.namespaces.forEach(namespace -> {
            buttons.add(new DialogActionButtonData(
                    new DialogButtonData(
                            Text.of(namespace),
                            125
                    ),
                    Optional.of(new SimpleDialogAction(
                            new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "tooltip_style/namespace"), Optional.of(NbtString.of(namespace)))
                    ))
            ));
        });

        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.tooltip_style.title", "Tooltip Style"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new ItemDialogBody(SEARCH_ICON, Optional.of(new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.select_namespace", "Select a namespace to browse"), 200)), false, false, 16, 16),
                                new PlainMessageDialogBody(Text.literal("/assets/").formatted(Formatting.GRAY), 200)
                        ),
                        List.of()
                ),
                buttons,
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "tooltip"), Optional.empty())
                                ))
                        )
                ),
                3
        );

        player.openDialog(RegistryEntry.of(dialog));
    }

    public static void openDialogForNamespace(ServerPlayerEntity player, String namespace) {
        ArrayList<DialogActionButtonData> buttons = new ArrayList<>();
        ItemCustomization.TOOLTIP_STYLE_INDEX.getIdentifiersOfNamespace(namespace).forEach(identifier -> {
            buttons.add(new DialogActionButtonData(
                    new DialogButtonData(
                            Text.literal(identifier.getPath()),
                            125
                    ),
                    Optional.of(new SimpleDialogAction(
                            new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "tooltip_style/set"), Optional.of(NbtString.of(identifier.toString())))
                    ))
            ));
        });

        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.tooltip_style.title", "Tooltip Style"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new ItemDialogBody(SEARCH_ICON, Optional.of(new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.tooltip_style.select_style", "Select a tooltip style"), 200)), false, false, 16, 16),
                                new PlainMessageDialogBody(Text.literal("/assets/").formatted(Formatting.GRAY).append(Text.literal(namespace).formatted(Formatting.WHITE).append(Text.literal("/textures/gui/sprites/tooltip/<style>_(frame|background).png").formatted(Formatting.GRAY))), 200)
                        ),
                        List.of()
                ),
                buttons,
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "tooltip_style"), Optional.empty())
                                ))
                        )
                ),
                3
        );

        player.openDialog(RegistryEntry.of(dialog));
    }
}
