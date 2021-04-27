package resources;

import components.City;

/**
 * Wheat resource which boosts production
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class Wheat extends Resource {

	public Wheat(City city) {
		super(city);
		label = "Wheat";
		unitUnlocked = "Milita";
	}

}
