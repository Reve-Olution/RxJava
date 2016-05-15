package ch.sebooom.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

/**
 * Created by seb on 14.05.16.
 */
public class MongoService {

    //paramétrage mongodb
    final static String MONGODB_URL = "mongodb://localhost";
    final static MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGODB_URL));
    final static MongoDatabase rxDatabase = mongoClient.getDatabase("rx");


    public static IndiceDAO getIndiceDao () {
        return new IndiceDAO(rxDatabase);
    };
}
