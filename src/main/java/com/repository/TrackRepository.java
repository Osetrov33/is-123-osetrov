package com.repository;

import com.model.Track;

import java.util.List;

public interface TrackRepository {

    void addTrack(Track track);

    void clearTracks();

    List<Track> getAllTracks();

    boolean existsByFilePath(String filePath);
}
