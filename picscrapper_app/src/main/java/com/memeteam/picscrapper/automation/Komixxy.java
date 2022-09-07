package com.memeteam.picscrapper.automation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.memeteam.picscrapper.model.ScrapModel;
import com.memeteam.picscrapper.utility.SeleniumConfigurator;
import com.memeteam.picscrapper.view.AutomationController;

import javafx.concurrent.Task;

public class Komixxy extends AutomationController {
	
	public static Task startAutomation(ScrapModel scrapModel) {
		//Running a task.
		Task<Void> automationTask = new Task<Void>() {
			@Override
			public Void call() throws Exception {		
				String message = "Starting automation task for " + scrapModel.getWebsite() + "\n";
				updateMessage(message);
				try {
					driver = SeleniumConfigurator.setupDriver(scrapModel.getHeadlessMode());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				message += "Selenium started successfully. \n";
				updateMessage(message);
				
				for(int i = 1; i <= scrapModel.getSubpagesToHandle(); i++) {
					driver.get("https://komixxy.pl/page/" + i);
					List<WebElement> picList = driver.findElements(By.className("pic"));
					List<WebElement> memeObjectList = new ArrayList<>();
					for(WebElement picture: picList) {						
						if(picture.getAttribute("id").startsWith("pic"))
							memeObjectList.add(picture);
					}
					
					for(WebElement memeObject: memeObjectList) {
						try {
						WebElement memeName = memeObject.findElement(By.className("picture"));
						WebElement memeDate = memeObject.findElement(By.className("infobar"));
						
						String[] dateTable = memeDate.getText().split(" ");	
						List<String> dateList = new ArrayList<>();
						for(int j = 1; j <= 5; j++) 
							if(j != 4) 
								dateList.add(dateTable[j]);
						
						String date = "";
						for(String dateElement: dateList) 							
							date += dateElement + " ";
						
						date = date.trim();
						
						WebElement meme = memeObject.findElement(By.className("pic_image")).findElement(By.className("pic"));
						String memeImageLink = meme.getAttribute("src");
						
						if(memeImageLink.contains("blank.gif")) 
							memeImageLink = "https://komixxy.pl/" + meme.getAttribute("data-src");
																		
						BufferedImage image = ImageIO.read(new URL(memeImageLink));
						File savedImage = new File(scrapModel.getSavingLocation() + "/" + memeName.getText() + ".jpg");
						ImageIO.write(image, "jpg", savedImage);						
						
						message += "Meme: [" + memeName.getText() + "] saved successfully. \n";
						updateMessage(message);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}				
				
				return null;
			}
        };		
        return automationTask;
	}
}
