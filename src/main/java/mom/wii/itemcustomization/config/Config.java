package mom.wii.itemcustomization.config;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import mom.wii.itemcustomization.ItemCustomization;

import java.util.List;

public class Config extends WrappedConfig {
    @Comment("A list of namespaces to exclude from available options")
    public List<String> excludedNamespaces = ValueList.create("", "minecraft", ItemCustomization.MOD_ID);

    @Comment("The item to use as the Smithing Table ingredient for Item Customization Smithing Templates")
    public String smithingIngredient = "minecraft:resin_clump";

    @Comment("Whether or not to remove the 'minecraft:waypoint_transmit_range_hide' attribute modifier from Heads/Carved Pumpkins that get their Camera Overlay customized")
    public Boolean isCustomizedHeadVisibleOnPlayerLocatorBar = true;
}
