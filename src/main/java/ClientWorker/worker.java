package ClientWorker;

import ClientConstants.constants;
import ClientPool.jedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.XAddParams;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.params.XReadParams;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class worker implements Callable<String> {
    private Logger logger = LoggerFactory.getLogger(worker.class);
    private jedisPool pool;
    private constants.ds_type type;
    public worker(constants.ds_type type , jedisPool pool)
    {
        this.pool = pool;
        this.type = type;
    }

    @Override
    public String call() throws Exception {
        Jedis jedis =  pool.getConnection();
        logger.info("worker connection check in progress");
        String result = jedis.ping("worker connection check");
        logger.info(result+": success");
        String key;
        String message;
        switch (type)
        {
            case hash:
                Map<String,String> map = new HashMap<>();
                map.put("key1","val1");
                map.put("key2","val2");
                key= "java_client_map";
                jedis.hset(key,map);
                break;
            case string:
                message = "First redis message from client worker";
                key = "java_client_string";
                jedis.set(key.getBytes(StandardCharsets.UTF_8),message.getBytes());
                break;
            case set:
                key = "java_client_set";
                message = "set data#1 from client worker";
                jedis.sadd(key,message);
                break;
            case lists:
                key = "java_client_lists";
                message = "O_ADD";
                jedis.lpush(key, message);
                break;
            case bitmap:
                key = "java_client_bitmap";
                //set the offset posn 1001
                jedis.setbit(key,1001,true);
                break;
            case bitfield:
                key = "java_client_bitfield";
                jedis.bitfield(key,"set","u32","#1","250");
                break;
            case bloomfilter:
                key = "java_client_bloom";
                // to do
                break;
            case sorted_set:
                key = "java_client_sortedset";
                Map<String,Double> zmap = new HashMap<>();
                zmap.put("member1",(double)100);
                zmap.put("member2",(double)2);
                zmap.put("member3",(double)31);
                zmap.put("member4",(double)15);
                jedis.zadd(key,zmap);
                break;
            case pub_sub:
                String channel = "channel1";
                message = "redis publish to channel1";
                jedis.publish(channel,message);
                break;
            case streams:
                 key = "java_client_stream";
                XAddParams params = new XAddParams()
                        .id("*")
                        .maxLen(100);
                Map<String,String> smap = new HashMap<>();
                smap.put("stream_key","stream_key_1");
                smap.put("stream_value","stream_value_1");
                jedis.xadd(key,params,smap);
                break;
            case hyperloglog:
                key = "java_client_hyperloglog";
                jedis.pfadd(key,"element1","element2","element3","element4");
                break;
            default:
                break;

        }
        jedis.close();
        return "done";
    }




}
