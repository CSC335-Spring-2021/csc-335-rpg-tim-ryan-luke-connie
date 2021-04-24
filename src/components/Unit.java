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

	private String owner;

	protected double HP;
	protected int movement;
	protected double cost;
	protected int sight;

	public Unit(String player) {
		this.owner = player;
	}

	/*
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
	 * Return unit's base move speed
	 * 
	 * @return int representing move speed
	 */
	public int getMovement() {
		return this.movement;
	}

	public void resetMovement() {
		// reset to original max movement value
	}

	public void move(int cost) {
		// decrease current movement by cost of movement
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

	public Player getOwner() {
		// return owner
		return null;
	}

}
