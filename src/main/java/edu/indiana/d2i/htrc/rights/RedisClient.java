package edu.indiana.d2i.htrc.rights;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

/*
 * Class that interacts with Redis, i.e., gets and sets values in Redis.
 */
public class RedisClient {
	private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);
	
	private JedisPool jedisPool;
	private int numHmsetsPerPipeline;
	
	public RedisClient(String redisHost) {
		this.jedisPool = new JedisPool(new JedisPoolConfig(), redisHost);
		numHmsetsPerPipeline = Config.INSTANCE.getRedisNumHmsetsPerPipeline();
	}
	
	// assign values to one or more hash fields at keys in redis
	public void setHashFieldValues(List<RedisHashKeyAndValues> hashes) {
		int size = hashes.size();
		if ((hashes == null) || (size == 0)) {
			return;
		}
		
        try (Jedis jedis = this.jedisPool.getResource()) {
        	Pipeline pipeline = jedis.pipelined();
        	int i = 0;
        	while (i < size) {
        		int numHmsets = 0;
        		while ((i < size) && (numHmsets < numHmsetsPerPipeline)) {
        			RedisHashKeyAndValues hashKeyAndValues = hashes.get(i);
        			pipeline.hmset(hashKeyAndValues.getKey(), hashKeyAndValues.getFieldNamesNValues());
        			numHmsets++;
        			i++;
        		}
        		pipeline.sync();
        	}
        	// logger.info("Time to set values for {} keys = {} seconds", hashes.size(), (end - start)/1000.0);
        } catch (Exception e) {
        	logger.error("Exception while trying to access redis: {}", e.getMessage(), e); 
        }
	}
	
	// return the no. of keys defined in redis
	public long getNumKeys() {
        try (Jedis jedis = this.jedisPool.getResource()) {
        	return jedis.dbSize();
        } catch (Exception e) {
        	logger.error("Exception while trying to access redis: {}", e.getMessage(), e);
        	return -1;
        }
	}

//	public List<String> getKeyValuesPipelined(List<String> keys) {
//		if (keys.size() == 0) {
//			return Collections.emptyList();
//		}
//		
//        try (Jedis jedis = this.jedisPool.getResource()) {
//        	Pipeline pipeline = jedis.pipelined();
//        	int i = 0; 
//        	int numMgets = 0;
//        	int size = keys.size();
//        	List<String> res = new ArrayList<String>();
//        	List<Response<List<String>>> batchRes;
//        	long start = System.currentTimeMillis();
//        	while (i < size) {
//        		batchRes = new ArrayList<Response<List<String>>>(numMgetsPerPipeline);
//        		while ((i < size) && (numMgets < numMgetsPerPipeline)) {
//        			int endIndex = Integer.min(i + numKeysPerMget, size);
//        			// System.out.println("iteration: i = " + i + ", endIndex = " + endIndex);
//        			// printArrayElems(keys.subList(i, endIndex).toArray(new String[numKeysPerMget]));
//        			//System.out.println("  list = " + keys.subList(i, endIndex).stream().collect(Collectors.joining(",", "[", "]")));
//        			batchRes.add(pipeline.mget(keys.subList(i, endIndex).toArray(new String[endIndex - i])));
//        			i += numKeysPerMget;
//        			numMgets++;
//        		}
//        		pipeline.sync();
//        		numMgets = 0;
//        		batchRes.forEach(response -> res.addAll(response.get()));
//        	}
//        	long end = System.currentTimeMillis();	
//        	logger.info("Time to get values for {} keys = {} seconds", keys.size(), (end - start)/1000.0);
//        	return res;
//        } catch (Exception e) {
//        	logger.error("Exception while trying to access redis: {}", e.getMessage(), e); 
//        	return Collections.emptyList();
//        }
//	}
}
