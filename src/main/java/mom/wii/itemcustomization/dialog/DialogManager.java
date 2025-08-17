package mom.wii.itemcustomization.dialog;

import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.util.BiConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DialogManager {
    public HashMap<Identifier, BiConsumer<CustomClickActionC2SPacket, ServerPlayerEntity>> customClickEvents = new HashMap<>();

    public BiConsumer<CustomClickActionC2SPacket, ServerPlayerEntity> register(Identifier identifier, BiConsumer<CustomClickActionC2SPacket, ServerPlayerEntity> handler) {
        return this.customClickEvents.put(identifier, handler);
    }

    public static NoticeDialog simpleNoticeDialog(Text message) {
        return new NoticeDialog(
                new DialogCommonData(
                        Text.empty(),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.CLOSE,
                        List.of(
                                new PlainMessageDialogBody(message, 200)
                        ),
                        List.of()
                ),
                new DialogActionButtonData(
                        new DialogButtonData(
                                Text.translatable("gui.ok"),
                                Optional.empty(),
                                150
                        ),
                        Optional.empty()
                )
        );
    }

    public static DialogActionButtonData simpleTranslatableMenuButton(String translation, String fallback, String action) {
        return new DialogActionButtonData(
                new DialogButtonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization." + translation, fallback),
                        125
                ),
                Optional.of(new SimpleDialogAction(
                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, action), Optional.empty())
                ))
        );
    }

    public static DialogActionButtonData translatableMenuButtonWithTooltip(String labelTranslation, String labelFallback, String tooltipTranslation, String tooltipFallback, String action) {
        return new DialogActionButtonData(
                new DialogButtonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization." + labelTranslation, labelFallback),
                        Optional.of(Text.translatableWithFallback("gui.igalaxy_item_customization." + tooltipTranslation, tooltipFallback)),
                        125
                ),
                Optional.of(new SimpleDialogAction(
                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, action), Optional.empty())
                ))
        );
    }

    public static class SimpleDialogCustomClickEventHandler implements BiConsumer<CustomClickActionC2SPacket, ServerPlayerEntity> {
        private Identifier id;

        public SimpleDialogCustomClickEventHandler(Identifier id) {
            this.id = id;
        }

        public SimpleDialogCustomClickEventHandler register() {
            ItemCustomization.DIALOG_MANAGER.customClickEvents.put(id, this);
            return this;
        }

        public SimpleDialogAction getAction(Optional<NbtElement> optionalNbtElement) {
            return new SimpleDialogAction(
                    new ClickEvent.Custom(this.id, optionalNbtElement)
            );
        }

        public Dialog getDialog(CustomClickActionC2SPacket customClickActionC2SPacket, ServerPlayerEntity serverPlayerEntity) {
            return new NoticeDialog(
                    new DialogCommonData(
                            Text.empty(),
                            Optional.empty(),
                            true,
                            false,
                            AfterAction.CLOSE,
                            List.of(
                                    new PlainMessageDialogBody(Text.of("Dialog not found"), 200)
                            ),
                            List.of()
                    ),
                    new DialogActionButtonData(
                            new DialogButtonData(
                                Text.translatable("gui.ok"),
                                    Optional.empty(),
                                    150
                            ),
                            Optional.empty()
                    )
            );
        }

        @Override
        public void accept(CustomClickActionC2SPacket packet, ServerPlayerEntity player) {
            player.networkHandler.sendPacket(new ShowDialogS2CPacket(RegistryEntry.of(getDialog(packet, player))));
        }
    }
}
