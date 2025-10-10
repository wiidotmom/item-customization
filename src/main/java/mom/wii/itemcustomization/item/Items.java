package mom.wii.itemcustomization.item;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;

public class Items {
    public static final ItemStack ITEM_CUSTOMIZATION_SMITHING_TEMPLATE;
    public static final ItemGroup ITEM_GROUP;

    static {
        ItemStack commandBlock = new ItemStack(net.minecraft.item.Items.COMMAND_BLOCK);
        NbtCompound tag = new NbtCompound();
        tag.putBoolean(ItemCustomization.MOD_ID + ":is_customization_template", true);
        ComponentMap componentMap = ComponentMap.builder()
                .add(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag))
                .add(DataComponentTypes.ITEM_MODEL, Identifier.of(ItemCustomization.MOD_ID, "item_customization_smithing_template"))
                .add(DataComponentTypes.ITEM_NAME, Text.of("Item Customization"))
                .add(DataComponentTypes.RARITY, Rarity.UNCOMMON)
                .add(DataComponentTypes.LORE, new LoreComponent(List.of(Text.translatable("item.minecraft.smithing_template").formatted(Formatting.GRAY))))
                .build();
        commandBlock.applyComponentsFrom(componentMap);
        ITEM_CUSTOMIZATION_SMITHING_TEMPLATE = commandBlock;
        ITEM_GROUP = PolymerItemGroupUtils.builder()
                .displayName(Text.of("Item Customization"))
                .icon(() -> ITEM_CUSTOMIZATION_SMITHING_TEMPLATE)
                .entries((displayContext, entries) -> entries.add(ITEM_CUSTOMIZATION_SMITHING_TEMPLATE)).build();
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(net.minecraft.item.Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE.getDefaultStack(), Items.ITEM_CUSTOMIZATION_SMITHING_TEMPLATE);
        });

        PolymerItemGroupUtils.registerPolymerItemGroup(ItemGroups.INGREDIENTS, Registries.ITEM_GROUP.get(ItemGroups.INGREDIENTS));
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(ItemCustomization.MOD_ID, "item_group"), ITEM_GROUP);

        PolymerItemUtils.CONTEXT_ITEM_CHECK.register(SmithingTemplate::isItemCustomizationSmithingTemplate);
        PolymerItemUtils.ITEM_MODIFICATION_EVENT.register(
                (original, client, context) -> {
                    if (SmithingTemplate.isItemCustomizationSmithingTemplate(original)) {
                        SmithingTemplate template = SmithingTemplate.from(original);
                        client.set(DataComponentTypes.LORE, new LoreComponent(template.getTooltip()));
                        client.set(DataComponentTypes.MAX_DAMAGE, 1);
                        client.set(DataComponentTypes.DAMAGE, 0);
                    }
                    return client;
                }
        );
    }
}
