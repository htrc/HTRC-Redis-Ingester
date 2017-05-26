package edu.indiana.d2i.htrc.rights;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Config {
	INSTANCE;
	
	private String redisHost;
	private int redisNumHmsetsPerPipeline;
	private String redisAccessLevelHashFieldName;
	private String redisAvailStatusHashFieldName;
	private String redisVolumeIdKeyPrefix;
	private String redisVolumeIdKeySuffix;
	
	Config() {
		Properties props = new Properties();
		String propFile = Constants.PROPERTIES_FILENAME;
		Logger logger = LoggerFactory.getLogger(Config.class);
		
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(propFile)) {
			if (is != null) {
				props.load(is);
				this.redisHost = props.getProperty(Constants.REDIS_HOST_PROP_KEY, Constants.DEFAULT_REDIS_HOST);
				this.redisNumHmsetsPerPipeline = Integer.parseInt(props.getProperty(Constants.REDIS_NUM_HMSETS_PER_PIPELINE_PARAM_KEY, Constants.DEFAULT_REDIS_NUM_HMSETS_PER_PIPELINE));
				this.redisAccessLevelHashFieldName = props.getProperty(Constants.REDIS_ACCESS_LEVEL_HASH_FIELD_NAME_PROP_KEY, Constants.DEFAULT_REDIS_ACCESS_LEVEL_HASH_FIELD_NAME);
				this.redisAvailStatusHashFieldName = props.getProperty(Constants.REDIS_AVAIL_STATUS_HASH_FIELD_NAME_PROP_KEY, Constants.DEFAULT_REDIS_AVAIL_STATUS_HASH_FIELD_NAME);
				this.redisVolumeIdKeyPrefix = props.getProperty(Constants.REDIS_VOLUME_ID_KEY_PREFIX_PROP_KEY, Constants.DEFAULT_REDIS_VOLUME_ID_KEY_PREFIX);
				this.redisVolumeIdKeySuffix = props.getProperty(Constants.REDIS_VOLUME_ID_KEY_SUFFIX_PROP_KEY, Constants.DEFAULT_REDIS_VOLUME_ID_KEY_SUFFIX);
			} else {
				logger.error("In Config(): unable to find properties file {}", propFile);
				setDefaultValues();
			}	
		} catch (IOException e) {
			logger.error("In Config(): error while reading properties from file {}", propFile);
			setDefaultValues();
		}
	}
	
	private void setDefaultValues() {
		this.redisHost = Constants.DEFAULT_REDIS_HOST;
		this.redisNumHmsetsPerPipeline = Integer.parseInt(Constants.DEFAULT_REDIS_NUM_HMSETS_PER_PIPELINE);
		this.redisAccessLevelHashFieldName = Constants.DEFAULT_REDIS_ACCESS_LEVEL_HASH_FIELD_NAME;
		this.redisAvailStatusHashFieldName = Constants.DEFAULT_REDIS_AVAIL_STATUS_HASH_FIELD_NAME;
		this.redisVolumeIdKeyPrefix = Constants.DEFAULT_REDIS_VOLUME_ID_KEY_PREFIX;
		this.redisVolumeIdKeySuffix = Constants.DEFAULT_REDIS_VOLUME_ID_KEY_SUFFIX;
	}
	
	public String getRedisHost() {
		return redisHost;
	}

	public int getRedisNumHmsetsPerPipeline() {
		return redisNumHmsetsPerPipeline;
	}

	public String getRedisAccessLevelHashFieldName() {
		return redisAccessLevelHashFieldName;
	}


	public String getRedisAvailStatusHashFieldName() {
		return redisAvailStatusHashFieldName;
	}

	public String getRedisVolumeIdKeyPrefix() {
		return redisVolumeIdKeyPrefix;
	}

	public String getRedisVolumeIdKeySuffix() {
		return redisVolumeIdKeySuffix;
	}
}
