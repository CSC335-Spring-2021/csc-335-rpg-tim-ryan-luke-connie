package models;

import java.util.Observable;

/**
 * Holds game state data and provides utility methods to query or update it.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
@SuppressWarnings("deprecation")
public class CivModel extends Observable {

	private CivBoard board;

	/**
	 * Initialize a new model.
	 */
	public CivModel() {
		this.board = new CivBoard(10);
	}

}
