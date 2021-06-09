import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class ExtractDelta {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// parse arguments
		ArgumentParser parser = ArgumentParsers.newFor("ExtractDelta").build()
                .defaultHelp(true)
                .description("Extract delta of files.");
		parser.addArgument("first")
				.required(true)
				.help("First file");
		parser.addArgument("second")
				.required(true)
				.help("Second file");
		
		Namespace namespace = null;
        
        try {
        	namespace = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        
        // Read arguments
        String filenameFirst = namespace.getString("first");
        String filenameSecond = namespace.getString("second");
        process(filenameFirst, filenameSecond);
	}
	
	private static void process(String filenameFirst, String filenameSecond) {
		// Populate hashmap key with source sentences
		Map<String, String> sentMap = buildMapKeys(filenameFirst);
		System.err.println("Hash map keys created");
		// Match source file with target file
		getDelta(sentMap, filenameSecond);
	}
	
	/***
	 * Match sentences from source and target files, pivoting off sources from both files
	 * @param sourceMap
	 * @param filenameTarget
	 */
	private static void getDelta(Map<String, String> sentMap, String filenameSecond) {
		try {
			// Read
			FileInputStream fileInputStream = new FileInputStream(filenameSecond);
	        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
	        BufferedReader reader = new BufferedReader(inputStreamReader);
	        
	        String line = null;
	        
	        // Progress tracking variables
	        int iDotNLCount = 1000;
            int iDotCount = 10;
            int iFileCount = 0;
	        while ((line = reader.readLine()) != null) {
	        	try {
	        		// Print progress
	        		if (iFileCount % iDotNLCount == 0) {
	                    if (iFileCount > 0) {
	                        System.err.print("\n");
	                    }
	                    System.err.print(iFileCount + " ");
	                }
	                if (iFileCount % iDotCount == 0) {
	                    System.err.print(".");
	                }
	                iFileCount++;
		        	// If sentence not exist in the first file then keep it
		        	if (sentMap.get(line) == null) {
		        		System.out.println(line);
		        	}
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}
	        	
	        }
	        
	        reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * Initialise hashmap with dedupped source sentences as keys 
	 * @param filenameKeys
	 * @return
	 */
	private static Map<String, String> buildMapKeys(String filenameKeys) {
		Map<String, String> sourceMap = new HashMap<String, String>();
		try {
			// Read
			FileInputStream fileInputStream = new FileInputStream(filenameKeys);
	        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
	        BufferedReader reader = new BufferedReader(inputStreamReader);
	        
	        String line = null;
	        while ((line = reader.readLine()) != null) {
	        	// Insert non-empty sentences as keys
	        	if (!line.equals("")) {
	        		sourceMap.put(line, "1");
	        	}
	        }
	        
	        reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceMap;
	}

}
