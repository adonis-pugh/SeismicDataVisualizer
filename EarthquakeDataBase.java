
/* EarthquakeDataBase variable type
 * Author: Adonis Pugh
 * --------------------------
 * This class defines the EarthquakeDataBase object and the scope
 * of its capabilities. */
import acm.util.*;
import java.util.*;
import java.io.*;

public class EarthquakeDataBase {

	/***************************************************************
	 * 					  INSTANCE VARIABLES 					   *
	 **************************************************************/
	
	private ArrayList<String[]> damageData = new ArrayList<String[]>();
	private HashMap<String, ArrayList<String>> dataMap = new HashMap<String, ArrayList<String>>();

	/***************************************************************
	 *                         METHODS                             *
	 **************************************************************/
	
	/* The input file is read according to the standard format of the earthquake data. *
	 * Each input line is added to an standard ArrayList for integer access as well as 
	 * a HashMap associating the country/region with an input line of data. If the user
	 * happened to know the exact location of the Earthquake, such information is also
	 * added to the HashMap and associated with the same data line. */
	public EarthquakeDataBase(String filename) {
		try {
			Scanner input = new Scanner(new File(filename));
			while (input.hasNextLine()) {
				String quakeData = input.nextLine();
				String[] damageList = quakeData.split("\\|");
				String cityAndCountry = (damageList[damageList.length - 1]);
				damageData.add(damageList);
				ArrayList<String> updatedQuakes = new ArrayList<String>();
				updatedQuakes.add(quakeData);
				dataMap.put(cityAndCountry, updatedQuakes);
				if (cityAndCountry.contains(", ")) {
					String[] locationInfo = damageList[damageList.length - 1].split(", ");
					String city = locationInfo[0];
					String country = locationInfo[1];
					if (dataMap.containsKey(country)) {
						updatedQuakes.addAll(dataMap.get(country));
						dataMap.put(country, updatedQuakes);
					} else if (dataMap.containsKey(city)) {
						updatedQuakes.addAll(dataMap.get(city));
						dataMap.put(city, updatedQuakes);
					} else {
						dataMap.put(country, updatedQuakes);
						dataMap.put(city, updatedQuakes);
					}
				} else {
					String place = damageList[damageList.length - 1];
					if (dataMap.containsKey(place)) {
						updatedQuakes.addAll(dataMap.get(place));
						dataMap.put(place, updatedQuakes);
					} else {
						dataMap.put(place, updatedQuakes);
					}
				}
			}
			input.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/* Returns total number of unique earthquakes in the database. */
	public int totalQuakes() {
		return damageData.size();
	}

	/* An Earthquake can be accessed by an integer input. */
	public Earthquake findQuake(int quakeNum) {
		String[] quakeData = (damageData.get(quakeNum));
		double lat = Double.parseDouble(quakeData[0]);
		double lon = Double.parseDouble(quakeData[1]);
		double mag = Double.parseDouble(quakeData[3]);
		Earthquake quake = new Earthquake(lat, lon, mag);
		quake.setDepth(Double.parseDouble(quakeData[2]));
		quake.setDescription(quakeData[4]);
		return quake;
	}

	/* All Earthquakes associated with a matching description of the
	 * location of the Earthquake are retrieved. */
	public ArrayList<Earthquake> findQuake(String descrp) {
		ArrayList<Earthquake> allQuakes = null;
		if (dataMap.containsKey(descrp)) {
			ArrayList<String> quakeData = dataMap.get(descrp);
			allQuakes = new ArrayList<Earthquake>();
			for (String singleQuake : quakeData) {
				String[] quakeInfoLine = singleQuake.split("\\|");
				double lat = Double.parseDouble(quakeInfoLine[0]);
				double lon = Double.parseDouble(quakeInfoLine[1]);
				double mag = Double.parseDouble(quakeInfoLine[3]);
				Earthquake quake = new Earthquake(lat, lon, mag);
				quake.setDepth(Double.parseDouble(quakeInfoLine[2]));
				quake.setDescription(quakeInfoLine[4]);
				allQuakes.add(quake);
			}
		}
		return allQuakes;
	}

}
