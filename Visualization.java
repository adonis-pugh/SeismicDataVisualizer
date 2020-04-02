
/* Earthquake Visualization 
 * Name: Adonis Pugh
 * Section Leader: Laura Cruz-Albrecht
 * ------------------------------------
 * 	This program provides a visualization of the locations and magnitudes of prominent
 *	earthquakes (> 4.5 on Richter* scale) for the year 2018. The map that is generated
 *	consists of a multitude of blue dots of slightly varying size, representing the magnitude
 *	of earthquakes. The map is interactive, and mousing over an earthquake representation 
 *	will cause a label to be added to the bottom of the screen containing a brief description
 *	of the location of the earthquake. Furthermore, specific nations or regions can be searched
 *	for via the search bar at the top of the screen (e.g., Japan), in which case the earthquakes
 *  will be represented as red rather than blue dots for visual distinction. 
 *	
 *	Note: The Richter scale is no longer very commonly used for earthquakes within the magnitude
 *	this program considers, due to inaccuracies of scale with the logarithmic system. Magnitude 
 *	Moment (MM) is also a logarithmic scale, but it preserves its accuracy when describing stronger
 *	earthquakes. Hence, MM is more commonly used and technically all values in this program are in
 *	terms of magnitude moment. The Richter scale is advertised by press and, correspondingly, the
 *	general public is more familiar with this scale. 
 */

import acm.program.*;
import acm.graphics.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.ArrayList;


public class Visualization extends GraphicsProgram {
	
	/*************************************************************************
	 * 								CONSTANTS                               *
	 ************************************************************************/
	
	/* The file containing earthquake data. */
	private static final String EARTHQUAKE_DATA_FILE = "all-earthquakes.txt";
	
	/* The width of the search bar. */
	private static final int TEXT_FIELD_WIDTH = 16;
	
	/* The diameter of the smallest earthquake dot. */
	private static final double SMALL_QUAKE_DIAMETER = 1;
	
	/* The distance between the location label and the bottom of the screen. */
	private static final double LOCATION_LABEL_OFFSET = 8;

	
	/*************************************************************************
	 * 							INSTANCE VARIABLES                           *							
	 *************************************************************************/
	
	/* The database that is responsible for managing the earthquake data. */
	private EarthquakeDataBase damageData = new EarthquakeDataBase(EARTHQUAKE_DATA_FILE);
	
	/* ArrayList storing all the earthquakes */
	private ArrayList<Earthquake> allQuakes = new ArrayList<Earthquake>();
	
	/* ArrayList storing all the visual earthquake representations */
	private ArrayList<GOval> quakeDisplay = new ArrayList<GOval>();
	
	/* HashMap that associates each earthquake representation (GOval) with an Earthquake
	 * (variable type)  */
	private HashMap<GOval, Earthquake> earthTracker = new HashMap<GOval, Earthquake>();
	
	/* Search bar at the top of the screen */
	private JTextField locationSearch = new JTextField(TEXT_FIELD_WIDTH);
	
	/* Used to determine if the user has moused over an earthquake representation */
	private GObject potentialQuake = null;
	
	/* The label at the bottom of the screen that gives location information about the
	 * earthquake representation the user has moused over */
	private GLabel infoLabel = new GLabel("");
	
	/* Used to keep track of previously placed information labels to avoid stacking */
	private GLabel oldInfoLabel = new GLabel("");
	
	/* The world map projection image used by the program */
	private GImage worldMap = new GImage(
			"file:///C:/Users/Adonis-Surface%20Book/Pictures/Screenshots/worldmap_projection.PNG");
	

	/*************************************************************************
	 * 								METHODS 								 *
	 *************************************************************************/
	
	/* All the GUI elements are added to the screen: three buttons and a search bar. */
	public void init() {
		JButton visualize = new JButton("Visualize");
		JButton clear = new JButton("Clear");
		JButton search = new JButton("Search");
		add(locationSearch, NORTH);
		add(search, NORTH);
		add(visualize, NORTH);
		add(clear, NORTH);
		addActionListeners();
		locationSearch.addActionListener(this);
	}
	
	/* The world map is added to the screen. */
	private void drawWorldMap() {
		worldMap.setBounds(0, 0, getWidth(), getHeight());
		add(worldMap);
	}

