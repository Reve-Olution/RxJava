package ch.sebooom.observable.mongodb.dao;

import java.util.List;
import java.util.concurrent.Future;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ProjectDAO {

private final MongoCollection<Document> projectCollection;
    
    public ProjectDAO(final MongoDatabase userDatabase) {
    	projectCollection = userDatabase.getCollection("project");
    }
    
    public boolean addProjects(List<Document> projectsBson) {
   	 projectCollection.insertMany(projectsBson);
   	 
   	 
   	 
   	 return true;
   }

	public FindIterable<Document> findProjectsByUserId(String user_id) {
		return projectCollection.find(new Document("user_id",user_id));
	}
}
