package components;

import java.awt.Point;

import models.Player;

/**
 * Basic setup for a settler unit, cant attack and founds 1 city
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Settler extends Unit {

	private int charges = 1;

	public Settler(Player player, Point coord) {
		super(player, coord);
		label = "Settler";
		// TODO: Rebalance settler defaults
		HP = 1;
		maxMovement = 2;
		resetMovement();
		sight = 2;
		attackValue = 0;
	}

	public City foundCity() {
		City foundedCity = new City(owner, coord.x, coord.y);
		// TODO: Update tile with correct information

		owner.addCity(foundedCity);
		this.charges = 0;
		return foundedCity;
	}

	/**
	 * Retrieve the number of cities this settler can still found.
	 *
	 * @return int representing the number of cities the settler can create.
	 */
	public int getCharges() {
		return this.charges;
	}

}