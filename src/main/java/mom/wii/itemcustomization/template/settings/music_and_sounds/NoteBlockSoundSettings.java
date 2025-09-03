package mom.wii.itemcustomization.template.settings.music_and_sounds;

import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.DynamicCustomDialogAction;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.dialog.type.ConfirmationDialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class NoteBlockSoundSettings {
    public static void openRootDialog(ServerPlayerEntity player, SmithingTemplate template) {
        String initial = template.getSettingOrElse("note_block_sound", () -> NbtString.of("")).asString().get();

        ConfirmationDialog dialog = new ConfirmationDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.note_block_sound.title", "Note Block Sound"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of(
                                new DialogInput(
                                        "note_block_sound",
                                        new TextInputControl(
                                                200,
                                                Text.translatableWithFallback("gui.igalaxy_item_customization.sound_event", "Sound Event"),
                                                true,
                                                initial,
                                                32,
                                                Optional.empty()
                                        )
                                )
                        )
                ),
                new DialogActionButtonData(
                        new DialogButtonData(Text.translatableWithFallback("gui.submit", "Submit"), 150),
                        Optional.of(new DynamicCustomDialogAction(
                                Identifier.of(ItemCustomization.MOD_ID, "note_block_sound/set"), Optional.empty()
                        ))
                ),
                new DialogActionButtonData(
                        new DialogButtonData(Text.translatable("gui.back"), 150),
                        Optional.of(new SimpleDialogAction(
                                new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "music_and_sounds"), Optional.empty())
                        ))
                )
        );

        player.openDialog(RegistryEntry.of(dialog));
    }
}
