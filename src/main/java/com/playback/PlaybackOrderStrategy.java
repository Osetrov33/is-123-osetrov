package com.playback;

import java.util.List;

public interface PlaybackOrderStrategy {

    int nextIndex(int currentIndex, int queueSize);

    int previousIndex(int currentIndex, int queueSize);
}
