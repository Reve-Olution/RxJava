package mongo.find;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import mongo.MongoUtils;
import mongo.TestCollectionUtils;
import org.bson.Document;

import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by seb on 15.05.16.
 */
public class IndexSanple {

    private final static Logger log = Logger.getLogger(IndexSanple.class.getName());
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    public static void main(String[] args) {

        MongoClient client = MongoUtils.getMongoClient();
        MongoDatabase db = MongoUtils.getDatabase(client);

        MongoCollection<Document> collection = MongoUtils.getCollectionFor(db, MongoUtils.Collections.TEST);
        collection.drop();

        TestCollectionUtils.insertBulkUser(100, collection);

        Document user = TestCollectionUtils.getRandomUserDocument(100);
        findOne(collection, user);

        collection.createIndex(new Document("user",1));

        findOne(collection, user);

    }




    private static void findOne(MongoCollection<Document> collection, Document user) {

        long ts = new Date().getTime();

        System.out.println(getPrettyJson(collection.find(user).modifiers(new Document("$explain", true).append("verbose","allPlansExecution")).first().toJson()));

        log.info("Find One Index, in : " + (new Date().getTime() - ts));

    }

    private static String getPrettyJson (String uglyJson) {
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJson);
        return gson.toJson(je);
    }

}