	/* This method deals with the GUI of the program. If the "Clear" button is pressed,
	 * all GObjects are erased from the screen. If the "Visualize" button is pressed, all
	 * earthquakes in the database are plotted to the map. If the "Search" button or "Enter"
	 * is pressed, all the earthquakes, if any, associated with the location entered in
	 * the search bar. */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("Clear")) {
			clearAll();
			drawWorldMap();
		} else if (cmd.equals("Visualize")) {
			removeAll();
			drawWorldMap();
			plotQuakeData();
		} else {
			ArrayList<Earthquake> quakeSearched = damageData.findQuake(locationSearch.getText());
			if (quakeSearched != null) {
				for (Earthquake quake : quakeSearched) {
					double mag = quake.getMagnitude();
					double earthLat = quake.getLat();
					double earthLong = quake.getLong();
					double screenLat = (earthLat * getHeight()) / 180;
					double screenLong = (earthLong * getWidth()) / 360;
					double x = getWidth() / 2 + screenLong;
					double y = getHeight() / 2 - screenLat;
					GOval burst = new GOval(x, y, SMALL_QUAKE_DIAMETER * mag, SMALL_QUAKE_DIAMETER * mag);
					earthTracker.put(burst, quake);
					burst.setFilled(true);
					burst.setColor(Color.RED);
					add(burst);
				}
			}
		}
	}

	/* All GObjects are cleared from the screen and the ArrayList of earthquake representations is emptied. */
	private void clearAll() {
		removeAll();
		quakeDisplay.clear();
	}

	
	/* If the mouse is over an earthquake representation, a label with a brief description of the location of the
	 * event is added to the bottom of the screen. */
	public void mouseMoved(MouseEvent e) {
		potentialQuake = getElementAt(e.getX(), e.getY());
		if (potentialQuake != worldMap && potentialQuake != infoLabel) {
			Earthquake quakeSelected = earthTracker.get(potentialQuake);
			String description = quakeSelected.getDescrp();
			infoLabel = new GLabel(description);
			infoLabel.setFont("Courier-24");
			if (!infoLabel.equals(oldInfoLabel)) {
				remove(oldInfoLabel);
				add(infoLabel, getWidth()/2 - infoLabel.getWidth()/2, getHeight() - LOCATION_LABEL_OFFSET);
				oldInfoLabel = infoLabel;
			}
		}
	}

	
	/* If the screen is resized, all GObjects are cleared from the screen, the ArrayList of earthquake representations is
	 * emptied, the world map is redrawn with appropriate dimensions, and the earthquake data is reloaded. The data is not
	 * replotted in this method to avoid long computation times due to the scope of the dataset. To replot data, respective
	 * buttons must be selected again. */
	public void componentResized(ComponentEvent e) {
		clearAll();
		drawWorldMap();
		loadEarthquakeData();
	}

	/* When plotting the earthquake representations, the Earthquake has to be converted to converted to a GOval.
	 * The respective GOvals are then plotted. */
	private void plotQuakeData() {
		convertQuake();
		plotDamagePoints();
	}

	/* Loading the earthquake data accesses the EarthquakeDatabase to add all Earthquakes in the database to an ArrayList
	 * of Earthquakes in the main program. */
	private void loadEarthquakeData() {
		for (int i = 0; i < damageData.totalQuakes(); i++) {
			Earthquake quake = damageData.findQuake(i);
			allQuakes.add(quake);
		}
	}

	/* All Earthquakes are converted to GOvals based on their data content. The GOvals are also associated with their
	 * parent Earthquakes. */
	private void convertQuake() {
		for (Earthquake quake : allQuakes) {
			double mag = quake.getMagnitude();
			double earthLat = quake.getLat();
			double earthLong = quake.getLong();
			double screenLat = (earthLat * getHeight()) / 180;
			double screenLong = (earthLong * getWidth()) / 360;
			double x = getWidth() / 2 + screenLong;
			double y = getHeight() / 2 - screenLat;
			GOval burst = new GOval(x, y, SMALL_QUAKE_DIAMETER * mag, SMALL_QUAKE_DIAMETER * mag);
			quakeDisplay.add(burst);
			earthTracker.put(burst, quake);
		}
	}

	/* The earthquake representations are added to the screen. */
	private void plotDamagePoints() {
		for (GOval burst : quakeDisplay) {
			burst.setFilled(true);
			burst.setColor(Color.BLUE);
			add(burst);
		}
	}

}