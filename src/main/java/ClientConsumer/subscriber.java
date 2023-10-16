package ClientConsumer;

import ClientPool.jedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.Callable;

public class subscriber implements Callable<String> {
    private jedisPool pool;
    private Jedis jedis;
    private Logger logger = LoggerFactory.getLogger(subscriber.class);
    public subscriber(jedisPool pool)
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
        String channel = "channel1";
        // this is a blocking call !!!!
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                logger.info("subscribed message has been received with value:"+message);
            }
        },channel);
        jedis.close();
        return "done";
    }
}
