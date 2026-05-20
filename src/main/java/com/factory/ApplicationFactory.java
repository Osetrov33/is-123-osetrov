package com.factory;

import com.metadata.FallbackTrackMetadataReader;
import com.metadata.FileNameTrackMetadataReader;
import com.metadata.TagTrackMetadataReader;
import com.metadata.TrackMetadataReader;
import com.playback.PlaybackOrderStrategy;
import com.playback.RepeatPlaybackOrderStrategy;
import com.playback.SequentialPlaybackOrderStrategy;
import com.playback.ShufflePlaybackOrderStrategy;
import com.repository.CachedTrackRepository;
import com.repository.SqlTrackRepository;
import com.repository.TrackRepository;

import java.util.List;
import java.util.Random;

public class ApplicationFactory {

    private final TrackRepository trackRepository;
    private final TrackMetadataReader metadataReader;
    private final PlaybackOrderStrategy sequentialPlayback;
    private final PlaybackOrderStrategy shufflePlayback;
    private final PlaybackOrderStrategy repeatPlayback;

    public ApplicationFactory() {
        TrackMetadataReader fileNameReader = new FileNameTrackMetadataReader();
        this.trackRepository = new CachedTrackRepository(new SqlTrackRepository());
        this.metadataReader = new FallbackTrackMetadataReader(List.of(
                new TagTrackMetadataReader(fileNameReader),
                fileNameReader
        ));
        this.sequentialPlayback = new SequentialPlaybackOrderStrategy();
        this.shufflePlayback = new ShufflePlaybackOrderStrategy(new Random());
        this.repeatPlayback = new RepeatPlaybackOrderStrategy();
    }

    public TrackRepository createTrackRepository() {
        return trackRepository;
    }

    public TrackMetadataReader createMetadataReader() {
        return metadataReader;
    }

    public PlaybackOrderStrategy createPlaybackOrderStrategy(boolean shuffleEnabled, boolean repeatEnabled) {
        if (repeatEnabled) {
            return repeatPlayback;
        }
        if (shuffleEnabled) {
            return shufflePlayback;
        }
        return sequentialPlayback;
    }
}
