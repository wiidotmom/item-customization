package mom.wii.itemcustomization.template.settings;

import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
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

public class CustomModelDataSettings {
    public static void openRootDialog(ServerPlayer player, SmithingTemplate template) {
        CompoundTag customModelData = (CompoundTag) template.getSettingOrElse("custom_model_data", CompoundTag::new);
        StringTagVisitor writer = new StringTagVisitor();
        writer.visitCompound(customModelData);
        String previewString = writer.build();

        MultiActionDialog dialog = new MultiActionDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.title", "Custom Model Data"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new PlainMessage(Component.translatableWithFallback("gui.igalaxy_item_customization.preview", "Preview"), 200),
                                new PlainMessage(Component.literal(previewString).withStyle(ChatFormatting.GRAY), 200),
                                new PlainMessage(Component.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.add_new", "Add New"), 200)
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
                        new ActionButton(
                                new CommonButtonData(Component.translatable("gui.back"), 200),
                                Optional.of(new StaticAction(
                                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "root"), Optional.empty())
                                ))
                        )
                ),
                2
        );

        player.openDialog(Holder.direct(dialog));
    }

    public static void openAddNewDialog(ServerPlayer player, String id, String fallback, boolean multiline) {
        ConfirmationDialog dialog = new ConfirmationDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.add_new", "Add New"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of(
                                new Input(id, new TextInput(
                                        multiline ? 200 : 100,
                                        Component.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data." + id, fallback),
                                        true,
                                        "",
                                        32,
                                        Optional.ofNullable(multiline ? new TextInput.MultilineOptions(
                                                Optional.empty(),
                                                Optional.of(32)
                                        ) : null)
                                ))
                        )
                ),
                new ActionButton(
                        new CommonButtonData(Component.translatableWithFallback("gui.submit", "Submit"), 150),
                        Optional.of(new CustomAll(
                                Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "custom_model_data/" + id + "/add"), Optional.empty()
                        ))
                ),
                new ActionButton(
                        new CommonButtonData(Component.translatable("gui.back"), 150),
                        Optional.of(new StaticAction(
                                new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "custom_model_data"), Optional.empty())
                        ))
                )
        );

        player.openDialog(Holder.direct(dialog));
    }

    public static void openAddNewFlagDialog(ServerPlayer player) {
        MultiActionDialog dialog = new MultiActionDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.add_new", "Add New"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new PlainMessage(Component.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.flag", "Flag"), 200)
                        ),
                        List.of()
                ),
                List.of(
                        new ActionButton(
                                new CommonButtonData(Component.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.flag.true", "true (1b)"), 75),
                                Optional.of(new StaticAction(
                                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "custom_model_data/flag/add"), Optional.of(StringTag.valueOf("true")))
                                ))
                        ),
                        new ActionButton(
                                new CommonButtonData(Component.translatableWithFallback("gui.igalaxy_item_customization.custom_model_data.flag.false", "false (0b)"), 75),
                                Optional.of(new StaticAction(
                                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "custom_model_data/flag/add"), Optional.of(StringTag.valueOf("false")))
                                ))
                        )
                ),
                Optional.of(
                        new ActionButton(
                                new CommonButtonData(Component.translatable("gui.back"), 200),
                                Optional.of(new StaticAction(
                                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "custom_model_data"), Optional.empty())
                                ))
                        )
                ),
                1
        );

        player.openDialog(Holder.direct(dialog));
    }
}
