package com.metadata;

import com.model.Track;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class TagTrackMetadataReader implements TrackMetadataReader {

    private final TrackMetadataReader fallback;

    public TagTrackMetadataReader(TrackMetadataReader fallback) {
        this.fallback = fallback;
    }

    @Override
    public Track read(File audioFile) {
        Track byFileName = fallback.read(audioFile);

        try {
            AudioFile source = AudioFileIO.read(audioFile);
            Tag tag = source.getTag();
            if (tag == null) {
                return byFileName;
            }

            String title = firstNotBlank(tag.getFirst(FieldKey.TITLE), byFileName.getTitle());
            String artist = firstNotBlank(tag.getFirst(FieldKey.ARTIST), byFileName.getArtist());
            String album = firstNotBlank(tag.getFirst(FieldKey.ALBUM), byFileName.getAlbum());

            return new Track(0, title, artist, album, audioFile.getAbsolutePath());
        } catch (Exception ex) {
            return byFileName;
        }
    }

    private String firstNotBlank(String value, String fallbackValue) {
        return value == null || value.isBlank() ? fallbackValue : value.trim();
    }
}
