package com.memeteam.picscrapper.automation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.memeteam.picscrapper.model.ScrapModel;
import com.memeteam.picscrapper.utility.FfmpegConfigurator;
import com.memeteam.picscrapper.utility.SeleniumConfigurator;
import com.memeteam.picscrapper.view.AutomationController;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class Coub extends AutomationController {
	
	FfmpegConfigurator ffmpegConfigurator = new FfmpegConfigurator();
	
	@FXML
	Button stopButton;
	
	//Creating the JavascriptExecutor interface object by Type casting		
    JavascriptExecutor jsExecutor;
    
    JsonObject jsonObject;
	
	public Coub(Button stopButton) {
		this.stopButton = stopButton;
	}
			
	public Task<Void> startAutomation(ScrapModel scrapModel) {		
		//Running a task.
		Task<Void> automationTask = new Task<Void>() {			
			@Override
			public Void call() throws Exception {
				messageList.clear();
				
				updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Checking FFMpeg plugin state..."))));		
				try {
					ffmpegConfigurator.setupFfmpeg();
					updateMessage(new String(String.join("\n", updateMessageStack(messageList, "FFMpeg initialized successfully."))));
				} catch(Exception e) {
					updateMessage(new String(String.join("\n", updateMessageStack(messageList, "An exception has been occured during FFMPEG plugin init..."))));	
					updateMessage(new String(String.join("\n", updateMessageStack(messageList, e.getMessage()))));	
					super.failed();
				}
				
				updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Starting automation task for " + scrapModel.getWebsite() + "..."))));		
				try {
					driver = SeleniumConfigurator.setupDriver(scrapModel.getHeadlessMode());
					jsExecutor = (JavascriptExecutor)driver;	
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
				
				//TimeUnit.MINUTES.sleep(5);
				if(!scrapModel.getLogin().isEmpty()) {
					updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Proceeding to login page and waiting up for 2 minutes for user input..."))));	
					driver.get("https://coub.com/auth/");
					WebElement emailField = driver.findElement(By.id("email"));
					emailField.clear();
					emailField.sendKeys(scrapModel.getLogin() + Keys.ENTER);
					
					boolean loginSuccessful = false;
					//Waiting for up to 120 seconds for user input
					for(int i = 0; i < 24; i++) {						
						if(driver.getCurrentUrl().equalsIgnoreCase("https://coub.com/")) {
							updateMessage(new String(String.join("\n", updateMessageStack(messageList, "User successfully logged in. Begin the automation task..."))));	
							loginSuccessful = true;
							break;
						}			
						TimeUnit.SECONDS.sleep(5);
					}
					if(!loginSuccessful) {
						updateMessage(new String(String.join("\n", updateMessageStack(messageList, "User did not log-in successfully for 2 minutes. Proceeding as a guest..."))));	
					}
					
				}
				driver.get("https://coub.com/");				
				driver.findElement(By.className("close-button")).click();
				
				if(!scrapModel.getCommunity().isEmpty()) {
					driver.get("https://coub.com/community/" + scrapModel.getCommunity());
					if(driver.getTitle().equalsIgnoreCase("Coub. Page not found")) {
						updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Community named [" + scrapModel.getCommunity() + "] does not exist!\nPlease re-run the application and ensure that chosen community exists!"))));	
						super.failed();
					}
				}
				if(!scrapModel.getTag().isEmpty()) {
					driver.get("https://coub.com/tags/" + scrapModel.getTag());
					if(driver.getTitle().equalsIgnoreCase("Nothing found")) {
						updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Tag named [" + scrapModel.getCommunity() + "] does not exist!\nPlease re-run the application and ensure that chosen tag exists!"))));	
						super.failed();
					}
				}
				
				boolean moreCoubs = true;		
				
				//inner paging - counting from 1 to x.
				int currentCoubPage = 1;
				
				while(moreCoubs) {
					try {
						WebElement coubPageList = driver.findElement(By.className("coubs-list__inner"));
						List<WebElement> loadedPages = coubPageList.findElements(By.className("page"));
						WebElement currentPage = null;
						System.out.println("loadedpages size: " + loadedPages.size());
						
						boolean moreLoadedPagesSpotted = false;
						for(int i = 0; i < loadedPages.size(); i++) {
							System.out.println("LoadedPage" + i);
							if(loadedPages.get(i).getAttribute("data-page-id").equals("" + currentCoubPage)) {
								currentPage = loadedPages.get(i);
								moreLoadedPagesSpotted = true;
								break;
							}
						}
						if(!moreLoadedPagesSpotted) {
							updateMessage(new String(String.join("\n", updateMessageStack(messageList, "No more coubs found!"))));	
							break;
						}
						
						List<WebElement> coubsOfCurrentPage = currentPage.findElements(By.className("coub"));
						
						if(coubsOfCurrentPage.size() == 0 ) {
							updateMessage(new String(String.join("\n", updateMessageStack(messageList, "No more coubs found!"))));	
							break;
						}
						System.out.println("Coub in that page: " + coubsOfCurrentPage.size());
						
						for(WebElement coub: coubsOfCurrentPage) {
							System.out.println("Coub: " + coub.getAttribute("data-permalink"));
							Point coubLocation = coub.getLocation();
							jsExecutor.executeScript("window.scrollTo(0, " + coubLocation.getY() + ");");	
							coub.click();
							WebElement coubData = coub.findElement(By.className("data"));
							
							//Generating JSON object from the scrapped output.
							jsonObject = JsonParser.parseString(coubData.getAttribute("textContent")).getAsJsonObject();
							
							//Assigning the correct data.
							String coubTitle = jsonObject.get("title").getAsString().replaceAll("[^A-Za-z0-9]", "").trim();
							String coubUploadDate = jsonObject.get("updated_at").getAsString().replaceAll(":", ".");
							JsonObject coubFiles = jsonObject.get("file_versions").getAsJsonObject();
							JsonObject coubHtml5Links = coubFiles.get("html5").getAsJsonObject();

							String coubAudioLink = coubHtml5Links.get("audio").getAsJsonObject().get("high").getAsJsonObject().get("url").getAsString();
							String coubVideoLink = coubHtml5Links.get("video").getAsJsonObject().get("higher").getAsJsonObject().get("url").getAsString();

							String audioName = scrapModel.getSavingLocation() + "\\" + coubTitle + coubUploadDate + ".mp3";
							String videoName = scrapModel.getSavingLocation() + "\\" + coubTitle + coubUploadDate + ".mp4";

							downloadData(coubAudioLink, audioName);
							downloadData(coubVideoLink, videoName);
							
							System.out.println("COUBDATA:\nname : " + coubTitle + " uploaded at: " + coubUploadDate + "\nvid  : " + coubVideoLink + "\naudio: " + coubAudioLink);
							TimeUnit.SECONDS.sleep(2);
							
							System.out.println("invoking...\n" + "\"" + ffmpegConfigurator.getFfmpegLocation().substring(0, ffmpegConfigurator.getFfmpegLocation().length() - 4) + "\" -stream_loop -1 -i \"" + videoName + "\" -i \"" + audioName + "\" -shortest -map 0:v:0 -map 1:a:0 -y \"" + scrapModel.getSavingLocation() + "\\ps_" + coubTitle + coubUploadDate + ".mp4\"");

							Process ffmpegProcess = Runtime.getRuntime().exec("\"" + ffmpegConfigurator.getFfmpegLocation().substring(0, ffmpegConfigurator.getFfmpegLocation().length() - 4) + "\" -stream_loop -1 -i \"" + videoName + "\" -i \"" + audioName + "\" -shortest -map 0:v:0 -map 1:a:0 -y \"" + scrapModel.getSavingLocation() + "\\ps_" + coubTitle + coubUploadDate + ".mp4\"");

							
							updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Rendering '" + coubTitle + "'..."))));	
							
							//Let the process start.
							TimeUnit.SECONDS.sleep(5);
							
							boolean ffmpegStillUp = true;
							
							while(ffmpegStillUp) {
								
								InputStream ffmpegStream = ffmpegProcess.getErrorStream();
								for (int i = 0; i < ffmpegStream.available(); ++i)
									ffmpegStream.read();
								
								Process ffmpegProcessChecker = Runtime.getRuntime().exec("tasklist.exe /nh /fi \"Imagename eq ffmpeg.exe\"");
								BufferedReader responseReader = new BufferedReader(new InputStreamReader(ffmpegProcessChecker.getInputStream()));

								ArrayList<String> processList = new ArrayList<String>();
								String line = null;
								while ((line = responseReader.readLine()) != null) 
									processList.add(line);

								responseReader.close();
																
								if(processList.contains("INFO: No tasks are running which match the specified criteria.")) {
									updateMessage(new String(String.join("\n", updateMessageStack(messageList, "'" + coubTitle + "' rendered."))));
									ffmpegStillUp = false;
								} else 
									TimeUnit.SECONDS.sleep(2);
							}
							
							//cleanup...
							File video = new File(videoName);
							video.delete();
							File audio = new File(audioName);
							audio.delete();
						}
						
						TimeUnit.SECONDS.sleep(2);
						currentCoubPage++;
					} catch(Exception e) {
						e.printStackTrace();
						e.getMessage();
						moreCoubs = false;
					}
				}
				
				TimeUnit.HOURS.sleep(1);
				return null;
			}
			
			//When task is completed.
			@Override
			public void succeeded() {
				updateMessage(new String(String.join("\n", updateMessageStack(messageList, "All memes saved successfully. Task completed!"))));				
				driver.quit();
				stopButton.setText("Go Back");
			}
			
			//When task fails.
			@Override
			public void failed() {
				updateMessage(new String(String.join("\n", updateMessageStack(messageList, "Task failed!"))));
				FfmpegConfigurator.killFfmpeg();
				stopButton.setText("Go Back");
			}
        };		
        return automationTask;
	}
	
	private void downloadData(String urlLink, String fileName) {
        URL url = null;
        try {
        	url = new URL(urlLink);
            FileUtils.copyURLToFile(url, new File(fileName));
            System.out.println("File Downloaded Successfully.");
        } catch (IOException e) {
            System.out.println("Failed to download a file. Reason:");
            e.printStackTrace();
        }             
    }
}
