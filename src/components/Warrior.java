package components;

import java.awt.Point;

import models.Player;

/**
 * Basic setup for a warrior unit, the starting attack unit.
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Warrior extends Unit {

	public Warrior(Player player, Point coord) {
		super(player, coord);
		// TODO: Rebalance default values
		HP = 100;
		maxMovement = 2;
		resetMovement();
		sight = 2;
		attackValue = 10;
	}

}
