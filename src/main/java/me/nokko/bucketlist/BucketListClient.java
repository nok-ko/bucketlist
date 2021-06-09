package me.nokko.bucketlist;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Arrays;

import static me.nokko.bucketlist.BucketListComponents.BLIST;

@Environment(EnvType.CLIENT)
public class BucketListClient implements ClientModInitializer {

    private static BucketListTracker BLT;
    private static final ArrayList<String> reference_lines = new ArrayList<>(Arrays.asList("Latest block:", String.format("Progress: 0/%d", BucketListComponent.fullList.size()), ""));
    private static ArrayList<String> lines = reference_lines;


    public static void onJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient client) {
        lines = reference_lines;
        sync(client);
    }

    private static void onDisconnect(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient client) {
//        lines = reference_lines;
        sync(client);
    }

    private static void sync(MinecraftClient client) {
        assert client.world != null;
        BLIST.sync(client.world.getScoreboard());
        BLT = (BucketListTracker) BLIST.maybeGet(client.world.getScoreboard()).orElse(new BucketListTracker());
    }

    public static void render(MatrixStack matrices, float delta) {
        // shouldRender var?

        MinecraftClient client = MinecraftClient.getInstance();

        //TODO: fix below

        if (client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            Block block = client.world.getBlockState(((BlockHitResult) client.crosshairTarget).getBlockPos()).getBlock();
            if (!BLT.lookedAt.contains(block) && BucketListComponent.fullList.contains(block)) {
                BLT.lookedAt.add(block);
                String name = Registry.BLOCK.getId(block).toString();
                ClientPlayNetworking.send(new Identifier("modid", "blist_add_entry"), PacketByteBufs.create().writeString(name));
                String key = block.getTranslationKey();
                lines.set(0, String.format("Latest block: %s", I18n.translate(key)));
                lines.set(1, String.format("Progress: %d/%d", BLT.lookedAt.size(), BucketListComponent.fullList.size()));
                if (BLT.lookedAt.size() == BucketListComponent.fullList.size()) {
                    lines.set(2, "A WINNER IS YOU!!!! I'M SO PROUD <3");
                }
            }
        }
        TextRenderer textRenderer = client.textRenderer;

        matrices.push();
        int yOffset = 0;
        for (String line : lines) {
            textRenderer.drawWithShadow(matrices, I18n.translate(line), 8, 8 + yOffset, 0xFFFFFFFF);
            yOffset += 8;
        }
        matrices.pop();
    }

    @Override
    public void onInitializeClient() {
        System.out.println("Client init!");
        HudRenderCallback.EVENT.register(BucketListClient::render);
        ClientPlayConnectionEvents.JOIN.register(BucketListClient::onJoin);
        ClientPlayConnectionEvents.DISCONNECT.register(BucketListClient::onDisconnect);
    }
}

