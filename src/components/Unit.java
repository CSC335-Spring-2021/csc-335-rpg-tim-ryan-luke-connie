package components;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import models.Player;

/**
 *
 * TODO: City handling for Tim
 *
 * Unit superclass.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Unit {

	public static final Map<String, Integer> unitCosts = new HashMap<String, Integer>();
	static {
		// TODO: rebalance unit costs
		unitCosts.put("Scout", 500);
		unitCosts.put("Settler", 1000);
		unitCosts.put("Warrior", 750);
		// add more as we add different types of Units
	}

	protected final Player owner;
	protected Point coord;

	protected String label;
	protected double maxHP;
	protected double HP;
	protected int maxMovement;
	protected int remainingMovement;
	protected double attackValue;
	protected int sight;


	/**
	 * Make a new unit for the specified player at the city coordinates
	 *
	 * @param player
	 * @param coord
	 */
	public Unit(Player player, Point coord) {
		this.owner = player;
		this.coord = coord;
	}

	/**
	 * Decrement the amount this unit can still move this turn and set the new
	 * coordinates
	 *
	 * @param cost integer representing the movement cost of this move.
	 * @param x    the unit's new x value on the grid
	 * @param y    the unit's new y value on our grid
	 * @return boolean representing whether the move was a success.
	 */
	public void move(int cost, int x, int y) {
		this.remainingMovement -= cost;
		this.coord.x = x;
		this.coord.y = y;
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
	 * Retrieve the owner of this unit.
	 *
	 * @return Player object representing the owner of this unit.
	 */
	public Player getOwner() {
		return this.owner;
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

	/**
	 * Getter for this unit's remaining health
	 *
	 * @return double representing health, if it is any value above 0 they are still
	 *         alive.
	 */
	public double getHP() {
		return HP;
	}


	/**
	 * Get the unit's total possible health
	 *
	 * @return A double representing the unit's starting health
	 */
	public double getMaxHP() {
		return this.maxHP;
	}

	/**
	 * Decrement this unit's HP from an attack
	 *
	 * TODO: This might need to be modifed to make movement 0 after a unit has
	 * attacked.
	 *
	 * @param damage double representing the amount of damage dealt to our unit HP
	 */
	public void takeAttack(double damage) {
		this.HP -= damage;
	}


	/**
	 * Retrieve remaining unit movement
	 *
	 * @return int representing number of standard tiles the unit can still move
	 *         over
	 */
	public int getMovement() {
		return this.remainingMovement;
	}


	/**
	 * Unit is done moving, reset its movement for next turn.
	 */
	public void resetMovement() {
		this.remainingMovement = this.maxMovement;
	}


	/**
	 * Retrieve this unit's un-buffed attack value
	 *
	 * @return double representing the damage inflicted upon enemy units or cities.
	 */
	public double getAttackValue() {
		return attackValue;
	}


	/**
	 * Retrieve total cost of producing this unit
	 *
	 * @return double representing production cost of unit
	 */
	public double getCost(String unitType) {
		return unitCosts.get(unitType);
	}

	/**
	 * Retrieve this unit's sight value
	 *
	 * @return int representing radius of surrounding tiles that this unit can
	 *         reveal.
	 */
	public int getSight() {
		return this.sight;
	}

}
