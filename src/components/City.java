package components;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.Player;

/**
 * TODO: What happens if city HP reaches 0 - I currently think that the city and
 * any unit within should be destroyed. Maybe units garrisoned in a city can
 * have infinite hp until they leave? Either way garrisoned units need some sort
 * of bonus which I am unsure of rn.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class City {

	private final Player owner;
	private final Point coord;

	private double production;
	private double productionReserve;
	private int turnsBeforeGrowth;
	private int population; // population can represent city level
	private double cityHPMax;
	private double cityHPCur;

	private List<String> producableUnits;


	public City(Player player, int row, int col) {
		this.owner = player;
		this.coord = new Point(row, col);

		// TODO: balance default values
		this.production = 50;
		this.productionReserve = 0;
		this.turnsBeforeGrowth = 5;
		this.population = 1;
		this.cityHPMax = 100;
		this.cityHPCur = this.cityHPMax;
		// For use if we want to add more units with conditions later on.
		this.producableUnits = new ArrayList<String>(Arrays.asList("Settler", "Scout", "Warrior"));
	}

	/**
	 * TODO: Should the city delete itself if it is out of health?
	 *
	 * Deal damage to the city from a unit
	 *
	 * @param damage attack value of unit hitting the city
	 */
	public void takeAttack(double damage) {
		this.cityHPCur -= damage;
	}

	/**
	 * A new unit has been purchased in this city, create and return it.
	 *
	 * @param unitType String representing the type of unit to be created
	 * @return Unit object that has been created for a player in a city
	 */
	public Unit produceUnit(String unitType) {
		Unit retUnit = null;
		if (unitType.equals("Settler")) {
			// settlers decrease city population by 1
			this.population -= 1;
			retUnit = new Settler(owner, coord);
		} else if (unitType.equals("Scout")) {
			retUnit = new Scout(owner, coord);
		} else if (unitType.equals("Warrior")) {
			retUnit = new Warrior(owner, coord);
		}
		this.productionReserve -= Unit.unitCosts.get(unitType);
		return retUnit;
	}

	/**
	 * Increment production, population, and city health, and grow the city if
	 * necessary.
	 */
	public void cityIncrement() {
		productionReserve += production;
		this.turnsBeforeGrowth -= 1;
		// city grows
		if (this.turnsBeforeGrowth == 0) {
			this.population += 1;
			// TODO: Balance growth
			this.turnsBeforeGrowth = this.population * 2 + 1;
			this.production += (5);
			this.cityHPMax += (this.cityHPMax / 10);
		}
		// repairs
		if (this.cityHPCur < this.cityHPMax) {
			// TODO: balance city repairs
			this.cityHPCur += (2 * this.population);
			if (this.cityHPCur > this.cityHPMax) {
				this.cityHPCur = this.cityHPMax;
			}
		}
	}

	/**
	 * Retrieve the player who owns the city
	 *
	 * @return Player object representing the city owner.
	 */
	public Player getOwner() {
		return this.owner;
	}
	/**
	 * Retrieve this city's X coordinate
	 *
	 * @return integer representing x value
	 */
	public int getX() {
		return this.coord.x;
	}

	/**
	 * Retrieve this city's Y coordinate
	 *
	 * @return integer representing y value;
	 */
	public int getY() {
		return this.coord.y;
	}

	/**
	 * retrieve the city's turn by turn production value
	 *
	 * @return double representing production per turn.
	 */
	public double getProduction() {
		return this.production;
	}

	/**
	 * retrieve the city's turn by turn production value
	 *
	 * @return double representing current accumulated production.
	 */
	public double getProductionReserve() {
		return this.productionReserve;
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
	 * retrieve the city's max HP, necessary for the view
	 *
	 * @return double representing the city's max HP value
	 */
	public double getMaxHP() {
		return this.cityHPMax;
	}

	/**
	 * retrieve the city's remaining HP, for use in the controller and view
	 *
	 * @return double representing the city's current HP value
	 */
	public double getRemainingHP() {
		return this.cityHPCur;
	}


	/**
	 * Retrieve the city's turns before growth.
	 *
	 * @return The number of turns before this city grows
	 */
	public int getTurnsBeforeGrowth() {
		return this.turnsBeforeGrowth;
	}


}
