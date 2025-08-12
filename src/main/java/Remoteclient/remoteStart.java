package Remoteclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

public class remoteStart {
    private static Logger logger = LoggerFactory.getLogger(remoteStart.class);

    public static void main(String[] args) {
        JedisCluster cl = null;
        try
        {
            cl = new JedisCluster(new HostAndPort("esmjaga-redis.canadacentral.cloudapp.azure.com",6379),10000,10);
            logger.info("connecting to redis cluster");
            cl.set("foo123","bar123");
            if(cl.ping().equalsIgnoreCase("pong"))
                logger.info("connection successful");
            else
            {
                cl.close();
                logger.info("connection failed");
            }
            cl.set("foo123","bar123");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            cl.close();
        }
    }
}
