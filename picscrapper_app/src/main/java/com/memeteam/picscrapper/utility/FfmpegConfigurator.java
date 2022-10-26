package com.memeteam.picscrapper.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

public class FfmpegConfigurator {
	
	String ffmpegLocation = "";
	
	public void setupFfmpeg() {
		try {
			InputStream ffmpegResource = FfmpegConfigurator.class.getClassLoader().getResourceAsStream("ffmpeg/ffmpeg.exe");
			System.out.print("stream catched.");
			File ffmpegDirectory = new File("ffmpeg");
			if(!ffmpegDirectory.exists()) 
				ffmpegDirectory.mkdir();
			
			File ffmpegFile = new File("");
			
			if(ffmpegDirectory.exists()) {
				boolean isFileExists = false;
				for(File file: ffmpegDirectory.listFiles()) {
					System.out.print("file: " + file.getName());
					if(file.exists()) {
						if(file.getName().contains("ffmpeg.exe")) {		
							if(file.length() != 0) {
								isFileExists = true;
								ffmpegFile = new File("ffmpeg" + File.separator + file.getName());
								break;
							} else
								file.delete();
						}						
					}
				}
				if(isFileExists == false) {
					ffmpegFile = new File("ffmpeg/ffmpeg.exe");
					if(!ffmpegFile.exists()) {
						ffmpegFile.createNewFile();
						FileUtils.copyInputStreamToFile(ffmpegResource, ffmpegFile);
					}
				}
			}

			ffmpegLocation = ffmpegFile.getAbsolutePath();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getFfmpegLocation() {
		return ffmpegLocation;
	}
	
	public static void killFfmpeg() {		
		try {
			String cmdKillCommand = "TASKKILL /F /IM ffmpeg*";
			Process process = Runtime.getRuntime().exec(cmdKillCommand);
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
