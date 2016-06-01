package ch.sebooom.mongodb;


import bourse.*;
import ch.sebooom.servers.WebServer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IndiceDAO {

	private final MongoCollection<Document> indiceCollection;
	public static final Logger log = Logger.getLogger(IndiceDAO.class.getName());
	private final static long VALIDITE_DONNEE = 60 * 3 * 1000;
	public IndiceDAO(final MongoDatabase rxDatabase){
		indiceCollection = rxDatabase.getCollection("indice");
	}
	
	public Observable<Indices> findAllIndices () {

        Observable obs = Observable.create(subscriber -> {
                log.info("Retrieving datas from MongoDB Disk");

                Document indicesDocument = indiceCollection.find().first();

                Indices indices = null;

                List<ValeurBoursiere> valeurBoursiereList = new ArrayList<>();

                List<Document> mongoDbIndices;

                if(indicesDocument == null){
                    mongoDbIndices = new ArrayList<Document>();
                }else{
                    mongoDbIndices = (List<Document>)indicesDocument.get("indicesCache");

                }

                for(Document indice : mongoDbIndices){

                    log.info(indice.toJson());
                    Document cours = (Document)indice.get("cours");

                    valeurBoursiereList.add(new ValeurBoursiere(indice.getString("nom"),
                             indice.getString("nom").replace(" ","_").toUpperCase(),
                            new Cours(cours.getDouble("valeurCours")),
                            Pays.PAYS_BAS, TypeValeurs.INDICE_BOURSIER));
                }

                log.info("s2:" + valeurBoursiereList.size());
                if(!valeurBoursiereList.isEmpty()){
                    indices = new Indices(VALIDITE_DONNEE);
                    indices.setValeurBoursieres(valeurBoursiereList);
                    log.info("s3:");

                }

                boolean  cacheEmpty = (indices != null && indices.containsData()) ? Boolean.FALSE : Boolean.TRUE;

            log.info("MongoDB contain " + ((cacheEmpty) ? "no values": indices.getValeurBoursieres().size() + " values"));

            if(!cacheEmpty){log.info("MongoDB Cache expire on : " + indices.getExpirationDonnees());};

            log.info("MongoDB Cache datas are " + ((indices != null && indices.isUpToDate()) ? "upToDate" : "stale"));




                subscriber.onNext(indices);
                subscriber.onCompleted();
            }

        );

        return obs.doOnNext(ids -> {
            log.info("DO ON NEXT");
            WebServer.updateIndicesCaches((Indices) ids);

        });
	}

    public void saveIndices (Indices indices) {

        Document indicesMongo = new Document("expirationDonnees",indices.getExpirationDonnees());
        List<Document> indiceForIndices = new ArrayList<>();

        for(ValeurBoursiere valeurBoursiere : indices.getValeurBoursieres()){
            indiceForIndices.add(new Document("nom", valeurBoursiere.getIdentifiant())
                    .append("cours",new Document("dateValeur", valeurBoursiere.getCours().getDateValeur())
                                        .append("valeurCours", valeurBoursiere.getCours().getValeurCours().doubleValue())));
        }

        indicesMongo.append("indicesCache",indiceForIndices);

                log.info("Saving indicesCache in db");
            indiceCollection.insertOne(indicesMongo);



    }
	
}
