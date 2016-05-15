package ch.sebooom.mongodb;


import ch.sebooom.servers.WebServer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import cotation.Cours;
import cotation.Indice;
import cotation.Indices;
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

                List<Indice> indiceList = new ArrayList<>();

                List<Document> mongoDbIndices;

                if(indicesDocument == null){
                    mongoDbIndices = new ArrayList<Document>();
                }else{
                    mongoDbIndices = (List<Document>)indicesDocument.get("indices");

                }

                for(Document indice : mongoDbIndices){

                    log.info(indice.toJson());
                    Document cours = (Document)indice.get("cours");

                    indiceList.add(new Indice(indice.getString("nom"),
                            new Cours(cours.getDouble("valeurCours"))));
                }

                log.info("s2:" + indiceList.size());
                if(!indiceList.isEmpty()){
                    indices = new Indices(VALIDITE_DONNEE);
                    indices.setIndices(indiceList);
                    log.info("s3:");

                }

                boolean  cacheEmpty = (indices != null && indices.containsData()) ? Boolean.FALSE : Boolean.TRUE;

            log.info("MongoDB contain " + ((cacheEmpty) ? "no values": indices.getIndices().size() + " values"));

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

        for(Indice indice : indices.getIndices()){
            indiceForIndices.add(new Document("nom",indice.getNom())
                    .append("cours",new Document("dateValeur",indice.getCours().getDateValeur())
                                        .append("valeurCours",indice.getCours().getValeurCours().doubleValue())));
        }

        indicesMongo.append("indices",indiceForIndices);

                log.info("Saving indices in db");
            indiceCollection.insertOne(indicesMongo);



    }
	
}
