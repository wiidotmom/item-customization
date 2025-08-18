package mom.wii.itemcustomization.dialog;

import com.mojang.serialization.DataResult;
import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import mom.wii.itemcustomization.template.settings.ItemModelSettings;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static mom.wii.itemcustomization.ItemCustomization.MOD_ID;
import static mom.wii.itemcustomization.template.SmithingTemplate.isItemCustomizationSmithingTemplate;

public class Dialogs {
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
                        new ItemModelSettings(SmithingTemplate.from(player.getMainHandStack())).openRootDialog(player);
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
                                new ItemModelSettings(SmithingTemplate.from(player.getMainHandStack())).openDialogForNamespace(player, namespace);
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
                            SmithingTemplate template = SmithingTemplate.from(player.getMainHandStack());
                            template.setSetting("item_model", NbtString.of(itemModel.toString()));
                            template.openDialog(player);
                            return;
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
