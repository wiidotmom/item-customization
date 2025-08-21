package mom.wii.itemcustomization.template.settings;

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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
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

public class CustomModelDataSettings {
    public static void openRootDialog(ServerPlayerEntity player, SmithingTemplate template) {
        NbtCompound customModelData = (NbtCompound) template.getSettingOrElse("custom_model_data", NbtCompound::new);
        StringNbtWriter writer = new StringNbtWriter();
        writer.visitCompound(customModelData);
        String previewString = writer.getString();

        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.title", "Custom Model Data"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.add", "Preview"), 200),
                                new PlainMessageDialogBody(Text.literal(previewString).formatted(Formatting.GRAY), 200),
                                new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.add_new", "Add New"), 200)
                        ),
                        List.of()
                ),
                List.of(
                        simpleTranslatableMenuButton("custom_model_data.float.external_title", "Float...", "custom_model_data/float", 50),
                        simpleTranslatableMenuButton("custom_model_data.flag.external_title", "Flag...", "custom_model_data/flag", 50),
                        simpleTranslatableMenuButton("custom_model_data.string.external_title", "String...", "custom_model_data/string", 50),
                        simpleTranslatableMenuButton("custom_model_data.color.external_title", "Color...", "custom_model_data/color", 50)
                ),
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "root"), Optional.empty())
                                ))
                        )
                ),
                2
        );

        player.openDialog(RegistryEntry.of(dialog));
    }

    public static void openAddNewDialog(ServerPlayerEntity player, String id, String fallback, boolean multiline) {
        ConfirmationDialog dialog = new ConfirmationDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.add_new", "Add New"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of(
                                new DialogInput(id, new TextInputControl(
                                        multiline ? 200 : 100,
                                        Text.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data." + id, fallback),
                                        true,
                                        "",
                                        32,
                                        Optional.ofNullable(multiline ? new TextInputControl.Multiline(
                                                Optional.empty(),
                                                Optional.of(32)
                                        ) : null)
                                ))
                        )
                ),
                new DialogActionButtonData(
                        new DialogButtonData(Text.translatableWithFallback("gui.submit", "Submit"), 150),
                        Optional.of(new DynamicCustomDialogAction(
                                Identifier.of(ItemCustomization.MOD_ID, "custom_model_data/" + id + "/add"), Optional.empty()
                        ))
                ),
                new DialogActionButtonData(
                        new DialogButtonData(Text.translatable("gui.back"), 150),
                        Optional.of(new SimpleDialogAction(
                                new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "custom_model_data"), Optional.empty())
                        ))
                )
        );

        player.openDialog(RegistryEntry.of(dialog));
    }

    public static void openAddNewFlagDialog(ServerPlayerEntity player) {
        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.add_new", "Add New"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.flag", "Flag"), 200)
                        ),
                        List.of()
                ),
                List.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.flag.true", "true (1b)"), 75),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "custom_model_data/flag/add"), Optional.of(NbtString.of("true")))
                                ))
                        ),
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.flag.false", "false (0b)"), 75),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "custom_model_data/flag/add"), Optional.of(NbtString.of("false")))
                                ))
                        )
                ),
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "custom_model_data"), Optional.empty())
                                ))
                        )
                ),
                1
        );

        player.openDialog(RegistryEntry.of(dialog));
    }
}
