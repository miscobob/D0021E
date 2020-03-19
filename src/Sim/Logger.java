package Sim;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	public static String newLine = System.getProperty("line.separator");
	
	public static void LogTime(String fileToWrite, String data) throws IOException
	{
		
		//IOException: if the named file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite+".txt", true));
		writer.write(data + newLine);
		writer.close();
	}
}
