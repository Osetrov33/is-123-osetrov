package com.playback;

public class RepeatPlaybackOrderStrategy implements PlaybackOrderStrategy {

    @Override
    public int nextIndex(int currentIndex, int queueSize) {
        return queueSize == 0 ? -1 : Math.max(currentIndex, 0);
    }

    @Override
    public int previousIndex(int currentIndex, int queueSize) {
        return queueSize == 0 ? -1 : Math.max(currentIndex, 0);
    }
}
