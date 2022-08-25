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
import com.memeteam.picscrapper.model.ScrapModel;
import com.memeteam.picscrapper.utility.Constants;

import javafx.fxml.FXML;

import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

public class WelcomeController {
	
	Stage stage;
		
	private App app;
	
	DirectoryChooser directoryChooser = new DirectoryChooser();
			
	@FXML
	Label buildInformer;
	@FXML
	Label titleLabel;
	@FXML
	Label locationLabel;
	
	@FXML
	TextField subpagesField;
	
	@FXML
	ChoiceBox<String> websiteChoiceBox;
	
	@FXML
	Button locationButton;
	
	final ToggleGroup radioButtonToggleGroup = new ToggleGroup();
	
	@FXML
	RadioButton overwriteRadio;
	@FXML
	RadioButton keepRadio;
	
	@FXML
	CheckBox headlessModeCheckBox;
	@FXML
	CheckBox selectorCheckBox;
	
	//Model
	ScrapModel scrapModel;
	//values what model will gather when scrap picture event will be triggered.
	String chosenWebsite;
	File chosenLocation;
	String chosenBehavior;
	
		
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
		scrapModel = new ScrapModel();
		
    	locationLabel.setText("");    	
    	subpagesField.setText("");
    	
    	//Website ChoiceBox script initiation.
    	websiteChoiceBox.setTooltip(new Tooltip("List of supported pages ready to be scrapped."));
    	websiteChoiceBox.getSelectionModel().clearSelection(); //Clean-up of selected item.
    	websiteChoiceBox.getItems().clear(); //Clearing the items content.
    	websiteChoiceBox.getItems().addAll(Constants.SUPPORTED_WEBSITES);
    	websiteChoiceBox.getSelectionModel().selectFirst();
    	
    	chosenWebsite = Constants.SUPPORTED_WEBSITES.get(0);
    	
    	//Listener for possible changing of value inside websiteChoiceBox object.
    	websiteChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
    		//observableValue - chosen item, currentValue - old index of chosen item, newValue - new index of chosen item. 
    		public void changed(ObservableValue observableValue, Number currentValue, Number newValue) {
    			if(newValue.equals(-1)) {
    				Number fixingNumber = 1;
    				newValue = newValue.intValue() + fixingNumber.intValue();
    				observableValue.removeListener(this); //Deleting listener - for stopping the listening event. It will solve the issue of continuous listening of the list what leads to infinite loop. Consider this as a 'break;' in loop.
    			}
    			chosenWebsite = Constants.SUPPORTED_WEBSITES.get(newValue.intValue());
    		}
    	});
    	
    	//SubPages count Field script initiation.
    	subpagesField.setTooltip(new Tooltip("Specify how many newest sub-pages you want to scrap.\nLeave this field empty in order to scrap everything."));
    	subpagesField.setPromptText("0-x (0 means that you want to scrap all)");
    	
    	//Destination of saved Content script initiation
    	locationButton.setTooltip(new Tooltip("Specify the location for your pictures..."));
    	locationButton.setOnAction((final ActionEvent e) -> {
    		directoryChooser.setTitle("Specify the location for your saved pictures...");
    		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    		chosenLocation = directoryChooser.showDialog(stage);
    		if(chosenLocation != null) {
    			locationLabel.setText("Directory chosen.");
    			locationLabel.setTextFill(Color.web("#006400"));
    		} else {
    			locationLabel.setText("Directory not chosen.");
    			locationLabel.setTextFill(Color.web("#be0000"));
    			chosenLocation = null;
    		}
    	});
    	
    	//Duplicates treating strategy script initiation.
    	//assigning radio buttons to one toggle group - in order to allow to assign a state relation between them.
    	overwriteRadio.setToggleGroup(radioButtonToggleGroup);
    	overwriteRadio.setUserData("overwrite");
    	overwriteRadio.setSelected(true);
    	chosenBehavior = overwriteRadio.getUserData().toString();
    	keepRadio.setToggleGroup(radioButtonToggleGroup);
    	keepRadio.setUserData("keep");
    	
    	radioButtonToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
    		public void changed(ObservableValue observableValue, Toggle currentToggle, Toggle newToggle) {
    			if(radioButtonToggleGroup.getSelectedToggle() != null) {
    				chosenBehavior = radioButtonToggleGroup.getSelectedToggle().getUserData().toString();
    			}
    		}
    	});
    	
    	headlessModeCheckBox.setSelected(false);
    	selectorCheckBox.setSelected(false);
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
    
    @FXML
    void handleScrapping() {
    	if(dataValidator()) {
        	//Starting to fill ScrapModel object.        	
    		scrapModel.setScrapModel(chosenWebsite, Integer.parseInt(subpagesField.getText()), chosenLocation, chosenBehavior, headlessModeCheckBox.isSelected(), selectorCheckBox.isSelected());
    		System.out.println(scrapModel.toString());
    		try {
    			Media sound = new Media(App.class.getClassLoader().getResource("sounds/done.mp3").toURI().toString()); //getting the proper sound file.
    			AudioClip mediaPlayer = new AudioClip(sound.getSource()); //assign a sound as an audioClip.
    	        mediaPlayer.play();
    		} catch (URISyntaxException e) {
    			e.printStackTrace();
    		}
    	}    	
    }
    
    private boolean dataValidator() {
    	String errorMessage = "";
    	if(chosenWebsite.isEmpty())
    		errorMessage += "No website is chosen!\n";
    	if(subpagesField.getText().isEmpty()) 
    		errorMessage += "Subpages count is not set up!\n";
    	try {
    		int subpagesCount = Integer.parseInt(subpagesField.getText());
    		if(subpagesCount < 0)
    			errorMessage += "Subpages count cannot be lower than 0!\n";
    	} catch(NumberFormatException e) {
    		errorMessage += "Subpages count field allows only digits!\n";
    	}
    	if(chosenLocation == null) 
    		errorMessage += "Saving location is not set up!\n";
    	if(chosenBehavior.isEmpty()) 
    		errorMessage += "Duplicated files treat strategy not set up!\n";
    	if(errorMessage.isEmpty()) {
    		return true;
    	} else {
    		try {
    			Media sound = new Media(App.class.getClassLoader().getResource("sounds/exit.mp3").toURI().toString()); //getting the proper sound file.
    			AudioClip mediaPlayer = new AudioClip(sound.getSource()); //assign a sound as an audioClip.
    	        mediaPlayer.play();
    		} catch (URISyntaxException e) {
    			e.printStackTrace();
    		}
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.initOwner(stage);
    		alert.setTitle("Error!");;
    		alert.setHeaderText("Something went wrong...");
    		alert.setContentText(errorMessage);
    		alert.showAndWait();
    		return false;
    	}
    	
    }
}


