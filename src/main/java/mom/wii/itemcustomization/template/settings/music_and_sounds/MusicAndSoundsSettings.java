package mom.wii.itemcustomization.template.settings.music_and_sounds;

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

public class MusicAndSoundsSettings {
    public static void openRootDialog(ServerPlayerEntity player) {
        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.music_and_sounds.title", "Music & Sounds"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of()
                ),
                List.of(
                        translatableMenuButtonWithTooltip("note_block_sound.external_title", "Note Block Sound...", "note_block_sound.tooltip", "Only applies to Player Heads", "note_block_sound"),
                        translatableMenuButtonWithTooltip("jukebox_song.external_title", "Jukebox Song...", "jukebox_song.tooltip", "Only applies to Music Discs", "jukebox_song"),
                        translatableMenuButtonWithTooltip("instrument.external_title", "Instrument...", "instrument.tooltip", "Only applies to Goat Horns", "instrument")
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
