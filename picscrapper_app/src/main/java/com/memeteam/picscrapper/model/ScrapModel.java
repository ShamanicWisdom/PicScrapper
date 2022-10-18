package com.memeteam.picscrapper.model;

import java.io.File;

public class ScrapModel {
	
	private String website;
	private int subpagesToHandle;
	private File savingLocation;
	private String duplicateBehaviorPattern;
	private boolean headlessMode;
	private boolean automatedWebDriverSelector;
	String tag;
	String community;
	String login;
	String password;
	
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
	
	public String getTag() { return tag; }
	
	public void setTag(String tag) { this.tag = tag; }
	
	public String getCommunity() { return community; }
	
	public void setCommunity(String community) { this.community = community; }
	
	public String getLogin() { return login; }
	
	public void setLogin(String login) { this.login = login; }
	
	public String getPassword() { return password; }
	
	public void setPassword(String password) { this.password = password; }
	
	public void setScrapModel(String website, int subpagesToHandle, File savingLocation, String duplicateBehaviorPattern, boolean headlessMode, boolean automatedWebDriverSelector, String tag, String community, String login, String password) {
		this.website = website;
		this.subpagesToHandle = subpagesToHandle;
		this.savingLocation = savingLocation;
		this.duplicateBehaviorPattern = duplicateBehaviorPattern;
		this.headlessMode = headlessMode;
		this.automatedWebDriverSelector = automatedWebDriverSelector;
		this.tag = tag; 
		this.community = community;
		this.login = login;
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "Content of this ScrapModel:"
				+ "\nWebsite: [" + website + "]" 
				+ "\nSubpages: [" + subpagesToHandle + "]" 
				+ "\nSaving location: [" + savingLocation + "]" 
				+ "\nDuplicate behavior pattern: [" + duplicateBehaviorPattern + "]" 
				+ "\nHeadless mode: [" + headlessMode + "]" 
				+ "\nDriver selector: [" + automatedWebDriverSelector + "]"
				+ "\nTag: [" + tag + "]"
				+ "\nCommunity: [" + community + "]"
				+ "\nLogin: [" + login + "]";
	}
}
