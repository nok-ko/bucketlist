package me.nokko.bucketlist;

import me.nokko.bucketlist.mixin.IntegratedServerAccessor;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

public class BucketListTracker {

    public static final Logger LOGGER = LogManager.getLogger();

    public HashSet<Block> lookedAt = new HashSet<>();
    public Block latest = Blocks.AIR;

    // Filesystem
    private String save_path;
    private NbtCompound tag;
    private boolean dirty;

    public BucketListTracker(MinecraftClient client) {

        String filename;
        if (client.isInSingleplayer()) {
            filename = ((IntegratedServerAccessor) client.getServer()).getSession().getLevelSummary().getName();
        } else {
            filename = client.getCurrentServerEntry().name;
        }

        this.save_path = MinecraftClient.getInstance()
                .runDirectory
                .toPath()
                .resolve(BucketList.MODID)
                .resolve(filename).toAbsolutePath().toString() + ".dat";
        try {
            this.tag = Optional.ofNullable(NbtIo.readCompressed(new File(save_path))).orElse(new NbtCompound());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.fromTag(this.tag);
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        LOGGER.debug("writing to NBT");
        NbtList NbtList = new NbtList();
        for (Block block : this.lookedAt) {
            NbtList.add(NbtString.of(Registry.BLOCK.getId(block).toString()));
        }
        nbt.put("bucketList", NbtList);
        nbt.put("latest", NbtString.of(Registry.BLOCK.getId(this.latest).toString()));
        LOGGER.debug(String.format("wrote %d values to NBT%n %s%n", this.lookedAt.size(), nbt.toString()));
        return nbt;
    }

    public void addFromString(String str) {
        Block block = Registry.BLOCK.get(new Identifier(str));
        this.lookedAt.add(block);
        this.latest = block;
        this.markDirty();
    }

    public void reset() {
        this.lookedAt = new HashSet<>();
        this.latest = Blocks.AIR;
        this.markDirty();
    }

    private void markDirty() {
        this.dirty = true;
    }

    public void toFile() {
        if (this.dirty) {
            LOGGER.debug("writing to NBT");
            NbtList NbtList = new NbtList();
            for (Block block : this.lookedAt) {
                NbtList.add(NbtString.of(Registry.BLOCK.getId(block).toString()));
            }
            this.tag.put("bucketList", NbtList);
            this.tag.put("latest", NbtString.of(Registry.BLOCK.getId(this.latest).toString()));

            try {
                NbtIo.writeCompressed(this.tag, new File(this.save_path));
                LOGGER.debug(String.format("wrote %d values to NBT%n %s%n", this.lookedAt.size(), this.tag.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void fromTag(NbtCompound tag) {
        LOGGER.debug(String.format("reading from NBT %s%n", tag.toString()));
        NbtList list = tag.getList("bucketList", NbtType.STRING);
        String latest = tag.getString("latest");
        HashSet<String> names = new HashSet<>();
        LOGGER.debug(list.toString());


        for(int i = 0; i < list.size(); ++i) {
            String sTag = list.getString(i);
            LOGGER.debug(String.format("%s! %n",sTag));
            names.add(sTag);
        }

        this.reset();
        for (String name : names) {
            this.addFromString(name);
        }
        LOGGER.debug(String.format("read %d values from NBT%n", this.lookedAt.size()));
        if (!latest.equals("")) {
            this.latest = Registry.BLOCK.get(new Identifier(latest));
        }
    }
}

