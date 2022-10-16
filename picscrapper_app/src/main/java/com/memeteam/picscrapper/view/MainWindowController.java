package com.memeteam.picscrapper.view;

import java.net.URISyntaxException;
import java.util.Optional;

import com.memeteam.picscrapper.App;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public class MainWindowController extends App {
	
	Stage stage;
	
	private App app;
	
	double xOffset, yOffset;
	
	@FXML
	AnchorPane titleBar;
	
	public void setApp(App app, Stage stage) { 
		this.app = app;
		this.stage = stage;
		initTitleBar();
	}
	
	private void initTitleBar() {
				
		titleBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
		
		titleBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	stage.setX(event.getScreenX() + xOffset);
            	stage.setY(event.getScreenY() + yOffset);
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
	void handleMinimize() {
		stage.setIconified(true);
	}
}
