package mom.wii.itemcustomization.template.settings.music_and_sounds;

import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.core.Holder;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.ConfirmationDialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.dialog.input.TextInput;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import java.util.Optional;

public class NoteBlockSoundSettings {
    public static void openRootDialog(ServerPlayer player, SmithingTemplate template) {
        String initial = template.getSettingOrElse("note_block_sound", () -> StringTag.valueOf("")).asString().get();

        ConfirmationDialog dialog = new ConfirmationDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.note_block_sound.title", "Note Block Sound"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of(
                                new Input(
                                        "note_block_sound",
                                        new TextInput(
                                                200,
                                                Component.translatableWithFallback("gui.igalaxy_item_customization.sound_event", "Sound Event"),
                                                true,
                                                initial,
                                                32,
                                                Optional.empty()
                                        )
                                )
                        )
                ),
                new ActionButton(
                        new CommonButtonData(Component.translatableWithFallback("gui.submit", "Submit"), 150),
                        Optional.of(new CustomAll(
                                Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "note_block_sound/set"), Optional.empty()
                        ))
                ),
                new ActionButton(
                        new CommonButtonData(Component.translatable("gui.back"), 150),
                        Optional.of(new StaticAction(
                                new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "music_and_sounds"), Optional.empty())
                        ))
                )
        );

        player.openDialog(Holder.direct(dialog));
    }
}
