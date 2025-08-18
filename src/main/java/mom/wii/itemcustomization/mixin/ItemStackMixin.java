package mom.wii.itemcustomization.mixin;

import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract ItemStack copy();

    @Inject(method = "isDamageable", at = @At("HEAD"), cancellable = true)
    private void itemCustomization$isDamageable(CallbackInfoReturnable<Boolean> cir) {
        // hacky workaround to make grindstone screen work
        if (SmithingTemplate.isItemCustomizationSmithingTemplate(this.copy())) cir.setReturnValue(true);
    }
}
