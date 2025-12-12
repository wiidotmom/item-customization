package mom.wii.itemcustomization.command;

import com.mojang.brigadier.context.CommandContext;
import de.maxhenkel.admiral.annotations.Command;
import de.maxhenkel.admiral.annotations.Name;
import de.maxhenkel.admiral.annotations.RequiresPermission;
import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import mom.wii.itemcustomization.util.IdentifierIndex;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@Command("item-customization")
@Command("ic")
public class ItemCustomizationCommand {
    private void setFromIdentifierIndex(CommandContext<CommandSourceStack> context, Identifier identifier, IdentifierIndex identifierIndex, String settingKey, boolean validateEntry) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player;
        if (source.isPlayer() && (player = source.getPlayer()) != null) {
            if (validateEntry && !identifierIndex.contains(identifier)) {
                source.sendFailure(Component.literal("Not a valid identifier"));
                return;
            }
            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty()) {
                SmithingTemplate template = SmithingTemplate.virtual();
                template.setSetting(settingKey, StringTag.valueOf(identifier.toString()));
                template.applySettings(stack, player.level());
            } else source.sendFailure(Component.literal("Not holding a customizable item"));
        } else source.sendFailure(Component.literal("Not a player"));
    }

//    private <T> void customModelDataAdd(CommandContext<CommandSourceStack> context, String customModelDataKey, T value, Function<T, Tag> tagFunction) {
//        CommandSourceStack source = context.getSource();
//        ServerPlayer player;
//        if (source.isPlayer() && (player = source.getPlayer()) != null) {
//            ItemStack stack = player.getMainHandItem();
//            if (!stack.isEmpty()) {
//                SmithingTemplate template = SmithingTemplate.virtual();
//                CustomModelData existingCMD = stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(), List.of()));
//                List<T> existingList =
//                        customModelDataKey.equals("floats") ? (List<T>) existingCMD.floats() :
//                                customModelDataKey.equals("flags") ? (List<T>) existingCMD.flags() :
//                                        customModelDataKey.equals("strings") ? (List<T>) existingCMD.strings() :
//                                                (List<T>) existingCMD.colors();
//                CompoundTag tag = new CompoundTag();
//                ListTag list = new ListTag();
//                existingList.stream().map(tagFunction).forEach(list::add);
//                list.add(tagFunction.apply(value));
//                tag.put(customModelDataKey, list);
//                template.setSetting("custom_model_data", tag);
//                template.applySettings(stack, player.level());
//            }
//        } else source.sendFailure(Component.literal("Not a player in creative mode or not holding a customizable item"));
//    }

    @Command({"customize", "item-model"})
    @RequiresPermission("igalaxy_item_customization.customize.item_model")
    public void customizeItemModel(CommandContext<CommandSourceStack> context, @Name("item-model") Identifier identifier) {
        setFromIdentifierIndex(context, identifier, ItemCustomization.ITEMS_MODEL_INDEX, "item_model", false);
    }

    @Command({"customize", "custom-model-data", "reset"})
    @RequiresPermission("igalaxy_item_customization.customize.custom_model_data.reset")
    public void resetCustomModelData(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player;
        if (source.isPlayer() && (player = source.getPlayer()) != null) {
            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty()) {
                SmithingTemplate template = SmithingTemplate.virtual();
                template.setSetting("custom_model_data", new CompoundTag());
                template.applySettings(stack, player.level());
            } else source.sendFailure(Component.literal("Not holding a customizable item"));
        } else source.sendFailure(Component.literal("Not a player"));
    }

