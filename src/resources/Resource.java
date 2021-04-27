package resources;

import java.awt.Point;

import components.City;

/**
 * Resource superclass. Instead of having resources we could also just unlock
 * the two new units upon reaching a certain city level, but I feel like
 * resources enhance the strategic aspect of city founding significantly.
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Resource {

	protected final Point coord;

	protected String label;
	protected City cityInControl;
	protected String unitUnlocked = "";

	/**
	 * Make a new unit for the specified player at the city coordinates
	 *
	 * @param player
	 * @param coord
	 */
	public Resource(City city) {
		this.coord = new Point(city.getX(), city.getY());
		this.cityInControl = city;
	}


	/**
	 * Retrieve the new unit type that this resource unlocks.
	 * 
	 * @return
	 */
	public String getUnitUnlocked() {
		return this.unitUnlocked;
	}


	/**
	 * Retrieve a label for this unit that can be used in the game UI.
	 *
	 * @return This unit's name or label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Retrieve the city in control of the resource.
	 * 
	 * @return City currently making use of the resource
	 */
	public City getCityInControl() {
		return this.cityInControl;
	}

	/**
	 * retrieve this unit's x coordinate within the grid
	 *
	 * @return int representing the x position
	 */
	public int getX() {
		return coord.x;
	}

	/**
	 * retrieve this unit's y coordinate within the grid
	 *
	 * @return int representing the y position
	 */
	public int getY() {
		return coord.y;
	}

}
