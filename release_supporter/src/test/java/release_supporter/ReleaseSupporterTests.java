package release_supporter;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
/**
 * Release Supporter module is considered as single pack to Tests what will be executed as a part of SuperPOM execution.
 * During the test execution:
 * - generated JAR from utility_tool module will be moved from /target to /release folder
 * - JAR name will be checked and compared with the one contained within .bat file
 * - if names won't be the same - .bat file will be updated - old .JAR name will be replaced to the new one.
 */

public class ReleaseSupporterTests {
    @Test
    public void moveNewlyGeneratedJarFileAndUpdateRunnerFile() throws Exception {
        System.out.println("Attempting to move generated JAR file into /release folder...");
        System.out.println("1. Getting proper paths...");
        //Getting proper paths.
        String properGeneratedJarPath = System.getProperty("user.dir");
        String properReleaseLocation = properGeneratedJarPath.replace("_supporter", "");
        properGeneratedJarPath = properGeneratedJarPath.replace("release_supporter", "");
        properGeneratedJarPath += "picscrapper_app\\target\\";
        properReleaseLocation += "\\";
        System.out.println("Release location: " + properReleaseLocation);
        System.out.println("JAR location    : " + properGeneratedJarPath);
        System.out.println("2. Gripping proper files...");
        File generatedJarDirectory = new File(properGeneratedJarPath);
        File releaseLocation = new File(properReleaseLocation);
        File generatedJarFile = new File("");
        File runnerFile = new File("");
        String[] generatedJarDirectoryContent = generatedJarDirectory.list();
        assert generatedJarDirectoryContent != null;
        for(String fileName: generatedJarDirectoryContent) {
            File file = new File(properGeneratedJarPath + fileName);
            if(file.getName().contains("jar-with-dependencies"))
                generatedJarFile = file;
        }
        System.out.println("2.1 New generated JAR file found...");
        String[] releaseLocationDirectoryContent = releaseLocation.list();
        assert releaseLocationDirectoryContent != null;
        for(String fileName: releaseLocationDirectoryContent) {
            File file = new File(properReleaseLocation + fileName);
            if (file.getName().contains("jar-with-dependencies")) {
                file.delete();
                System.out.println("2.2 Old generated JAR file from /release folder found and deleted...");
            }
            if(file.getName().equalsIgnoreCase("run me.bat")) {
                runnerFile = file;
                System.out.println("2.3 Runner file found...");
            }
        }
        System.out.println("3. Copying newly generated JAR file into /release folder...");
        if(generatedJarFile.exists())
            FileUtils.copyFileToDirectory(generatedJarFile, releaseLocation);
        else throw new Exception("Something went wrong with copying generated JAR file...");
        if(!runnerFile.exists())
            throw new Exception("Something went wrong with checking the Runner file...");
        System.out.println("4. Checking and updating Runner file...");
        String runnerFileContent = FileUtils.readFileToString(runnerFile, StandardCharsets.UTF_8);
        runnerFileContent = runnerFileContent.substring(0, 65);
        runnerFileContent += "\"" + generatedJarFile.getName() + "\"";
        FileUtils.writeStringToFile(runnerFile, runnerFileContent, StandardCharsets.UTF_8);
        System.out.println("Everything went correctly.");
    }
}