package mom.wii.itemcustomization.mixin;

import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin implements ForgingScreenHandlerAccessor, SmithingScreenHandlerAccessor {
    @Shadow protected abstract List<ItemStack> getInputStacks();

    @ModifyArg(method = "createForgingSlotsManager", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;", ordinal = 0), index = 3)
    private static Predicate<ItemStack> itemCustomization$canUseTemplate(Predicate<ItemStack> canUse) {
        return itemStack -> {
            if (SmithingTemplate.isItemCustomizationSmithingTemplate(itemStack)) {
                return true;
            }
            return canUse.test(itemStack);
        };
    }

    @ModifyArg(method = "createForgingSlotsManager", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;", ordinal = 1), index = 3)
    private static Predicate<ItemStack> itemCustomization$canUseBase(Predicate<ItemStack> canUse) {
        return itemStack -> true;
    }

    @ModifyArg(method = "createForgingSlotsManager", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;input(IIILjava/util/function/Predicate;)Lnet/minecraft/screen/slot/ForgingSlotsManager$Builder;", ordinal = 2), index = 3)
    private static Predicate<ItemStack> itemCustomization$canUseAddition(Predicate<ItemStack> canUse) {
        return itemStack -> {
            if (itemStack.isOf(SmithingTemplate.ingredient)) return true;
            return canUse.test(itemStack);
        };
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    private void itemCustomization$updateResult(CallbackInfo ci) {
        if (SmithingTemplate.isItemCustomizationSmithingTemplate(this.getInput().getStack(0))) {
            SmithingTemplate template = SmithingTemplate.from(this.getInput().getStack(0));
            if (template.hasSettings()) {
                ItemStack base = this.getInput().getStack(1);
                ItemStack ingredient = this.getInput().getStack(2);
                if (ingredient.isOf(SmithingTemplate.ingredient) && template.canApplyToStack(base) && ingredient.getCount() >= template.getCost()) {
                    ItemStack output = base.copy();
                    output.setCount(1);
                    template.applySettings(output);
                    this.getOutput().setStack(0, output);
                    ci.cancel();
                }
            }
        }
    }

    @Unique
    private void decrementStackByCount(int slot, int count) {
        ItemStack itemStack = this.getInput().getStack(slot);
        if (!itemStack.isEmpty()) {
            itemStack.decrement(count);
            this.getInput().setStack(slot, itemStack);
        }
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    private void itemCustomization$onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (SmithingTemplate.isItemCustomizationSmithingTemplate(this.getInput().getStack(0))) {
            SmithingTemplate template =  SmithingTemplate.from(this.getInput().getStack(0));
            ItemStack ingredient = this.getInput().getStack(2);
            ItemStack base = this.getInput().getStack(1);
            int cost;
            if (ingredient.isOf(SmithingTemplate.ingredient) && template.hasSettings() && template.canApplyToStack(base) && ingredient.getCount() >= (cost = template.getCost())) {
                stack.onCraftByPlayer(player, stack.getCount());
                this.getOutput().unlockLastRecipe(player, this.getInputStacks());
                this.callDecrementStack(0);
                this.callDecrementStack(1);
                this.decrementStackByCount(2, cost);
                this.getContext().run((world, pos) -> world.syncWorldEvent(WorldEvents.SMITHING_TABLE_USED, pos, 0));
                ci.cancel();
            }
        }
    }
}
