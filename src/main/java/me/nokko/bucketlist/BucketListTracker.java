package me.nokko.bucketlist;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;

public class BucketListTracker implements Component, AutoSyncedComponent, BucketListComponent {

    public HashSet<Block> lookedAt = new HashSet<>();

    //    private BucketListTracker blankTracker = new BucketListTracker();

    @Override
    public void addFromString(String str) {
        this.lookedAt.add(Registry.BLOCK.get(new Identifier(str)));
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        System.out.println("reading from NBT");
        ListTag listTag = tag.getList("bucketList", 9);
        HashSet<String> names = new HashSet<String>();
        for (int i = 0; i < listTag.size(); i++) {
            names.add(listTag.get(i).toString());
        }

        for (String name : names) {
            addFromString(name);
        }
        System.out.println(String.format("read %d values from NBT", this.lookedAt.size()));


    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        System.out.println("writing to NBT");
        ListTag listTag = new ListTag();
        for (Block block : this.lookedAt) {
            listTag.add(StringTag.of(Registry.BLOCK.getId(block).toString()));
        }
        tag.put("bucketList", listTag);
        System.out.println(String.format("wrote %d values to NBT", this.lookedAt.size()));

    }
}
