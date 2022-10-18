package com.memeteam.picscrapper.view;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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
import javafx.stage.StageStyle;
import javafx.stage.DirectoryChooser;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.media.AudioClip;
import javafx.scene.layout.RowConstraints;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

public class WelcomeController extends App {
	
	Stage stage;
	BorderPane root;
		
	private App app;
	
	DirectoryChooser directoryChooser = new DirectoryChooser();
			
	@FXML
	GridPane configurationPane;
	
	@FXML
	Label buildInformer;
	@FXML
	Label titleLabel;
	@FXML
	Label locationLabel;
	
	@FXML
	TextField subpagesField;
	@FXML
	TextField tagField;
	@FXML
	TextField communityField;
	@FXML
	TextField loginField;
	
	@FXML
	PasswordField passwordField;
	
	@FXML
	ChoiceBox<String> websiteChoiceBox;
	
	@FXML
	Button locationButton;

	final ToggleGroup duplicateBehaviorPatternToggleGroup = new ToggleGroup();
	final ToggleGroup tagOrCommunityToggleGroup = new ToggleGroup();
	final ToggleGroup styleToggleGroup = new ToggleGroup();
	
	@FXML
	RadioButton overwriteRadio;
	@FXML
	RadioButton keepRadio;
	
	@FXML
	RadioButton tagRadio;
	@FXML
	RadioButton communityRadio;
	
	@FXML
	CheckBox headlessModeCheckBox;
	@FXML
	CheckBox selectorCheckBox;
	
	@FXML
	ToggleButton lightModeToggle;
	@FXML
	ToggleButton darkModeToggle;
	
	//GridPane rows.
	RowConstraints tagOrCommunityToggleRow;
	RowConstraints tagRow;
	RowConstraints communityRow;
	RowConstraints subpagesRow;
	RowConstraints loginRow;
	RowConstraints passwordRow;
	
	//(CSS only) row cell anchor panes.
	@FXML
	AnchorPane tagOrCommunityLeftAnchor;
	@FXML
	AnchorPane tagOrCommunityRightAnchor;
	@FXML
	AnchorPane tagLeftAnchor;
	@FXML
	AnchorPane tagRightAnchor;
	@FXML
	AnchorPane communityLeftAnchor;
	@FXML
	AnchorPane communityRightAnchor;
	@FXML
	AnchorPane subpagesLeftAnchor;
	@FXML
	AnchorPane subpagesRightAnchor;
	@FXML
	AnchorPane loginLeftAnchor;
	@FXML
	AnchorPane loginRightAnchor;
	@FXML
	AnchorPane passwordLeftAnchor;
	@FXML
	AnchorPane passwordRightAnchor;
	
	//Model
	ScrapModel scrapModel;
	//values what model will gather when scrap picture event will be triggered.
	String chosenWebsite;
	File chosenLocation;
	String chosenBehavior;
	String chosenSource;

	List<String> automationWebsiteNameList = new ArrayList<>();
	List<Object> rowList = new ArrayList<>();
			
