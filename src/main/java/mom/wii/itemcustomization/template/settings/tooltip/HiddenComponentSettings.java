package mom.wii.itemcustomization.template.settings.tooltip;

import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.ConfirmationDialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.dialog.input.TextInput;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import java.util.Optional;

import static mom.wii.itemcustomization.dialog.DialogManager.simpleTranslatableMenuButton;

public class HiddenComponentSettings {
    public static void openRootDialog(ServerPlayer player, SmithingTemplate template) {
        ListTag hiddenComponents = (ListTag) template.getSettingOrElse("hidden_components", ListTag::new);
        StringTagVisitor writer = new StringTagVisitor();
        writer.visitList(hiddenComponents);
        String previewString = writer.build();

        MultiActionDialog dialog = new MultiActionDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.hidden_components.title", "Hidden Components"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new PlainMessage(Component.translatableWithFallback("gui.igalaxy_item_customization.preview", "Preview"), 200),
                                new PlainMessage(Component.literal(previewString).withStyle(ChatFormatting.GRAY), 200)
                        ),
                        List.of()
                ),
                List.of(
                        simpleTranslatableMenuButton("hidden_components.add_component.external_title", "Add Component...", "hidden_components/add_component")
                ),
                Optional.of(
                        new ActionButton(
                                new CommonButtonData(Component.translatable("gui.back"), 200),
                                Optional.of(new StaticAction(
                                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "tooltip"), Optional.empty())
                                ))
                        )
                ),
                2
        );

        player.openDialog(Holder.direct(dialog));
    }

    public static void openAddComponentDialog(ServerPlayer player) {
        ConfirmationDialog dialog = new ConfirmationDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.hidden_components.add_component.title", "Add Component"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of(
                                new Input(
                                        "component",
                                        new TextInput(
                                                200,
                                                Component.translatableWithFallback("gui.igalaxy_item_customization.hidden_components.component", "Component"),
                                                true,
                                                "",
                                                32,
                                                Optional.empty()
                                        )
                                )
                        )
                ),
                new ActionButton(
                        new CommonButtonData(Component.translatableWithFallback("gui.submit", "Submit"), 150),
                        Optional.of(new CustomAll(
                                Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "hidden_components/add_component/add"), Optional.empty()
                        ))
                ),
                new ActionButton(
                        new CommonButtonData(Component.translatable("gui.back"), 150),
                        Optional.of(new StaticAction(
                                new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "hidden_components"), Optional.empty())
                        ))
                )
        );

        player.openDialog(Holder.direct(dialog));
    }
}
