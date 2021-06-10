package me.nokko.bucketlist;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.PersistentState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public class BucketListTracker extends PersistentState {

    public static final Logger LOGGER = LogManager.getLogger();

    public HashSet<Block> lookedAt = new HashSet<>();
    public Block latest = Blocks.AIR;

    public BucketListTracker() {
//        super("bucketlist");
    }

    @Override
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

    public static BucketListTracker fromTag(NbtCompound tag) {

        BucketListTracker tracker = new BucketListTracker();

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

        tracker.reset();
        for (String name : names) {
            tracker.addFromString(name);
        }
        LOGGER.debug(String.format("read %d values from NBT%n", tracker.lookedAt.size()));
        if (!latest.equals("")) {
            tracker.latest = Registry.BLOCK.get(new Identifier(latest));
        }
        return tracker;
    }
}

