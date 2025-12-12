package mom.wii.itemcustomization.mixin;

import net.minecraft.world.inventory.SmithingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SmithingMenu.class)
public interface SmithingScreenHandlerAccessor {
    @Invoker
    void callShrinkStackInSlot(int slot);
}
