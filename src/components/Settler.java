package components;

/**
 * Basic setup for a settler, all values subject to change. Settler needs to be
 * able to found a city but im not sure how we want to do this yet.
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Settler extends Unit {

	public Settler(String player) {
		super(player);
		HP = 1;
		movement = 2;
		cost = 1000;
		sight = 2;
	}
}