package edu.indiana.d2i.htrc.rights;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import com.opencsv.CSVReader;

/*
 * Class to set access levels and availability status of volumes in Redis.
 */
public class RightsIngester {
	private static final Logger logger = LoggerFactory.getLogger(RightsIngester.class);

	private File volumeInfoFile;
	private RedisClient redisClient;

	public RightsIngester(File volumeInfoFile, RedisClient redisClient) {
		super();
		this.volumeInfoFile = volumeInfoFile;
		this.redisClient = redisClient;
	}
	
	// sets the access levels in redis of the volumes in volumeInfoFile to the access levels associated with the volume ids in the file; 
	// only the access level fields of the hashes corresponding to volumes in redis are set, and not the availability status
	public void setAccessLevels() {
		try {
			CSVReader reader = new CSVReader(new FileReader(this.volumeInfoFile), '\t');

			logger.info("No. of keys in redis before update = {}", this.redisClient.getNumKeys());

			// no. of lines to read at a time from volumeAccessLevelsFile; set this to the same value as the no. of hash set ops in a pipleine
			int batchSize = Config.INSTANCE.getRedisNumHmsetsPerPipeline();

			long start = System.currentTimeMillis();
			int totalSize = 0;
			List<Map.Entry<String, String>> volAccessLevelsList = readNextBatch(reader, batchSize);
			while (volAccessLevelsList.size() > 0) {
				System.out.println("-----------");
				totalSize += volAccessLevelsList.size();
				this.redisClient.setHashFieldValues(createHashesWithAccessLevels(volAccessLevelsList));
				volAccessLevelsList = readNextBatch(reader, batchSize);
			}
			long end = System.currentTimeMillis();
			logger.info("Time taken to set access levels for {} volumes = {} seconds", totalSize, (end - start)/1000.0);
			logger.info("No. of keys in redis after update = {}", this.redisClient.getNumKeys());	
		} catch (IOException e) {
			logger.error("Exception while setting access levels in file {}", this.volumeInfoFile, e);
		}
	}
	
	// sets the availability status in redis of all the volumes in volumeInfoFile to "true" or "false"; 
	// only the availability status fields of the hashes corresponding to volumes in redis are set
	public void setAvailabilityStatus(boolean availStatus) {		
		String availability = (availStatus ? "true" : "false");
		
		try (BufferedReader reader = new BufferedReader(new FileReader(this.volumeInfoFile))) {
			logger.info("No. of keys in redis before update = {}", this.redisClient.getNumKeys());
			
			// no. of lines to read at a time from volumeAccessLevelsFile; set this to the same value as the no. of hash set ops in a pipleine
			int batchSize = Config.INSTANCE.getRedisNumHmsetsPerPipeline();

			long start = System.currentTimeMillis();
			int totalSize = 0;
			List<String> volIdsList = readNextBatch(reader, batchSize);
			while (volIdsList.size() > 0) {
				System.out.println("-----------");
				totalSize += volIdsList.size();
				this.redisClient.setHashFieldValues(createHashesWithAvailStatus(volIdsList, availability));
				volIdsList = readNextBatch(reader, batchSize);
			}
			long end = System.currentTimeMillis();
			logger.info("Time taken to set availability status for {} volumes = {} seconds", totalSize, (end - start)/1000.0);
			logger.info("No. of keys in redis after update = {}", this.redisClient.getNumKeys());	
		} catch (IOException e) {
			logger.error("Exception while setting availability status of volumes in file {}", this.volumeInfoFile, e);
		}
	}
	
	// given a list of (volId, accessLevel) tuples, create a list of RedisHashKeyAndValues, each RedisHashKeyAndValues object containing the volume 
	// id key, and the value of the access level field of the hash at the key in redis; volAccessLevelsList contains the volume ids and their access 
	// levels; this method, unlike createHashesWithAllFields, does not set the availability status of the hash; using this method there are no 
	// constraints on the ordering of ingestion into Redis and Cassandra, i.e., the ingestions may be performed in any order
	private List<RedisHashKeyAndValues> createHashesWithAccessLevels(List<Map.Entry<String, String>> volAccessLevelsList) {
		return volAccessLevelsList.stream().map(entry -> VolumeRightsInfo.volRightsWithAccessLevel(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}
	
	// given a list of (volId, accessLevel) tuples, create a list of RedisHashKeyAndValues, each RedisHashKeyAndValues object containing the volume 
	// id key, and the values of the access level and availability status fields of the hash at the key in redis; volAccessLevelsList contains the 
	// volume ids and their access levels; during initialization the availability status, signifying availability at HTRC (in Cassandra, at this 
	// time), is set to false; using this method enforces an ordering on ingestion into Redis and Cassandra, i.e., ingestion into Redis has to 
	// be performed before ingestion into Cassandra
	private List<RedisHashKeyAndValues> createHashesWithAllFields(List<Map.Entry<String, String>> volAccessLevelsList) {
		return volAccessLevelsList.stream().map(entry -> VolumeRightsInfo.volRightsWithAllFields(entry.getKey(), entry.getValue(), "false")).collect(Collectors.toList());
	}

	private List<RedisHashKeyAndValues> createHashesWithAvailStatus(List<String> volIdsList, String availStatus) {
		return volIdsList.stream().map(volumeId -> VolumeRightsInfo.volRightsWithAvailStatus(volumeId, availStatus))
				.collect(Collectors.toList());
	}
	
	private List<Map.Entry<String, String>> readNextBatch(CSVReader reader, int batchSize) throws IOException {
		int count = 0;
		String [] nextLine;
		List<Map.Entry<String, String>> volAccessLevelsList = new ArrayList<>();
	    while ((count < batchSize) && ((nextLine = reader.readNext()) != null)) {
	    	count++;
	    	// each line is expected to have 2 fields, the volume id and the access level, separated by a tab
	    	if (nextLine.length == 2) {
	    		System.out.println(nextLine[0] + ", " + nextLine[1]);
	    		volAccessLevelsList.add(new AbstractMap.SimpleEntry<>(nextLine[0], nextLine[1]));
	    	} else {
	    		logger.debug("Unexpected line in TSV file; each line should have 2 fields (volume id, access level): {}", arrayToString(nextLine, "\t"));
	    	}
	    }
	    return volAccessLevelsList;
	}

	private List<String> readNextBatch(BufferedReader reader, int batchSize) throws IOException {
		int count = 0;
		String line;
		List<String> result = new ArrayList<>();
	    while ((count < batchSize) && ((line = reader.readLine()) != null)) {
	    	count++;
	    	System.out.println("<" + line + ">");
	    	result.add(line);
	    }
	    return result;
	}
	
	private <T> String arrayToString(T[] a, String sep) {
		if ((a == null) || (a.length == 0)) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		for(T t : a) {
		    sb.append(t).append(sep);
		}
		return sb.toString();
	}	
}
