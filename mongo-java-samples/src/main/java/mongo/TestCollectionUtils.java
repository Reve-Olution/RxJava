package mongo;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by seb on 15.05.16.
 */
public class TestCollectionUtils {

    private final static Logger log = Logger.getLogger(TestCollectionUtils.class.getName());
    private static final Document testDocument = new Document("docType","test");



    private static Document getTestDocument (String user) {

        Document document = new Document("docType","test")
                .append("user",user);

        return document;
    }

    public static Document getRandomUserDocument(int maxUserNo) {

        int userIdRandom = (int) (Math.random() * maxUserNo);

        //getTestDocument();

        return new Document("docType","test").append("user","user_" + userIdRandom) ;
    }

    public static BulkWriteResult insertBulkUser(int size, MongoCollection<Document> collection) {

        long ts = new Date().getTime();
        List<WriteModel<Document>> bulk = new ArrayList<>();

        //log.info("Inserting " + size + " documents in collection " + collection.toString());
        for(int cpt = 0; cpt < size; cpt++){
            Document doc = TestCollectionUtils.getTestDocument("user_" + cpt);

            bulk.add(new InsertOneModel<>(doc));
        }

        return collection.bulkWrite(bulk);

    }

}
