package com.playback;

import java.util.Random;

public class ShufflePlaybackOrderStrategy implements PlaybackOrderStrategy {

    private final Random random;

    public ShufflePlaybackOrderStrategy(Random random) {
        this.random = random;
    }

    @Override
    public int nextIndex(int currentIndex, int queueSize) {
        if (queueSize == 0) {
            return -1;
        }
        if (queueSize == 1) {
            return 0;
        }

        int nextIndex = currentIndex;
        while (nextIndex == currentIndex) {
            nextIndex = random.nextInt(queueSize);
        }
        return nextIndex;
    }

    @Override
    public int previousIndex(int currentIndex, int queueSize) {
        return nextIndex(currentIndex, queueSize);
    }
}
