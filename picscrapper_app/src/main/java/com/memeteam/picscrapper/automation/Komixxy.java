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
		
	public static Task<Void> startAutomation(ScrapModel scrapModel) {		
	
		//Running a task.
		Task<Void> automationTask = new Task<Void>() {			
			@Override
			public Void call() throws Exception {
				messageList.clear();
				updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Starting automation task for " + scrapModel.getWebsite()))));
				try {
					driver = SeleniumConfigurator.setupDriver(scrapModel.getHeadlessMode());
					updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Selenium started successfully."))));
				} catch (InterruptedException e) {
					e.printStackTrace();
					updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Selenium Driver init has been interrupted unexpectedly..."))));	
					updateMessage(new String(String.join("\n", updateMessageStack(messageList, e.getMessage()))));	
					super.failed();
				} catch(Exception e) {
					updateMessage(new String(String.join("\n", updateMessageStack(messageList, "An exception has been occured during Selenium Driver init..."))));	
					updateMessage(new String(String.join("\n", updateMessageStack(messageList, e.getMessage()))));	
					super.failed();
				}
				
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
						File savedImage = new File(scrapModel.getSavingLocation() + "/" + memeName.getText().replace("?", "") + ".jpg");
						if(!scrapModel.getDuplicateBehaviorPattern().equalsIgnoreCase("overwrite"))
							if(savedImage.exists()) {
								int duplicateNumber = 2;
								
								while(savedImage.exists()) {	
									String duplicatedName = savedImage.getName().replaceAll(".jpg", "");
									if(savedImage.getName().contains("_"))
										duplicatedName = duplicatedName.substring(0, (duplicatedName.lastIndexOf('_'))) + "_" + duplicateNumber + ".jpg";
									else 
										duplicatedName = duplicatedName + "_" + duplicateNumber + ".jpg";
									savedImage = new File(scrapModel.getSavingLocation() + "/" + duplicatedName);
									duplicateNumber++;
								}
							}
						ImageIO.write(image, "jpg", savedImage);						
						
						updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Meme: [" + memeName.getText() + "] saved successfully."))));
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}				
				
				return null;
			}
			
			//When task is completed.
			@Override
			public void succeeded() {
				updateMessage(new String(String.join("\n", updateMessageStack(messageList, "All memes saved successfully. Task completed!"))));				
				driver.quit();
			}
			
			//When task fails.
			@Override
			public void failed() {
				updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Task failed!"))));
			}
        };		
        return automationTask;
	}
}
