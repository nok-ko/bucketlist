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
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static me.nokko.bucketlist.BucketList.LOGGER;
import static me.nokko.bucketlist.BucketList.MODID;

@Environment(EnvType.CLIENT)
public class BucketListClient implements ClientModInitializer {

    private static BucketListTracker BLT;

    public static final HashSet<Block> fullList = new HashSet<>(Arrays.asList(Blocks.DIRT, Blocks.STONE, Blocks.OAK_LOG, Blocks.OAK_LEAVES, Blocks.GRASS_BLOCK));
    private static final ArrayList<String> reference_lines = new ArrayList<>(Arrays.asList("Latest block:", String.format("Progress: 0/%d", fullList.size()), ""));
    private static ArrayList<String> lines = reference_lines;

    private static Boolean dirty = true;

    public static void onJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient client) {
        lines = reference_lines;
        BLT = new BucketListTracker(client);
        sync(client);
    }

    private static void sync(MinecraftClient client) {
        assert client.world != null;
//        ClientPlayNetworking.send(new Identifier(MODID, "blist_update"), PacketByteBufs.create());
    }

    private static void updateLines(String latestKey) {
        lines.set(0, String.format("Latest block: %s", I18n.translate(latestKey)));
        lines.set(1, String.format("Progress: %d/%d", BLT.lookedAt.size(), fullList.size()));
        if (BLT.lookedAt.size() == fullList.size()) {
            lines.set(2, "A WINNER IS YOU!!! I'M SO PROUD <3");
        }
    }

    public static void onPlayDisconnect(PacketListener clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        LOGGER.log(Level.INFO, "Disconnect event handler fired");
        BLT.markDirty();
        BLT.toFile();
        BLT.reset();
    }

    public static void render(MatrixStack matrices, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (BLT == null) {
            BLT = new BucketListTracker(client);
        }

        if (dirty) {
            updateLines(BLT.latest.getTranslationKey());
            dirty = false;
        }

        // The raycast only checks for blocks, it isn't *technically* necessary to find water and lava.
        if (client.world != null && client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            Block block = client.world.getBlockState(((BlockHitResult) client.crosshairTarget).getBlockPos()).getBlock();
            if (!BLT.lookedAt.contains(block) && fullList.contains(block)) {
                String name = Registry.BLOCK.getId(block).toString();
                BLT.addFromString(name);
                BLT.markDirty();

                LOGGER.debug(String.format("Saving! %s %n", name));
                ClientPlayNetworking.send(new Identifier(MODID, "blist_add_entry"), PacketByteBufs.create().writeString(name));

                String key = block.getTranslationKey();
                updateLines(key);
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
        LOGGER.info("Client init!");
        HudRenderCallback.EVENT.register(BucketListClient::render);
        ClientPlayConnectionEvents.JOIN.register(BucketListClient::onJoin);
        ClientPlayConnectionEvents.DISCONNECT.register(BucketListClient::onPlayDisconnect);


        // setup data directory
        Path client_data_path = MinecraftClient.getInstance()
                .runDirectory
                .toPath()
                .resolve(BucketList.MODID);

        if (!Files.exists(client_data_path)) {
            try {
                Files.createDirectory(client_data_path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        ClientPlayNetworking.registerGlobalReceiver(new Identifier(MODID, "blist_init"),
//        (MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) -> {
//            CompoundTag tag = buf.readNbt();
//            LOGGER.debug(String.format("Client Synchronizing! %s%n", tag != null ? tag.toString() : "<empty tag>"));
////            BLT = BucketListTracker.fromTag(tag != null ? tag : new CompoundTag());
//            lines = reference_lines;
//            markDirty();
//        });
        HashSet<Block> unlookables = new HashSet<>(Arrays.asList(
                Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK,
                Blocks.STRUCTURE_BLOCK, Blocks.STRUCTURE_VOID, Blocks.BARRIER, Blocks.JIGSAW,
                Blocks.AIR, Blocks.WATER, Blocks.LAVA, /* we only scan for solid blocks, not fluids. */
                Blocks.PETRIFIED_OAK_SLAB, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD
        ));

        for (Map.Entry<RegistryKey<Block>, Block> entry : Registry.BLOCK.getEntries()) {
            Identifier id = entry.getKey().getValue();
            fullList.add(Registry.BLOCK.get(id));
        }
        fullList.removeAll(unlookables);
        LOGGER.info("All blocks required for completion:" + fullList);
    }
}

