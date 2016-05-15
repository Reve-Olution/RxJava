package ch.sebooom.observable.mongodb.domain;

import org.bson.Document;

public class People {

	private User user;
	private String name;
	private String surname;
	
	public People(User user, String name, String surname) {
		super();
		this.user = user;
		this.name = name;
		this.surname = surname;
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

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public Document asBson () {
		
		
		Document doc  = new Document();
		doc.append("name", name);
		doc.append("surname", surname);
		doc.append("user_id", user.get_Id());
		return doc;
	}
	
	
	
}
