package Client;

import ClientConstants.constants;
import ClientConsumer.consumer;
import ClientConsumer.streamReader;
import ClientConsumer.subscriber;
import ClientPool.jedisPool;
import ClientWorker.pipelineProducer;
import ClientWorker.worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class clientStart {
    private static Logger logger = LoggerFactory.getLogger(clientStart.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("test");
        logger.info("starting a java redis client");
        final jedisPool pool ;
        pool = new jedisPool();
        pool.createPool();
        ExecutorService es = Executors.newFixedThreadPool(10);
        Future<String> res = null;
        for(constants.ds_type type : constants.ds_type.values())
        {
            worker w = new worker(type,pool);
           res = es.submit(w);
            logger.info("redis_action:" + type.name() + ":is:" +res.get());
        }
        logger.info("starting the consumption");

        for(constants.ds_type type : constants.ds_type.values())
        {
            if(type != constants.ds_type.pub_sub) {
                consumer c = new consumer(type, pool);
                res = es.submit(c);
                logger.info("redis_consume:" + type.name() + ":is:" + res.get());
            }

        }
        //spins up a new thread here
        es.submit(new subscriber(pool));
        // spins up another new thread for streams
        es.submit(new streamReader(pool));
        // spins up a new thread for pipelining
        es.submit(new pipelineProducer(pool));

        pool.closePool();
    }
}
