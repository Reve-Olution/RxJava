package ch.sebooom.observable.mongodb;



import ch.sebooom.observable.mongodb.domain.*;
import ch.sebooom.observable.mongodb.dao.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

public class UsersExample {

	final static String MONGODB_URL = "mongodb://localhost";
	final MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGODB_URL));
    final MongoDatabase rxDatabase = mongoClient.getDatabase("rx");
    
	public static void main(String[] args) throws InterruptedException {
		new UsersExample().insertSamplesDatas();
		
		//new UsersExample().findPeopleByNameSurname();
		
		//new UsersExample().findUserByPeopleNameSurnameUglyStyle();
		
		//new UsersExample().findUserByPeopleNameSurnameFlatMapStyle();
		
		//new UsersExample().findProjectsByPeopleNameSurnameUglyStyle();
		
		new UsersExample().findProjectsByPeopleNameSurnameFlatMapSytle();
	}
	
	public void insertSamplesDatas () {
		
		rxDatabase.drop();

        People p;
		List<Document> userSamplesDatas = new ArrayList<>();
		List<Document> personnsSamplesDatas = new ArrayList<>();
		List<Document> projectsSamplesDatas = new ArrayList<>();
		
		User u = new User("sce","tutu");
		userSamplesDatas.add(u.asBson());
		personnsSamplesDatas.add(new People(u, "S�bastien", "Ch�vre").asBson());
		projectsSamplesDatas.add(new Project(u,"'The '2016's 100K challenge'").asBson());
		projectsSamplesDatas.add(new Project(u,"'CCVD - Chrysaor'").asBson());
		projectsSamplesDatas.add(new Project(u,"'PCFamille 1.15.3'").asBson());
		
		u = new User("sdr","tutu");
		userSamplesDatas.add(u.asBson());
		personnsSamplesDatas.add(new People(u, "St�phane", "Drot").asBson());
		projectsSamplesDatas.add(new Project(u,"'WebAvs 1.16.0'").asBson());
		projectsSamplesDatas.add(new Project(u,"'ELKAT'").asBson());
		
		
		u = new User("cvf","tutu");
		userSamplesDatas.add(u.asBson());
		personnsSamplesDatas.add(new People(u, "Charle", "Vijkef").asBson());
		projectsSamplesDatas.add(new Project(u,"'JADE 6.0'").asBson());
		
		
		u =new User("wsa","tutu");
		userSamplesDatas.add(u.asBson());
		personnsSamplesDatas.add(new People(u, "William", "Salomon").asBson());
		
		
		u = new User("hjz","tutu");
		userSamplesDatas.add(u.asBson());
		personnsSamplesDatas.add(new People(u, "Henry", "Juzkov").asBson());
		projectsSamplesDatas.add(new Project(u,"'ELKAT'").asBson());
		projectsSamplesDatas.add(new Project(u,"'WEBAI 2.14'").asBson());
		projectsSamplesDatas.add(new Project(u,"'CCVD - Chrysaor'").asBson());
		
		
		
		UserDAO udao = new UserDAO(rxDatabase);
		udao.addUsers(userSamplesDatas);
		
		PeopleDAO pdao = new PeopleDAO(rxDatabase);
		pdao.addPeoples(personnsSamplesDatas);
		
		ProjectDAO prdao = new ProjectDAO(rxDatabase);
		prdao.addProjects(projectsSamplesDatas);
		
	}
	
	/**
	 * recherche une personne avec son nom, prenom
	 */
	public void findPeopleByNameSurname () {
		PeopleDAO pdao = new PeopleDAO(rxDatabase);
		
		Observable<Document> peopleObservable = Observable.from(pdao.findByNameAndSurname("S�bastien","Ch�vre"));
		
		peopleObservable.subscribe(
				peopleBSON -> {
					System.out.println("People retrieving: " + peopleBSON.toString());
				},
				error -> {},
				() -> {}
		);
	}
	
	/**
	 * Rechecrche un utilisateur, avec le nom, prenom de la personne UGLY
	 * modele: people 1 - 1 user
	 */
	public void findUserByPeopleNameSurnameUglyStyle () throws InterruptedException {
		PeopleDAO pdao = new PeopleDAO(rxDatabase);
		Observable<Document> peopleObservable = Observable.from(pdao.findByNameAndSurname("S�bastien","Ch�vre"));
		
		peopleObservable.subscribe(
				peopleBSON -> {
					System.out.println("People retrieving: " + peopleBSON.toString());
					//recup de l'id de l'utilisateur referenc�
					
					String user_id = peopleBSON.getString("user_id");
					
					UserDAO udao = new UserDAO(rxDatabase);
					Observable<Document> userObservable = Observable.from(udao.findBy_Id(user_id));
					
					userObservable.subscribe(
							userBSON -> {
								System.out.println("User retrieving: " + userBSON.toString());
							}
					);
				}
		);
	}
	
	/**
	 * Recherche des projets pour une personne avec le nom prenom de la personne UGLY
	 * modele: people 1 - 1 user 1 - n project 
	 */
	public void findProjectsByPeopleNameSurnameUglyStyle () {
		PeopleDAO pdao = new PeopleDAO(rxDatabase);
		Observable<Document> peopleObservable = Observable.from(pdao.findByNameAndSurname("S�bastien","Ch�vre"));
		
		peopleObservable.subscribe(
				peopleBSON -> {
					System.out.println("People retrieving: " + peopleBSON.toString());
					//recup de l'id de l'utilisateur referenc�
					
					String user_id = peopleBSON.getString("user_id");
					
					UserDAO udao = new UserDAO(rxDatabase);
					Observable<Document> userObservable = Observable.from(udao.findBy_Id(user_id));
					
					userObservable.subscribe(
							userBSON -> {
								System.out.println("User retrieving: " + userBSON.toString());
								
								ProjectDAO prdao = new ProjectDAO(rxDatabase);
								Observable<Document> projectsObservable = Observable.from(prdao.findProjectsByUserId(userBSON.getString("_id")));
								
								projectsObservable.subscribe(
										projectsBSON -> {
											System.out.println("Projects retrieving: " + projectsBSON.toString());
											
										}
								);
							}
					);
				}
		);
	}
	
	/**
	 * Rechecrche un utilisateur, avec le nom, prenom de la personne PRETTY
	 * modele: people 1 - 1 user
	 */
	public void findUserByPeopleNameSurnameFlatMapStyle () {
		UserDAO udao = new UserDAO(rxDatabase);
		PeopleDAO pdao = new PeopleDAO(rxDatabase);
		
		Observable<Document> peopleObservable = Observable.from(pdao.findByNameAndSurname("S�bastien","Ch�vre"));
		
		
		peopleObservable
		.flatMap(
				(peopleBSON)->{
					System.out.println(peopleBSON);
					return Observable.from(udao.findBy_Id(peopleBSON.getString("user_id")));
				})
		.subscribe(
				userBSON -> {
					System.out.println(userBSON);
				}
		);
	}
	
	/**
	 * Recherche des projets pour une personne avec le nom prenom de la personne PRETTY
	 * modele: people 1 - 1 user 1 - n project 
	 */
	public void findProjectsByPeopleNameSurnameFlatMapSytle () {
		UserDAO udao = new UserDAO(rxDatabase);
		PeopleDAO pdao = new PeopleDAO(rxDatabase);
		ProjectDAO prdao = new ProjectDAO(rxDatabase);
		
		
		Observable<Document> peopleObservable = Observable.from(pdao.findByNameAndSurname("S�bastien","Ch�vre"));
		
		
		peopleObservable
		.flatMap(
				peopleBSON -> {
					return Observable.from(udao.findBy_Id(peopleBSON.getString("user_id")));
				})
		.flatMap(
				projectBSON -> {
					System.out.println(projectBSON);
					return Observable.from(prdao.findProjectsByUserId(projectBSON.getString("_id")));
					
				})
		.subscribe(
				projectBSON -> {
					System.out.println(projectBSON);
				}
		);
	}
	
	
}
