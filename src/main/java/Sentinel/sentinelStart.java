package Sentinel;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.*;

public class sentinelStart {
private static final Logger logger = LoggerFactory.getLogger(sentinelStart.class);
    public static void main(String[] args) throws InterruptedException {
        try {
            // define sentinel host and port
            Set<HostAndPort> set = new HashSet<>();
            set.add(new HostAndPort("localhost", 26379));
            // sentinel pool definition
            // this will behind the scenes identify the master at any given instance and setup the jedis connections that will be drawn from the pool
            JedisSentinelPool spool = new JedisSentinelPool("mymaster", set, new JedisClientConfig() {
                @Override
                public RedisProtocol getRedisProtocol() {
                    return JedisClientConfig.super.getRedisProtocol();
                }
            }, new JedisClientConfig() {
                @Override
                public RedisProtocol getRedisProtocol() {
                    return JedisClientConfig.super.getRedisProtocol();
                }
            });
            // gets a connection pointing to current master
            Jedis jedis = spool.getResource();
            String s = jedis.get("java_client_string");
            logger.info("sentinel ret value:" + s);
            // testing a few sentinel api calls
            List<Map<String,String>> lmap = new ArrayList<>();
            // this is weird . to use the api i need to setup a jedis resource pointing to sentinel process
            Jedis ljedis = new Jedis(new HostAndPort("localhost",26379));
            // getting the master node info
            lmap = ljedis.sentinelMasters();
            lmap.forEach(e-> e.entrySet().forEach(p->logger.info(p.getValue())));
            List <String> lstring = ljedis.sentinelGetMasterAddrByName("mymaster");
            lstring.forEach(e->logger.info("master addr is:"+e));
            Thread.sleep(20000);
            s = jedis.get("java_client_string");
            logger.info("sentinel second ret value:" + s);
        }
        catch(Exception e)
        {
          logger.info("bye");
        }
    }

}
