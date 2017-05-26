package edu.indiana.d2i.htrc.rights;

import java.util.Map;

public interface RedisHashKeyAndValues {
	  public String getKey();
	  public Map<String, String> getFieldNamesNValues();
}
