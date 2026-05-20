package com.dao;


import com.model.User;
import com.example.musicplayer.util.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DAO для пользователей
 */
public class UserDAO {

    // Регистрация
    public void register(String login, String password) throws Exception {

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO Users (login, password_hash) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, hash);
            stmt.executeUpdate();
        }
    }

    // Авторизация
    public User login(String login, String password) throws Exception {

        String sql = "SELECT * FROM Users WHERE login = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hash = rs.getString("password_hash");

                if (BCrypt.checkpw(password, hash)) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("login"),
                            hash
                    );
                }
            }
        }

        return null;
    }
}
