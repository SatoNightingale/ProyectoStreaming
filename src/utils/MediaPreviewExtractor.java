package utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MediaPreviewExtractor {

    public static Image getAlbumArt(String path) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException{
        File musicFile = new File(path);
        AudioFile audiofile = AudioFileIO.readMagic(musicFile);
        Tag tag = audiofile.getTag();
        Artwork art = tag.getFirstArtwork();

        if(art != null){
            byte[] imgData = art.getBinaryData();
            InputStream is = new ByteArrayInputStream(imgData);
            Image img = new Image(is);
            return img;
        } else
            return null;
    }

	public static void getVideoThumbnail(String videoPath, OnVideoLoadCallback callback) {
		Media media = new Media(new File(videoPath).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		MediaView mediaView = new MediaView(mediaPlayer);
		
		mediaPlayer.setOnReady(() -> {
			mediaPlayer.seek(Duration.seconds(1));
		});

		mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
			if(newTime.greaterThanOrEqualTo(Duration.seconds(1))){
				WritableImage snapshot = mediaView.snapshot(null, null);
				callback.activate(snapshot);
				mediaPlayer.dispose();
			}
		});

		mediaPlayer.play();
	}

	public interface OnVideoLoadCallback{
		public void activate(Image img);
	}

    public static void setFitImageView(ImageView view, Image img){
        double width = img.getWidth();
        double height = img.getHeight();
        double aspectRatio = width / height;
        
        // Encajar alto o ancho
        if(width > view.getFitWidth()){
            width = view.getFitWidth();
            height = width / aspectRatio;
        }

        if(height > view.getFitHeight()){
            height = view.getFitHeight();
            width = aspectRatio * height;
        }

        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setImage(img);
    }
}
