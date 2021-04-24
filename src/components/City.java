package components;

import models.Player;

/**
 * TODO: I will need to make it so the city levels up. To do this we can have an
 * integer representing the number of turns that need to pass before the city
 * levels up again, and every time that number is hit it increases by a larger
 * and larger amount. Turns passing also need to heal cities (I think). We could
 * also have an itemInProduction field to determine when a unit is being made
 * and where it should be placed.
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class City {

	private String owner;
	private double production;
	private int population; // population can represent city level
	// garrison is handled by tile.
	private double cityHP;
	private int productionReserve;

	public City(String playerName) {
		this.owner = playerName;
		// default production subject to change, higher start is better
		this.production = 50;
		this.population = 1; // we can say 1pop = 1000 people or something
		this.cityHP = 100;
		this.productionReserve = 0; // start with 0 production
	}

	/**
	 * retrieve the city's turn by turn production value
	 * 
	 * @return boolean representing production per turn.
	 */
	public double getProduction() {
		return this.production;
	}

	/**
	 * retrieve the city's population, aka level
	 * 
	 * @return integer representing level
	 */
	public int getPopulation() {
		return this.population;
	}

	/**
	 * Deal damage to the city from a unit and return the city's remaining hp
	 * 
	 * @param damage attack value of unit hitting the city
	 * @return boolean representing city hp
	 */
	public double takeAttack(double damage) {
		this.cityHP -= damage;
		return this.cityHP;
	}

	/**
	 * Add to reserve
	 */
	public void produce() {
		productionReserve += production;
	}

	public int getProductionReserve() {
		return productionReserve;
	}

	public Unit produceUnit(String unitType) {
		// TODO: make a unit of the right type and return it
		return null;
	}

	public void checkLevelUp() {
		// called by the controller every turn
	}

	/**
	 * Level up the city and increase its stats accordingly.
	 */
	private void levelUpCity() {
		// TODO: Stuff
	}

	public Player getOwner() {
		// return owner
		return null;
	}

}
