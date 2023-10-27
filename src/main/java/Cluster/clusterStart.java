package Cluster;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class clusterStart {
    private static final Logger logger = LoggerFactory.getLogger(clusterStart.class);
    public static void main(String[] args)
    {
        logger.info("starting a redis cluster client");
        Set<HostAndPort> set = new HashSet<>();
        // it is enough to provide a subset (even one) of the cluster nodes
        // client will auto-discover other cluster members
       //set.add(new HostAndPort("localhost",30001));
        set.add(new HostAndPort("localhost",30002));
       //set.add(new HostAndPort("localhost",30003));
        //set.add(new HostAndPort("localhost",30004));
        //set.add(new HostAndPort("localhost",30005));
        //set.add(new HostAndPort("localhost",30006));
        JedisCluster cluster = new JedisCluster(set,10000,10);
        // client exposes API to return the 'internal' connection pool used for each of the involved/associated nodes
        Map<String, ConnectionPool> pool = cluster.getClusterNodes();
        // printing the cluster node (ip and port info)
        pool.entrySet().forEach(e->{
            logger.info(e.getKey());
        });
        // the client is smart enough to connect to the correct 'node' which has the key 'hello'
        // my guess is that it uses a connection pool under the hood
        // each connection pool manages connections automatically
        logger.info(cluster.get("hello"));
    }
}
