package com.memeteam.picscrapper.exception;

public class DriverNotAvailableException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	String localDriverVersion = "";
		
	public DriverNotAvailableException(String localDriverVersion) {
        super("Your version of Web Browser is not yet supported by this application. Please add " + localDriverVersion + " to [driverdirectory] folder and re-run the application!");
        this.localDriverVersion = localDriverVersion;
    }
	
	public String getMessage() {
		return "Your version of Web Browser is not yet supported by this application. \nPlease add " + this.localDriverVersion + " to [driverdirectory] folder and re-run the application!";
    }
}

class ExceptionHelper {
    public static String getMessage(Exception e) {
        return e.getMessage();
    }
}