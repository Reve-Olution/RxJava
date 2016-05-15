/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.sebooom.observable.mongodb.dao;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Future;

import static com.mongodb.client.model.Filters.eq;

public class UserDAO {
    private final MongoCollection<Document> usersCollection;
    
    public UserDAO(final MongoDatabase userDatabase) {
        usersCollection = userDatabase.getCollection("user");
    }

    public boolean addUsers(List<Document> usersBson) {
    	 usersCollection.insertMany(usersBson);
    	 
    	 
    	 
    	 return true;
    }
    
    public FindIterable<Document> findByPseudo (String pseudo){
    	
    	return usersCollection.find(new Document().append("pseudo", pseudo));
    	
    }


    

	public FindIterable<Document> findBy_Id(String user_id) {
		return usersCollection.find(new Document("_id",user_id));
	}
}
