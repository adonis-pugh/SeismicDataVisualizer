
/* Earthquake variable type 
 * Author: Adonis Pugh
 * --------------------------
 * This class defines the Earthquake object and the scope
 * of its capabilities. */

public class Earthquake {

	/*****************************************************
	 * 				INSTANCE VARIABLES					 *
	 ****************************************************/
	/* Earth latitude of the Earthquake. */
	private double latitude;
	
	/* Earth longitude of the Earthquake */
	private double longitude;
	
	/* Magntiude on the Richter scale of the Earthquake */
	private double magnitude;
	
	/* Depth (below sea level) of the Earthquake */
	private double depth;
	
	/* Description of the location of the Earthquake */
	private String locationDescrp;

	/*****************************************************
	 * 					   METHODS 						 *
	 *****************************************************/
	
	/* A new Earthquake is made with three parameters, a
	 * latitude, a longitude, and a magnitude. */
	public Earthquake(double x, double y, double mag) {
		this.latitude = x;
		this.longitude = y;
		this.magnitude = mag;
	}

	/* The depth of the Earthquake is set as a secondary characteristic. */
	public void setDepth(double depth) {
		this.depth = depth;
	}

	/* The description of the Earthquake is set as a secondary characteristic. */
	public void setDescription(String dataLine) {
		this.locationDescrp = dataLine;
	}

	/* The magnitude of the Earthquake is returned. */
	public double getMagnitude() {
		return this.magnitude;
	}

	/* The latitude of the Earthaquake is returned. */
	public double getLat() {
		return this.latitude;
	}

	/* The longitude of the Earthquake is returned. */
	public double getLong() {
		return this.longitude;
	}

	/* The depth, if set, of the Earthquake is returned. */
	public double getDepth() {
		return this.depth;
	}
	
	/* The description, if set, of the Earthquake is returned. */
	public String getDescrp() {
		return this.locationDescrp;
	}
}
