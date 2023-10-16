package ClientPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class replicaPool {
    private JedisPool pool = null ;
    private Logger logger = LoggerFactory.getLogger(replicaPool.class);
    public replicaPool()
    {

    }

    public  void closePool() {
        if(pool!=null)
            pool.close();
    }

    public void createReplicaPool()
    {
        try {
            logger.info("connecting to a would-be replica");
            pool = new JedisPool( new JedisPoolConfig(),"localhost",10002);
        }
        catch(Exception e)
        {
            logger.info("can't connect to a world-be replica");
           if(pool!=null)
               pool.close();
        }

    }
    public Jedis createReplicaConnection()
    {
        try {
            logger.info("creating connection for would-be replica");
            Jedis jedis = pool.getResource();
            return jedis;
        }
        catch(Exception e)
        {
            logger.info("cannot create a connection for would-be replica");
        }
        return null;
    }

}
