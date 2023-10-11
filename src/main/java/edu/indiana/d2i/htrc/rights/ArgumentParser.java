package edu.indiana.d2i.htrc.rights;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

public class ArgumentParser {
    private static Parameter[] getApplicationParameters() {
        Parameter volAccessLevelsFile = new FlaggedOption("volAccessLevelsFile")
                .setStringParser(FileStringParser.getParser()
               					.setMustBeFile(true))
               	.setShortFlag('l')
               	.setRequired(false)
               	.setHelp("Set the access levels of the volumes in the given TSV file. The TSV file contains an HTRC volume id and access level on each line, separated by a tab.");

        Parameter availVolsFile = new FlaggedOption("availableVolsFile")
                .setStringParser(FileStringParser.getParser()
               					.setMustBeFile(true))
               	.setShortFlag('t')
               	.setRequired(false)
               	.setHelp("Set the availability status of volumes in the given TSV file to true. The text file contains an HTRC volume id per line.");
 
        Parameter unavailVolsFile = new FlaggedOption("unavailableVolsFile")
                .setStringParser(FileStringParser.getParser()
               					.setMustBeFile(true))
               	.setShortFlag('f')
               	.setRequired(false)
               	.setHelp("Set the availability status of volumes in the given TSV file to false. The text file contains an HTRC volume id per line.");

		Parameter redisHost = new FlaggedOption("redisHost")
				.setShortFlag('h')
				.setRequired(true)
				.setHelp("Redis host server.");

		Parameter redisPassword = new FlaggedOption("redisPassword")
				.setShortFlag('p')
				.setRequired(true)
				.setHelp("Redis server password.");
 
        return new Parameter[] {volAccessLevelsFile, availVolsFile, unavailVolsFile, redisHost, redisPassword};
    }

    private static String getApplicationHelp() {
        return "Application to set rights information (access levels, availability status) of volumes in Redis.";
    }

    public static JSAPResult parseArguments(String[] args) {
    	try {
    		SimpleJSAP jsap = new SimpleJSAP("Main", getApplicationHelp(), getApplicationParameters());
        	JSAPResult result = jsap.parse(args);
        	if (jsap.messagePrinted()) {
        		return null;
        	}
        	else return result;
        } catch (JSAPException e) {
        	System.out.println("Exception in parsing cmd line args: " + e);
        	return null;
        }
    }

}
