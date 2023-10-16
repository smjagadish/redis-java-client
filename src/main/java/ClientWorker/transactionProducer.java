package ClientWorker;

import ClientPool.jedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.Transaction;

import java.util.concurrent.Callable;

public class transactionProducer implements Callable<String> {
    private jedisPool pool;
    private Jedis jedis;
    private Logger logger = LoggerFactory.getLogger(transactionProducer.class);
    public transactionProducer(jedisPool pool)
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
        String key = "java_client_string";
        jedis.watch(key, "java_client_map");
        Transaction t = jedis.multi();
       // Thread.sleep(20000);
        t.get(key);
        t.set(key,"transaction_set_value");
        t.hset("java_client_map","f2","v2");
        t.exec();
        return "done";
    }
}
