package mom.wii.itemcustomization.dialog;

import com.mojang.serialization.DataResult;
import mom.wii.itemcustomization.template.SmithingTemplate;
import mom.wii.itemcustomization.template.settings.equipment.CameraOverlaySettings;
import mom.wii.itemcustomization.template.settings.equipment.EquipmentModelSettings;
import mom.wii.itemcustomization.template.settings.equipment.EquipmentSettings;
import mom.wii.itemcustomization.template.settings.ItemModelSettings;
import mom.wii.itemcustomization.util.IdentifierIndex;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static mom.wii.itemcustomization.ItemCustomization.*;
import static mom.wii.itemcustomization.template.SmithingTemplate.isItemCustomizationSmithingTemplate;

public class Dialogs {
    public static final ItemStack SEARCH_ICON;

    static {
        ItemStack searchIcon = new ItemStack(Items.EGG);
        searchIcon.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ITEM_MODEL, Identifier.of("igalaxy_item_customization:search_icon")).build());
        SEARCH_ICON = searchIcon;
    }

    private static void registerIndexRootAction(String id, IdentifierIndex index, Consumer<ServerPlayerEntity> openRootDialog, String errorMessage) {
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, id),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack()) && !index.isEmpty()) {
                        openRootDialog.accept(player);
                        return;
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of(errorMessage))
                            )
                    );
                }
        );
    }

    private static void registerIndexNamespaceAction(String id, IdentifierIndex index, BiConsumer<ServerPlayerEntity, String> openDialogForNamespace) {
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, id + "/namespace"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof NbtString) {
                            String namespace = ((NbtString) packet.payload().get()).value();
                            if (index.namespaces.contains(namespace)) {
                                openDialogForNamespace.accept(player, namespace);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of("Invalid namespace selected"))
                            )
                    );
                }
        );
    }

    private static void registerIndexSetAction(String id, IdentifierIndex index, Function<Identifier, String> entryToValue, String errorMessage) {
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, id + "/set"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack()) &&
                            packet.payload().isPresent() &&
                            packet.payload().get() instanceof NbtString
                    ) {
                        NbtString payload = (NbtString) packet.payload().get();
                        DataResult<Identifier> validated = Identifier.validate(payload.value());
                        if (validated.isSuccess()) {
                            Identifier entry = validated.getOrThrow();
                            if (index.identifiers.stream().anyMatch(entry::equals)) {
                                SmithingTemplate template = SmithingTemplate.from(player.getMainHandStack());
                                template.setSetting(id, NbtString.of(entryToValue.apply(entry)));
                                template.openDialog(player);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of(errorMessage))
                            )
                    );
                }
        );
    }

    public static void register() {
        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "root"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        SmithingTemplate.from(player.getMainHandStack()).openDialog(player);
                    }
                }
        );

        registerIndexRootAction("item_model", ITEM_MODEL_INDEX, ItemModelSettings::openRootDialog, "No usable item models present in resource pack");
        registerIndexNamespaceAction("item_model", ITEM_MODEL_INDEX, ItemModelSettings::openDialogForNamespace);
        registerIndexSetAction("item_model", ITEM_MODEL_INDEX, Identifier::toString, "Invalid item model selected");

        DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "equipment"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        EquipmentSettings.openRootDialog(player);
                    }
                }
        );

        registerIndexRootAction("equipment_model", EQUIPMENT_MODEL_INDEX, EquipmentModelSettings::openRootDialog, "No usable equipment models present in resource pack");
        registerIndexNamespaceAction("equipment_model", EQUIPMENT_MODEL_INDEX, EquipmentModelSettings::openDialogForNamespace);
        registerIndexSetAction("equipment_model", EQUIPMENT_MODEL_INDEX, Identifier::toString, "Invalid equipment model selected");

        registerIndexRootAction("camera_overlay", CAMERA_OVERLAY_INDEX, CameraOverlaySettings::openRootDialog, "No usable misc textures present in resource pack");
        registerIndexNamespaceAction("camera_overlay", CAMERA_OVERLAY_INDEX, CameraOverlaySettings::openDialogForNamespace);
        registerIndexSetAction("camera_overlay", CAMERA_OVERLAY_INDEX, id -> id.getNamespace() + ":misc/" + id.getPath(), "Invalid camera overlay texture selected");
    }
}
