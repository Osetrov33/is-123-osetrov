package com.dao;

import com.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO для избранного
 */
public class FavoriteDAO {

    // добавить в избранное
    public void addToFavorites(int userId, int trackId) throws Exception {

        String sql = "INSERT INTO Favorites (user_id, track_id) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, trackId);

            stmt.executeUpdate();
        }
    }

    // удалить из избранного
    public void removeFavorite(int userId, int trackId) throws Exception {

        String sql = "DELETE FROM Favorites WHERE user_id=? AND track_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, trackId);

            stmt.executeUpdate();
        }
    }

    // проверить избранное
    public boolean isFavorite(int userId, int trackId) {

        String sql = "SELECT 1 FROM Favorites WHERE user_id=? AND track_id=?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, trackId);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
