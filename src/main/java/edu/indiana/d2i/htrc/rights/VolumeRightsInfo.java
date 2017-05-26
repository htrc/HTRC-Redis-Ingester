package edu.indiana.d2i.htrc.rights;

import java.util.HashMap;
import java.util.Map;

/*
 * Class used to represent rights information (volume id keys, access levels, availability status), in a way that can be used to set values of
 * these attributes. 
 */
public class VolumeRightsInfo implements RedisHashKeyAndValues {
	private String volumeIdKey;
	private Map<String, String> rightsFields;

	// factory methods are used to construct instances of VolumeRightsInfo; this allows us to construct instances that have either the access level
	// field or the availability status field, both of which have the same type (String)
	
	// factory method to create a VolumeRightsInfo object with a value association for the access level field only
	public static VolumeRightsInfo volRightsWithAccessLevel(String volumeId, String accessLevel) {
		Map<String, String> rightsFields = new HashMap<String, String>(1);
		rightsFields.put(Config.INSTANCE.getRedisAccessLevelHashFieldName(), accessLevel);		
		return new VolumeRightsInfo(volumeId, rightsFields);
	}
	
	// factory method to create a VolumeRightsInfo object with a value association for the availability status field only
	public static VolumeRightsInfo volRightsWithAvailStatus(String volumeId, String availStatus) {
		Map<String, String> rightsFields = new HashMap<String, String>(1);
		rightsFields.put(Config.INSTANCE.getRedisAvailStatusHashFieldName(), availStatus);		
		return new VolumeRightsInfo(volumeId, rightsFields);
	}
	
	// factory method to create a VolumeRightsInfo object with value associations for all fields (access level and availability status)
	public static VolumeRightsInfo volRightsWithAllFields(String volumeId, String accessLevel, String availStatus) {
		Config config = Config.INSTANCE;
		Map<String, String> rightsFields = new HashMap<String, String>(2);
		rightsFields = new HashMap<String, String>(2);
		rightsFields.put(config.getRedisAccessLevelHashFieldName(), accessLevel);
		rightsFields.put(config.getRedisAvailStatusHashFieldName(), availStatus);
		return new VolumeRightsInfo(volumeId, rightsFields);
	}
	
	// this constructor should be accessed only by the factory methods in this class
	private VolumeRightsInfo(String volumeId, Map<String, String> rightsFields) {
		this.volumeIdKey = volumeIdToRedisKey(volumeId);
		this.rightsFields = rightsFields;		
	}
	
	@Override
	public String getKey() {
		return this.volumeIdKey;
	}

	@Override
	public Map<String, String> getFieldNamesNValues() {
		return this.rightsFields;
	}

	private String volumeIdToRedisKey(String volumeId) {
		Config config = Config.INSTANCE;
		return config.getRedisVolumeIdKeyPrefix() + volumeId + config.getRedisVolumeIdKeySuffix();
	}
}
