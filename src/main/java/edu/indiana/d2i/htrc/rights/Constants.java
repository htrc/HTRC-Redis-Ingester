package edu.indiana.d2i.htrc.rights;

public class Constants {
	public static final String PROPERTIES_FILENAME = "config.properties";
	
	public static final String REDIS_HOST_PROP_KEY = "redis-host";
	public static final String DEFAULT_REDIS_HOST = "localhost";
	
	public static final String REDIS_NUM_HMSETS_PER_PIPELINE_PARAM_KEY = "redis-num-hmsets-per-pipeline";
	public static final String DEFAULT_REDIS_NUM_HMSETS_PER_PIPELINE = "1000";
	
	public static final String REDIS_ACCESS_LEVEL_HASH_FIELD_NAME_PROP_KEY = "redis-access-level-hash-field-name";
	public static final String DEFAULT_REDIS_ACCESS_LEVEL_HASH_FIELD_NAME = "access-level";
	
	public static final String REDIS_AVAIL_STATUS_HASH_FIELD_NAME_PROP_KEY = "redis-avail-status-hash-field-name";
	public static final String DEFAULT_REDIS_AVAIL_STATUS_HASH_FIELD_NAME = "avail-status";
	
	public static final String REDIS_VOLUME_ID_KEY_PREFIX_PROP_KEY = "redis-volume-id-key-prefix";
	public static final String DEFAULT_REDIS_VOLUME_ID_KEY_PREFIX = "volume:";
	
	public static final String REDIS_VOLUME_ID_KEY_SUFFIX_PROP_KEY = "redis-volume-id-key-suffix";
	public static final String DEFAULT_REDIS_VOLUME_ID_KEY_SUFFIX = ":info";
}
