package mom.wii.itemcustomization.template.settings.music_and_sounds;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.level.ServerPlayer;
import java.util.List;
import java.util.Optional;

import static mom.wii.itemcustomization.dialog.DialogManager.translatableMenuButtonWithTooltip;

public class MusicAndSoundsSettings {
    public static void openRootDialog(ServerPlayer player) {
        MultiActionDialog dialog = new MultiActionDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.music_and_sounds.title", "Music & Sounds"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(),
                        List.of()
                ),
                List.of(
                        translatableMenuButtonWithTooltip("note_block_sound.external_title", "Note Block Sound...", "note_block_sound.tooltip", "Only applies to Player Heads", "note_block_sound"),
                        translatableMenuButtonWithTooltip("jukebox_song.external_title", "Jukebox Song...", "jukebox_song.tooltip", "Only applies to Music Discs", "jukebox_song"),
                        translatableMenuButtonWithTooltip("instrument.external_title", "Instrument...", "instrument.tooltip", "Only applies to Goat Horns", "instrument")
                ),
                Optional.of(
                        new ActionButton(
                                new CommonButtonData(Component.translatable("gui.back"), 200),
                                Optional.of(new StaticAction(
                                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "root"), Optional.empty())
                                ))
                        )
                ),
                1
        );

        player.openDialog(Holder.direct(dialog));
    }
}
