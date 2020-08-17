package module3;

//Java utilities libraries
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = true;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;
	public static final float THRESHOLD_LOWER = 0;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	private HashMap<Float,String> hashKey;
	
	
	public void setup() {
		size(950, 600, OPENGL);
		buildHashKeyText();
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    
	    for(PointFeature ponto : earthquakes) {
	    	markers.add(createMarker(ponto));
	    }
	    //TODO (Step 3): Add a loop here that calls createMarker (see below) 
	    // to create a new SimplePointMarker for each PointFeature in 
	    // earthquakes.  Then add each new SimplePointMarker to the 
	    // List markers (so that it will be added to the map in the line below)
	    
	    
	    // Add the markers to the map so that they are displayed
	    map.addMarkers(markers);
	}
		
	/* createMarker: A suggested helper method that takes in an earthquake 
	 * feature and returns a SimplePointMarker for that earthquake
	 * 
	 * In step 3 You can use this method as-is.  Call it from a loop in the 
	 * setp method.  
	 * 
	 * TODO (Step 4): Add code to this method so that it adds the proper 
	 * styling to each marker based on the magnitude of the earthquake.  
	*/
	private SimplePointMarker createMarker(PointFeature feature)
	{  
		// To print all of the features in a PointFeature (so you can see what they are)
		// uncomment the line below.  Note this will only print if you call createMarker 
		// from setup
		//System.out.println(feature.getProperties());
		
		// Create a new SimplePointMarker at the location given by the PointFeature
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());

	    marker.setColor(getMarkerColor(mag));
	    marker.setRadius(getMarkerRadius(mag));
	    
	    // Finally return the marker
	    return marker;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}
	
	public int getMarkerColor(float magnitude) {
	    if ( magnitude >= THRESHOLD_MODERATE ) {
	    	return color(255, 0, 0);
	    } else if (magnitude >= THRESHOLD_LIGHT ) {
	    	return color(255, 255, 0);
	    }
	    
	    return color(0, 0, 255);
	}
	
	public int getMarkerRadius(float magnitude) {
	    if ( magnitude > THRESHOLD_MODERATE ) {
	    	return 15;
	    } else if (magnitude > THRESHOLD_LIGHT ) {
	    	return 10;
	    } 
	    
    	return 5;	   
	}


	// helper method to draw key in GUI
	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
		fill(255);
		rect(50,50,140,500);
		textSize(10);
		fill(0);
		text("Earthquake Key", 55, 70);

		
		createPoint(THRESHOLD_MODERATE, 105);
		createPoint(THRESHOLD_LIGHT   , 145);
		createPoint(THRESHOLD_LOWER   , 185);

	}
	
	private void createPoint(float magnitude, int verticalPosition) {
		
		int radius = getMarkerRadius(magnitude);
		
		fill(0);
		text( hashKey.get(magnitude), 75, verticalPosition );
		fill(getMarkerColor(magnitude));
		ellipse(60, verticalPosition, radius, radius );
	}
	
	private void buildHashKeyText() {
		hashKey = new HashMap<>();
		hashKey.put(THRESHOLD_MODERATE,"5.0+ Magnitude");
		hashKey.put(THRESHOLD_LIGHT,"4.0+ Magnitude");
		hashKey.put(THRESHOLD_LOWER,"Below 4.0" );
	}

}
