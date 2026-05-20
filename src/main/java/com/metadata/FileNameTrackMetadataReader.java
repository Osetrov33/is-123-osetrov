package com.metadata;

import com.model.Track;

import java.io.File;

public class FileNameTrackMetadataReader implements TrackMetadataReader {

    @Override
    public Track read(File audioFile) {
        String name = audioFile.getName().replaceFirst("(?i)\\.mp3$", "");
        String artist = "Unknown";
        String title = name;

        if (name.contains("-")) {
            String[] parts = name.split("-", 2);
            artist = normalize(parts[0]);
            title = normalize(parts[1]);
        }

        return new Track(0, title, artist, "Unknown", audioFile.getAbsolutePath());
    }

    private String normalize(String value) {
        String result = value == null ? "" : value.trim();
        return result.isBlank() ? "Unknown" : result;
    }
}
