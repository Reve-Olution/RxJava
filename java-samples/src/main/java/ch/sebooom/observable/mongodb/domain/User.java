package ch.sebooom.observable.mongodb.domain;

import java.util.UUID;

import org.bson.Document;


public class User {
	
	private String pseudo;
	private String password;
	private String _id;
	
	public User (String pseudo, String password) {
		this.password = password;
		this.pseudo = pseudo;
		this._id = UUID.randomUUID().toString();
	}

	public String get_Id(){
		return this._id;
	}
	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Document asBson () {
		
		
		Document doc  = new Document();
		doc.append("_id", _id);
		doc.append("pseudo", pseudo);
		doc.append("password", password);
		return doc;
	}

}
