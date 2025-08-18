package mom.wii.itemcustomization.dialog;

import com.mojang.serialization.DataResult;
import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import mom.wii.itemcustomization.template.settings.equipment.EquipmentModelSettings;
import mom.wii.itemcustomization.template.settings.equipment.EquipmentSettings;
import mom.wii.itemcustomization.template.settings.ItemModelSettings;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static mom.wii.itemcustomization.ItemCustomization.MOD_ID;
import static mom.wii.itemcustomization.template.SmithingTemplate.isItemCustomizationSmithingTemplate;

public class Dialogs {
    public static final ItemStack SEARCH_ICON;

    static {
        ItemStack searchIcon = new ItemStack(Items.EGG);
        searchIcon.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ITEM_MODEL, Identifier.of("igalaxy_item_customization:search_icon")).build());
        SEARCH_ICON = searchIcon;
    }

    public static void register() {
        ItemCustomization.DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "root"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        SmithingTemplate.from(player.getMainHandStack()).openDialog(player);
                    }
                }
        );
        ItemCustomization.DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "item_model"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        ItemModelSettings.openRootDialog(player);
                    }
                }
        );
        ItemCustomization.DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "item_model/namespace"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof NbtString) {
                            String namespace = ((NbtString) packet.payload().get()).value();
                            if (ItemCustomization.ITEM_MODEL_INDEX.namespaces.contains(namespace)) {
                                ItemModelSettings.openDialogForNamespace(player, namespace);
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
        ItemCustomization.DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "item_model/set"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack()) &&
                            packet.payload().isPresent() &&
                            packet.payload().get() instanceof NbtString
                    ) {
                        NbtString payload = (NbtString) packet.payload().get();
                        DataResult<Identifier> validated = Identifier.validate(payload.value());
                        if (validated.isSuccess()) {
                            Identifier itemModel = validated.getOrThrow();
                            if (ItemCustomization.ITEM_MODEL_INDEX.identifiers.stream().anyMatch(itemModel::equals)) {
                                SmithingTemplate template = SmithingTemplate.from(player.getMainHandStack());
                                template.setSetting("item_model", NbtString.of(itemModel.toString()));
                                template.openDialog(player);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of("Invalid item model selected"))
                            )
                    );
                }
        );
        ItemCustomization.DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "equipment"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        EquipmentSettings.openRootDialog(player);
                    }
                }
        );
        ItemCustomization.DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "equipment_model"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        EquipmentModelSettings.openRootDialog(player);
                    }
                }
        );
        ItemCustomization.DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "equipment_model/namespace"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack())) {
                        if (packet.payload().isPresent() && packet.payload().get() instanceof NbtString) {
                            String namespace = ((NbtString) packet.payload().get()).value();
                            if (ItemCustomization.EQUIPMENT_MODEL_INDEX.namespaces.contains(namespace)) {
                                EquipmentModelSettings.openDialogForNamespace(player, namespace);
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
        ItemCustomization.DIALOG_MANAGER.register(
                Identifier.of(MOD_ID, "equipment_model/set"),
                (packet, player) -> {
                    if (isItemCustomizationSmithingTemplate(player.getMainHandStack()) &&
                            packet.payload().isPresent() &&
                            packet.payload().get() instanceof NbtString
                    ) {
                        NbtString payload = (NbtString) packet.payload().get();
                        DataResult<Identifier> validated = Identifier.validate(payload.value());
                        if (validated.isSuccess()) {
                            Identifier equipmentModel = validated.getOrThrow();
                            if (ItemCustomization.EQUIPMENT_MODEL_INDEX.identifiers.stream().anyMatch(equipmentModel::equals)) {
                                SmithingTemplate template = SmithingTemplate.from(player.getMainHandStack());
                                template.setSetting("equipment_model", NbtString.of(equipmentModel.toString()));
                                template.openDialog(player);
                                return;
                            }
                        }
                    }
                    player.openDialog(
                            RegistryEntry.of(
                                    DialogManager.simpleNoticeDialog(Text.of("Invalid item model selected"))
                            )
                    );
                }
        );
    }
}
