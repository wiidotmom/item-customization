package mom.wii.itemcustomization.item;

import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import java.util.List;

public class Items {
    public static final ItemStack ITEM_CUSTOMIZATION_SMITHING_TEMPLATE;
    public static final CreativeModeTab ITEM_GROUP;

    static {
        ItemStack commandBlock = new ItemStack(net.minecraft.world.item.Items.COMMAND_BLOCK);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(ItemCustomization.MOD_ID + ":is_customization_template", true);
        DataComponentMap componentMap = DataComponentMap.builder()
                .set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                .set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "item_customization_smithing_template"))
                .set(DataComponents.ITEM_NAME, Component.nullToEmpty("Item Customization"))
                .set(DataComponents.RARITY, Rarity.UNCOMMON)
                .set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("item.minecraft.smithing_template").withStyle(ChatFormatting.GRAY))))
                .build();
        commandBlock.applyComponents(componentMap);
        ITEM_CUSTOMIZATION_SMITHING_TEMPLATE = commandBlock;
        ITEM_GROUP = PolymerItemGroupUtils.builder()
                .title(Component.nullToEmpty("Item Customization"))
                .icon(() -> ITEM_CUSTOMIZATION_SMITHING_TEMPLATE)
                .displayItems((displayContext, entries) -> entries.accept(ITEM_CUSTOMIZATION_SMITHING_TEMPLATE)).build();
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.addAfter(net.minecraft.world.item.Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE.getDefaultInstance(), Items.ITEM_CUSTOMIZATION_SMITHING_TEMPLATE);
        });

        PolymerItemGroupUtils.registerPolymerItemGroup(CreativeModeTabs.INGREDIENTS, BuiltInRegistries.CREATIVE_MODE_TAB.getValue(CreativeModeTabs.INGREDIENTS));
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "item_group"), ITEM_GROUP);

        PolymerItemUtils.CONTEXT_ITEM_CHECK.register(SmithingTemplate::isItemCustomizationSmithingTemplate);
        PolymerItemUtils.ITEM_MODIFICATION_EVENT.register(
                (original, client, context) -> {
                    if (SmithingTemplate.isItemCustomizationSmithingTemplate(original)) {
                        SmithingTemplate template = SmithingTemplate.from(original);
                        client.set(DataComponents.LORE, new ItemLore(template.getTooltip()));
                        client.set(DataComponents.MAX_DAMAGE, 1);
                        client.set(DataComponents.DAMAGE, 0);
                        client.set(DataComponents.CUSTOM_DATA, client.get(DataComponents.CUSTOM_DATA).update(nbtCompound -> {
                            nbtCompound.put("heywiki:identifier", StringTag.valueOf("igalaxy_item_customization:item_customization_smithing_template"));
                            nbtCompound.put("heywiki:fallback_title", StringTag.valueOf("Item Customization Smithing Template"));
                        }));
                    }
                    return client;
                }
        );
    }
}
