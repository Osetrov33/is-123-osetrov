package com.model;

import java.time.LocalDateTime;

/**
 * Рецензия (таблица Reviews)
 */
public class Review {

    private int id;
    private int userId;
    private Integer trackId; // может быть null
    private String album;    // может быть null
    private int rating;
    private String reviewText;
    private LocalDateTime reviewDate;

    public Review() {}

    public Review(int id, int userId, Integer trackId, String album,
                  int rating, String reviewText, LocalDateTime reviewDate) {
        this.id = id;
        this.userId = userId;
        this.trackId = trackId;
        this.album = album;
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
    }

    // Геттеры и сеттеры

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }


    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }


    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
}
