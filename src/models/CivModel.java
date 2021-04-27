package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;

import components.Tile;

/**
 * Holds game state data and provides utility methods to query or update it.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
@SuppressWarnings("deprecation")
public class CivModel extends Observable {

	private CivBoard board;
	private Node curPlayer;
	private Node head;
	private boolean singlePlayer;
	private int round;
	private int numPlayers;
	private ArrayList<int[]> playerStartingCoords;
	/**
	 * Initialize a new model.
	 * 
	 * @param playerCount indicates how many players this game will have
	 */
	public CivModel(int playerCount) {
		numPlayers = playerCount;
		round = 0;
		File f = new File(".");
		String[] files = f.list();
		this.board = new CivBoard("./src/models/Map1.txt");
		head = new Node(new Player(1)); // make a human player
		curPlayer = head;
		if (playerCount == 1) { // if singleplayer
			numPlayers = 2;
			singlePlayer = true;
			Node cpu = new Node(new Player(0)); // make a cpu player
			head.next = cpu;
			cpu.next = head; // have them wrap around
		} else {
			singlePlayer = false;
			for (int i = 0; i < playerCount - 1; i++) { // if not singleplayer, add playerCount - 1 more players
				Node player = new Node(new Player(1)); // that are human
				curPlayer.next = player; // set next
				curPlayer = curPlayer.next; // iter
			}
			curPlayer.next = head; // have it wrap around
		}
	}

	/**
	 * getter method for the tile held at row, col in our Board
	 * 
	 * @param row integer representing the outer index into our 2D array board
	 * @param col integer representing the inner index into our 2D array board
	 * @return Tile object contained at row, col loc in our board
	 */
	public Tile getTileAt(int x, int y) {
		return this.board.getTile(x, y);
	}

	/**
	 * getter method for the size of the board for move validity checking
	 * 
	 * @return integer specifying the height and width of our board
	 */
	public int getSize() {
		return this.board.getSize();
	}

	/**
	 * Set the state of the model to changed and notify Observers that the model has
	 * been updated. Pass the current game state (board) to all Observers.
	 */
	public void changeAndNotify() {
		this.setChanged();
		this.notifyObservers(this.board);
	}

	/**
	 * getter for Model's current player
	 * 
	 * @return Player object whose turn it is
	 */
	public Player getCurPlayer() {
		return this.curPlayer.getPlayer();
	}

	/**
	 * void function allowing turn logic control. Sets cur player to next player.
	 */
	public void nextPlayer() {
		curPlayer = curPlayer.next;
		if (curPlayer.equals(head)) {
			round++;
		}
	}

	public boolean isComputer() {
		return !curPlayer.getPlayer().isHuman();
	}

	public int roundNumber() {
		return round;
	}

	public boolean removePlayer(Player deadGuy) {
		Node prev = head;
		Node cur = head.next;
		Node next = cur.next;
		while (cur.getPlayer() != deadGuy) {
			prev = prev.next;
			cur = cur.next;
			next = next.next;
			if (prev == head)
				return false;
		}
		numPlayers--;
		prev.next = next;
		return true;
	}

	public int numPlayers() {
		return numPlayers;
	}

	/**
	 * Node class for keeping a wrapped list of players
	 * 
	 * @author Luke
	 * @field player Player object associated with this node
	 * @field next Next node that contains the player whose turn it is next
	 */
	private class Node {
		Player player;
		Node next;

		/**
		 * constructor takes a Player and creates a new node containing that player
		 * 
		 * @param player Player object that is the player
		 */
		private Node(Player player) {
			this.player = player;
			next = null;
		}

		/**
		 * getter for Node's player object
		 * 
		 * @return Node's player object
		 */
		private Player getPlayer() {
			return this.player;
		}
	}
}
