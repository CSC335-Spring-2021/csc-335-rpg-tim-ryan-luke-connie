package models;

import components.Tile;

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
		this.board = new CivBoard(20);
	}
	


	/**
	 * Get the size of the underlying board. Since boards are square, this needs to return only
	 * one number.
	 *
	 * @return The length of the square board's edges, in number of grid spaces
	 */
	public int getSize() {
		return board.size;
	}
}
