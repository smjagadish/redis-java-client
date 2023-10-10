package ClientConsumer;

import ClientConstants.constants;
import ClientPool.jedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.Callable;

public class consumer implements Callable<String> {
    private Logger logger = LoggerFactory.getLogger(consumer.class);
    private Jedis jedis;
    private constants.ds_type type;
    private jedisPool pool;
  public  consumer(constants.ds_type type, jedisPool pool)
    {
        this.type = type;
        this.pool = pool;
    }
    @Override
    public String call() throws Exception {
        jedis = pool.getConnection();
        logger.info("consumer connection check in progress");
        String result = jedis.ping("consumer connection check");
        logger.info(result+": success");
        String key;
        String message;
        switch(type)
        {
            case string:
                key="java_client_string";
                message = jedis.get(key);
                logger.info("consumed a string key with value:"+message);
                break;

            case lists:
                key = "java_client_lists";
                List<String> list = new ArrayList<>();
                list = jedis.lrange(key,0 , -1);
                Iterator<String> it = list.iterator();
                while(it.hasNext())
                {
                    logger.info("consumed list element with value:"+(String)it.next());

                }
                break;
            case sorted_set:
                key = "java_client_sortedset";
                List<String> slist = new ArrayList<>();
                slist = jedis.zrangeByScore(key,"-inf","+inf");
                Iterator<String> stringIterator = slist.iterator();
                while(stringIterator.hasNext())
                {
                    logger.info("consumed sorted set element with value:"+(String)stringIterator.next());

                }
                break;
            case set:
                key = "java_client_set";
                Set<String> set = new HashSet<>();
                set = jedis.smembers(key);
                for (String s:set
                     ) {
                    logger.info("consumed set element with value:"+ s);
                }
                break;
            case hash:
                key = "java_client_map";
                jedis.hgetAll(key).forEach((mkey,mvalue)->{
                    logger.info("consumed hash element with value:"+mvalue);
                });
                break;
            case bitmap:
                key = "java_client_bitmap";
                long val = jedis.bitcount(key);
                logger.info("number of bitmap entries with non-zero values are:"+val);
                break;
            case bitfield:
                key = "java_client_bitfield";
                jedis.bitfield(key,"get","u32","#1")
                        .forEach(bval->{
                            logger.info("consumed the populated bitfield element with value:"+bval);
                        });
                break;
            case pub_sub:
                // the subscribe action runs in a never-ending loop
                // i have tested the consumption , so commenting it out for now
                // will later move this to a different consumer thread
                /*
                String channel = "channel1";
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        logger.info("subscribed message has been received with value:"+message);
                    }
                },channel);

                 */
                break;
            case streams:
                 break;
            case hyperloglog:
                key = "java_client_hyperloglog";
                long count = jedis.pfcount(key);
                logger.info("probable count in hyperloglog is:"+count);
                break;
            default:
                break;

        }
        jedis.close();
        return "done";
    }
}
