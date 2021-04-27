package resources;

import components.City;

/**
 * Iron resource which unlocks swordsman
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Iron extends Resource {

	public Iron(City city) {
		super(city);
		label = "Iron";
		unitUnlocked = "Swordsman";
	}
}
