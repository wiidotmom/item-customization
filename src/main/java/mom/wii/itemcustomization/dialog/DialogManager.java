package mom.wii.itemcustomization.dialog;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.core.Holder;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.BiConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DialogManager {
    public HashMap<Identifier, BiConsumer<ServerboundCustomClickActionPacket, ServerPlayer>> customClickEvents = new HashMap<>();

    public BiConsumer<ServerboundCustomClickActionPacket, ServerPlayer> register(Identifier identifier, BiConsumer<ServerboundCustomClickActionPacket, ServerPlayer> handler) {
        return this.customClickEvents.put(identifier, handler);
    }

    public static NoticeDialog simpleNoticeDialog(Component message) {
        return new NoticeDialog(
                new CommonDialogData(
                        Component.empty(),
                        Optional.empty(),
                        true,
                        true,
                        DialogAction.CLOSE,
                        List.of(
                                new PlainMessage(message, 200)
                        ),
                        List.of()
                ),
                new ActionButton(
                        new CommonButtonData(
                                Component.translatable("gui.ok"),
                                Optional.empty(),
                                200
                        ),
                        Optional.empty()
                )
        );
    }

    public static ActionButton simpleTranslatableMenuButton(String translation, String fallback, String action) {
        return simpleTranslatableMenuButton(translation, fallback, action, 125);
    }

    public static ActionButton simpleTranslatableMenuButton(String translation, String fallback, String action, int width) {
        return new ActionButton(
                new CommonButtonData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization." + translation, fallback),
                        width
                ),
                Optional.of(new StaticAction(
                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, action), Optional.empty())
                ))
        );
    }

    public static ActionButton translatableMenuButtonWithTooltip(String labelTranslation, String labelFallback, String tooltipTranslation, String tooltipFallback, String action) {
        return new ActionButton(
                new CommonButtonData(
                        Component.translatableWithFallback("gui.igalaxy_item_customization." + labelTranslation, labelFallback),
                        Optional.of(Component.translatableWithFallback("gui.igalaxy_item_customization." + tooltipTranslation, tooltipFallback)),
                        125
                ),
                Optional.of(new StaticAction(
                        new ClickEvent.Custom(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, action), Optional.empty())
                ))
        );
    }

    public static class SimpleDialogCustomClickEventHandler implements BiConsumer<ServerboundCustomClickActionPacket, ServerPlayer> {
        private Identifier id;

        public SimpleDialogCustomClickEventHandler(Identifier id) {
            this.id = id;
        }

        public SimpleDialogCustomClickEventHandler register() {
            ItemCustomization.DIALOG_MANAGER.customClickEvents.put(id, this);
            return this;
        }

        public StaticAction getAction(Optional<Tag> optionalNbtElement) {
            return new StaticAction(
                    new ClickEvent.Custom(this.id, optionalNbtElement)
            );
        }

        public Dialog getDialog(ServerboundCustomClickActionPacket customClickActionC2SPacket, ServerPlayer serverPlayerEntity) {
            return new NoticeDialog(
                    new CommonDialogData(
                            Component.empty(),
                            Optional.empty(),
                            true,
                            false,
                            DialogAction.CLOSE,
                            List.of(
                                    new PlainMessage(Component.nullToEmpty("Dialog not found"), 200)
                            ),
                            List.of()
                    ),
                    new ActionButton(
                            new CommonButtonData(
                                Component.translatable("gui.ok"),
                                    Optional.empty(),
                                    150
                            ),
                            Optional.empty()
                    )
            );
        }

        @Override
        public void accept(ServerboundCustomClickActionPacket packet, ServerPlayer player) {
            player.connection.send(new ClientboundShowDialogPacket(Holder.direct(getDialog(packet, player))));
        }
    }
}
