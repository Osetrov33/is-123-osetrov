package com.dao;

import com.model.Track;
import com.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с треками
 */
public class TrackDAO {

    /**
     * Добавить трек в БД
     */
    public void addTrack(Track track) {
        String sql = "INSERT INTO Tracks (title, artist, album, file_path) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, track.getTitle());
            stmt.setString(2, track.getArtist());
            stmt.setString(3, track.getAlbum());
            stmt.setString(4, track.getFilePath());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTrack(Track track) {
        String sql = "UPDATE Tracks SET title = ?, artist = ?, album = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, track.getTitle());
            stmt.setString(2, track.getArtist());
            stmt.setString(3, track.getAlbum());
            stmt.setInt(4, track.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Удалить ВСЕ треки
     */
    public void clearTracks() {
        String sql = "DELETE FROM Tracks";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получить все треки
     */
    public List<Track> getAllTracks() {
        List<Track> tracks = new ArrayList<>();

        String sql = "SELECT * FROM Tracks";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Track track = new Track(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        rs.getString("album"),
                        rs.getString("file_path")
                );
                tracks.add(track);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tracks;
    }

    public boolean existsByFilePath(String filePath) {
        String sql = "SELECT 1 FROM Tracks WHERE file_path = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, filePath);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
