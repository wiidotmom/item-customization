package mom.wii.itemcustomization.mixin;

import com.mojang.authlib.GameProfile;
import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.dialog.DialogManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonNetworkHandler.class)
public abstract class ServerCommonNetworkHandlerMixin {
    @Shadow @Final
    protected MinecraftServer server;

    @Shadow protected abstract GameProfile getProfile();

    @Shadow @Final protected ClientConnection connection;

    @Inject(method = "onCustomClickAction", at = @At("TAIL"))
    private void itemcustomization$onCustomClickAction(CustomClickActionC2SPacket packet, CallbackInfo ci) {
        if (ItemCustomization.DIALOG_MANAGER.customClickEvents.containsKey(packet.id())) {
            try {
                ServerPlayerEntity player;
                if ((player = server.getPlayerManager().getPlayer(this.getProfile().id())) != null)
                    ItemCustomization.DIALOG_MANAGER.customClickEvents.get(packet.id()).accept(packet, player);
            } catch (Exception e) {
                this.connection.send(new ShowDialogS2CPacket(
                        RegistryEntry.of(DialogManager.simpleNoticeDialog(Text.of("An error occurred: " + e.getMessage())))
                ));
            }
        } else if (packet.id().getNamespace().equals(ItemCustomization.MOD_ID)) {
            this.connection.send(new ShowDialogS2CPacket(
                    RegistryEntry.of(DialogManager.simpleNoticeDialog(Text.of(packet.id() + " not found")))
            ));
        }
    }
}
