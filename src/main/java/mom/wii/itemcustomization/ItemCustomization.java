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
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
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
	public static final IdentifierIndex CAMERA_OVERLAY_INDEX = new IdentifierIndex();
	public static final IdentifierIndex TOOLTIP_STYLE_INDEX = new IdentifierIndex();
	public static final IdentifierIndex JUKEBOX_SONG_INDEX = new IdentifierIndex();
	public static DialogManager DIALOG_MANAGER = new DialogManager();

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MOD_ID);

		Items.register();
		Dialogs.register();

		PolymerResourcePackUtils.RESOURCE_PACK_FINISHED_EVENT.register(() -> {
			Set<Pair<Pattern, IdentifierIndex>> PATTERN_TO_INDEX = Set.of(
					new Pair<>(Pattern.compile("^assets/([^/]+)/items/(.+)\\.json$"), ITEM_MODEL_INDEX),
					new Pair<>(Pattern.compile("^assets/([^/]+)/equipment/(.+)\\.json$"), EQUIPMENT_MODEL_INDEX),
					new Pair<>(Pattern.compile("^assets/([^/]+)/textures/misc/(.+)\\.png$"), CAMERA_OVERLAY_INDEX),
					new Pair<>(Pattern.compile("^assets/([^/]+)/textures/gui/sprites/tooltip/(.+)_frame\\.png$"), TOOLTIP_STYLE_INDEX)
			);
            try {
                ZipFile zipFile = new ZipFile(RESOURCE_PACK_PATH.toFile());

				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					PATTERN_TO_INDEX.forEach(pair -> {
						Pattern pattern = pair.getLeft();
						IdentifierIndex index = pair.getRight();
						if (entry.getName().matches(pattern.pattern())) {
							Matcher matcher = pattern.matcher(entry.getName());
							while (matcher.find()) {
								if (CONFIG.excludedNamespaces.stream().noneMatch(namespace -> namespace.equals(matcher.group(1)))) {
									index.add(Identifier.of(matcher.group(1), matcher.group(2)));
								}
							}
						}
					});
				}

				zipFile.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

		UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (isItemCustomizationSmithingTemplate(itemStack)) {
				if (hand.equals(Hand.MAIN_HAND)) {
					SmithingTemplate.from(itemStack).openDialog((ServerPlayerEntity) playerEntity);
					return ActionResult.FAIL;
				} else {
					playerEntity.sendMessage(Text.translatableWithFallback("igalaxy_item_customization.main_hand_error", "Item Customization Smithing Template can only be used with your Main Hand").formatted(Formatting.RED), true);
				}
			}
			return ActionResult.PASS;
		}));

		UseItemCallback.EVENT.register(((playerEntity, world, hand) -> {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (isItemCustomizationSmithingTemplate(itemStack)) {
				if (hand.equals(Hand.MAIN_HAND)) {
					SmithingTemplate.from(itemStack).openDialog((ServerPlayerEntity) playerEntity);
				} else {
					playerEntity.sendMessage(Text.translatableWithFallback("igalaxy_item_customization.main_hand_error", "Item Customization Smithing Template can only be used with your Main Hand").formatted(Formatting.RED), true);
				}
			}
			return ActionResult.PASS;
		}));

		DynamicRegistrySetupCallback.EVENT.register(view -> {
			view.registerEntryAdded(RegistryKeys.JUKEBOX_SONG, (i, id, song) -> {
				if (CONFIG.excludedNamespaces.stream().noneMatch(namespace -> namespace.equals(id.getNamespace()))) {
					JUKEBOX_SONG_INDEX.add(id);
				}
			});
		});
	}
}