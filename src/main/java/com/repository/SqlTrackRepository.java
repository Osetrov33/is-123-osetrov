package com.repository;

import com.dao.TrackDAO;
import com.model.Track;

import java.util.List;

public class SqlTrackRepository implements TrackRepository {

    private final TrackDAO trackDAO;

    public SqlTrackRepository() {
        this(new TrackDAO());
    }

    public SqlTrackRepository(TrackDAO trackDAO) {
        this.trackDAO = trackDAO;
    }

    @Override
    public void addTrack(Track track) {
        trackDAO.addTrack(track);
    }

    @Override
    public void clearTracks() {
        trackDAO.clearTracks();
    }

    @Override
    public List<Track> getAllTracks() {
        return trackDAO.getAllTracks();
    }

    @Override
    public boolean existsByFilePath(String filePath) {
        return trackDAO.existsByFilePath(filePath);
    }
}
