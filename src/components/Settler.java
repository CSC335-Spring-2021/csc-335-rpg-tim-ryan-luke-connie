package components;

import models.Player;

/**
 * Basic setup for a settler, all values subject to change. Settler needs to be
 * able to found a city but im not sure how we want to do this yet.
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Settler extends Unit {

	public Settler(Player player) {
		super(player);
		HP = 1;
		maxMovement = 2;
		resetMovement();
		cost = 1000;
		sight = 2;
		attackValue = 0;

	}
}