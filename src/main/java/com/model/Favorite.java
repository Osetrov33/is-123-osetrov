package com.model;



/**
 * Избранное (таблица Favorites)
 */
public class Favorite {

    private int userId;
    private int trackId;

    public Favorite() {}

    public Favorite(int userId, int trackId) {
        this.userId = userId;
        this.trackId = trackId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }
}
