package com.playback;

public class SequentialPlaybackOrderStrategy implements PlaybackOrderStrategy {

    @Override
    public int nextIndex(int currentIndex, int queueSize) {
        if (queueSize == 0) {
            return -1;
        }
        return (currentIndex + 1) % queueSize;
    }

    @Override
    public int previousIndex(int currentIndex, int queueSize) {
        if (queueSize == 0) {
            return -1;
        }
        return currentIndex <= 0 ? queueSize - 1 : currentIndex - 1;
    }
}
