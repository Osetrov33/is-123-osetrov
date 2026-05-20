package com.util;

import com.factory.ApplicationFactory;
import com.metadata.TrackMetadataReader;
import com.repository.TrackRepository;

import java.io.File;
import java.util.Locale;

public class MusicScanner {

    private final TrackRepository trackRepository;
    private final TrackMetadataReader metadataReader;

    public MusicScanner() {
        ApplicationFactory factory = new ApplicationFactory();
        this.trackRepository = factory.createTrackRepository();
        this.metadataReader = factory.createMetadataReader();
    }

    public MusicScanner(TrackRepository trackRepository, TrackMetadataReader metadataReader) {
        this.trackRepository = trackRepository;
        this.metadataReader = metadataReader;
    }

    public void scanFolder(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanFolder(file);
                continue;
            }

            if (file.getName().toLowerCase(Locale.ROOT).endsWith(".mp3")
                    && !trackRepository.existsByFilePath(file.getAbsolutePath())) {
                trackRepository.addTrack(metadataReader.read(file));
            }
        }
    }
}