	public void setApp(App app, Stage stage, BorderPane root) { 
		this.app = app; 
		this.stage = stage;		
		this.root = root;
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
		
		resetFields();    	
    	initializeRowNodes();
    	
    	//Website ChoiceBox script initiation.
    	websiteChoiceBox.setTooltip(new Tooltip("List of supported pages ready to be scrapped."));
    	websiteChoiceBox.getSelectionModel().clearSelection(); //Clean-up of selected item.
    	websiteChoiceBox.getItems().clear(); //Clearing the items content.
    	websiteChoiceBox.getItems().addAll(Constants.SUPPORTED_WEBSITES);
    	websiteChoiceBox.getSelectionModel().selectFirst();
    	
    	chosenWebsite = Constants.SUPPORTED_WEBSITES.get(0);
    	updateTheGrid(chosenWebsite);
    	
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
    			updateTheGrid(chosenWebsite);
    		}
    	});
    	
    	//SubPages count field script initiation.
    	subpagesField.setTooltip(new Tooltip("Specify how many newest sub-pages you want to scrap.\nLeave this field empty in order to scrap everything."));
    	subpagesField.setPromptText("0-x (0 means that you want to scrap all)");
    	
    	tagField.setTooltip(new Tooltip("Provide a tag name."));
    	tagField.setPromptText("Provide a tag name.");
    	
    	communityField.setTooltip(new Tooltip("Provide a community name."));
    	communityField.setPromptText("Provide a community name.");
    	
    	loginField.setTooltip(new Tooltip("Provide a login (if empty, then automation will proceed without a login attempt)."));
    	loginField.setPromptText("Optional - provide a login.");
    	
    	passwordField.setTooltip(new Tooltip("Provide a password (if empty, then automation will proceed without a login attempt)."));
    	passwordField.setPromptText("Optional - provide a password.");
    	
    	//Destination of saved Content script initiation
    	locationButton.setTooltip(new Tooltip("Specify the location for your pictures..."));
    	locationButton.setOnAction((final ActionEvent e) -> {
    		directoryChooser.setTitle("Specify the location for your saved pictures...");
    		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    		chosenLocation = directoryChooser.showDialog(stage);
    		if(chosenLocation != null) {
    			locationLabel.setText("Directory chosen.");
    			locationLabel.setTextFill(Color.web("#39ff14"));
    		} else {
    			locationLabel.setText("Directory not chosen.");
    			locationLabel.setTextFill(Color.web("#be0000"));
    			chosenLocation = null;
    		}
    	});
    	
    	//Duplicates treating strategy script initiation.
    	//assigning radio buttons to one toggle group - in order to allow to assign a state relation between them.
    	overwriteRadio.setToggleGroup(duplicateBehaviorPatternToggleGroup);
    	overwriteRadio.setUserData("overwrite");
    	overwriteRadio.setSelected(true);
    	chosenBehavior = overwriteRadio.getUserData().toString();
    	keepRadio.setToggleGroup(duplicateBehaviorPatternToggleGroup);
    	keepRadio.setUserData("keep");
    	
    	duplicateBehaviorPatternToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
    		public void changed(ObservableValue observableValue, Toggle currentToggle, Toggle newToggle) {
    			if(duplicateBehaviorPatternToggleGroup.getSelectedToggle() != null) {
    				chosenBehavior = duplicateBehaviorPatternToggleGroup.getSelectedToggle().getUserData().toString();
    			}
    		}
    	});
    	
    	//Tag or community strategy.
    	//assigning radio buttons to one toggle group - in order to allow to assign a state relation between them.
    	tagRadio.setToggleGroup(tagOrCommunityToggleGroup);
    	tagRadio.setUserData("tag");
    	tagRadio.setSelected(true);
    	chosenSource = tagRadio.getUserData().toString();
    	communityRadio.setToggleGroup(tagOrCommunityToggleGroup);
    	communityRadio.setUserData("community");
    	    	
    	tagOrCommunityToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
    		public void changed(ObservableValue observableValue, Toggle currentToggle, Toggle newToggle) {
    			if(tagOrCommunityToggleGroup.getSelectedToggle() != null) {
    				chosenSource = tagOrCommunityToggleGroup.getSelectedToggle().getUserData().toString();
    				showSpecificRow(chosenSource);
    			}
    		}
    	});
    	
    	headlessModeCheckBox.setSelected(false);
    	selectorCheckBox.setSelected(false);
    	
    	//modeGroup
    	//Tag or community strategy.
    	//assigning radio buttons to one toggle group - in order to allow to assign a state relation between them.
    	darkModeToggle.setToggleGroup(styleToggleGroup);
    	darkModeToggle.setUserData("Dark");
    	lightModeToggle.setToggleGroup(styleToggleGroup);
    	lightModeToggle.setUserData("Light");
    	
    	if(app.currentStyle.equalsIgnoreCase("Dark"))
    		darkModeToggle.setSelected(true);
    	else
    		lightModeToggle.setSelected(true);
    	    	
    	styleToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
    		public void changed(ObservableValue observableValue, Toggle currentToggle, Toggle newToggle) {
    			if(styleToggleGroup.getSelectedToggle() != null) {
    				app.currentStyle = styleToggleGroup.getSelectedToggle().getUserData().toString();
    				root.getStylesheets().clear();
    				root.getStylesheets().add(getClass().getResource("/styles/" + app.currentStyle + ".css").toExternalForm());
    			}
    		}
    	});
    	
    	//To prevent unclicking the selected toggle button.
    	styleToggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
    	    if (newVal == null)
    	        oldVal.setSelected(true);
    	});
    }
    
    private void initializeRowNodes() {
    	rowList = Arrays.asList(configurationPane.getRowConstraints().toArray());
    	
    	tagOrCommunityToggleRow = (RowConstraints)rowList.get(5);
    	tagRow = (RowConstraints)rowList.get(6);
    	communityRow = (RowConstraints)rowList.get(7);
    	subpagesRow = (RowConstraints)rowList.get(8);
    	loginRow = (RowConstraints)rowList.get(9);
    	passwordRow = (RowConstraints)rowList.get(10);
    }
    
    private void updateTheGrid(String website) {    	
    	
    	hideAllSpecificRows();
    	
		switch(website.toLowerCase()) {
			case "komixxy": {
				showSpecificRow(subpagesRow);				
				break;
			}
			case "coub": {
				showSpecificRow(tagOrCommunityToggleRow);
				showSpecificRow(loginRow);
				break;
			}
			case "jbzd": {
				showSpecificRow(tagRow);
				break;
			}
			default: {
				System.exit(0);
			}
		}
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
		alert.initStyle(StageStyle.UNDECORATED);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/" + app.currentStyle + ".css").toExternalForm());
		alert.setGraphic(new ImageView(new Image(App.class.getClassLoader().getResourceAsStream("images/" + app.currentStyle.toLowerCase() + "/questionIcon.png"))));
		
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == okButton)
			System.exit(0);
		else
			alert.close();
		
    }
    
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
		alert.initStyle(StageStyle.UNDECORATED);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/" + app.currentStyle + ".css").toExternalForm());
		alert.setGraphic(new ImageView(new Image(App.class.getClassLoader().getResourceAsStream("images/" + app.currentStyle.toLowerCase() + "/questionIcon.png"))));
		
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == githubButton)
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/ShamanicWisdom/PicScrapper")); //Opening a GitHub page on default browser.
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		else
			alert.close();
    }

    @FXML
    void handleScrapping() throws Exception {
    	if(dataValidator()) {
        	//Starting to fill ScrapModel object.   
    		int subpagesToScrap = 0;
    		try {
    			subpagesToScrap = Integer.parseInt(subpagesField.getText());
    		} catch(NumberFormatException fieldMightBeNull) {
    			subpagesToScrap = 0;
    		}
    		scrapModel.setScrapModel(chosenWebsite, subpagesToScrap, chosenLocation, chosenBehavior, headlessModeCheckBox.isSelected(), selectorCheckBox.isSelected(), tagField.getText(), communityField.getText(), loginField.getText(), passwordField.getText());
    		app.showAutomationProgress(scrapModel);
    	}    	
    }
    
    private boolean dataValidator() {
    	automationWebsiteNameList.clear();
    	
    	String errorMessage = "";
    	if(chosenWebsite.isEmpty())
    		errorMessage += "No website is chosen!\n";

    	InputStream automationDirectoryInputStream = AutomationController.class.getClassLoader().getResourceAsStream("com/memeteam/picscrapper/automation");
		try {
			//If 0, then resource is unavailable - most probable scenario is: this application has been started from JAR file. Due to that, gripping a directory directly is impossible. 
			//It is needed to use JarFile and other support classes for having a grip on packed tunes.
			if(automationDirectoryInputStream.available() == 0) {
				//getting a grip to caller JAR file.
				JarFile jarFile = new JarFile(AutomationController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
					
				//Iterate over every directory and file of caller JAR.
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
				    JarEntry entry = entries.nextElement();
				    //if song-directory is found, then begin a saving process.
				    if (entry.getName().contains("com/memeteam/picscrapper/automation")) 
				    	//Grab every file (excluding the directory itself).
				    	if(!entry.getName().equalsIgnoreCase("com/memeteam/picscrapper/automation/") || !entry.getName().contains("$"))
				    		automationWebsiteNameList.add(entry.getName().replaceAll("com/memeteam/picscrapper/automation/", "").replaceAll(".class", ""));				    
				}
			} else {
				InputStreamReader streamReader = new InputStreamReader(automationDirectoryInputStream, StandardCharsets.UTF_8);
				BufferedReader bufferedReader = new BufferedReader(streamReader);				
				bufferedReader.lines().forEach(saveTheWebsiteClass);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!automationWebsiteNameList.contains(chosenWebsite)) 
			errorMessage += "Given website [" + chosenWebsite + "] is not supported!\n";
    	
		if(subpagesRow.getMaxHeight() != 0.0) {
	    	if(subpagesField.getText().isEmpty()) 
	    		errorMessage += "Subpages count is not set up!\n";
	    	try {
	    		int subpagesCount = Integer.parseInt(subpagesField.getText());
	    		if(subpagesCount < 0)
	    			errorMessage += "Subpages count cannot be lower than 0!\n";
	    	} catch(NumberFormatException e) {
	    		errorMessage += "Subpages count field allows only digits!\n";
	    	}
		}	
		
		//Login and password checkout. Coub has a different login pattern - instead of password, a code is sent to a given email in order to log-in.
		if(!chosenWebsite.equalsIgnoreCase("Coub")) {
			if(loginRow.getMaxHeight() != 0.0 || passwordRow.getMaxHeight() != 0.0) {
				if(loginRow.getMaxHeight() != 0.0 && passwordRow.getMaxHeight() != 0.0) {
					if(!loginField.getText().trim().isEmpty() || !passwordField.getText().trim().isEmpty()) {
						if(loginField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty())
							errorMessage += "In order to log in to a chosen website, please provide both login and password!\n";	
					}
				} else 
					errorMessage += "Internal error - login and password rows should be visible at once!!\n";			
			}
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
    		alert.initStyle(StageStyle.UNDECORATED);
			alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/" + app.currentStyle + ".css").toExternalForm());
    		alert.setGraphic(new ImageView(new Image(App.class.getClassLoader().getResourceAsStream("images/" + app.currentStyle.toLowerCase() + "/errorIcon.png"))));
    		alert.showAndWait();
    		return false;
    	}
    }
    
    //Creating a custom Consumer event for lambda expression.
  	Consumer<String> saveTheWebsiteClass = new Consumer<String>() {
  	    public void accept(String automationClassName) {
  	        automationWebsiteNameList.add(automationClassName.replaceAll("com/memeteam/picscrapper/automation/", "").replaceAll(".class", ""));
  	    }
  	};
  	
  	private void resetFields() {	   	
    	tagField.setText("");
    	communityField.setText("");
    	subpagesField.setText("");
    	loginField.setText("");
    	passwordField.setText("");
  	}
  	
  	private void hideAllSpecificRows() {  		
  		resetFields();
  		
  		tagOrCommunityToggleRow.setMinHeight(0.0);
  		tagOrCommunityToggleRow.setMaxHeight(0.0);
  		tagOrCommunityToggleRow.setPrefHeight(0.0);
  		tagRadio.setVisible(false);
  		communityRadio.setVisible(false);
  		tagOrCommunityLeftAnchor.setVisible(false);
  		tagOrCommunityRightAnchor.setVisible(false);

  		tagRow.setMinHeight(0.0);
  		tagRow.setMaxHeight(0.0);
  		tagRow.setPrefHeight(0.0);
  		tagLeftAnchor.setVisible(false);
  		tagRightAnchor.setVisible(false);
  		
  		communityRow.setMinHeight(0.0);
  		communityRow.setMaxHeight(0.0);
  		communityRow.setPrefHeight(0.0);
  		communityLeftAnchor.setVisible(false);
  		communityRightAnchor.setVisible(false);
  		
  		subpagesRow.setMinHeight(0.0);
  		subpagesRow.setMaxHeight(0.0);
  		subpagesRow.setPrefHeight(0.0);
  		subpagesLeftAnchor.setVisible(false);
  		subpagesRightAnchor.setVisible(false);
  		  		
  		loginRow.setMinHeight(0.0);
  		loginRow.setMaxHeight(0.0);
  		loginRow.setPrefHeight(0.0);
  		loginLeftAnchor.setVisible(false);
  		loginRightAnchor.setVisible(false);
  		
  		passwordRow.setMinHeight(0.0);
  		passwordRow.setMaxHeight(0.0);
  		passwordRow.setPrefHeight(0.0);
  		passwordLeftAnchor.setVisible(false);
  		passwordRightAnchor.setVisible(false);
  	}
  	
  	private void showSpecificRow(RowConstraints row) {
  		row.setMaxHeight(50.0);
  		row.setPrefHeight(50.0);
  		if(tagOrCommunityToggleRow.getPrefHeight() == 50.0) {
  			tagRadio.setVisible(true);
  			communityRadio.setVisible(true);
  			showSpecificRow(chosenSource);
  			tagOrCommunityLeftAnchor.setVisible(true);
  			tagOrCommunityRightAnchor.setVisible(true);
  		} 
  		if(tagRow.getPrefHeight() == 50.0) {
  			tagLeftAnchor.setVisible(true);
  	  		tagRightAnchor.setVisible(true);
  		}
  		if(communityRow.getPrefHeight() == 50.0) {
  			communityLeftAnchor.setVisible(true);
  			communityRightAnchor.setVisible(true);
  		}
  		if(subpagesRow.getPrefHeight() == 50.0) {
  			subpagesLeftAnchor.setVisible(true);
  			subpagesRightAnchor.setVisible(true);
  		}
  		if(loginRow.getPrefHeight() == 50.0) {
  			loginLeftAnchor.setVisible(true);
  			loginRightAnchor.setVisible(true);
  		}
  		if(passwordRow.getPrefHeight() == 50.0) {
  			passwordLeftAnchor.setVisible(true);
  			passwordRightAnchor.setVisible(true);
  		}
  	}
  	
  	private void showSpecificRow(String source) {
  		if(source.equalsIgnoreCase("tag")) {
  			communityRow.setMinHeight(0.0);
  	  		communityRow.setMaxHeight(0.0);
  	  		communityRow.setPrefHeight(0.0);
  	  		communityField.setText("");
  	  		communityLeftAnchor.setVisible(false);
  	  		communityRightAnchor.setVisible(false);
  	  		
	  		tagRow.setMaxHeight(50.0);
	  		tagRow.setPrefHeight(50.0);
	  		tagLeftAnchor.setVisible(true);
	  		tagRightAnchor.setVisible(true);
  		} else {
  			tagRow.setMinHeight(0.0);
  			tagRow.setMaxHeight(0.0);
  			tagRow.setPrefHeight(0.0);
  			tagField.setText("");
  			tagLeftAnchor.setVisible(false);
  	  		tagRightAnchor.setVisible(false);
  	  		
	  		communityRow.setMaxHeight(50.0);
	  		communityRow.setPrefHeight(50.0);
	  		communityLeftAnchor.setVisible(true);
	  		communityRightAnchor.setVisible(true);
  		}
  	}
}



