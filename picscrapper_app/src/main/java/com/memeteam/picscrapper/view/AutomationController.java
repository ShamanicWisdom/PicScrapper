package com.memeteam.picscrapper.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.memeteam.picscrapper.App;
import com.memeteam.picscrapper.model.ScrapModel;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.Media;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.shape.Circle;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;


public class AutomationController {

	Stage stage;
	
	private App app;
	
	ScrapModel scrapModel;
	
	List<Media> songFilesList = new ArrayList<>();
	List<String> songNamesList = new ArrayList<>();
	
	//For randomizing tracks properly.
	Random randomGenerator = new Random();
	int currentSongIndex = -1;
	
	@FXML
	TextArea progressTextArea;
	
	@FXML
	Button playButton;
	@FXML
	Button previousSongButton;
	@FXML
	Button nextSongButton;
	@FXML
	Button nextSongBehaviorButton;
		
	@FXML
	Label songNameLabel;
	
	@FXML
	Slider volumeSlider;
	
	MediaPlayer mediaPlayer;
	
	double songVolume = 0.25;
	
	enum NextSongBehaviors {
		ORDERED,
		REPEAT,
		RANDOM,
	}
	
	NextSongBehaviors nextSongBehavior = NextSongBehaviors.ORDERED;
		
	public void setApp(App app, Stage stage, ScrapModel scrapModel) { 
		this.app = app; 
		this.stage = stage;		
		this.scrapModel = scrapModel;	
		System.out.println(scrapModel.toString());
		initializePlayer();
	}	
	
	private void initializePlayer() {
		playButton.setShape(new Circle(5));
		InputStream songDirectory = AutomationController.class.getClassLoader().getResourceAsStream("sounds/player");
		InputStreamReader streamReader = new InputStreamReader(songDirectory, StandardCharsets.UTF_8);
		BufferedReader bufferedReader = new BufferedReader(streamReader);
		
		bufferedReader.lines().forEach(saveTheSong);
		
		getRandomTrackNumber();
		playTheCurrentSong();        
        
        volumeSlider.setValue(25);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
        	public void invalidated(Observable ov)
            {
                if (volumeSlider.isPressed()) {
                	songVolume = volumeSlider.getValue() / 100; //Volume value is set between 0.0 and 1.0 - slider has values between 0.0 and 100.0 - division by 0 is required.
                	mediaPlayer.setVolume(songVolume); 
                }
            }
        });
	}
	
	@FXML
	void handleStop() {
		mediaPlayer.dispose();
		app.showWelcome();
	}
		
	//Creating a custom Consumer event for lambda expression.
	Consumer<String> saveTheSong = new Consumer<String>() {
	    public void accept(String songName) {
	        try {
	        	songFilesList.add(new Media(AutomationController.class.getClassLoader().getResource("sounds/player/" + songName).toURI().toString()));
	        	songNamesList.add(songName.replaceAll(".mp3", ""));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
	    }
	};
	
	//Randomizing tracks.
	private int getRandomTrackNumber() {
		int randomNumber = randomGenerator.nextInt(songFilesList.size());
		if(currentSongIndex < 0)
			currentSongIndex = randomNumber;
		else {
			if(randomNumber == currentSongIndex) 
				getRandomTrackNumber();
			else 
				currentSongIndex = randomNumber;
		}
		return currentSongIndex;
	}
	
	private void playTheCurrentSong() {
		if(mediaPlayer != null)
			mediaPlayer.dispose();
		Media currentSong = songFilesList.get(currentSongIndex);
		songNameLabel.setText("Now playing: " + songNamesList.get(currentSongIndex));
		mediaPlayer = new MediaPlayer(currentSong); 
	    mediaPlayer.setAutoPlay(true);
	    mediaPlayer.setVolume(songVolume);
	    
	    mediaPlayer.setOnEndOfMedia(() -> { 
        	mediaPlayer.stop();
        	playMusic(1); 
    	});
	    
        mediaPlayer.play();
	}
	
	private void playMusic(int indexValue) { //indexValue is for allowing the proper navigation via handlePrevious and handleNext methods. 1 for next, -1 for previous.		
		Status mediaStatus = mediaPlayer.getStatus();
		if(mediaStatus == Status.STOPPED || mediaStatus == Status.PLAYING) { //Stopping the song does not set up the STOPPED status instantly - need to check PLAYING status as well.
			switch(nextSongBehavior) {
				case ORDERED: {
					currentSongIndex += indexValue;
					if(currentSongIndex == songFilesList.size()) 
						currentSongIndex = 0;
					if(currentSongIndex < 0) 
						currentSongIndex = songFilesList.size() - 1;
					playTheCurrentSong();
					break;
				}
				case REPEAT: {
					playTheCurrentSong();
					break;
				}
				case RANDOM: {
					getRandomTrackNumber();
					playTheCurrentSong();
					break;
				}
			}
		}
	}
	
	@FXML
	private void handlePlayPause() {
		Status mediaStatus = mediaPlayer.getStatus();
		if(mediaStatus == Status.PLAYING) {
			mediaPlayer.pause();
			playButton.setText("PA");
		} else {
			mediaPlayer.play();
			playButton.setText("PL");
			playMusic(1);
		}
	}
	
	@FXML
	private void handlePrevious() {
		mediaPlayer.stop();
		playMusic(-1);
	}
	
	@FXML
	private void handleNext() {
		mediaPlayer.stop();
		playMusic(1);
	}
	
	@FXML
	private void handleNextSongBehavior() {
		switch(nextSongBehavior) {
			case ORDERED: {
				nextSongBehavior = NextSongBehaviors.REPEAT;
				nextSongBehaviorButton.setText("RPT");
				break;
			}
			case REPEAT: {
				nextSongBehavior = NextSongBehaviors.RANDOM;
				nextSongBehaviorButton.setText("RND");
				break;
			}
			case RANDOM: {
				nextSongBehavior = NextSongBehaviors.ORDERED;
				nextSongBehaviorButton.setText("ORD");
				break;
			}
		}
	}
}
