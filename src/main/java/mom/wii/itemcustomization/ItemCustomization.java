package mom.wii.itemcustomization;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import mom.wii.itemcustomization.dialog.DialogManager;
import mom.wii.itemcustomization.util.IdentifierIndex;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.DynamicCustomDialogAction;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.network.packet.s2c.common.ShowDialogS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ItemCustomization implements ModInitializer {
	public static final String MOD_ID = "igalaxy_item_customization";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Path RESOURCE_PACK_PATH = PolymerResourcePackUtils.getMainPath().toAbsolutePath().normalize();
	private static IdentifierIndex models = new IdentifierIndex();
	public static DialogManager DIALOG_MANAGER = new DialogManager();

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MOD_ID);

		PolymerResourcePackUtils.RESOURCE_PACK_FINISHED_EVENT.register(() -> {
			Pattern namespacePattern = Pattern.compile("^assets/([^/]+)/items/([^/]+)\\.json$");
            try {
                ZipFile zipFile = new ZipFile(RESOURCE_PACK_PATH.toFile());

				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().matches("^assets/([^/]+)/items/.+\\.json$")) {
						Matcher matcher = namespacePattern.matcher(entry.getName());
						while (matcher.find()) {
							if (!matcher.group(1).equals(MOD_ID))
								models.add(Identifier.of(matcher.group(1), matcher.group(2)));
						}
					}
				}

				LOGGER.info(models.set.toString());

				zipFile.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

		UseItemCallback.EVENT.register(((playerEntity, world, hand) -> {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (itemStack.hasChangedComponent(DataComponentTypes.CUSTOM_DATA) && Objects.requireNonNull(itemStack.get(DataComponentTypes.CUSTOM_DATA)).contains("igalaxy_item_customization:is_customization_template")) {
				ServerPlayerEntity serverPlayerEntity = world.getServer().getPlayerManager().getPlayer(playerEntity.getUuid());

				ArrayList<DialogActionButtonData> buttons = new ArrayList<>();
				HashSet<String> namespaces = new HashSet<>();
				models.set.forEach(identifier -> {
					namespaces.add(identifier.getNamespace());
				});
				namespaces.forEach(namespace -> {
					buttons.add(new DialogActionButtonData(
							new DialogButtonData(Text.of(namespace), 150),
							Optional.of(
									new DialogManager.SimpleDialogCustomClickEventHandler(Identifier.of(MOD_ID, "template/item_model/namespace")) {
										@Override
										public Dialog getDialog(CustomClickActionC2SPacket customClickActionC2SPacket, ServerPlayerEntity serverPlayerEntity) {
											return super.getDialog(customClickActionC2SPacket, serverPlayerEntity);
										}
									}.register().getAction(Optional.of(NbtString.of(namespace)))
							)
					));
				});

				ShowDialogS2CPacket dialogS2CPacket = new ShowDialogS2CPacket(
						RegistryEntry.of(new MultiActionDialog(
								new DialogCommonData(
										itemStack.getName(), Optional.empty(), true, false, AfterAction.CLOSE, List.of(), List.of()
								),
								buttons,
								Optional.empty(),
								2
						))
				);

				serverPlayerEntity.networkHandler.sendPacket(dialogS2CPacket);
			}
			return ActionResult.PASS;
		}));
	}
}