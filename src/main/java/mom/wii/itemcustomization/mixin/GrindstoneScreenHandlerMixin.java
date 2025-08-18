package mom.wii.itemcustomization.mixin;

import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin {
    @Inject(method = "getOutputStack", at = @At("HEAD"), cancellable = true)
    private void itemCustomization$getOutputStack(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> cir) {
        if (!firstInput.isEmpty() && secondInput.isEmpty()) {
            if (SmithingTemplate.isItemCustomizationSmithingTemplate(firstInput)) {
                ItemStack output = firstInput.copy();
                SmithingTemplate template = SmithingTemplate.from(output);
                template.resetSettings();
                cir.setReturnValue(output);
            }
        } else if (firstInput.isEmpty() && !secondInput.isEmpty()) {
            if (SmithingTemplate.isItemCustomizationSmithingTemplate(secondInput)) {
                ItemStack output = secondInput.copy();
                SmithingTemplate template = SmithingTemplate.from(output);
                template.resetSettings();
                cir.setReturnValue(output);
            }
        }
    }
}
