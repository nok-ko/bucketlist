package me.nokko.bucketlist;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactory;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Identifier;


public class BucketListComponents implements ScoreboardComponentInitializer {
    public static final ComponentKey<BucketListComponent> BLIST =
            ComponentRegistry.getOrCreate(new Identifier("modid", "bucketlist"), BucketListComponent.class);

    private class BucketListTrackerFactory implements ScoreboardComponentFactory {

        @Override
        public BucketListComponent createForScoreboard(Scoreboard scoreboard) {
            return new BucketListTracker();
        }
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerForScoreboards(BLIST, new BucketListTrackerFactory());
    }
}