package com.service;

import com.model.Track;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class PlayerService {

    private MediaPlayer player;
    private Track current;

    public void play(Track t) {

        if (player != null) {
            player.stop();
            player.dispose();
        }

        current = t;

        player = new MediaPlayer(
                new Media(new File(t.getFilePath()).toURI().toString())
        );

        player.play();
    }

    public void pause() {
        if (player != null) player.pause();
    }

    public void stop() {
        if (player != null) player.stop();
    }

    public Track getCurrent() {
        return current;
    }

    public MediaPlayer getPlayer() {
        return player;
    }
}