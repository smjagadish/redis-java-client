package ReplicaManager;

import ClientPool.replicaPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

public class replicaSetup implements Runnable{
    private Logger logger = LoggerFactory.getLogger(replicaSetup.class);
    private Jedis jedis;
    private replicaPool pool;
    public replicaSetup(replicaPool pool)
    {
        this.pool = pool;
    }
    private Jedis getConnection()
    {
        this.jedis = pool.createReplicaConnection();
        return this.jedis;
    }
    @Override
    public void run() {
        Jedis jedis = getConnection();
        logger.info("setting up as a replica");
        // each time you need to change this IP as localhost wouldnt work
        jedis.slaveof("172.17.229.215",10001);
        logger.info("replica set-up. check in some time");
        jedis.close();
    }
}
