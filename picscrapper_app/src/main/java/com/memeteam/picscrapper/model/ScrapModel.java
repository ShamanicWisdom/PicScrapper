package com.memeteam.picscrapper.model;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ScrapModel {
	
	private String website;
	private int subpagesToHandle;
	private File savingLocation;
	private String duplicateBehaviorPattern;
	private boolean headlessMode;
	private boolean automatedWebDriverSelector;
	
	public String getWebsite() { return website; }
	
	public void setWebsite(String website) { this.website = website; }
	
	public int getSubpagesToHandle() { return subpagesToHandle; }
	
	public void setSubpagesToHandle(int subpagesToHandle) { this.subpagesToHandle = subpagesToHandle; }
	
	public File getSavingLocation() { return savingLocation; }
	
	public void setSavingLocation(File savingLocation) { this.savingLocation = savingLocation; }

	public String getDuplicateBehaviorPattern() { return duplicateBehaviorPattern; }
	
	public void setDuplicateBehaviorPattern(String duplicateBehaviorPattern) { this.duplicateBehaviorPattern = duplicateBehaviorPattern; }
	
	public boolean getHeadlessMode() { return headlessMode; }
	
	public void setHeadlessMode(boolean headlessMode) { this.headlessMode = headlessMode; }
	
	public boolean getAutomatedWebDriverSelector() { return automatedWebDriverSelector; }
	
	public void setAutomatedWebDriverSelector(boolean automatedWebDriverSelector) { this.automatedWebDriverSelector = automatedWebDriverSelector; }
	
	public void setScrapModel(String website, int subpagesToHandle, File savingLocation, String duplicateBehaviorPattern, boolean headlessMode, boolean automatedWebDriverSelector) {
		this.website = website;
		this.subpagesToHandle = subpagesToHandle;
		this.savingLocation = savingLocation;
		this.duplicateBehaviorPattern = duplicateBehaviorPattern;
		this.headlessMode = headlessMode;
		this.automatedWebDriverSelector = automatedWebDriverSelector;
	}
	
	@Override
	public String toString() {
		return "Content of this ScrapModel:"
				+ "\nWebsite: [" + website + "]" 
				+ "\nSubpages: [" + subpagesToHandle + "]" 
				+ "\nSaving location: [" + savingLocation + "]" 
				+ "\nDuplicate behavior pattern: [" + duplicateBehaviorPattern + "]" 
				+ "\nHeadless mode: [" + headlessMode + "]" 
				+ "\nDriver selector: [" + automatedWebDriverSelector + "]";
	}
}
