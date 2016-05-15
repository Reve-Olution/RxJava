package ch.sebooom.observable.mongodb.domain;

import org.bson.Document;

public class Project {
	
	private User user;
	private String name;
	
	public Project(User user, String name) {

		this.user = user;
		this.name = name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Document asBson () {
		
		
		Document doc  = new Document();
		doc.append("name", name);
		doc.append("user_id", user.get_Id());
		return doc;
	}
	
	
	

}
