package components;

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
	private float production;
	private int population; // population can represent city level
	// garrison is handled by tile.
	private float cityHP;

	public City(String playerName) {
		this.owner = playerName;
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
	public float getProduction() {
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
	public float takeAttack(float damage) {
		this.cityHP -= damage;
		return this.cityHP;
	}

	/**
	 * Level up the city and increase its stats accordingly.
	 */
	private void levelUpCity() {
		// TODO: Stuff
	}

}
