package edu.indiana.d2i.htrc.rights;

import java.io.File;

import com.martiansoftware.jsap.JSAPResult;

public class Main {		
	private static final String VOL_ACCESS_LEVELS_FILE_ARG_KEY = "volAccessLevelsFile";
	private static final String AVAIL_VOLS_FILE_ARG_KEY = "availableVolsFile";
	private static final String UNAVAIL_VOLS_FILE_ARG_KEY = "unavailableVolsFile";
		
	public static void main(String[] args) {
		JSAPResult cmdLine = parseCmdLineArgs(args);
		
		RedisClient redisClient = new RedisClient(Config.INSTANCE.getRedisHost());
		
		File volAccessLevelsFile = cmdLine.getFile(VOL_ACCESS_LEVELS_FILE_ARG_KEY);
		if (volAccessLevelsFile != null) {
			// set access levels for volumes specified in the given file
			RightsIngester rightsIngester = new RightsIngester(volAccessLevelsFile, redisClient);
			rightsIngester.setAccessLevels();
		}
		
		File availableVolsFile = cmdLine.getFile(AVAIL_VOLS_FILE_ARG_KEY);
		if (availableVolsFile != null) {
			RightsIngester rightsIngester = new RightsIngester(availableVolsFile, redisClient);
			// set the availability status of all the volumes in availableVolsFile to true
			rightsIngester.setAvailabilityStatus(true);	
		}

		File unavailableVolsFile = cmdLine.getFile(UNAVAIL_VOLS_FILE_ARG_KEY);
		if (unavailableVolsFile != null) {
			RightsIngester rightsIngester = new RightsIngester(unavailableVolsFile, redisClient);
			// set the availability status of all the volumes in unavailableVolsFile to false
			rightsIngester.setAvailabilityStatus(false);	
		}
		
	}
	
	private static JSAPResult parseCmdLineArgs(String[] args) {
		// parse command line arguments
		JSAPResult cmdLine = ArgumentParser.parseArguments(args);
		if (cmdLine == null) {
			printUsage();
			System.exit(1);
		}
		return cmdLine;
	}
	
	private static void printUsage() {
		System.out.println("Usage: java -jar HTRC-Redis-Ingester.jar [-l volAccessLevelsFile] [-t availableVolsFile] [-f unavailableVolsFile]");
	}
}