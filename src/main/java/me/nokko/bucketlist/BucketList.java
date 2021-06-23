package me.nokko.bucketlist;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentStateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BucketList implements ModInitializer {
//    public static final ScreenHandlerType<? extends ScreenHandler> BOX_SCREEN_HANDLER = ;
	public static final String MODID = "bucketlist";
	public static final Logger LOGGER = LogManager.getLogger(MODID);


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Bucket List initializing!");

//		ServerWorldEvents.LOAD.register((MinecraftServer server, ServerWorld serverWorld) -> {
//			PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
//			BucketListTracker blt = manager.getOrCreate(BucketListTracker::fromTag, BucketListTracker::new, MODID);
//			LOGGER.debug(String.format("Load! Sending list: %s%n", blt.lookedAt.toString()));
//		});

//		ServerWorldEvents.UNLOAD.register((MinecraftServer server, ServerWorld world) -> {
//			PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
//			BucketListTracker blt = manager.getOrCreate(BucketListTracker::fromTag, BucketListTracker::new, MODID);
//			LOGGER.debug(String.format("Unload! Saving list: %s%n", blt.lookedAt.toString()));
//			server.getOverworld().getPersistentStateManager().save();
//		});

//		ServerPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "blist_add_entry"),
//				(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
//					Scoreboard scoreboard = player.world.getScoreboard();
//					PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
//					BucketListTracker blt = manager.get(BucketListTracker::fromTag, MODID);
//
//					String name = buf.readString();
//					if (blt != null) {
//						blt.addFromString(name);
//						LOGGER.debug(String.format("Synchronizing! %s -> %s %n", name, blt.lookedAt.toString()));
//					} else {
//						LOGGER.debug("Server got blist_add_entry packet but blt was null (??)");
//					}
//				});

//		ServerPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "blist_update"),
//				(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
//					PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
//					BucketListTracker blt = manager.getOrCreate(BucketListTracker::fromTag, BucketListTracker::new, MODID);
//
//					ServerPlayNetworking.send(handler.player, new Identifier(MODID, "blist_init"), PacketByteBufs.create().writeNbt(blt.writeNbt(new NbtCompound())));
//				});

//		ServerPlayConnectionEvents.JOIN.register((ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) -> {
//			PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
//			BucketListTracker blt = manager.getOrCreate(BucketListTracker::fromTag, BucketListTracker::new, MODID);
//
//			NbtCompound tag = blt.writeNbt(new NbtCompound());
//			ServerPlayNetworking.send(handler.player, new Identifier(MODID, "blist_init"),
//					PacketByteBufs.create().writeNbt(blt.writeNbt(new NbtCompound()))
//			);
//			LOGGER.debug(String.format("Join! Sending list: %s%n", tag.toString()));
//
//			});


	}
}
