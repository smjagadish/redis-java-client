package ClientWorker;

import ClientPool.jedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class pipelineProducer implements Callable<String> {
    private Logger logger = LoggerFactory.getLogger(pipelineProducer.class);
    private jedisPool pool;
    private Jedis jedis;
    public pipelineProducer(jedisPool pool)
    {
        this.pool = pool;
    }
    private void getConnection()
    {
        this.jedis = pool.getConnection();
    }
    @Override
    public String call() throws Exception {
        getConnection();
        Pipeline p = jedis.pipelined();
        p.set("java_client_pipeline_string","pipelined string value");
        // this is how you get the value , commenting now as i dont want to consume here
        // the ret_val is a future and a get on it can be done only after the sync()
        //Response<String> ret_val= p.get("java_client_pipeline_string");
        Map<String,String> hmap = new HashMap<>();
        hmap.put("pkey1","pval1");
        hmap.put("pkey2","pval2");
        hmap.put("pkey3","pval3");
        p.hset("java_client_pipeline_map",hmap);
        p.sync();
        return "done";
    }
}
