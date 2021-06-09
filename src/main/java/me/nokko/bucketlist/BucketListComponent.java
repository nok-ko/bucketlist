package me.nokko.bucketlist;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.Arrays;
import java.util.HashSet;

public interface BucketListComponent extends Component {
    HashSet<Block> fullList = new HashSet<>(Arrays.asList(Blocks.DIRT, Blocks.STONE, Blocks.OAK_LOG, Blocks.OAK_LEAVES, Blocks.GRASS_BLOCK));

    void addFromString(String str);
}
