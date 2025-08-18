package mom.wii.itemcustomization.template.settings;

import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemModelSettings {
    private static final ItemStack SEARCH_ICON;

    static {
        ItemStack searchIcon = new ItemStack(Items.EGG);
        searchIcon.applyComponentsFrom(ComponentMap.builder().add(DataComponentTypes.ITEM_MODEL, Identifier.of("igalaxy_item_customization:search_icon")).build());
        SEARCH_ICON = searchIcon;
    }

    private SmithingTemplate template;

    public ItemModelSettings(SmithingTemplate template) {
        this.template = template;
    }

    public void openRootDialog(ServerPlayerEntity player) {
        ArrayList<DialogActionButtonData> buttons = new ArrayList<>();
        ItemCustomization.ITEM_MODEL_INDEX.namespaces.forEach(namespace -> {
            buttons.add(new DialogActionButtonData(
                    new DialogButtonData(
                            Text.of(namespace),
                            125
                    ),
                    Optional.of(new SimpleDialogAction(
                            new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "item_model/namespace"), Optional.of(NbtString.of(namespace)))
                    ))
            ));
        });

        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.item_model.title", "Item Model"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new ItemDialogBody(SEARCH_ICON, Optional.of(new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.item_model.select_namespace", "Select a namespace to browse"), 200)), false, false, 16, 16),
                                new PlainMessageDialogBody(Text.literal("/assets/").formatted(Formatting.GRAY), 200)
                        ),
                        List.of()
                ),
                buttons,
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "root"), Optional.empty())
                                ))
                        )
                ),
                3
        );

        player.openDialog(RegistryEntry.of(dialog));
    }

    public void openDialogForNamespace(ServerPlayerEntity player, String namespace) {
        ArrayList<DialogActionButtonData> buttons = new ArrayList<>();
        ItemCustomization.ITEM_MODEL_INDEX.getIdentifiersOfNamespace(namespace).forEach(identifier -> {
            buttons.add(new DialogActionButtonData(
                    new DialogButtonData(
                            Text.literal(identifier.getPath()).append(Text.literal(".json").formatted(Formatting.GRAY)),
                            125
                    ),
                    Optional.of(new SimpleDialogAction(
                            new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "item_model/set"), Optional.of(NbtString.of(identifier.toString())))
                    ))
            ));
        });

        MultiActionDialog dialog = new MultiActionDialog(
                new DialogCommonData(
                        Text.translatableWithFallback("gui.igalaxy_item_customization.item_model.title", "Item Model"),
                        Optional.empty(),
                        true,
                        true,
                        AfterAction.WAIT_FOR_RESPONSE,
                        List.of(
                                new ItemDialogBody(SEARCH_ICON, Optional.of(new PlainMessageDialogBody(Text.translatableWithFallback("gui.igalaxy_item_customization.item_model.select_model", "Select an item model"), 200)), false, false, 16, 16),
                                new PlainMessageDialogBody(Text.literal("/assets/").formatted(Formatting.GRAY).append(Text.literal(namespace).formatted(Formatting.WHITE).append(Text.literal("/items/").formatted(Formatting.GRAY))), 200)
                        ),
                        List.of()
                ),
                buttons,
                Optional.of(
                        new DialogActionButtonData(
                                new DialogButtonData(Text.translatable("gui.back"), 200),
                                Optional.of(new SimpleDialogAction(
                                        new ClickEvent.Custom(Identifier.of(ItemCustomization.MOD_ID, "item_model"), Optional.empty())
                                ))
                        )
                ),
                3
        );

        player.openDialog(RegistryEntry.of(dialog));
    }
}
