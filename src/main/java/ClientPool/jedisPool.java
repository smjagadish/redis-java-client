package ClientPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.UnifiedJedis;

public class jedisPool {
    private static Logger logger = LoggerFactory.getLogger(jedisPool.class);
    private JedisPool pool = null;
    public jedisPool()
    {

    }
   public void createPool()
    {
        logger.info("creating a jedis pool");
        try {
             pool = new JedisPool(new JedisPoolConfig(),"localhost",10001);

        }
        catch(Exception e)
        {
           logger.info("issues in creating pool");
           if(pool !=null)
           pool.close();
        }

    }
    public void  closePool()
    {
        logger.info("closing the jedis pool");
        try
        {
          pool.close();
        }
        catch (Exception e)
        {

        }
    }
   public  Jedis getConnection()
    {
        logger.info("checking for connection from the pool");
        try {
            Jedis jedis = pool.getResource();
            // enable below command if you have auth setup. ensure proper permissions at redis end
           // jedis.auth("user2","user2");
            if(jedis!=null)
            {
                logger.info("obtained connection");
                return jedis;
            }
            else {
                logger.info("no free connection try later");

            }
        }
        catch(Exception e)
        {
            logger.info("no free connection try later");
        }
        return null;
    }
}

