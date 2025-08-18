package mom.wii.itemcustomization.mixin;

import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SmithingScreenHandler.class)
public interface SmithingScreenHandlerAccessor {
    @Invoker
    void callDecrementStack(int slot);
}
