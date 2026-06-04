package com.repository;

import com.model.Track;

import java.util.ArrayList;
import java.util.List;

public class CachedTrackRepository implements TrackRepository {

    private final TrackRepository source;
    private List<Track> cache;

    public CachedTrackRepository(TrackRepository source) {
        this.source = source;
    }

    @Override
    public void addTrack(Track track) {
        source.addTrack(track);
        cache = null;
    }

    @Override
    public void updateTrack(Track track) {
        source.updateTrack(track);
        cache = null;
    }

    @Override
    public void clearTracks() {
        source.clearTracks();
        cache = null;
    }

    @Override
    public List<Track> getAllTracks() {
        if (cache == null) {
            cache = new ArrayList<>(source.getAllTracks());
        }
        return new ArrayList<>(cache);
    }

    @Override
    public boolean existsByFilePath(String filePath) {
        return getAllTracks().stream()
                .anyMatch(track -> track.getFilePath() != null && track.getFilePath().equals(filePath));
    }
}
