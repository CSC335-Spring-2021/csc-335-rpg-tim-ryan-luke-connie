package components;

/**
 * Basic setup for a scout unit all vals subject to change. Scouts move and
 * reveal more.
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Scout extends Unit {

	public Scout(String player) {
		super(player);
		HP = 50;
		maxMovement = 4;
		resetMovement();
		cost = 500;
		sight = 4;
		attackValue = 5;
	}

}
