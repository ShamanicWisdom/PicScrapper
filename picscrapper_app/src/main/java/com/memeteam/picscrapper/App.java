package com.memeteam.picscrapper;

import java.io.IOException;
import java.util.Optional;

import com.memeteam.picscrapper.view.MainWindowController;
import com.memeteam.picscrapper.view.WelcomeController;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.event.EventHandler;

public class App extends Application {

	private Stage primaryStage;
	
	public BorderPane root;
	
	FXMLLoader loader;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;			
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
				ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
				Alert alert = new Alert(AlertType.CONFIRMATION, "", okButton, cancelButton);
				alert.setTitle("Exit");
				alert.setHeaderText(null);
				alert.setContentText("Are you sure?");
				
				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == okButton) {
					System.exit(0);
				} else {
					alert.close();
					event.consume();
				}
				
			}
		});
		
		initializeMainWindow();		
	}
	
    public void initializeMainWindow() {
        try {
            loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/fxml/view/MainWindow.fxml")); 
            root = (BorderPane) loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            MainWindowController controller = loader.getController();
            controller.setApp(this);
            primaryStage.show();                   
            primaryStage.setResizable(false);
            showWelcome();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Start scene.
    public void showWelcome()
    {
        try {
            loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/fxml/view/Welcome.fxml"));
            AnchorPane welcome = (AnchorPane) loader.load();
            root.setCenter(welcome);
            WelcomeController controller = loader.getController();
            controller.setApp(this, primaryStage);
        } catch (IOException e) {
           e.printStackTrace();
        }
    }
}
