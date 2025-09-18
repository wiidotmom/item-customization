package mom.wii.itemcustomization.template.settings.tooltip;

import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.DynamicCustomDialogAction;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.dialog.type.ConfirmationDialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

import static mom.wii.itemcustomization.dialog.DialogManager.simpleTranslatableMenuButton;

public class HiddenComponentSettings {
    public static void openRootDialog(ServerPlayerEntity player, SmithingTemplate template) {
        NbtList hiddenComponents = (NbtList) template.getSettingOrElse("hidden_components", NbtList::new);
        StringNbtWriter writer = new StringNbtWriter();
        writer.visitList(hiddenComponents);
        String previewString = writer.getString();

        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.hidden_components.title", "Hidden Components"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.preview", "Preview"), 200),
                                new PlainMessageDialogBody(Text.literal(previewString).formatted(Formatting.GRAY), 200)
                        ),
                        List.of()
                ),
                List.of(
                        simpleTranslatableMenuButton("hidden_components.add_component.external_title", "Add Component...", "hidden_components/add_component")
                ),
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "tooltip"), Optional.empty())
                                ))
                        )
                ),
                2
        );

        player.openDialog(RegistryEntry.of(dialog));
    }

    public static void openAddComponentDialog(ServerPlayerEntity player) {
        ConfirmationDialog dialog = new ConfirmationDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.hidden_components.add_component.title", "Add Component"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of(
                                new DialogInput(
                                        "component",
                                        new TextInputControl(
                                                200,
                                                Text.translatableWithFallback("gui.igalaxy_item_customization.hidden_components.component", "Component"),
                                                true,
                                                "",
                                                32,
                                                Optional.empty()
                                        )
                                )
                        )
                ),
                new DialogActionButtonData(
                        new DialogButtonData(Text.translatableWithFallback("gui.submit", "Submit"), 150),
                        Optional.of(new DynamicCustomDialogAction(
                                Identifier.of(ItemCustomization.MOD_ID, "hidden_components/add_component/add"), Optional.empty()
                        ))
                ),
                new DialogActionButtonData(
                        new DialogButtonData(Text.translatable("gui.back"), 150),
                        Optional.of(new SimpleDialogAction(
                                new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "hidden_components"), Optional.empty())
                        ))
                )
        );

        player.openDialog(RegistryEntry.of(dialog));
    }
}
