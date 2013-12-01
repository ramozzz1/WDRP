package util;

import java.io.FileWriter;
import java.io.IOException;

import main.Config;
import model.Node;
import model.Path;

public class IOUtils {
	
	public static void writePathToFile(String algName, Path path, long source, long target) {
		try
		{
		    FileWriter writer = new FileWriter(Config.PATHSDIR+algName+"_"+source+"_"+target + ".csv");
	 
		    writer.append("Lat");
		    writer.append("\t");
		    writer.append("Lon");
		    writer.append("\n");
		    
		    for (Node node : path.getNodes()) {
		    	writer.append(""+node.getLat());
		    	writer.append("\t");
		    	writer.append(""+node.getLon());
		    	writer.append("\n");
			}
		    
		    writer.flush();
		    writer.close();
		}
		catch(IOException e)
		{
		     System.err.println("Could not make file: " +e);
		} 
	}
}
