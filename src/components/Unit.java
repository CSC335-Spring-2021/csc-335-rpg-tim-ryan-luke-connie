package components;

/**
 * Unit superclass.
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Unit {

	private String owner;

	protected double HP;
	protected int movement;
	protected double cost;
	protected int sight;

	public Unit(String player) {
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
	 * Return unit's base move speed
	 * 
	 * @return int representing move speed
	 */
	public int getMovement() {
		return this.movement;
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




}
