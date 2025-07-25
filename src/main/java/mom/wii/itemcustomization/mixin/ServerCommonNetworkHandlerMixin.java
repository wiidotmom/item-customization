package mom.wii.itemcustomization.mixin;

import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin {
    @Shadow @Final
    protected MinecraftServer server;

    @Inject(method = "onCustomClickAction", at = @At("HEAD"))
    private void itemcustomization$onCustomClickAction(CustomClickActionC2SPacket packet, CallbackInfo ci) {
        if (packet.id().getNamespace().equals("igalaxy_item_customization"))
            this.server.sendMessage(Text.of("Received custom click action " + packet.id() + " with payload " + packet.payload().get()));
    }
}
