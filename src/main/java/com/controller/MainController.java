package com.controller;

import com.dao.FavoriteDAO;
import com.dao.ReviewDAO;
import com.factory.ApplicationFactory;
import com.metadata.TrackMetadataReader;
import com.model.Review;
import com.model.Track;
import com.model.User;
import com.playback.PlaybackOrderStrategy;
import com.repository.TrackRepository;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class MainController {

    private final ApplicationFactory applicationFactory = new ApplicationFactory();
    private final TrackRepository trackRepository = applicationFactory.createTrackRepository();
    private final TrackMetadataReader metadataReader = applicationFactory.createMetadataReader();
    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final Stage stage;
    private final User currentUser;
    private final Random random = new Random();

    private StackPane root;
    private BorderPane shell;
    private BorderPane contentFrame;
    private BorderPane centerPane;
    private Region ambientOrbOne;
    private Region ambientOrbTwo;
    private Region ambientOrbThree;

    private VBox trackContainer;
    private ListView<String> queueListView;
    private ListView<String> trackReviewsList;
    private TextField searchField;
    private TextArea reviewArea;
    private Slider reviewRatingSlider;
    private Slider progressSlider;
    private Slider volumeSlider;
    private Label sectionBadge;
    private Label heroTitle;
    private Label totalTracksLabel;
    private Label favoriteCountLabel;
    private Label currentTrackTitleLabel;
    private Label currentTrackArtistLabel;
    private Label spotlightTitleLabel;
    private Label spotlightArtistLabel;
    private Label spotlightAlbumLabel;
    private Label spotlightRatingLabel;
    private Label queueTitleLabel;
    private Label queueMetaLabel;
    private Label timeLabel;
    private Button playPauseButton;
    private Button shuffleButton;
    private Button repeatButton;
    private Button spotlightFavoriteButton;

    private final List<Region> visualizerBars = new ArrayList<>();
    private Region animatedGlow;
    private StackPane animatedCover;
    private Timeline visualizerTimeline;
    private Animation glowAnimation;
    private Animation coverAnimation;

    private MediaPlayer mediaPlayer;
    private Track selectedTrack;
    private Track currentTrack;
    private List<Track> libraryTracks = new ArrayList<>();
    private List<Track> displayedTracks = new ArrayList<>();
    private List<Track> currentQueue = new ArrayList<>();
    private int currentQueueIndex = -1;
    private boolean isSeeking = false;
    private boolean showFavoritesOnly = false;
    private boolean refreshingLibrary = false;
    private boolean shuffleEnabled = false;
    private boolean repeatEnabled = false;
    private boolean trackPageVisible = false;

    private double dragOffsetX;
    private double dragOffsetY;

    public MainController(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        createView();
        refreshLibrary();
    }

    public Parent getView() {
        return root;
    }

    private void createView() {
        root = new StackPane();
        root.getStyleClass().add("app-root");

        StackPane ambientLayer = createAmbientLayer();

        shell = new BorderPane();
        shell.getStyleClass().add("app-shell");

        contentFrame = new BorderPane();
        contentFrame.getStyleClass().add("content-frame");

        centerPane = new BorderPane();
        centerPane.getStyleClass().add("content-pane");

        shell.setTop(createWindowBar());
        contentFrame.setLeft(createSidebar());
        contentFrame.setCenter(centerPane);
        contentFrame.setRight(createRightPanel());
        contentFrame.setBottom(createPlayerBar());
        shell.setCenter(contentFrame);

        root.getChildren().addAll(ambientLayer, shell);
        initParallax();
        initDragAndDrop();
    }

    private StackPane createAmbientLayer() {
        StackPane ambient = new StackPane();
        ambient.setMouseTransparent(true);

        ambientOrbOne = createAmbientOrb("ambient-orb-one", 440, 440);
        ambientOrbTwo = createAmbientOrb("ambient-orb-two", 360, 360);
        ambientOrbThree = createAmbientOrb("ambient-orb-three", 280, 280);

        StackPane.setAlignment(ambientOrbOne, Pos.TOP_LEFT);
        StackPane.setAlignment(ambientOrbTwo, Pos.BOTTOM_RIGHT);
        StackPane.setAlignment(ambientOrbThree, Pos.CENTER);

        StackPane.setMargin(ambientOrbOne, new Insets(-120, 0, 0, -120));
        StackPane.setMargin(ambientOrbTwo, new Insets(0, -90, -100, 0));

        ambient.getChildren().addAll(ambientOrbOne, ambientOrbTwo, ambientOrbThree);
        return ambient;
    }

    private Region createAmbientOrb(String styleClass, double width, double height) {
        Region orb = new Region();
        orb.getStyleClass().add(styleClass);
        orb.setPrefSize(width, height);
        orb.setEffect(new GaussianBlur(120));
        return orb;
    }

    private Parent createWindowBar() {
        HBox titleBar = new HBox(14);
        titleBar.getStyleClass().add("window-bar");
        titleBar.setPadding(new Insets(14, 18, 14, 18));
        titleBar.setAlignment(Pos.CENTER_LEFT);

        Label brand = new Label("PulseBeat");
        brand.getStyleClass().add("window-brand");
        brand.setOnMouseClicked(e -> goHome());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER_RIGHT);

        Button minimizeButton = createWindowControl("-", "window-minimize");
        Button maximizeButton = createWindowControl("□", "window-maximize");
        Button closeButton = createWindowControl("X", "window-close");

        minimizeButton.setOnAction(e -> stage.setIconified(true));
        maximizeButton.setOnAction(e -> stage.setMaximized(!stage.isMaximized()));
        closeButton.setOnAction(e -> stage.close());

        controls.getChildren().addAll(minimizeButton, maximizeButton, closeButton);
        titleBar.getChildren().addAll(brand, spacer, controls);

        titleBar.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX();
            dragOffsetY = e.getSceneY();
        });

        titleBar.setOnMouseDragged(e -> {
            if (!stage.isMaximized()) {
                stage.setX(e.getScreenX() - dragOffsetX);
                stage.setY(e.getScreenY() - dragOffsetY);
            }
        });

        return titleBar;
    }

    private Button createWindowControl(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("window-control", styleClass);
        return button;
    }

    private Parent createSidebar() {
        VBox sidebar = new VBox(16);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24));
        sidebar.setPrefWidth(230);

        Label collectionTitle = new Label("Library");
        collectionTitle.getStyleClass().add("sidebar-title");

        Label greeting = new Label(currentUser.getLogin());
        greeting.getStyleClass().add("sidebar-caption");

        Button libraryButton = createSidebarButton("Коллекция");
        Button favoritesButton = createSidebarButton("Любимые");
        Button importButton = createSidebarButton("Импорт");
        Button refreshButton = createSidebarButton("Обновить");

        libraryButton.setOnAction(e -> {
            showFavoritesOnly = false;
            refreshLibrary();
        });

        favoritesButton.setOnAction(e -> {
            showFavoritesOnly = true;
            refreshLibrary();
        });

        importButton.setOnAction(e -> importMusic());
        refreshButton.setOnAction(e -> refreshLibrary());

        VBox actions = new VBox(10, libraryButton, favoritesButton, importButton, refreshButton);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(collectionTitle, greeting, actions, spacer);
        return sidebar;
    }

    private Button createSidebarButton(String title) {
        Button button = new Button(title);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        return button;
    }

    private Parent createRightPanel() {
        VBox panel = new VBox(18);
        panel.getStyleClass().add("details-panel");
        panel.setPadding(new Insets(24));
        panel.setPrefWidth(330);

        Label panelTitle = new Label("Now Playing");
        panelTitle.getStyleClass().add("panel-title");

        VBox spotlightCard = new VBox(12);
        spotlightCard.getStyleClass().add("glass-card");

        spotlightTitleLabel = new Label("Выбери трек");
        spotlightTitleLabel.getStyleClass().add("details-title");
        spotlightTitleLabel.setWrapText(true);

        spotlightArtistLabel = new Label("Открой карточку или включи трек");
        spotlightArtistLabel.getStyleClass().add("details-subtitle");
        spotlightArtistLabel.setWrapText(true);

        spotlightAlbumLabel = new Label("Album: -");
        spotlightAlbumLabel.getStyleClass().add("details-meta");

        spotlightRatingLabel = new Label("Rating: -");
        spotlightRatingLabel.getStyleClass().add("details-meta");

        HBox spotlightActions = new HBox(10);
        Button openTrackPageButton = new Button("Открыть");
        openTrackPageButton.getStyleClass().add("accent-button");
        openTrackPageButton.setOnAction(e -> {
            if (selectedTrack != null) {
                showTrackPage(selectedTrack);
            }
        });

        spotlightFavoriteButton = new Button("В избранное");
        spotlightFavoriteButton.getStyleClass().add("glass-button");
        spotlightFavoriteButton.setOnAction(e -> {
            if (selectedTrack != null) {
                toggleFavorite(selectedTrack);
            }
        });

        spotlightActions.getChildren().addAll(openTrackPageButton, spotlightFavoriteButton);
        spotlightCard.getChildren().addAll(
                spotlightTitleLabel,
                spotlightArtistLabel,
                spotlightAlbumLabel,
                spotlightRatingLabel,
                spotlightActions
        );

        Label queuePanelTitle = new Label("Queue");
        queuePanelTitle.getStyleClass().add("panel-title");

        queueTitleLabel = new Label("Очередь пока пустая");
        queueTitleLabel.getStyleClass().add("queue-title");

        queueMetaLabel = new Label("Запусти композицию, чтобы собрать очередь.");
        queueMetaLabel.getStyleClass().add("details-subtitle");
        queueMetaLabel.setWrapText(true);

        queueListView = new ListView<>();
        queueListView.getStyleClass().add("queue-list");
        queueListView.setPrefHeight(320);
        queueListView.setOnMouseClicked(e -> {
            int index = queueListView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < currentQueue.size()) {
                playTrack(currentQueue.get(index), currentQueue);
            }
        });

        panel.getChildren().addAll(panelTitle, spotlightCard, queuePanelTitle, queueTitleLabel, queueMetaLabel, queueListView);
        return panel;
    }

    private Parent createPlayerBar() {
        BorderPane playerBar = new BorderPane();
        playerBar.getStyleClass().add("player-bar");
        playerBar.setPadding(new Insets(18, 24, 20, 24));

        VBox currentInfo = new VBox(4);
        currentTrackTitleLabel = new Label("Ничего не играет");
        currentTrackTitleLabel.getStyleClass().add("player-track-title");
        currentTrackArtistLabel = new Label("Запусти трек");
        currentTrackArtistLabel.getStyleClass().add("player-track-subtitle");
        currentInfo.getChildren().addAll(currentTrackTitleLabel, currentTrackArtistLabel);

        VBox controls = new VBox(12);
        controls.setAlignment(Pos.CENTER);

        HBox buttonsRow = new HBox(10);
        buttonsRow.setAlignment(Pos.CENTER);

        shuffleButton = createTransportButton("⇄", "transport-button");
        Button previousButton = createTransportButton("⏮", "transport-button");
        playPauseButton = createTransportButton("▶", "transport-primary");
        Button nextButton = createTransportButton("⏭", "transport-button");
        repeatButton = createTransportButton("↺", "transport-button");

        shuffleButton.setOnAction(e -> toggleShuffle());
        previousButton.setOnAction(e -> playPreviousTrack());
        playPauseButton.setOnAction(e -> togglePlayPause());
        nextButton.setOnAction(e -> playNextTrack());
        repeatButton.setOnAction(e -> toggleRepeat());

        buttonsRow.getChildren().addAll(shuffleButton, previousButton, playPauseButton, nextButton, repeatButton);

        HBox timelineRow = new HBox(12);
        timelineRow.setAlignment(Pos.CENTER);

        progressSlider = new Slider();
        progressSlider.getStyleClass().add("player-slider");
        HBox.setHgrow(progressSlider, Priority.ALWAYS);

        timeLabel = new Label("00:00 / 00:00");
        timeLabel.getStyleClass().add("player-time");

        progressSlider.setOnMousePressed(e -> isSeeking = true);
        progressSlider.setOnMouseReleased(e -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
            }
            isSeeking = false;
        });

        timelineRow.getChildren().addAll(progressSlider, timeLabel);
        controls.getChildren().addAll(buttonsRow, timelineRow);

        HBox volumeBox = new HBox(10);
        volumeBox.setAlignment(Pos.CENTER_RIGHT);

        Label volumeLabel = new Label("Звук");
        volumeLabel.getStyleClass().add("player-time");

        volumeSlider = new Slider(0, 100, 70);
        volumeSlider.getStyleClass().add("player-slider");
        volumeSlider.setPrefWidth(160);
        volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
            }
        });

        volumeBox.getChildren().addAll(volumeLabel, volumeSlider);

        playerBar.setLeft(currentInfo);
        playerBar.setCenter(controls);
        playerBar.setRight(volumeBox);

        syncTransportState();
        return playerBar;
    }

    private Button createTransportButton(String symbol, String styleClass) {
        Button button = new Button(symbol);
        button.getStyleClass().add(styleClass);
        return button;
    }

    private void showLibraryView() {
        trackPageVisible = false;

        VBox page = new VBox(20);
        page.getStyleClass().add("content-pane");
        page.setPadding(new Insets(26));

        HBox hero = new HBox(24);
        hero.getStyleClass().add("hero-card");
        hero.setPadding(new Insets(24));
        hero.setAlignment(Pos.CENTER_LEFT);

        VBox copy = new VBox(10);
        copy.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(copy, Priority.ALWAYS);

        sectionBadge = new Label(showFavoritesOnly ? "Favourite" : "Library");
        sectionBadge.getStyleClass().add("badge-label");

        heroTitle = new Label(showFavoritesOnly ? "Любимые треки" : "Коллекция");
        heroTitle.getStyleClass().add("hero-title");

        HBox statsRow = new HBox(12);
        totalTracksLabel = createStatChip();
        favoriteCountLabel = createStatChip();
        statsRow.getChildren().addAll(totalTracksLabel, favoriteCountLabel);

        copy.getChildren().addAll(sectionBadge, heroTitle, statsRow);
        hero.getChildren().addAll(copy, createReactiveShowcase(selectedTrack != null ? selectedTrack : currentTrack, false));

        HBox toolbar = new HBox(12);
        toolbar.getStyleClass().add("toolbar-row");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Ищи по названию, артисту или альбому");
        searchField.getStyleClass().add("search-field");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!refreshingLibrary) {
                refreshLibrary();
            }
        });

        toolbar.getChildren().add(searchField);

        Label listTitle = new Label(showFavoritesOnly ? "Любимые треки" : "Вся библиотека");
        listTitle.getStyleClass().add("section-title");

        trackContainer = new VBox(12);

        ScrollPane scrollPane = new ScrollPane(trackContainer);
        scrollPane.getStyleClass().add("track-scroll");
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        page.getChildren().addAll(hero, toolbar, listTitle, scrollPane);
        centerPane.setCenter(page);
    }

    private Label createStatChip() {
        Label chip = new Label();
        chip.getStyleClass().add("stat-chip");
        return chip;
    }

    private Parent createReactiveShowcase(Track track, boolean compact) {
        visualizerBars.clear();

        VBox showcase = new VBox(compact ? 10 : 14);
        showcase.getStyleClass().add(compact ? "showcase-compact" : "showcase-large");
        showcase.setAlignment(Pos.CENTER);

        StackPane stageVisual = new StackPane();
        stageVisual.setPrefSize(compact ? 220 : 280, compact ? 220 : 280);

        Region glow = new Region();
        glow.getStyleClass().add(compact ? "music-glow-compact" : "music-glow");
        glow.setEffect(new GaussianBlur(compact ? 55 : 80));

        StackPane cover = new StackPane();
        cover.getStyleClass().add(compact ? "music-cover-compact" : "music-cover");

        Label monogram = new Label(buildMonogram(track));
        monogram.getStyleClass().add("cover-monogram");
        cover.getChildren().add(monogram);

        stageVisual.getChildren().addAll(glow, cover);

        HBox visualizer = new HBox(compact ? 6 : 8);
        visualizer.setAlignment(Pos.CENTER);
        visualizer.getStyleClass().add("visualizer-row");

        int barsCount = compact ? 10 : 14;
        for (int i = 0; i < barsCount; i++) {
            Region bar = new Region();
            bar.getStyleClass().add("eq-bar");
            bar.setPrefSize(compact ? 8 : 10, compact ? 24 : 28);
            bar.setScaleY(0.65 + random.nextDouble() * 0.4);
            visualizerBars.add(bar);
            visualizer.getChildren().add(bar);
        }

        showcase.getChildren().addAll(stageVisual, visualizer);

        animatedGlow = glow;
        animatedCover = cover;
        startReactiveAnimations();
        return showcase;
    }

    private String buildMonogram(Track track) {
        String source = track == null ? "PulseBeat" : safeText(track.getTitle());
        String[] parts = source.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (!part.isBlank()) {
                builder.append(Character.toUpperCase(part.charAt(0)));
            }
            if (builder.length() == 2) {
                break;
            }
        }
        if (builder.isEmpty()) {
            builder.append("PB");
        }
        return builder.toString();
    }

    private void startReactiveAnimations() {
        if (coverAnimation != null) {
            coverAnimation.stop();
        }
        if (glowAnimation != null) {
            glowAnimation.stop();
        }
        if (visualizerTimeline != null) {
            visualizerTimeline.stop();
        }

        if (animatedCover != null) {
            ScaleTransition coverPulse = new ScaleTransition(Duration.seconds(currentTrack == null ? 4.2 : 2.7), animatedCover);
            coverPulse.setFromX(1.0);
            coverPulse.setFromY(1.0);
            coverPulse.setToX(currentTrack == null ? 1.02 : 1.045);
            coverPulse.setToY(currentTrack == null ? 1.02 : 1.045);
            coverPulse.setAutoReverse(true);
            coverPulse.setCycleCount(Animation.INDEFINITE);
            coverPulse.play();
            coverAnimation = coverPulse;
        }

        if (animatedGlow != null) {
            ScaleTransition glowPulse = new ScaleTransition(Duration.seconds(currentTrack == null ? 4.8 : 3.2), animatedGlow);
            glowPulse.setFromX(1.0);
            glowPulse.setFromY(1.0);
            glowPulse.setToX(currentTrack == null ? 1.05 : 1.10);
            glowPulse.setToY(currentTrack == null ? 1.05 : 1.10);
            glowPulse.setAutoReverse(true);
            glowPulse.setCycleCount(Animation.INDEFINITE);
            glowPulse.play();
            glowAnimation = glowPulse;
        }

        visualizerTimeline = new Timeline(new KeyFrame(Duration.millis(360), e -> updateVisualizerBars()));
        visualizerTimeline.setCycleCount(Animation.INDEFINITE);
        visualizerTimeline.play();
    }

    private void updateVisualizerBars() {
        double base = mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING ? 1.05 : 0.45;
        for (Region bar : visualizerBars) {
            bar.setScaleY(0.65 + random.nextDouble() * base);
            bar.setOpacity(0.60 + random.nextDouble() * 0.22);
        }
    }

    private void showTrackPage(Track track) {
        trackPageVisible = true;
        selectedTrack = track;

        VBox page = new VBox(20);
        page.getStyleClass().add("content-pane");
        page.setPadding(new Insets(26));

        Button backButton = new Button("Вернуться");
        backButton.getStyleClass().add("glass-button");
        backButton.setOnAction(e -> refreshLibrary());

        VBox hero = new VBox(14);
        hero.getStyleClass().add("track-page-hero");
        hero.setPadding(new Insets(24));

        Label title = new Label(track.getTitle());
        title.getStyleClass().add("track-page-title");
        title.setWrapText(true);

        Label meta = new Label(safeText(track.getArtist()) + " • " + safeText(track.getAlbum()));
        meta.getStyleClass().add("track-page-subtitle");

        Label rating = new Label(String.format(Locale.US, "Средняя оценка: %.1f", reviewDAO.getAverageRatingForTrack(track.getId())));
        rating.getStyleClass().add("track-page-subtitle");

        HBox actionRow = new HBox(12);
        Button playButton = new Button("Слушать");
        playButton.getStyleClass().add("accent-button");
        playButton.setOnAction(e -> playTrack(track, displayedTracks.isEmpty() ? trackRepository.getAllTracks() : displayedTracks));

        Button favoriteButton = new Button(favoriteDAO.isFavorite(currentUser.getId(), track.getId()) ? "Убрать из избранного" : "В избранное");
        favoriteButton.getStyleClass().add("glass-button");
        favoriteButton.setOnAction(e -> {
            toggleFavorite(track);
            favoriteButton.setText(favoriteDAO.isFavorite(currentUser.getId(), track.getId()) ? "Убрать из избранного" : "В избранное");
        });

        Button editButton = new Button("Edit metadata");
        editButton.getStyleClass().add("glass-button");
        editButton.setOnAction(e -> showEditTrackDialog(track));

        actionRow.getChildren().addAll(playButton, favoriteButton, editButton);
        hero.getChildren().addAll(title, meta, rating, actionRow);

        HBox body = new HBox(20);
        VBox.setVgrow(body, Priority.ALWAYS);

        VBox reviewsCard = new VBox(14);
        reviewsCard.getStyleClass().add("glass-card");
        HBox.setHgrow(reviewsCard, Priority.ALWAYS);

        Label reviewsTitle = new Label("Рецензии");
        reviewsTitle.getStyleClass().add("section-title");

        trackReviewsList = new ListView<>();
        trackReviewsList.getStyleClass().add("reviews-list");
        VBox.setVgrow(trackReviewsList, Priority.ALWAYS);

        reviewsCard.getChildren().addAll(reviewsTitle, trackReviewsList);

        VBox formCard = new VBox(14);
        formCard.getStyleClass().add("glass-card");
        formCard.setPrefWidth(360);

        Label formTitle = new Label("Твоя рецензия");
        formTitle.getStyleClass().add("section-title");

        reviewRatingSlider = new Slider(1, 5, 5);
        reviewRatingSlider.getStyleClass().add("player-slider");
        reviewRatingSlider.setMajorTickUnit(1);
        reviewRatingSlider.setMinorTickCount(0);
        reviewRatingSlider.setSnapToTicks(true);
        reviewRatingSlider.setShowTickLabels(true);
        reviewRatingSlider.setShowTickMarks(true);

        reviewArea = new TextArea();
        reviewArea.getStyleClass().add("review-area");
        reviewArea.setPromptText("Напиши рецензию к этому треку...");
        reviewArea.setWrapText(true);
        reviewArea.setPrefRowCount(8);

        Button submitButton = new Button("Опубликовать");
        submitButton.getStyleClass().add("accent-button");
        submitButton.setMaxWidth(Double.MAX_VALUE);
        submitButton.setOnAction(e -> submitReview());

        formCard.getChildren().addAll(formTitle, reviewRatingSlider, reviewArea, submitButton);

        body.getChildren().addAll(reviewsCard, formCard);
        page.getChildren().addAll(backButton, hero, body);

        centerPane.setCenter(page);
        updateTrackPageReviews();
        updateSpotlight();
    }

    private void refreshLibrary() {
        if (refreshingLibrary) {
            return;
        }

        refreshingLibrary = true;
        String currentQuery = searchField == null ? "" : searchField.getText();

        showLibraryView();
        searchField.setText(currentQuery);

        libraryTracks = trackRepository.getAllTracks();
        displayedTracks = buildDisplayedTracks();
        updateHero();

        trackContainer.getChildren().clear();
        if (displayedTracks.isEmpty()) {
            trackContainer.getChildren().add(createEmptyState());
        } else {
            for (int i = 0; i < displayedTracks.size(); i++) {
                trackContainer.getChildren().add(createTrackCard(displayedTracks.get(i), i));
            }
        }

        if (selectedTrack == null && !displayedTracks.isEmpty()) {
            selectedTrack = displayedTracks.get(0);
        }

        updateSpotlight();
        refreshQueueView();
        refreshingLibrary = false;
    }

    private List<Track> buildDisplayedTracks() {
        List<Track> tracks = new ArrayList<>(libraryTracks);
        String query = searchField == null ? "" : searchField.getText();

        if (query != null && !query.isBlank()) {
            String normalized = query.toLowerCase(Locale.ROOT);
            tracks = tracks.stream()
                    .filter(track -> containsIgnoreCase(track.getTitle(), normalized)
                            || containsIgnoreCase(track.getArtist(), normalized)
                            || containsIgnoreCase(track.getAlbum(), normalized))
                    .collect(Collectors.toList());
        }

        if (showFavoritesOnly) {
            tracks = tracks.stream()
                    .filter(track -> favoriteDAO.isFavorite(currentUser.getId(), track.getId()))
                    .collect(Collectors.toList());
        }

        return tracks;
    }

    private Parent createEmptyState() {
        VBox emptyState = new VBox(10);
        emptyState.getStyleClass().add("glass-card");
        emptyState.setPadding(new Insets(30));
        emptyState.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Библиотека пока пустая");
        title.getStyleClass().add("empty-title");

        Label subtitle = new Label("Добавь папку с MP3, и здесь появятся треки.");
        subtitle.getStyleClass().add("empty-subtitle");
        subtitle.setWrapText(true);

        Button importButton = new Button("Добавить музыку");
        importButton.getStyleClass().add("accent-button");
        importButton.setOnAction(e -> importMusic());

        emptyState.getChildren().addAll(title, subtitle, importButton);
        return emptyState;
    }

    private Parent createTrackCard(Track track, int index) {
        HBox card = new HBox(16);
        card.getStyleClass().add("track-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(18));

        Label indexBadge = new Label(String.format("%02d", index + 1));
        indexBadge.getStyleClass().add("index-badge");

        VBox meta = new VBox(6);
        Label title = new Label(track.getTitle());
        title.getStyleClass().add("track-title");
        Label subtitle = new Label(safeText(track.getArtist()) + " • " + safeText(track.getAlbum()));
        subtitle.getStyleClass().add("track-subtitle");
        meta.getChildren().addAll(title, subtitle);
        HBox.setHgrow(meta, Priority.ALWAYS);

        boolean favorite = favoriteDAO.isFavorite(currentUser.getId(), track.getId());
        Label status = new Label(favorite ? "Liked" : "Library");
        status.getStyleClass().add(favorite ? "liked-pill" : "library-pill");

        Button playButton = new Button("▶");
        playButton.getStyleClass().add("mini-icon-button");
        playButton.setOnAction(e -> playTrack(track, displayedTracks));

        Button openButton = new Button("Открыть");
        openButton.getStyleClass().add("glass-button");
        openButton.setOnAction(e -> showTrackPage(track));

        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("glass-button");
        editButton.setOnAction(e -> showEditTrackDialog(track));

        card.setOnMouseClicked(e -> {
            if (!(e.getTarget() instanceof Button)) {
                showTrackPage(track);
            }
        });

        card.getChildren().addAll(indexBadge, meta, status, playButton, openButton, editButton);
        return card;
    }

    private void showEditTrackDialog(Track track) {
        Dialog<Track> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.setTitle("Edit track metadata");
        dialog.setHeaderText("Track metadata");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        dialog.getDialogPane().getStyleClass().add("metadata-dialog");
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/style/styles.css").toExternalForm()
        );

        TextField titleField = new TextField(track.getTitle());
        TextField artistField = new TextField(track.getArtist());
        TextField albumField = new TextField(track.getAlbum());
        titleField.setPromptText("Title");
        artistField.setPromptText("Artist");
        albumField.setPromptText("Album");

        GridPane form = new GridPane();
        form.getStyleClass().add("metadata-form");
        form.setHgap(10);
        form.setVgap(10);

        Label titleLabel = createMetadataDialogLabel("Title");
        Label artistLabel = createMetadataDialogLabel("Artist");
        Label albumLabel = createMetadataDialogLabel("Album");
        form.addRow(0, titleLabel, titleField);
        form.addRow(1, artistLabel, artistField);
        form.addRow(2, albumLabel, albumField);

        dialog.getDialogPane().setContent(form);
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.getStyleClass().add("accent-button");
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelButtonType);
        cancelButton.getStyleClass().add("glass-button");

        dialog.setResultConverter(button -> {
            if (button != saveButtonType) {
                return null;
            }

            String title = titleField.getText() == null ? "" : titleField.getText().trim();
            if (title.isEmpty()) {
                showError("Title cannot be empty.");
                return null;
            }

            Track updatedTrack = new Track(
                    track.getId(),
                    title,
                    normalizeMetadata(artistField.getText()),
                    normalizeMetadata(albumField.getText()),
                    track.getFilePath()
            );
            return updatedTrack;
        });

        Optional<Track> result = dialog.showAndWait();
        result.ifPresent(this::saveTrackMetadata);
    }

    private Label createMetadataDialogLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("metadata-label");
        return label;
    }

    private void saveTrackMetadata(Track updatedTrack) {
        trackRepository.updateTrack(updatedTrack);
        selectedTrack = updatedTrack;

        if (currentTrack != null && currentTrack.getId() == updatedTrack.getId()) {
            currentTrack = updatedTrack;
            updateNowPlaying();
        }

        libraryTracks = trackRepository.getAllTracks();
        displayedTracks = buildDisplayedTracks();
        updateHero();
        updateSpotlight();
        refreshQueueView();

        if (trackPageVisible) {
            showTrackPage(updatedTrack);
        } else if (trackContainer != null) {
            trackContainer.getChildren().clear();
            if (displayedTracks.isEmpty()) {
                trackContainer.getChildren().add(createEmptyState());
            } else {
                for (int i = 0; i < displayedTracks.size(); i++) {
                    trackContainer.getChildren().add(createTrackCard(displayedTracks.get(i), i));
                }
            }
        }
    }

    private String normalizeMetadata(String value) {
        return value == null || value.isBlank() ? "Unknown" : value.trim();
    }

    private void submitReview() {
        if (selectedTrack == null) {
            showError("Сначала открой страницу нужного трека.");
            return;
        }

        String text = reviewArea.getText() == null ? "" : reviewArea.getText().trim();
        if (text.isEmpty()) {
            showError("Текст рецензии не должен быть пустым.");
            return;
        }

        Review review = new Review();
        review.setUserId(currentUser.getId());
        review.setTrackId(selectedTrack.getId());
        review.setAlbum(selectedTrack.getAlbum());
        review.setRating((int) Math.round(reviewRatingSlider.getValue()));
        review.setReviewText(text);

        reviewDAO.addReview(review);
        reviewArea.clear();
        updateTrackPageReviews();
        updateSpotlight();
    }

    private void updateTrackPageReviews() {
        if (trackReviewsList == null || selectedTrack == null) {
            return;
        }

        List<String> reviews = reviewDAO.getReviewsByTrack(selectedTrack.getId()).stream()
                .map(review -> review.getRating() + "/5 • " + review.getReviewText())
                .collect(Collectors.toList());

        if (reviews.isEmpty()) {
            trackReviewsList.getItems().setAll("Пока рецензий нет.");
        } else {
            trackReviewsList.getItems().setAll(reviews);
        }
    }

    private void updateHero() {
        if (sectionBadge == null) {
            return;
        }

        int totalCount = libraryTracks.size();
        long favoriteCount = libraryTracks.stream()
                .filter(track -> favoriteDAO.isFavorite(currentUser.getId(), track.getId()))
                .count();

        sectionBadge.setText(showFavoritesOnly ? "Favourite" : "Library");
        heroTitle.setText(showFavoritesOnly ? "Любимые треки" : "Коллекция");
        totalTracksLabel.setText("Треков: " + totalCount);
        favoriteCountLabel.setText("Любимых: " + favoriteCount);
    }

    private void updateSpotlight() {
        if (selectedTrack == null) {
            spotlightTitleLabel.setText("Выбери трек");
            spotlightArtistLabel.setText("Открой карточку или включи трек");
            spotlightAlbumLabel.setText("Album: -");
            spotlightRatingLabel.setText("Rating: -");
            spotlightFavoriteButton.setText("В избранное");
            return;
        }

        spotlightTitleLabel.setText(selectedTrack.getTitle());
        spotlightArtistLabel.setText(safeText(selectedTrack.getArtist()));
        spotlightAlbumLabel.setText("Album: " + safeText(selectedTrack.getAlbum()));
        spotlightRatingLabel.setText(String.format(Locale.US, "Rating: %.1f", reviewDAO.getAverageRatingForTrack(selectedTrack.getId())));
        spotlightFavoriteButton.setText(
                favoriteDAO.isFavorite(currentUser.getId(), selectedTrack.getId())
                        ? "Убрать из избранного"
                        : "В избранное"
        );
    }

    private void refreshQueueView() {
        if (currentQueue.isEmpty()) {
            queueTitleLabel.setText("Очередь пока пустая");
            queueMetaLabel.setText("Запусти композицию, чтобы собрать очередь.");
            queueListView.getItems().clear();
            return;
        }

        queueTitleLabel.setText(currentTrack == null ? "Активная очередь" : currentTrack.getTitle());
        queueMetaLabel.setText("Треков в очереди: " + currentQueue.size());
        queueListView.getItems().setAll(currentQueue.stream()
                .map(track -> safeText(track.getArtist()) + " • " + track.getTitle())
                .collect(Collectors.toList()));

        if (currentQueueIndex >= 0 && currentQueueIndex < currentQueue.size()) {
            queueListView.getSelectionModel().select(currentQueueIndex);
            queueListView.scrollTo(currentQueueIndex);
        }
    }

    private void playTrack(Track track, List<Track> sourceQueue) {
        if (track == null) {
            return;
        }

        if (track.getFilePath() == null || track.getFilePath().isBlank()) {
            showError("У трека не указан путь к файлу.");
            return;
        }

        File audioFile = new File(track.getFilePath());
        if (!audioFile.exists()) {
            showError("Файл не найден: " + track.getFilePath());
            return;
        }

        if (sourceQueue != null && !sourceQueue.isEmpty()) {
            currentQueue = new ArrayList<>(sourceQueue);
            currentQueueIndex = findTrackIndex(currentQueue, track);
        }

        selectedTrack = track;
        currentTrack = track;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        mediaPlayer = new MediaPlayer(new Media(audioFile.toURI().toString()));
        mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);

        mediaPlayer.setOnReady(() -> {
            progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            updateTimeLabel(mediaPlayer.getCurrentTime().toSeconds(), mediaPlayer.getTotalDuration().toSeconds());
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldValue, newValue) -> {
            if (!isSeeking) {
                progressSlider.setValue(newValue.toSeconds());
                updateTimeLabel(newValue.toSeconds(), mediaPlayer.getTotalDuration().toSeconds());
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            if (repeatEnabled) {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            } else {
                playNextTrack();
            }
        });

        mediaPlayer.play();
        playPauseButton.setText("⏸");
        startReactiveAnimations();
        syncTransportState();
        updateNowPlaying();
        updateSpotlight();
        refreshQueueView();
    }

    private void togglePlayPause() {
        if (mediaPlayer == null) {
            if (selectedTrack != null) {
                playTrack(selectedTrack, displayedTracks.isEmpty() ? trackRepository.getAllTracks() : displayedTracks);
            }
            return;
        }

        MediaPlayer.Status status = mediaPlayer.getStatus();
        if (status == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseButton.setText("▶");
        } else {
            mediaPlayer.play();
            playPauseButton.setText("⏸");
        }
    }

    private void playNextTrack() {
        if (currentQueue.isEmpty()) {
            return;
        }

        PlaybackOrderStrategy strategy = applicationFactory.createPlaybackOrderStrategy(shuffleEnabled, repeatEnabled);
        currentQueueIndex = strategy.nextIndex(currentQueueIndex, currentQueue.size());

        if (currentQueueIndex >= 0) {
            playTrack(currentQueue.get(currentQueueIndex), currentQueue);
        }
    }

    private void playPreviousTrack() {
        if (currentQueue.isEmpty()) {
            return;
        }

        PlaybackOrderStrategy strategy = applicationFactory.createPlaybackOrderStrategy(shuffleEnabled, repeatEnabled);
        currentQueueIndex = strategy.previousIndex(currentQueueIndex, currentQueue.size());

        if (currentQueueIndex >= 0) {
            playTrack(currentQueue.get(currentQueueIndex), currentQueue);
        }
    }

    private void toggleShuffle() {
        shuffleEnabled = !shuffleEnabled;
        syncTransportState();
    }

    private void toggleRepeat() {
        repeatEnabled = !repeatEnabled;
        syncTransportState();
    }

    private void updateNowPlaying() {
        if (currentTrack == null) {
            currentTrackTitleLabel.setText("Ничего не играет");
            currentTrackArtistLabel.setText("Запусти трек");
            return;
        }

        currentTrackTitleLabel.setText(currentTrack.getTitle());
        currentTrackArtistLabel.setText(safeText(currentTrack.getArtist()) + " • " + safeText(currentTrack.getAlbum()));
    }

    private void toggleFavorite(Track track) {
        try {
            if (favoriteDAO.isFavorite(currentUser.getId(), track.getId())) {
                favoriteDAO.removeFavorite(currentUser.getId(), track.getId());
            } else {
                favoriteDAO.addToFavorites(currentUser.getId(), track.getId());
            }

            displayedTracks = buildDisplayedTracks();
            updateHero();
            updateSpotlight();
            refreshQueueView();

            if (trackContainer != null) {
                trackContainer.getChildren().clear();
                if (displayedTracks.isEmpty()) {
                    trackContainer.getChildren().add(createEmptyState());
                } else {
                    for (int i = 0; i < displayedTracks.size(); i++) {
                        trackContainer.getChildren().add(createTrackCard(displayedTracks.get(i), i));
                    }
                }
            }
        } catch (Exception ex) {
            showError("Не удалось обновить избранное: " + ex.getMessage());
        }
    }

    private void importMusic() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Выберите папку с музыкой");
        File folder = chooser.showDialog(stage);
        if (folder != null) {
            scanMusic(folder);
            refreshLibrary();
        }
    }

    private void initDragAndDrop() {
        root.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles() && dragboard.getFiles().stream().anyMatch(this::isSupportedDropItem)) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        root.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean imported = false;

            if (dragboard.hasFiles()) {
                for (File file : dragboard.getFiles()) {
                    imported = importDroppedItem(file) || imported;
                }
            }

            if (imported) {
                refreshLibrary();
            }

            event.setDropCompleted(imported);
            event.consume();
        });
    }

    private boolean importDroppedItem(File file) {
        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            return scanMusic(file);
        }

        return importAudioFile(file);
    }

    private boolean isSupportedDropItem(File file) {
        return file != null && (file.isDirectory() || isMp3(file));
    }

    private boolean scanMusic(File folder) {
        File[] files = folder.listFiles();
        if (files == null) {
            return false;
        }

        boolean imported = false;
        for (File file : files) {
            if (file.isDirectory()) {
                imported = scanMusic(file) || imported;
                continue;
            }

            imported = importAudioFile(file) || imported;
        }

        return imported;
    }

    private boolean importAudioFile(File file) {
        if (!isMp3(file) || trackRepository.existsByFilePath(file.getAbsolutePath())) {
            return false;
        }

        trackRepository.addTrack(metadataReader.read(file));
        return true;
    }

    private boolean isMp3(File file) {
        return file != null && file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".mp3");
    }

    private int findTrackIndex(List<Track> tracks, Track target) {
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getId() == target.getId()) {
                return i;
            }
        }
        return 0;
    }

    private void updateTimeLabel(double currentSeconds, double totalSeconds) {
        timeLabel.setText(format(currentSeconds) + " / " + format(totalSeconds));
    }

    private String format(double seconds) {
        int mins = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return safeText(value).toLowerCase(Locale.ROOT).contains(query);
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "Unknown" : value;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Ошибка");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void goHome() {
        showFavoritesOnly = false;
        refreshLibrary();
    }

    private void syncTransportState() {
        updateToggleButtonState(shuffleButton, shuffleEnabled);
        updateToggleButtonState(repeatButton, repeatEnabled);
    }

    private void updateToggleButtonState(Button button, boolean active) {
        if (button == null) {
            return;
        }

        if (active) {
            if (!button.getStyleClass().contains("transport-active")) {
                button.getStyleClass().add("transport-active");
            }
        } else {
            button.getStyleClass().remove("transport-active");
        }
    }

    private void initParallax() {
        root.setOnMouseMoved(event -> {
            double xRatio = event.getSceneX() / Math.max(root.getWidth(), 1) - 0.5;
            double yRatio = event.getSceneY() / Math.max(root.getHeight(), 1) - 0.5;

            ambientOrbOne.setTranslateX(xRatio * 24);
            ambientOrbOne.setTranslateY(yRatio * 18);
            ambientOrbTwo.setTranslateX(-xRatio * 18);
            ambientOrbTwo.setTranslateY(-yRatio * 14);
            ambientOrbThree.setTranslateX(xRatio * 10);
            ambientOrbThree.setTranslateY(-yRatio * 8);
        });
    }
}
