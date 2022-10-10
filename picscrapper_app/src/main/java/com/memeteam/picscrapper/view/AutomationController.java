package com.memeteam.picscrapper.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.openqa.selenium.WebDriver;

import com.memeteam.picscrapper.App;
import com.memeteam.picscrapper.automation.Komixxy;
import com.memeteam.picscrapper.model.ScrapModel;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.scene.shape.Circle;
import javafx.beans.Observable;
import javafx.beans.InvalidationListener;

public class AutomationController extends App {

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
	
	public static WebDriver driver;
	
	NextSongBehaviors nextSongBehavior = NextSongBehaviors.ORDERED;
	
	Task<Void> labelTask = null;
	Thread labelThread = null;
	
	protected static Task<Void> automationTask = null;
	protected static Thread automationThread = null;
	
	public static List<String> messageList = new ArrayList<>();
	
	public void setApp(App app, Stage stage, ScrapModel scrapModel) { 
		this.app = app; 
		this.stage = stage;			
		this.scrapModel = scrapModel;	
		System.out.println(scrapModel.toString());
		
		progressTextArea.setEditable(false);
		
		initializePlayer();
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() { //adding onClose event - will trigger 'Are you sure?' dialog stage instead of casually closing the window.
			@Override
			public void handle(WindowEvent event) {
				try {
					Media sound = new Media(App.class.getClassLoader().getResource("sounds/exit.mp3").toURI().toString()); //getting the proper sound file.
					AudioClip audioClip = new AudioClip(sound.getSource()); //assign a sound as an audioClip.
					mediaPlayer.pause();
					audioClip.play();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}				
				ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
				ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
				Alert alert = new Alert(AlertType.CONFIRMATION, "", okButton, cancelButton);
				alert.setTitle("Exit");
				alert.setHeaderText(null);
				alert.setContentText("Are you sure?");
				alert.initStyle(StageStyle.UNDECORATED);
				
				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == okButton) {
					 handleStop();
					System.exit(0);
				} else {
					alert.close();
					mediaPlayer.play();
					event.consume();
				}
			}
		});
		
		switch(scrapModel.getWebsite().toLowerCase()) {
			case "komixxy":
				automationTask = Komixxy.startAutomation(scrapModel);
				break;
		}
		
		progressTextArea.textProperty().bind(automationTask.messageProperty());	
        
		automationThread = new Thread(automationTask);
		automationThread.setDaemon(true);
		automationThread.start();
	}	
	
	private void initializePlayer() {
		playButton.setShape(new Circle(5));
		InputStream songDirectoryAsInputStream = AutomationController.class.getClassLoader().getResourceAsStream("sounds/player");
		try {
			//If 0, then resource is unavailable - most probable scenario is: this application has been started from JAR file. Due to that, gripping a directory directly is impossible. 
			//It is needed to use JarFile and other support classes for having a grip on packed tunes.
			if(songDirectoryAsInputStream.available() == 0) {
				//getting a grip to caller JAR file.
				JarFile jarFile = new JarFile(AutomationController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
					
				//Iterate over every directory and file of caller JAR.
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
				    JarEntry entry = entries.nextElement();
				    //if song-directory is found, then begin a saving process.
				    if (entry.getName().contains("sounds/player")) {
				    	//Grab every file (excluding the directory itself).
				    	if(!entry.getName().equalsIgnoreCase("sounds/player/")) {
					        songFilesList.add(new Media(AutomationController.class.getClassLoader().getResource(entry.getName()).toURI().toString()));
				        	songNamesList.add(entry.getName().replaceAll(".mp3", "").replaceAll("sounds/player/", ""));
				    	}
				    }
				}
			} else {
				InputStreamReader streamReader = new InputStreamReader(songDirectoryAsInputStream, StandardCharsets.UTF_8);
				BufferedReader bufferedReader = new BufferedReader(streamReader);
				
				bufferedReader.lines().forEach(saveTheSong);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		if(automationThread.isAlive())
			automationThread.interrupt();
		if(driver != null) {
			try {
				driver.quit();
			} catch(Exception e) {
				System.out.println("Exception: " + e.getLocalizedMessage());
			}
		}
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
		 
		mediaPlayer = new MediaPlayer(currentSong); 
	    mediaPlayer.setAutoPlay(true);
	    mediaPlayer.setVolume(songVolume);
	    
	    mediaPlayer.setOnEndOfMedia(() -> { 
        	mediaPlayer.stop();
        	playMusic(1); 
    	});
	    
        mediaPlayer.play();        
        runLabelTask();
	}
	
	private void runLabelTask() {
		labelTask = new Task<Void>() {
			@Override
			public Void call() throws Exception {

				List<String> splitSongNameList = new ArrayList<>();
				splitSongNameList.addAll(Arrays.asList(songNamesList.get(currentSongIndex).split("")));
				splitSongNameList.addAll(Arrays.asList(" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "));
				
				updateMessage("Now playing: " + songNamesList.get(currentSongIndex));
								
				while(mediaPlayer.getStatus() == Status.UNKNOWN || mediaPlayer.getStatus() == Status.PLAYING || mediaPlayer.getStatus() == Status.PAUSED) {
					Thread.sleep(125);
					splitSongNameList.add(splitSongNameList.get(0)); //move the first element at the end of list.
					splitSongNameList.remove(0);
					
					updateMessage("Now playing: " + new String(String.join("", splitSongNameList)));
				}
				return null;
			}
        };
        
        songNameLabel.textProperty().bind(labelTask.messageProperty());
        songNameLabel.setEllipsisString("");
        
        labelThread = new Thread(labelTask);
        labelThread.setDaemon(true);
        labelThread.start();
	}
	
	private void playMusic(int indexValue) { //indexValue is for allowing the proper navigation via handlePrevious and handleNext methods. 1 for next, -1 for previous.		
		Status mediaStatus = mediaPlayer.getStatus();
		if(mediaStatus == Status.STOPPED || mediaStatus == Status.PLAYING || mediaStatus == Status.PAUSED) { //Stopping the song does not set up the STOPPED status instantly - need to check PLAYING status as well.
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
	
	protected static List<String> updateMessageStack(List<String> messageList, String newMessage) {
		messageList.add(newMessage);		
		if(messageList.size() > 20)
			messageList.remove(0);
		return messageList;
	}
}
