package position;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrackingObject {

	
	private String description;
	private String identification;
	
	public TrackingObject(String description) {
		this.description = description;
        this.identification = UUID.randomUUID().toString();
	}

	public String identification() {
		return identification;
	}

	public String description () { return description; }

	public static List<TrackingObject> generateRandomObjectsList () {

		int numberOfObjects = (int)(Math.random()*10);

		List<TrackingObject> objects = new ArrayList<>(numberOfObjects);

		for(int cpt = 0; cpt < numberOfObjects; cpt ++){
			objects.add(new TrackingObject("avion n° "+cpt *100 + cpt * 10));
		}

		return  objects;
	}


	
	
	
	
	
}
