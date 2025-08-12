package mom.wii.itemcustomization.mixin;

import com.mojang.authlib.GameProfile;
import mom.wii.itemcustomization.ItemCustomization;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.NoticeDialog;
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

import java.util.List;
import java.util.Optional;

@Mixin(ServerCommonNetworkHandler.class)
public abstract class ServerCommonNetworkHandlerMixin {
    @Shadow @Final
    protected MinecraftServer server;

    @Shadow protected abstract GameProfile getProfile();

    @Shadow @Final protected ClientConnection connection;

    @Inject(method = "onCustomClickAction", at = @At("HEAD"))
    private void itemcustomization$onCustomClickAction(CustomClickActionC2SPacket packet, CallbackInfo ci) {
        if (ItemCustomization.DIALOG_MANAGER.customClickEvents.containsKey(packet.id())) {
            ServerPlayerEntity player;
            if ((player = server.getPlayerManager().getPlayer(this.getProfile().getId())) != null)
                ItemCustomization.DIALOG_MANAGER.customClickEvents.get(packet.id()).accept(packet, player);
        } else if (packet.id().getNamespace().equals(ItemCustomization.MOD_ID)) {
            this.connection.send(new ShowDialogS2CPacket(
                    RegistryEntry.of(new NoticeDialog(
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
                    ))
            ));
        }
    }
}
