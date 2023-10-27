package ClientConsumer;

import ClientPool.jedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.json.Path2;
import redis.clients.jedis.params.XReadParams;
import redis.clients.jedis.resps.StreamEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class streamReader implements Callable<String> {
    private jedisPool pool;
    private Jedis jedis;
    private Logger logger = LoggerFactory.getLogger(streamReader.class);
    public streamReader(jedisPool pool)
    {
        this.pool = pool;
    }
    private void createConnection()
    {
        jedis = pool.getConnection();
    }
    @Override
    public String call() throws Exception {
        createConnection();
        XReadParams params = new XReadParams()
                .block(0); // doesn't work ??
        String key = "java_client_stream";
        Map<String, StreamEntryID> smap = new HashMap<>();
        smap.put(key,new StreamEntryID()); // why cant i get only the new values ??
        List<Map.Entry<String, List<StreamEntry>>> slist;
        slist = jedis.xread(params,smap);
        slist.forEach(v->{

            v.getValue().forEach(val->logger.info("dumping:"+val));
        });
        jedis.close();
        return "done";
    }
}
