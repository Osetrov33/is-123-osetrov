package com.view;

import com.model.Track;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;

public class TrackView {

    private VBox view;

    public TrackView(Track t) {
        build(t);
    }

    public Parent getView() {
        return view;
    }

    private void build(Track t) {

        ImageView cover = new ImageView();
        cover.setFitWidth(220);
        cover.setFitHeight(220);

        try {
            cover.setImage(new Image(new File("default.png").toURI().toString()));
        } catch (Exception e) {}

        Label title = new Label(t.getTitle());
        Label artist = new Label(t.getArtist());

        title.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        artist.setStyle("-fx-text-fill: gray;");

        Button play = new Button("▶ Play");
        Button like = new Button("❤ Like");

        Label rating = new Label("⭐ Rating: -");

        ListView<String> reviews = new ListView<>();

        view = new VBox(15,
                cover,
                title,
                artist,
                play,
                like,
                rating,
                new Label("Reviews"),
                reviews
        );

        view.setPadding(new Insets(20));
        view.setStyle("-fx-background-color: #0f1115;");
    }
}