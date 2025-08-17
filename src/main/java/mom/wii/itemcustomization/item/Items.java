package mom.wii.itemcustomization.item;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import mom.wii.itemcustomization.ItemCustomization;
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
    public static final List<Text> ITEM_CUSTOMIZATION_SMITHING_TEMPLATE_TOOLTIP = List.of(
            Text.of("Smithing Template").copy().styled(style -> style.withColor(Formatting.GRAY).withItalic(false)),
            Text.empty(),
            Text.of("Applies to:").copy().styled(style -> style.withColor(Formatting.GRAY).withItalic(false)),
            Text.of(" Any").copy().styled(style -> style.withColor(Formatting.BLUE).withItalic(false)),
            Text.of("Ingredients:").copy().styled(style -> style.withColor(Formatting.GRAY).withItalic(false)),
            Text.of(" Resin Clump").copy().styled(style -> style.withColor(Formatting.BLUE).withItalic(false))
    );
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
                .add(DataComponentTypes.LORE, new LoreComponent(ITEM_CUSTOMIZATION_SMITHING_TEMPLATE_TOOLTIP))
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

        PolymerItemUtils.ITEM_CHECK.register(ItemCustomization::isItemCustomizationTemplate);
        PolymerItemUtils.ITEM_MODIFICATION_EVENT.register(
                (original, client, context) -> {
                    if (ItemCustomization.isItemCustomizationTemplate(original)) {
                        client.set(DataComponentTypes.LORE, new LoreComponent(Items.ITEM_CUSTOMIZATION_SMITHING_TEMPLATE_TOOLTIP));
                    }
                    return client;
                }
        );
    }
}
