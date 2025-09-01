package mom.wii.itemcustomization.template.settings.music_and_sounds;

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

public class JukeboxSongSettings {
    public static void openRootDialog(ServerPlayerEntity player) {
        ArrayList<DialogActionButtonData> buttons = new ArrayList<>();
        ItemCustomization.JUKEBOX_SONG_INDEX.namespaces.forEach(namespace -> {
            buttons.add(new DialogActionButtonData(
                    new DialogButtonData(
                            Text.of(namespace),
                            125
                    ),
                    Optional.of(new SimpleDialogAction(
                            new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "jukebox_song/namespace"), Optional.of(NbtString.of(namespace)))
                    ))
            ));
        });

        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.jukebox_song.title", "Jukebox Song"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new ItemDialogBody(SEARCH_ICON, Optional.of(new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.select_namespace", "Select a namespace to browse"), 200)), false, false, 16, 16),
                                new PlainMessageDialogBody(Text.literal("/data/").formatted(Formatting.GRAY), 200)
                        ),
                        List.of()
                ),
                buttons,
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "music_and_sounds"), Optional.empty())
                                ))
                        )
                ),
                3
        );

        player.openDialog(RegistryEntry.of(dialog));
    }

    public static void openDialogForNamespace(ServerPlayerEntity player, String namespace) {
        ArrayList<DialogActionButtonData> buttons = new ArrayList<>();
        ItemCustomization.JUKEBOX_SONG_INDEX.getIdentifiersOfNamespace(namespace).forEach(identifier -> {
            buttons.add(new DialogActionButtonData(
                    new DialogButtonData(
                            Text.literal(identifier.getPath()).append(Text.literal(".json").formatted(Formatting.GRAY)),
                            125
                    ),
                    Optional.of(new SimpleDialogAction(
                            new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "jukebox_song/set"), Optional.of(NbtString.of(identifier.toString())))
                    ))
            ));
        });

        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.jukebox_song.title", "Jukebox Song"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new ItemDialogBody(SEARCH_ICON, Optional.of(new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.jukebox_song.select_song", "Select a song"), 200)), false, false, 16, 16),
                                new PlainMessageDialogBody(Text.literal("/data/").formatted(Formatting.GRAY).append(Text.literal(namespace).formatted(Formatting.WHITE).append(Text.literal("/jukebox_song/").formatted(Formatting.GRAY))), 200)
                        ),
                        List.of()
                ),
                buttons,
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "jukebox_song"), Optional.empty())
                                ))
                        )
                ),
                3
        );

        player.openDialog(RegistryEntry.of(dialog));
    }
}
