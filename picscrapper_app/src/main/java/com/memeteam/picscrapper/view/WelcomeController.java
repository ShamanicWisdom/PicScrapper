package com.memeteam.picscrapper.view;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.memeteam.picscrapper.App;

import javafx.fxml.FXML;

import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;
import javafx.event.ActionEvent;

public class WelcomeController {
	
	Stage stage;
		
	private App app;
	
	DirectoryChooser directoryChooser = new DirectoryChooser();
	
	File destinationDirectory;
	
	@FXML
	Label buildInformer;
	@FXML
	Label titleLabel;
	@FXML
	Label locationLabel;
	
	@FXML
	TextField subpagesField;
	
	@FXML
	ChoiceBox siteChoiceBox;
	
	@FXML
	Button locationButton;
	
	@FXML
	RadioButton overwriteRadio;
	@FXML
	RadioButton keepRadio;
		
	public void setApp(App app, Stage stage) { 
		this.app = app; 
		this.stage = stage;
		showBuildInfo(); 
		initContent();
	}	
    
    void showBuildInfo() {
    	try {
    		MavenXpp3Reader reader = new MavenXpp3Reader();
    		Model model = reader.read(new FileReader("pom.xml"));
			buildInformer.setText("Current version: " + model.getParent().getVersion());
			stage.setTitle(model.getName());
			titleLabel.setText(model.getName());
    	} catch(FileNotFoundException e) { //File not found exception will be triggered here if application is ran from JAR itself, not from LocalRunner execution.
    		File jarFile;
    		try {
    			jarFile = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI()); //Gripping a URI of existing JAR file.
    			FileInputStream fileStream = new FileInputStream(jarFile); //getting input stream
    			JarInputStream jarStream = new JarInputStream(fileStream); //getting jar stream from file stream
    			Manifest manifest = jarStream.getManifest(); //Getting access to Manifest file of generated jar.
    			Attributes manifestAttributes = manifest.getMainAttributes(); //getting all main attributes contained in manifest file.
    			buildInformer.setText("Current version: " + manifestAttributes.getValue("Implementation-Version"));
    			stage.setTitle(manifestAttributes.getValue("Implementation-Title"));    	
    			titleLabel.setText(manifestAttributes.getValue("Implementation-Title"));
    			jarStream.close();
    			fileStream.close();
    		} catch(URISyntaxException | IOException ex) { //URL issue - malformed JAR. Should not happen.
    			ex.printStackTrace();
    		}
    	} catch(IOException | XmlPullParserException e) { //pom.xml not found for some reason - that's a serious error.
    		e.printStackTrace();
    	}        
    }
    
    void initContent() {
    	locationLabel.setText("");    	
    	subpagesField.setText("");
    	
    	siteChoiceBox.setTooltip(new Tooltip("List of supported pages ready to be scrapped."));
    	
    	subpagesField.setTooltip(new Tooltip("Specify how many newest sub-pages you want to scrap.\nLeave this field empty in order to scrap everything."));
    	subpagesField.setPromptText("1-x or empty");
    	
    	locationButton.setTooltip(new Tooltip("Specify the location for your pictures..."));
    	locationButton.setOnAction((final ActionEvent e) -> {
    		directoryChooser.setTitle("Specify the location for your saved pictures...");
    		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    		destinationDirectory = directoryChooser.showDialog(stage);
    		if(destinationDirectory != null) {
    			locationLabel.setText("Directory chosen.");
    			locationLabel.setTextFill(Color.web("#006400"));
    		} else {
    			locationLabel.setText("Directory not chosen.");
    			locationLabel.setTextFill(Color.web("#be0000"));
    		}
    	});
    }
    
    @FXML
    void handleExit() {
    	try {
			Media sound = new Media(App.class.getClassLoader().getResource("sounds/exit.mp3").toURI().toString()); //getting the proper sound file.
			AudioClip mediaPlayer = new AudioClip(sound.getSource()); //assign a sound as an audioClip.
	        mediaPlayer.play();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(AlertType.CONFIRMATION, "", okButton, cancelButton);
		alert.setTitle("Exit");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure?");
		
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == okButton)
			System.exit(0);
		else
			alert.close();
		
    };
    
    @FXML
    void handleAbout() {
    	ButtonType githubButton = new ButtonType("Check GitHub", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(AlertType.CONFIRMATION, "", githubButton, cancelButton);
		alert.setTitle("About");
		alert.setHeaderText("About the program");
		alert.setContentText("PicScrapper Application\n"
				+ "Application made for educational purposes.\n"
				+ "Select your favourite (and supported) site and scrap\n"
				+ "all available pictures and memes!");   
		
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == githubButton)
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/ShamanicWisdom/PicScrapper")); //Opening a GitHub page on default browser.
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		else
			alert.close();
    };    
}


