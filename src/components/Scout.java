package components;

import java.awt.Point;

import models.Player;

/**
 * Basic setup for a scout unit which has higher movement and sight.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Scout extends Unit {

	public Scout(Player player, Point coord) {
		super(player, coord);
		label = "Scout";
		// TODO: Rebalance default values
		HP = 50;
		maxMovement = 4;
		resetMovement();
		sight = 4;
		attackValue = 5;
	}


	@Override
	public double getMaxHP() {
		return 50;
	}

}
