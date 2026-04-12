package mom.wii.itemcustomization.mixin;

import com.mojang.authlib.GameProfile;
import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.dialog.DialogManager;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin {
    @Shadow @Final
    protected MinecraftServer server;

    @Shadow protected abstract GameProfile playerProfile();

    @Shadow @Final protected Connection connection;

    @Inject(method = "handleCustomClickAction", at = @At("TAIL"))
    private void itemcustomization$onCustomClickAction(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
        if (ItemCustomization.DIALOG_MANAGER.customClickEvents.containsKey(packet.id())) {
            try {
                ServerPlayer player;
                if ((player = server.getPlayerList().getPlayer(this.playerProfile().id())) != null)
                    ItemCustomization.DIALOG_MANAGER.customClickEvents.get(packet.id()).accept(packet, player);
            } catch (Exception e) {
                this.connection.send(new ClientboundShowDialogPacket(
                        Holder.direct(DialogManager.simpleNoticeDialog(Component.nullToEmpty("An error occurred: " + e.getMessage())))
                ));
            }
        } else if (packet.id().getNamespace().equals(ItemCustomization.MOD_ID)) {
            this.connection.send(new ClientboundShowDialogPacket(
                    Holder.direct(DialogManager.simpleNoticeDialog(Component.nullToEmpty(packet.id() + " not found")))
            ));
        }
    }
}
