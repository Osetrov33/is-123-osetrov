package com.repository;

import com.model.Track;

import java.util.List;

public class ReadOnlyTrackRepository implements TrackRepository {

    private final TrackRepository source;

    public ReadOnlyTrackRepository(TrackRepository source) {
        this.source = source;
    }

    @Override
    public void addTrack(Track track) {
        throw new UnsupportedOperationException("Репозиторий открыт только для чтения");
    }

    @Override
    public void clearTracks() {
        throw new UnsupportedOperationException("Репозиторий открыт только для чтения");
    }

    @Override
    public List<Track> getAllTracks() {
        return source.getAllTracks();
    }

    @Override
    public boolean existsByFilePath(String filePath) {
        return source.existsByFilePath(filePath);
    }
}
