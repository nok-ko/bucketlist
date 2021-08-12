package me.nokko.bucketlist;

import me.nokko.bucketlist.mixin.IntegratedServerAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashSet;
import java.util.Optional;

public class BucketListTracker {

    public static final Logger LOGGER = LogManager.getLogger();

    public HashSet<Block> lookedAt = new HashSet<>();
    public Block latest = Blocks.AIR;

    // Filesystem
    private String save_path;
    private CompoundTag tag;
    private boolean dirty;

    public BucketListTracker(MinecraftClient client) {

        String filename;
        if (client.isInSingleplayer()) {
            filename = ((IntegratedServerAccessor) client.getServer()).getSession().method_29584().getName();
        } else {
            filename = client.getCurrentServerEntry().name;
        }

        this.save_path = MinecraftClient.getInstance()
                .runDirectory
                .toPath()
                .resolve(BucketList.MODID)
                .resolve(filename).toAbsolutePath() + ".dat";
        try {
            this.tag = Optional.ofNullable(NbtIo.readCompressed(new FileInputStream(save_path))).orElse(new CompoundTag());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.tag == null) {
            this.tag = new CompoundTag();
        }
        this.fromTag(this.tag);
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        LOGGER.debug("writing to NBT");
        ListTag ListTag = new ListTag();
        for (Block block : this.lookedAt) {
            ListTag.add(StringTag.of(Registry.BLOCK.getId(block).toString()));
        }
        nbt.put("bucketList", ListTag);
        nbt.put("latest", StringTag.of(Registry.BLOCK.getId(this.latest).toString()));
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
//        this.markDirty(); // oh no no no
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void toFile() {
        if (this.dirty) {
            LOGGER.info(String.format("writing to NBT at %s", this.save_path));
            ListTag listTag = new ListTag();
            for (Block block : this.lookedAt) {
                listTag.add(StringTag.of(Registry.BLOCK.getId(block).toString()));
            }
            this.tag.put("bucketList", listTag);
            this.tag.put("latest", StringTag.of(Registry.BLOCK.getId(this.latest).toString()));

            try {
                NbtIo.writeCompressed(this.tag, new FileOutputStream(this.save_path));
                LOGGER.info(String.format("wrote %d values to NBT%n %s%n", this.lookedAt.size(), this.tag.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.dirty = false; // hopefully prevent saving over ourselves?
        }
    }

    private void fromTag(CompoundTag tag) {
        LOGGER.info(String.format("reading from NBT %s%n", tag.toString()));
        ListTag list = tag.getList("bucketList", 8 /*NbtType.STRING*/);
        String latest = tag.getString("latest");
        HashSet<String> names = new HashSet<>();
        LOGGER.info(list.toString());


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

