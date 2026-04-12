package mom.wii.itemcustomization.item;

import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import java.util.List;

public class Items {
    public static final ItemStackTemplate ITEM_CUSTOMIZATION_SMITHING_TEMPLATE_TEMPLATE;

    static {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(ItemCustomization.MOD_ID + ":is_customization_template", true);
        ITEM_CUSTOMIZATION_SMITHING_TEMPLATE_TEMPLATE = new ItemStackTemplate(
                net.minecraft.world.item.Items.COMMAND_BLOCK.builtInRegistryHolder(),
                1,
                DataComponentPatch.builder()
                        .set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                        .set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(ItemCustomization.MOD_ID, "item_customization_smithing_template"))
                        .set(DataComponents.ITEM_NAME, Component.nullToEmpty("Item Customization"))
                        .set(DataComponents.RARITY, Rarity.UNCOMMON)
                        .set(DataComponents.LORE, new ItemLore(List.of(Component.translatable("item.minecraft.smithing_template").withStyle(ChatFormatting.GRAY))))
                        .build()
        );
    }

    public static void register() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.insertAfter(net.minecraft.world.item.Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE.getDefaultInstance(), Items.ITEM_CUSTOMIZATION_SMITHING_TEMPLATE_TEMPLATE.create());
        });


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
