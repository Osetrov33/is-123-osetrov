package com.metadata;

import com.model.Track;

import java.io.File;

public interface TrackMetadataReader {

    Track read(File audioFile);
}
