package Unified;

import Pojo.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.json.JsonObjectMapper;
import redis.clients.jedis.json.Path;
import redis.clients.jedis.json.Path2;
import redis.clients.jedis.search.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class unifiedStart {
    private static final Logger logger = LoggerFactory.getLogger(unifiedStart.class);
    public static void main(String[] args) throws JsonProcessingException {
        UnifiedJedis ujedis = new UnifiedJedis(new HostAndPort("localhost",10001));
        String[] category = {"ab","bc","de"};
        Employee e = new Employee("emp1",34,category);
        logger.info("adding a json doc");
        // this is ugly . figure out a cleaner way to pass the object ?
        ujedis.jsonSet("json_arr:1", Path2.of("$"),"{\"key\":\"val\"}");
        ujedis.jsonSet("json_arr:2", Path2.of("$"),"{\"key\":\"values\",\"obj\":\"dummy\"}");
        // can do in-place edits
        ujedis.jsonSet("json_arr:2", Path2.of("$.obj"),"\"update\"");
        // define schema for the doc
        Schema schema = new Schema()
                .addTextField("$.key",1.0).as("key");
        //define index type and doc of interest thru prefix
        IndexDefinition idx = new IndexDefinition(IndexDefinition.Type.JSON).setPrefixes("json_arr");
        // create the index in redis with the defn and schema
        ujedis.ftCreate("java_cli_idx", IndexOptions.defaultOptions().setDefinition(idx),schema);
        // perform search using index and get the results
        SearchResult sr = ujedis.ftSearch("java_cli_idx","va*");
        List<Document> lsd = sr.getDocuments();
        lsd.forEach(val->{
            // each matched result has its content in an iterable struct called properties
            // that struct is a key-value mapping of our doc
            logger.info((String) val.getProperties().iterator().next().getValue());
        });
        while (1==1)
        {
         // intentionally blank 
        }
    }
}
