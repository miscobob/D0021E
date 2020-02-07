package Sim;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	public static void LogTime(String fileToWrite, String data) throws IOException
	{
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite, true));
			writer.write(data + "\n");
			writer.close();
	}
	
}
