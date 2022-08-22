package picscrapper;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;
import picscrapper.view.MainWindowController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

public class App extends Application {

	private Stage primaryStage;
	
	public BorderPane root;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		initializeMainWindow();
	}
	
	public void initializeMainWindow() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(App.class.getResource("/fxml/view/MainWindow.fxml"));
			root = (BorderPane) loader.load();
			Scene scene = new Scene(root);
			MainWindowController controller;
			controller = loader.getController();
			controller.setApp(this);
			primaryStage.show();
			primaryStage.setResizable(false);;
			primaryStage.setTitle("PicScrapper Application");
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
