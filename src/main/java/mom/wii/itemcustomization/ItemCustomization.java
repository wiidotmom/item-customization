package mom.wii.itemcustomization;

import de.maxhenkel.admiral.MinecraftAdmiral;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import mom.wii.itemcustomization.command.ItemCustomizationCommand;
import mom.wii.itemcustomization.config.Config;
import mom.wii.itemcustomization.dialog.DialogManager;
import mom.wii.itemcustomization.dialog.Dialogs;
import mom.wii.itemcustomization.item.Items;
import mom.wii.itemcustomization.template.SmithingTemplate;
import mom.wii.itemcustomization.util.IdentifierIndex;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
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
	public static final IdentifierIndex ITEMS_MODEL_INDEX = new IdentifierIndex(Identifier.fromNamespaceAndPath(MOD_ID, "items_model"));
	public static final IdentifierIndex EQUIPMENT_MODEL_INDEX = new IdentifierIndex(Identifier.fromNamespaceAndPath(MOD_ID, "equipment_model"));
	public static final IdentifierIndex CAMERA_OVERLAY_INDEX = new IdentifierIndex(Identifier.fromNamespaceAndPath(MOD_ID, "camera_overlay"));
	public static final IdentifierIndex TOOLTIP_STYLE_INDEX = new IdentifierIndex(Identifier.fromNamespaceAndPath(MOD_ID, "tooltip_style"));
	public static final IdentifierIndex JUKEBOX_SONG_INDEX = new IdentifierIndex(Identifier.fromNamespaceAndPath(MOD_ID, "jukebox_song"));
	public static final IdentifierIndex INSTRUMENT_INDEX = new IdentifierIndex(Identifier.fromNamespaceAndPath(MOD_ID, "instrument"));
	public static DialogManager DIALOG_MANAGER = new DialogManager();

	@Override
	public void onInitialize() {
		PolymerResourcePackUtils.addModAssets(MOD_ID);

		Items.register();
		Dialogs.register();

		PolymerResourcePackUtils.RESOURCE_PACK_FINISHED_EVENT.register(ItemCustomization::refreshRuntimeChangeableIndexes);

		UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
			ItemStack itemStack = playerEntity.getItemInHand(hand);
			if (isItemCustomizationSmithingTemplate(itemStack)) {
				if (hand.equals(InteractionHand.MAIN_HAND)) {
					SmithingTemplate.from(itemStack).openDialog((ServerPlayer) playerEntity);
					return InteractionResult.FAIL;
				} else {
					playerEntity.displayClientMessage(Component.translatableWithFallback("igalaxy_item_customization.main_hand_error", "Item Customization Smithing Template can only be used with your Main Hand").withStyle(ChatFormatting.RED), true);
				}
			}
			return InteractionResult.PASS;
		}));

		UseItemCallback.EVENT.register(((playerEntity, world, hand) -> {
			ItemStack itemStack = playerEntity.getItemInHand(hand);
			if (isItemCustomizationSmithingTemplate(itemStack)) {
				if (hand.equals(InteractionHand.MAIN_HAND)) {
					SmithingTemplate.from(itemStack).openDialog((ServerPlayer) playerEntity);
				} else {
					playerEntity.displayClientMessage(Component.translatableWithFallback("igalaxy_item_customization.main_hand_error", "Item Customization Smithing Template can only be used with your Main Hand").withStyle(ChatFormatting.RED), true);
				}
			}
			return InteractionResult.PASS;
		}));

		DynamicRegistrySetupCallback.EVENT.register(view -> {
			view.registerEntryAdded(Registries.JUKEBOX_SONG, (i, id, song) -> {
				if (CONFIG.excludedNamespaces.stream().noneMatch(namespace -> namespace.equals(id.getNamespace()))) {
					if (JUKEBOX_SONG_INDEX.add(id))
						LOGGER.info("Found new {} : {}", JUKEBOX_SONG_INDEX.id, id);
				}
			});
			view.registerEntryAdded(Registries.INSTRUMENT, (i, id, instrument) -> {
				if (CONFIG.excludedNamespaces.stream().noneMatch(namespace -> namespace.equals(id.getNamespace()))) {
					if (INSTRUMENT_INDEX.add(id))
						LOGGER.info("Found new {} : {}", INSTRUMENT_INDEX.id, id);
				}
			});
		});

//		CommandRegistrationCallback.EVENT.register(
//				(commandDispatcher, commandBuildContext, commandSelection) ->
//						MinecraftAdmiral.builder(commandDispatcher, commandBuildContext).addCommandClasses(
//								ItemCustomizationCommand.class
//						).build()
//		);
	}

	public static void incrementItemsCustomized(ServerPlayer player, int amount) {
		MinecraftServer server = player.level().getServer();
		ScoreHolder scoreHolder = server.getScoreboard().getTrackedPlayers().stream().filter(x -> x.getScoreboardName().equals(player.getScoreboardName())).findFirst().get();
		Objective objective = server.getScoreboard().getObjectives().stream().filter(x -> x.getName().equals("igy_item_customization_items_customized")).findFirst().get();
		server.getScoreboard().getOrCreatePlayerScore(scoreHolder, objective).add(amount);

		AdvancementHolder entry = server.getAdvancements().get(Identifier.parse("igalaxy_item_customization:adventure/apply_item_customization_smithing_template"));
		if (!player.getAdvancements().getOrStartProgress(entry).isDone())
			player.getAdvancements().award(entry, "apply_item_customization_smithing_template");
	}

	public static void refreshRuntimeChangeableIndexes() {
		Set<Tuple<Pattern, IdentifierIndex>> PATTERN_TO_INDEX = Set.of(
				new Tuple<>(Pattern.compile("^assets/([^/]+)/items/(.+)\\.json$"), ITEMS_MODEL_INDEX),
				new Tuple<>(Pattern.compile("^assets/([^/]+)/equipment/(.+)\\.json$"), EQUIPMENT_MODEL_INDEX),
				new Tuple<>(Pattern.compile("^assets/([^/]+)/textures/misc/(.+)\\.png$"), CAMERA_OVERLAY_INDEX),
				new Tuple<>(Pattern.compile("^assets/([^/]+)/textures/gui/sprites/tooltip/(.+)_frame\\.png$"), TOOLTIP_STYLE_INDEX)
		);
		for (Tuple<Pattern, IdentifierIndex> pair : PATTERN_TO_INDEX) {
			LOGGER.info("Clearing {} index", pair.getB().id);
			pair.getB().clear();
		}

		try {
			ZipFile zipFile = new ZipFile(RESOURCE_PACK_PATH.toFile());

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				PATTERN_TO_INDEX.forEach(pair -> {
					Pattern pattern = pair.getA();
					IdentifierIndex index = pair.getB();
					if (entry.getName().matches(pattern.pattern())) {
						Matcher matcher = pattern.matcher(entry.getName());
						while (matcher.find()) {
							if (CONFIG.excludedNamespaces.stream().noneMatch(namespace -> namespace.equals(matcher.group(1)))) {
								String namespace = matcher.group(1);
								String path = matcher.group(2);
								if (index.add(Identifier.fromNamespaceAndPath(namespace, path)))
									LOGGER.info("Found new {} : {}:{}", index.id, namespace, path);
							}
						}
					}
				});
			}

			zipFile.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}