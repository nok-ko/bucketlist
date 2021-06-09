package me.nokko.bucketlist.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerEntity.class)
public abstract class RoofLogMixin extends Entity {
	public RoofLogMixin(EntityType<?> type, World world) {
		super(type, world);
	}
//	@Shadow
//	public World world;
//
//	@Shadow

	@Inject(at = @At("RETURN"), method = "Lnet/minecraft/entity/player/PlayerEntity;tickMovement()V", locals = LocalCapture.CAPTURE_FAILSOFT)
	private void tickMovement(CallbackInfo info) {


		if (!world.isClient) {
			BlockPos pos = new BlockPos(this.getX(), this.getY(), this.getZ());
			if (this.world.isSkyVisible(pos)) {
//				System.out.println("NO ROOF");
			} else { //if (this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
//				System.out.println("ROOF");
			}
		}
	}
}

// Lnet/minecraft/block/AbstractBlock;onBlockBreakStart(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)V