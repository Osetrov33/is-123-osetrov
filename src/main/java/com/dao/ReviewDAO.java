package com.dao;

import com.model.Review;
import com.example.musicplayer.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для рецензий
 */
public class ReviewDAO {

    // ===== ДОБАВИТЬ ОТЗЫВ =====
    public void addReview(Review review) {

        String sql = "INSERT INTO Reviews (user_id, track_id, album, rating, review_text, review_date) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, review.getUserId());

            if (review.getTrackId() != null) {
                stmt.setInt(2, review.getTrackId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, review.getAlbum());
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getReviewText());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== ОТЗЫВЫ ПО ТРЕКУ =====
    public List<Review> getReviewsByTrack(int trackId) {

        List<Review> list = new ArrayList<>();

        String sql = "SELECT * FROM Reviews WHERE track_id = ? ORDER BY review_date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trackId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Review review = new Review(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("track_id"),
                        rs.getString("album"),
                        rs.getInt("rating"),
                        rs.getString("review_text"),
                        rs.getTimestamp("review_date").toLocalDateTime()
                );

                list.add(review);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== СРЕДНИЙ РЕЙТИНГ =====
    public double getAverageRatingForTrack(int trackId) {

        String sql = "SELECT AVG(rating) as avg_rating FROM Reviews WHERE track_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trackId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}