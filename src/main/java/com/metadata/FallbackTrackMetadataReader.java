package com.metadata;

import com.model.Track;

import java.io.File;
import java.util.List;

public class FallbackTrackMetadataReader implements TrackMetadataReader {

    private final List<TrackMetadataReader> readers;

    public FallbackTrackMetadataReader(List<TrackMetadataReader> readers) {
        this.readers = readers;
    }

    @Override
    public Track read(File audioFile) {
        for (TrackMetadataReader reader : readers) {
            Track track = reader.read(audioFile);
            if (track != null && track.getTitle() != null && !track.getTitle().isBlank()) {
                return track;
            }
        }
        return new Track(0, audioFile.getName(), "Unknown", "Unknown", audioFile.getAbsolutePath());
    }
}
