package com.memeteam.picscrapper.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class SeleniumConfigurator {
	
	static WebDriver driver;
	static EdgeOptions options;
	
	static String localDriverVersion = "msedgedriver104.exe";
	
	public WebDriver setupDriver(boolean headlessMode) throws InterruptedException {
		try {
			InputStream driverResource = SeleniumConfigurator.class.getClassLoader().getResourceAsStream("drivers/" + localDriverVersion);
			
			File driverDirectory = new File("driverdirectory");
			if(!driverDirectory.exists()) 
				driverDirectory.mkdir();
			
			File driverFile = new File("");
			
			if(driverDirectory.exists()) {
				boolean isDriverExists = false;
				for(File file: driverDirectory.listFiles()) {
					if(file.exists()) {
						if(file.getName().contains("msedgedriver")) {
							isDriverExists = true;
							driverFile = new File("driverdirectory" + File.separator + file.getName());
							break;
						}						
					}
				}
				if(isDriverExists == false) {
					driverFile = new File("driverdirectory" + File.separator + localDriverVersion);
					if(!driverFile.exists()) {
						driverFile.createNewFile();
						FileUtils.copyInputStreamToFile(driverResource, driverFile);
					}
				}
			}
			
			System.setProperty("webdriver.edge.driver", driverFile.getAbsolutePath());
			
			options = new EdgeOptions();
			
			options.setHeadless(headlessMode);
			
			driver = new EdgeDriver(options);			
		} catch(SessionNotCreatedException e) {
			e.printStackTrace();
			String[] splitExceptionMessage = e.getMessage().split("\n");
			Pattern pattern = Pattern.compile("\\d+");
			Matcher matcher = pattern.matcher(splitExceptionMessage[1]);
			while(matcher.find()) {
				localDriverVersion = "msedgedriver" + matcher.group() + ".exe";
			}
			killDriver();
			TimeUnit.MILLISECONDS.sleep(500);
			deleteLocalDriver();
			driver = setupDriver(headlessMode);
			
		} catch(IOException ioEx) {
			ioEx.printStackTrace();
		}
		
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
		
		return driver;
	}
	
	public static void killDriver() {		
		try {
			String cmdKillCommand = "TASKKILL /F /IM msedgedriver*";
			Process process = Runtime.getRuntime().exec(cmdKillCommand);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public static void deleteLocalDriver() {
		killDriver();
		File driverDirectory = new File("driverdirectory");
		if(driverDirectory.exists()) {
			for(File file: driverDirectory.listFiles()) 
				if(file.exists()) 
					file.delete();
		}
	}
}
