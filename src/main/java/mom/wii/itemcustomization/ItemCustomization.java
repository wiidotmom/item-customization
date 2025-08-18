package mom.wii.itemcustomization;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import mom.wii.itemcustomization.config.Config;
import mom.wii.itemcustomization.dialog.DialogManager;
import mom.wii.itemcustomization.dialog.Dialogs;
import mom.wii.itemcustomization.item.Items;
import mom.wii.itemcustomization.template.SmithingTemplate;
import mom.wii.itemcustomization.util.IdentifierIndex;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static mom.wii.itemcustomization.template.SmithingTemplate.isItemCustomizationSmithingTemplate;

public class ItemCustomization implements ModInitializer {
	public static final String MOD_ID = "igalaxy_item_customization";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Config CONFIG = Config.createToml(
			FabricLoader.getInstance().getConfigDir(), "", MOD_ID, Config.class
	);
	public static final Path RESOURCE_PACK_PATH = PolymerResourcePackUtils.getMainPath().toAbsolutePath().normalize();
	public static final IdentifierIndex ITEM_MODEL_INDEX = new IdentifierIndex();
	public static final IdentifierIndex EQUIPMENT_MODEL_INDEX = new IdentifierIndex();
	public static DialogManager DIALOG_MANAGER = new DialogManager();

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MOD_ID);

		Items.register();
		Dialogs.register();

		PolymerResourcePackUtils.RESOURCE_PACK_FINISHED_EVENT.register(() -> {
			Pattern itemNamespacePattern = Pattern.compile("^assets/([^/]+)/items/([^/]+)\\.json$");
			Pattern equipmentNamespacePattern = Pattern.compile("^assets/([^/]+)/equipment/([^/]+)\\.json$");
            try {
                ZipFile zipFile = new ZipFile(RESOURCE_PACK_PATH.toFile());

				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().matches(itemNamespacePattern.pattern())) {
						Matcher matcher = itemNamespacePattern.matcher(entry.getName());
						while (matcher.find()) {
							if (CONFIG.excludedNamespaces.stream().noneMatch(namespace -> namespace.equals(matcher.group(1))))
								ITEM_MODEL_INDEX.add(Identifier.of(matcher.group(1), matcher.group(2)));
						}
					} else if (entry.getName().matches(equipmentNamespacePattern.pattern())) {
						Matcher matcher = equipmentNamespacePattern.matcher(entry.getName());
						while (matcher.find()) {
							if (CONFIG.excludedNamespaces.stream().noneMatch(namespace -> namespace.equals(matcher.group(1))))
								EQUIPMENT_MODEL_INDEX.add(Identifier.of(matcher.group(1), matcher.group(2)));
						}
					}
				}

                LOGGER.info("ITEM_MODEL_INDEX: {}", ITEM_MODEL_INDEX.identifiers);
                LOGGER.info("EQUIPMENT_MODEL_INDEX: {}", EQUIPMENT_MODEL_INDEX.identifiers);
				zipFile.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

		UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (isItemCustomizationSmithingTemplate(itemStack)) {
				SmithingTemplate.from(itemStack).openDialog((ServerPlayerEntity) playerEntity);
				return ActionResult.FAIL;
			}
			return ActionResult.PASS;
		}));

		UseItemCallback.EVENT.register(((playerEntity, world, hand) -> {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (isItemCustomizationSmithingTemplate(itemStack)) {
				SmithingTemplate.from(itemStack).openDialog((ServerPlayerEntity) playerEntity);
			}
			return ActionResult.PASS;
		}));
	}
}