package com.memeteam.picscrapper.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import com.memeteam.picscrapper.exception.DriverNotAvailableException;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SeleniumConfigurator {
		
	static List<String> driversList = new ArrayList<>();
	
	static WebDriver driver;
	static EdgeOptions options;
	
	static String localDriverVersion = "msedgedriver104.exe";
	
	public static WebDriver setupDriver(boolean headlessMode) throws InterruptedException, DriverNotAvailableException {
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
							if(file.length() != 0) {
								isDriverExists = true;
								driverFile = new File("driverdirectory" + File.separator + file.getName());
								break;
							} else
								file.delete();
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

			//options.addArgument("--mute-audio");
			
			driver = new EdgeDriver(options);
		} catch(SessionNotCreatedException e) {
			String[] splitExceptionMessage = e.getMessage().split("\n");
			Matcher matcher = Pattern.compile("\\d+").matcher(splitExceptionMessage[1]);	
			while(matcher.find()) {
				localDriverVersion = "msedgedriver" + matcher.group() + ".exe";		
				break;
			}
			killDriver();
			TimeUnit.MILLISECONDS.sleep(500);
			deleteLocalDriver();
			checkTheAvailability(localDriverVersion);
			driver = setupDriver(headlessMode);			
		} catch(Exception ex) {
			ex.printStackTrace();
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
	
	public static void checkTheAvailability(String localDriverVersion) throws DriverNotAvailableException {	
		
		InputStream driverDirectoryAsInputStream = SeleniumConfigurator.class.getClassLoader().getResourceAsStream("drivers");
		try {
			//If 0, then resource is unavailable - most probable scenario is: this application has been started from JAR file. Due to that, gripping a directory directly is impossible. 
			//It is needed to use JarFile and other support classes for having a grip on packed files.
			if(driverDirectoryAsInputStream.available() == 0) {
				//getting a grip to caller JAR file.
				JarFile jarFile = new JarFile(SeleniumConfigurator.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
					
				//Iterate over every directory and file of caller JAR.
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
				    JarEntry entry = entries.nextElement();
				    //if driver-directory is found, then begin a saving process.
				    if (entry.getName().contains("drivers")) {
				    	//Grab every file (excluding the directory itself).
				    	if(!entry.getName().equalsIgnoreCase("drivers/")) {
					        driversList.add(entry.getName().replaceAll("drivers/", ""));
				    	}
				    }
				}
			} else {
				InputStreamReader streamReader = new InputStreamReader(driverDirectoryAsInputStream, StandardCharsets.UTF_8);
				BufferedReader bufferedReader = new BufferedReader(streamReader);
				
				bufferedReader.lines().forEach(saveTheDriver);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!driversList.contains(localDriverVersion)) {
			throw new DriverNotAvailableException(localDriverVersion);
		}
	}
	
	//Creating a custom Consumer event for lambda expression.
	static Consumer<String> saveTheDriver = new Consumer<String>() {
	    public void accept(String songName) {
	        driversList.add(songName);
	    }
	};
}
