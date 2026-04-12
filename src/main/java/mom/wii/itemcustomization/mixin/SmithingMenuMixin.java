package mom.wii.itemcustomization.mixin;

import mom.wii.itemcustomization.ItemCustomization;
import mom.wii.itemcustomization.template.SmithingTemplate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin implements ItemCombinerMenuMixin, SmithingMenuAccessor {
    @Shadow protected abstract List<ItemStack> getRelevantItems();

    @Shadow @Final private Level level;

    @ModifyArg(method = "createInputSlotDefinitions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;withSlot(IIILjava/util/function/Predicate;)Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;", ordinal = 0), index = 3)
    private static Predicate<ItemStack> itemCustomization$canUseTemplate(Predicate<ItemStack> canUse) {
        return itemStack -> {
            if (SmithingTemplate.isItemCustomizationSmithingTemplate(itemStack)) {
                return true;
            }
            return canUse.test(itemStack);
        };
    }

    @ModifyArg(method = "createInputSlotDefinitions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;withSlot(IIILjava/util/function/Predicate;)Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;", ordinal = 1), index = 3)
    private static Predicate<ItemStack> itemCustomization$canUseBase(Predicate<ItemStack> canUse) {
        return itemStack -> true;
    }

    @ModifyArg(method = "createInputSlotDefinitions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;withSlot(IIILjava/util/function/Predicate;)Lnet/minecraft/world/inventory/ItemCombinerMenuSlotDefinition$Builder;", ordinal = 2), index = 3)
    private static Predicate<ItemStack> itemCustomization$canUseAddition(Predicate<ItemStack> canUse) {
        return itemStack -> {
            if (itemStack.is(SmithingTemplate.ingredient)) return true;
            return canUse.test(itemStack);
        };
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void itemCustomization$updateResult(CallbackInfo ci) {
        if (SmithingTemplate.isItemCustomizationSmithingTemplate(this.getInputSlots().getItem(0))) {
            SmithingTemplate template = SmithingTemplate.from(this.getInputSlots().getItem(0));
            if (template.hasSettings()) {
                ItemStack base = this.getInputSlots().getItem(1);
                ItemStack ingredient = this.getInputSlots().getItem(2);
                if (ingredient.is(SmithingTemplate.ingredient) && template.canApplyToStack(base) && ingredient.getCount() >= template.getCost()) {
                    ItemStack output = base.copy();
                    output.setCount(1);
                    template.applySettings(output, this.level);
                    this.getResultSlots().setItem(0, output);
                    ci.cancel();
                }
            }
        }
    }

    @Unique
    private void decrementStackByCount(int slot, int count) {
        ItemStack itemStack = this.getInputSlots().getItem(slot);
        if (!itemStack.isEmpty()) {
            itemStack.shrink(count);
            this.getInputSlots().setItem(slot, itemStack);
        }
    }

    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    private void itemCustomization$onTakeOutput(Player player, ItemStack stack, CallbackInfo ci) {
        if (SmithingTemplate.isItemCustomizationSmithingTemplate(this.getInputSlots().getItem(0))) {
            SmithingTemplate template =  SmithingTemplate.from(this.getInputSlots().getItem(0));
            ItemStack ingredient = this.getInputSlots().getItem(2);
            ItemStack base = this.getInputSlots().getItem(1);
            int cost;
            if (ingredient.is(SmithingTemplate.ingredient) && template.hasSettings() && template.canApplyToStack(base) && ingredient.getCount() >= (cost = template.getCost())) {
                stack.onCraftedBy(player, stack.getCount());
                this.getResultSlots().awardUsedRecipes(player, this.getRelevantItems());
                this.callShrinkStackInSlot(0);
                this.callShrinkStackInSlot(1);
                this.decrementStackByCount(2, cost);
                this.getAccess().execute((world, pos) -> world.levelEvent(LevelEvent.SOUND_SMITHING_TABLE_USED, pos, 0));
                ItemCustomization.incrementItemsCustomized((ServerPlayer) player, 1);
                ci.cancel();
            }
        }
    }
}
