package mom.wii.itemcustomization.mixin;

import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public class GrindstoneScreenHandlerMixin {
    @Inject(method = "computeResult", at = @At("HEAD"), cancellable = true)
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
        } else if (!firstInput.isEmpty() && !secondInput.isEmpty()) {
            if (SmithingTemplate.isItemCustomizationSmithingTemplate(firstInput) && SmithingTemplate.isItemCustomizationSmithingTemplate(secondInput)) {
                cir.setReturnValue(ItemStack.EMPTY);
            }
        }
    }
}
