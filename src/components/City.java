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

	// TODO: Row and col, set row and col
	private Player owner;
	private double production;
	private double productionReserve;
	private Unit unitInProduction;
	private int population; // population can represent city level
	// garrison is handled by tile.
	private double cityHP;

	public City(Player player) {
		this.owner = player;
		// default production subject to change, higher start is better
		this.production = 50;
		this.population = 1; // we can say 1pop = 1000 people or something
		this.cityHP = 100;
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


	public double getProductionReserve() {
		return productionReserve;
	}

	/**
	 * TODO:
	 * 
	 * @param unitType
	 * @return
	 */
	public Unit produceUnit(String unitType) {
		// TODO return a new unit
		return null;
	}

	/**
	 * Do all of the things at the start of the turn TODO:
	 */
	public void cityIncrement() {
		productionReserve += production;
	}

	/**
	 * Level up the city and increase its stats accordingly.
	 */
	private void levelUpCity() {
		// TODO: Stuff
	}

	public Player getOwner() {
		return this.owner;
	}

}
