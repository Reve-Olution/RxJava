package ch.sebooom.orchestrate;

public class User {

	private String nom;
	private String prenom;
	private String userKey;
	
	public User(String nom, String prenom, String userKey) {
		this.nom = nom;
		this.prenom = prenom;
		this.userKey = userKey;
	}

	public User(String nom, String prenom) {
		this.nom = nom;
		this.prenom = prenom;
	}

    public User () {};
	
	public String getNom () {
		return nom;
	}


	public String getPrenom () {
		return prenom;
	}

	public String getUserKey(){
		return userKey;
	}

	@Override
	public String toString() {
		return "User [nom=" + nom + ", prenom=" + prenom + "]";
	}
	
	
}
