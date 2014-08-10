package org.wdrp.core.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.wdrp.core.Config;
import org.wdrp.core.model.Node;
import org.wdrp.core.model.Path;

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
	
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		return file.delete();
	}
	
	/**
	 * @param file
	 * @param content
	 */
	public static void writeContentToFile(String fileName, String content) {
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(fileName), "utf-8"));
		    writer.write(content);
		} catch (IOException ex) {
			System.err.println("Problem with reading file:"+fileName);
		} 
		finally {try {writer.close();} catch (Exception ex) {}}
	}
}
