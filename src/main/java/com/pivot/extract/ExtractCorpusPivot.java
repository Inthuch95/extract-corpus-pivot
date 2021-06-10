package com.pivot.extract;
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

public class ExtractCorpusPivot {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// parse arguments
		ArgumentParser parser = ArgumentParsers.newFor("ExtractCorpusPivot").build()
                .defaultHelp(true)
                .description("Extract corpus based on source.");
		parser.addArgument("source")
				.required(true)
				.help("Source file");
		parser.addArgument("target")
				.required(true)
				.help("Target file");
		parser.addArgument("keys")
				.required(true)
				.help("Shared source sentences");
        
        Namespace namespace = null;
        
        try {
        	namespace = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        
        // Read arguments
        String filenameSource = namespace.getString("source");
        String filenameTarget = namespace.getString("target");
        String filenameKeys = namespace.getString("keys");
        process(filenameSource, filenameTarget, filenameKeys);
	}
	
	private static void process(String filenameSource, String filenameTarget, String filenameKeys) {
		// Populate hashmap key with source sentences
		Map<String, String> sourceMap = buildMapKeys(filenameKeys);
		System.err.println("Hash map keys created");
		// Insert target sentences of source file into hashmap
		sourceMap = buildSourceMap(sourceMap, filenameSource);
		System.err.println("Loaded source sentences");
		// Match source file with target file
		matchSourceTarget(sourceMap, filenameTarget);
	}
	
	/***
	 * Match sentences from source and target files, pivoting off sources from both files
	 * @param sourceMap
	 * @param filenameTarget
	 */
	private static void matchSourceTarget(Map<String, String> sourceMap, String filenameTarget) {
		try {
			// Read
			FileInputStream fileInputStream = new FileInputStream(filenameTarget);
	        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
	        BufferedReader reader = new BufferedReader(inputStreamReader);
	        
	        String line = null;
	        String source = ""; 
	        String target = "";
	        
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
	        		source = line.split("\t")[0];
		        	target = line.split("\t")[1];
		        	// If they share source sentence then output the target from both files
		        	if (sourceMap.get(source) != null) {
		        		System.out.println(sourceMap.get(source) + "\t" + target);
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
	 * Insert target sentences from source file into hashmap
	 * @param sourceMap
	 * @param filenameSource
	 * @return
	 */
	
	private static Map<String, String> buildSourceMap(Map<String, String> sourceMap, String filenameSource) {
		try {
			// Read
			FileInputStream fileInputStream = new FileInputStream(filenameSource);
	        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
	        BufferedReader reader = new BufferedReader(inputStreamReader);
	        
	        String line = null;
	        String source = ""; 
	        String target = "";
	        while ((line = reader.readLine()) != null) {
	        	try {
	        		source = line.split("\t")[0];
		        	target = line.split("\t")[1];
		        	// If source is in the hashmap then insert target
		        	if (sourceMap.get(source) != null) {
		        		sourceMap.put(source, target);
		        	}
	        	} catch (Exception e) {
	        		e.printStackTrace();
	        	}
	        }
	        
	        reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceMap;
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
	        		sourceMap.put(line, "");
	        	}
	        }
	        
	        reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceMap;
	}
}
