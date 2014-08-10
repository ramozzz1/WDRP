package org.wdrp.core.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

public class DownloadUtil {
	
	static final Logger logger = Logger.getLogger(DownloadUtil.class);
	
	public static void downloadAndSaveFromUrl(URL url, String localFilename, boolean decompress) throws IOException, URISyntaxException {
	    InputStream is = null;
	    FileOutputStream fos = null;

	    try {
	    	logger.debug("Downloading from: "+ url.toURI());
	    	
	    	URLConnection urlConn = url.openConnection(); //connect
	    	
	    	if(decompress) {
		    	String extension = FilenameUtils.getExtension(url.toURI().toString());
		    	if(extension.equals("bz2"))
		    		is = new BZip2CompressorInputStream(urlConn.getInputStream());
		    	else
		    		is = urlConn.getInputStream();
	    	}
	    	else
	    		is = urlConn.getInputStream();
	    	
	        fos = new FileOutputStream(localFilename);   //open outputstream to local file
	        
	        byte[] buffer = new byte[4096];              //declare 4KB buffer
	        int len;

	        //while we have availble data, continue downloading and storing to local file
	        while ((len = is.read(buffer)) > 0) {  
	            fos.write(buffer, 0, len);
	        }
	    } finally {
	        try {
	            if (is != null) {
	                is.close();
	            }
	        } finally {
	            if (fos != null) {
	                fos.close();
	                logger.debug("Done downloading, saved file as --> "+ localFilename);
	            }
	        }
	    }
	}
}