//    @Command({"customize", "custom-model-data", "add", "float"})
//    public void customModelDataAddFloat(CommandContext<CommandSourceStack> context, @Name("float") float value) {
//        customModelDataAdd(context, "floats", value, FloatTag::valueOf);
//    }
//
//    @Command({"customize", "custom-model-data", "add", "flag"})
//    public void customModelDataAddFlag(CommandContext<CommandSourceStack> context, @Name("flag") boolean value) {
//        customModelDataAdd(context, "flags", value, ByteTag::valueOf);
//    }
//
//    @Command({"customize", "custom-model-data", "add", "string"})
//    public void customModelDataAddFlag(CommandContext<CommandSourceStack> context, @Name("string") String value) {
//        customModelDataAdd(context, "strings", value, StringTag::valueOf);
//    }
//
//    @Command({"customize", "custom-model-data", "add", "color"})
//    public void customModelDataAddFlag(CommandContext<CommandSourceStack> context, @Name("color") HexColor value) {
//        customModelDataAdd(context, "colors", value.get(), IntTag::valueOf);
//    }

    @Command({"customize", "equipment-model"})
    @RequiresPermission("igalaxy_item_customization.customize.equipment_model")
    public void customizeEquipmentModel(CommandContext<CommandSourceStack> context, @Name("equipment-model") Identifier identifier) {
        setFromIdentifierIndex(context, identifier, ItemCustomization.EQUIPMENT_MODEL_INDEX, "equipment_model", false);
    }

    @Command({"customize", "camera-overlay"})
    @RequiresPermission("igalaxy_item_customization.customize.camera_overlay")
    public void customizeCameraOverlay(CommandContext<CommandSourceStack> context, @Name("camera-overlay") Identifier identifier) {
        setFromIdentifierIndex(context, identifier, ItemCustomization.CAMERA_OVERLAY_INDEX, "camera_overlay", false);
    }

    @Command({"customize", "tooltip-style"})
    @RequiresPermission("igalaxy_item_customization.customize.tooltip_style")
    public void customizeTooltipStyle(CommandContext<CommandSourceStack> context, @Name("tooltip-style") Identifier identifier) {
        setFromIdentifierIndex(context, identifier, ItemCustomization.TOOLTIP_STYLE_INDEX, "tooltip_style", false);
    }

    @Command({"customize", "hidden-components", "reset"})
    @RequiresPermission("igalaxy_item_customization.customize.hidden_components.reset")
    public void resetHiddenComponents(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player;
        if (source.isPlayer() && (player = source.getPlayer()) != null) {
            ItemStack stack = player.getMainHandItem();
            if (!stack.isEmpty()) {
                SmithingTemplate template = SmithingTemplate.virtual();
                template.setSetting("hidden_components", new ListTag());
                template.applySettings(stack, player.level());
            } else source.sendFailure(Component.literal("Not holding a customizable item"));
        } else source.sendFailure(Component.literal("Not a player"));
    }

    @Command({"customize", "instrument"})
    @RequiresPermission("igalaxy_item_customization.customize.instrument")
    public void customizeInstrument(CommandContext<CommandSourceStack> context, @Name("instrument") Identifier identifier) {
        setFromIdentifierIndex(context, identifier, ItemCustomization.INSTRUMENT_INDEX, "instrument", true);
    }

    @Command({"customize", "jukebox-song"})
    @RequiresPermission("igalaxy_item_customization.customize.jukebox_song")
    public void customizeJukeboxSong(CommandContext<CommandSourceStack> context, @Name("jukebox-song") Identifier identifier) {
        setFromIdentifierIndex(context, identifier, ItemCustomization.JUKEBOX_SONG_INDEX, "jukebox_song", true);
    }

    private void viewIdentifierIndex(CommandContext<CommandSourceStack> context, String title, IdentifierIndex index) {
        List<MutableComponent> namespaces = index.namespaces.stream().map(namespace ->
                Component.literal(namespace)
                        .withStyle(style -> style.withHoverEvent(
                                new HoverEvent.ShowText(Component.literal(index.getIdentifiersOfNamespace(namespace).stream().map(Identifier::getPath).collect(Collectors.joining(", "))))
                        ))
        ).toList();
        MutableComponent component = Component.literal("The " + title + " index has " + index.namespaces.size() + " namespaces with " + index.identifiers.size() + " total entries: ");
        for (int i = 0; i < namespaces.size(); i++) {
            component.append(namespaces.get(i));
            if (i != namespaces.size() - 1) component.append(", ");
        }
        context.getSource().sendSuccess(() -> component, true);
    }

    @Command({"index", "view", "item-model"})
    @RequiresPermission("igalaxy_item_customization.index.view.item_model")
    public void viewItemModelIndex(CommandContext<CommandSourceStack> context) {
        viewIdentifierIndex(context, "Items Model", ItemCustomization.ITEMS_MODEL_INDEX);
    }

    @Command({"index", "view", "equipment-model"})
    @RequiresPermission("igalaxy_item_customization.index.view.equipment_model")
    public void viewEquipmentModelIndex(CommandContext<CommandSourceStack> context) {
        viewIdentifierIndex(context, "Equipment Model", ItemCustomization.EQUIPMENT_MODEL_INDEX);
    }

    @Command({"index", "view", "tooltip-style"})
    @RequiresPermission("igalaxy_item_customization.index.view.tooltip_style")
    public void viewTooltipStyleIndex(CommandContext<CommandSourceStack> context) {
        viewIdentifierIndex(context, "Tooltip Style", ItemCustomization.TOOLTIP_STYLE_INDEX);
    }

    @Command({"index", "view", "jukebox-song"})
    @RequiresPermission("igalaxy_item_customization.index.view.jukebox_song")
    public void viewJukeboxSongIndex(CommandContext<CommandSourceStack> context) {
        viewIdentifierIndex(context, "Jukebox Song", ItemCustomization.JUKEBOX_SONG_INDEX);
    }

    @Command({"index", "view", "instrument"})
    @RequiresPermission("igalaxy_item_customization.index.view.instrument")
    public void viewInstrumentIndex(CommandContext<CommandSourceStack> context) {
        viewIdentifierIndex(context, "Instrument", ItemCustomization.INSTRUMENT_INDEX);
    }

    @Command({"index", "view", "camera-overlay"})
    @RequiresPermission("igalaxy_item_customization.index.view.camera_overlay")
    public void viewCameraOverlayIndex(CommandContext<CommandSourceStack> context) {
        viewIdentifierIndex(context, "Camera Overlay", ItemCustomization.CAMERA_OVERLAY_INDEX);
    }
}
