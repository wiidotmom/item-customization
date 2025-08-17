package mom.wii.itemcustomization.config;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import mom.wii.itemcustomization.ItemCustomization;

import java.util.List;

public class Config extends WrappedConfig {
    @Comment("A list of namespaces to exclude from available options")
    public List<String> excludedNamespaces = ValueList.create("", "minecraft", ItemCustomization.MOD_ID);
}
