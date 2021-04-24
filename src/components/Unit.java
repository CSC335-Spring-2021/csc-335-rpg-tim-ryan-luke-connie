package components;

import java.util.HashMap;
import java.util.Map;

import models.Player;

/**
 * Unit superclass.
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Unit {

	public static final Map<String, Integer> unitCosts = new HashMap<String, Integer>();
	static {
		unitCosts.put("Scout", 500);
		unitCosts.put("Settler", 1000);
		// add more as we add different types of Units
	}

	private Player owner;

	protected double HP;
	protected int maxMovement;
	protected int remainingMovement;
	protected double cost;
	protected int sight;
	protected double attackValue;

	public Unit(Player player) {
		this.owner = player;
	}

	/**
	 * make the unit take damage and return remaining hp, can be used with 0 to
	 * retrieve current HP
	 * 
	 * @return double representing HP
	 */
	public double takeAttack(double damage) {
		this.HP -= damage;
		return this.HP;
	}

	/**
	 * Return the amount of tiles a unit can still move.
	 * 
	 * @return int representing move speed
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
	 * Decrement the amount this unit can still move this turn
	 * 
	 * @param cost integer representing the movement cost of this move.
	 * @return boolean representing whether the move was a success.
	 */
	public boolean move(int cost) {
		if (cost > this.remainingMovement) {
			// there was an issue somewhwere, we shouldnt be able to move
			return false;
		}
		this.remainingMovement -= cost;
		return true;
	}

	/**
	 * Getter for this unit's attack value
	 * 
	 * @return double representing the damage inflicted upon enemy units or cities.
	 */
	public double getAttackValue() {
		return attackValue;
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
	 * Return unit's production cost
	 * 
	 * @return double representing production cost
	 */
	public double getCost() {
		return this.cost;
	}

	/**
	 * Return unit's sight value
	 * 
	 * @return int representing unit sight
	 */
	public int getSight() {
		return this.sight;
	}

	/**
	 * returns String representing the owner, can also be represented as a Player
	 * object if we decide that is a better implementation.
	 * 
	 * @return String representing owner's name
	 */
	public Player getOwner() {
		return this.owner;
	}

}
