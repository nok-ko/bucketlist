package me.nokko.bucketlist;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static me.nokko.bucketlist.BucketListComponents.BLIST;

public class BucketList implements ModInitializer {
//    public static final ScreenHandlerType<? extends ScreenHandler> BOX_SCREEN_HANDLER = ;

    @Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");
		ServerPlayNetworking.registerGlobalReceiver(new Identifier("modid", "blist_add_entry"),
				(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
					System.out.println("Synchronizing!");
					Scoreboard scoreboard = player.world.getScoreboard();
					String name = buf.readString();
					BucketListComponent BLT = BLIST.maybeGet(scoreboard).orElse(new BucketListTracker());
					BLT.addFromString(name);
					BLIST.sync(scoreboard);
				});


	}
}
