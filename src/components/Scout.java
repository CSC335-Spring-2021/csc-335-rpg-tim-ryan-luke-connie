package components;

/**
 * Basic setup for a scout unit all vals subject to change
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Scout extends Unit {

	public Scout(String player) {
		super(player);
		HP = 50;
		movement = 4;
		cost = 500;
		sight = 4;
	}

}
