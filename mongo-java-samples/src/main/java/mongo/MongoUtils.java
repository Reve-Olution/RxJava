package mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.logging.Logger;

import static mongo.MongoUtils.MongoURL.LOCALHOST_STANDARD;

/**
 * Created by seb on 15.05.16.
 */
public class MongoUtils {

    private final static Logger log = Logger.getLogger(MongoUtils.class.getName());

    enum MongoURL {
        LOCALHOST_STANDARD("localhost:27017");

        private String uri;

        MongoURL (String uri) {
            this.uri = uri;
        }

        public String uri () {
            return uri;
        }
    }

    private enum Databases {

        MONGO ("mongo"),
        MONGO_TEST ("mongo.test");

        private String dbName;

        Databases (String dbName) {
            this.dbName = dbName;
        }

        public String dbName () {
            return dbName;
        }
    }

    public enum Collections {
        TEST ("test"),
        USER ("user");

        private String colName;

        Collections (String colName) {
            this.colName = colName;
        }

        public String getColName () {
            return colName;
        }
    }

    public static MongoCollection<Document> getCollectionFor(MongoDatabase dataBase, Collections collection){

        log.info("Return collection for db:" + dataBase.getName() +", for collection: " + collection.colName);
        return dataBase.getCollection(collection.colName);
    }

    public static MongoDatabase getDatabase (MongoClient mongoClient) {
        if(null == mongoClient){
            log.info("MongoClient Null");
            throw new NullPointerException("MongoClient instance cannot be null");
        }

        return mongoClient.getDatabase(Databases.MONGO.dbName);
    }


    public static MongoClient getMongoClient() {
        log.info("Return client, url: " + LOCALHOST_STANDARD.uri());
        return new MongoClient(LOCALHOST_STANDARD.uri());
    }


}
