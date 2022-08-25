package com.memeteam.picscrapper.view;

import com.memeteam.picscrapper.App;
import com.memeteam.picscrapper.model.ScrapModel;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AutomationController {

	Stage stage;
	
	private App app;
	
	ScrapModel scrapModel;
	
	@FXML
	TextArea progressTextArea;
	
	public void setApp(App app, Stage stage, ScrapModel scrapModel) { 
		this.app = app; 
		this.stage = stage;		
		this.scrapModel = scrapModel;	
		System.out.println(scrapModel.toString());
	}	
	
	@FXML
	void handleStop() {
		app.showWelcome();
	}
}
