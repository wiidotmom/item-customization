package mom.wii.itemcustomization.template.settings.music_and_sounds;

import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.util.IdentifierIndex;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.dialog.body.ItemBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.level.ServerPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mom.wii.itemcustomization.dialog.Dialogs.SEARCH_ICON;

public class JukeboxSongSettings {
    public static void openRootDialog(ServerPlayer player) {
        ArrayList<ActionButton> buttons = new ArrayList<>();
        ItemCustomization.JUKEBOX_SONG_INDEX.namespaces.forEach(namespace -> {
            buttons.add(new ActionButton(
                    new CommonButtonData(
                            Component.nullToEmpty(namespace),
                            125
                    ),
                    Optional.of(new StaticAction(
                            new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "jukebox_song/namespace"), Optional.of(StringTag.valueOf(namespace)))
                    ))
            ));
        });

        MultiActionDialog dialog = new MultiActionDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.jukebox_song.title", "Jukebox Song"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new ItemBody(SEARCH_ICON, Optional.of(new PlainMessage(Component.translatableWithFallback("gui.igalaxy_item_customization.select_namespace", "Select a namespace to browse"), 200)), false, false, 16, 16),
                                new PlainMessage(Component.literal("/data/").withStyle(ChatFormatting.GRAY), 200)
                        ),
                        List.of()
                ),
                buttons,
                Optional.of(
                        new ActionButton(
                                new CommonButtonData(Component.translatable("gui.back"), 200),
                                Optional.of(new StaticAction(
                                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "music_and_sounds"), Optional.empty())
                                ))
                        )
                ),
                3
        );

        player.openDialog(Holder.direct(dialog));
    }

    public static void openDialogForNamespace(ServerPlayer player, String namespace) {
        openDialogForNamespaceAndPath(player, namespace, "");
    }

    public static void openDialogForNamespaceAndPath(ServerPlayer player, String namespace, String path) {
        Optional<Identifier> parentDir = IdentifierIndex.getParentDir(Identifier.fromNamespaceAndPath(namespace, path));

        ArrayList<ActionButton> buttons = new ArrayList<>();
        for (Identifier identifier : ItemCustomization.JUKEBOX_SONG_INDEX.getIdentifiersOfNamespaceAndPath(namespace, path)) {
            boolean isDirectory = IdentifierIndex.isDirectory(identifier);
            String p = identifier.getPath();
            MutableComponent text = Component.literal(isDirectory ? p : p.substring(p.lastIndexOf("/") + 1));
            if (!isDirectory)
                text = text.append(Component.literal(".json").withStyle(ChatFormatting.GRAY));
            buttons.add(new ActionButton(
                    new CommonButtonData(
                            text,
                            125
                    ),
                    Optional.of(new StaticAction(
                            isDirectory ?
                                    new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "jukebox_song/browse"), Optional.of(StringTag.valueOf(identifier.toString()))) :
                                    new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "jukebox_song/set"), Optional.of(StringTag.valueOf(identifier.toString())))
                    ))
            ));
        }

        MultiActionDialog dialog = new MultiActionDialog(
                new CommonDialogData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization.jukebox_song.title", "Jukebox Song"),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new ItemBody(SEARCH_ICON, Optional.of(new PlainMessage(Component.translatableWithFallback("gui.igalaxy_item_customization.jukebox_song.select_song", "Select a song"), 200)), false, false, 16, 16),
                                new PlainMessage(Component.literal("/data/").withStyle(ChatFormatting.GRAY).append(Component.literal(namespace).withStyle(ChatFormatting.WHITE).append(Component.literal("/jukebox_song/").withStyle(ChatFormatting.GRAY)).append(Component.literal(path).withStyle(ChatFormatting.WHITE))), 200)
                        ),
                        List.of()
                ),
                buttons,
                Optional.of(
                        new ActionButton(
                                new CommonButtonData(Component.translatable("gui.back"), 200),
                                Optional.of(new StaticAction(
                                        parentDir.isEmpty() ?
                                                new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "jukebox_song"), Optional.empty()) :
                                                new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "jukebox_song/browse"), Optional.of(StringTag.valueOf(parentDir.get().toString())))
                                ))
                        )
                ),
                3
        );

        player.openDialog(Holder.direct(dialog));
    }
}
