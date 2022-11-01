package dev.pixirora.janerator.mixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GeneratorOptions.class)
public class GeneratorOptionMixin {
    private static final Logger logger = LoggerFactory.getLogger("TitleScreenMixin");
	@Inject(method = "isFlatWorld", at = @At("RETURN"))
	public void overrideFlatWorld(CallbackInfo ci) {
		logger.info("The title screen was initialized");
	}
}
