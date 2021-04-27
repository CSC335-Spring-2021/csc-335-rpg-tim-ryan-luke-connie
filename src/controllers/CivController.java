package controllers;

import java.awt.Point;
import java.util.HashSet;

import components.City;
import components.Scout;
import components.Settler;
import components.Tile;
import components.Unit;
import components.Warrior;
import models.CivModel;
import models.Player;

/**
 * Provides methods to calculate data about game state or act as a computer
 * player that updates game state.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivController {

	private final CivModel model;
	private Player curPlayer;

	/**
	 * Constructor for controller
	 *
	 * @param model model that the controller will interact with to store the
	 *              results of the operations it performs
	 */
	public CivController(CivModel model) {
		this.model = model;
		curPlayer = model.getCurPlayer();
	}

	/**
	 * Configure the map with the units that should exist at the start of a new
	 * game.
	 * 
	 * Note that for testing, you have to add each unit/city to the current player.
	 * 
	 * For testing -- delete this later
	 */
	public void placeStartingUnits() {
		Settler settler = new Settler(model.getCurPlayer(), new Point(9, 9));
		model.getTileAt(9, 9).setUnit(settler);
		curPlayer.addUnit(settler);

		Scout scout = new Scout(model.getCurPlayer(), new Point(10, 9));
		model.getTileAt(10, 9).setUnit(scout);
		curPlayer.addUnit(scout);

		Warrior warrior = new Warrior(model.getCurPlayer(), new Point(11, 12));
		model.getTileAt(11, 12).setUnit(warrior);
		curPlayer.addUnit(warrior);

		City city = new City(model.getCurPlayer(), 10, 10);
		model.getTileAt(10, 10).foundCity(city);
		curPlayer.addCity(city);

		// second player
		model.nextPlayer();
		curPlayer = model.getCurPlayer();

		Warrior warrior2 = new Warrior(model.getCurPlayer(), new Point(14, 12));
		model.getTileAt(14, 12).setUnit(warrior2);
		curPlayer.addUnit(warrior2);

		City city2 = new City(model.getCurPlayer(), 14, 12);
		model.getTileAt(14, 12).foundCity(city2);
		city2.takeAttack(100);
		curPlayer.addCity(city2);

		// go back to player 1 to start the game
		model.nextPlayer();
	}

	/**
	 * Returns the Tile located at the board location x, y
	 *
	 * @param x int for x of board positionn
	 * @param y int for y of board position
	 *
	 * @return the Tile at x,y on the board
	 *
	 */
	public Tile getTileAt(int x, int y) {
		return model.getTileAt(x, y);
	}

	/**
	 * Starts a player's turn by doing all of the "housekeeping" automatic game
	 * events for a player turn
	 *
	 * All Units have their movement reset, all Cities owned by a Player are
	 * incremented
	 *
	 * @param player
	 */
	public void startTurn() {
		curPlayer = model.getCurPlayer();
		for (Unit u : curPlayer.getUnits()) {
			u.resetMovement();
			u.healUnit();
		}
		for (City c : curPlayer.getCities())
			c.cityIncrement();
		if (!curPlayer.isHuman())
			computerTurn();
		model.changeAndNotify();
	}

	/**
	 * Human player ends their turn, the model moves on to the next player. This
	 * will update the curPlayer for when the next turn begins.
	 */
	public void endTurn() {
		model.nextPlayer();
		model.changeAndNotify();
	}

	/**
	 * When there is only 1 player left, the game is won.
	 *
	 * @return true if the game is over, false otherwise
	 */
	public boolean gameOver() {
		return model.numPlayers() == 1;
	}

	/**
	 * does all the computer turn's AI stuff
	 */
	public void computerTurn() {
		// TODO AI logic
		endTurn();
	}

	/**
	 * Moves a unit from its old location to the new player-specified location.
	 *
	 * Unit moves one tile at a time. If enemy Unit or city on the location to move
	 * to, the move is an attack.
	 *
	 * @param toMove the Unit that is attempting a move/attack
	 * @param newx   int of new x location of unit
	 * @param newy   int of new y location of unit
	 * @return true if the unit successfully moved/attacked, false otherwise
	 */
	public boolean moveUnit(Unit toMove, int newX, int newY) {
		int oldX = toMove.getX(), oldY = toMove.getY();
		Tile moveFrom = getTileAt(oldX, oldY);
		int movement = toMove.getMovement();
		// this conditional checks that the unit is only moving 1 space
		if (Math.abs(newX - oldX) > 1 || Math.abs(newY - oldY) > 1)
			return false;
		Tile moveTo = getTileAt(newX, newY);
		int cost = -moveTo.getMovementModifier();
		if (cost + 1 > movement)
			return false;
		Unit onTile = moveTo.getUnit();
		boolean movesOnto = true;
		if (onTile != null) { // unit exists here, attack it
			if (onTile.getOwner().equals(curPlayer))
				return false;
			movesOnto = attack(moveFrom, moveTo);
			cost = toMove.getMovement() - 1; // have to deplete to if successful move
		} else if (moveTo.isCityTile() && !moveTo.getOwnerCity().getOwner().equals(curPlayer)) // city, attack
			movesOnto = attack(moveFrom, moveTo.getOwnerCity());
		if (movesOnto) {
			moveFrom.setUnit(null); // unit gone
			moveTo.setUnit(toMove); // successfully moves to new tile
			toMove.move(cost + 1, newX, newY); // update costs and unit location
			revealTiles(newX, newY); // reveal tiles around unit
		}
		model.changeAndNotify();
		return true;
	}

	/**
	 * Set all the tiles in a 1-tile radius around the given location as revealed
	 * for the current player.
	 *
	 * @param x int of x location middle tile
	 * @param y int of y location middle tile
	 */
	private void revealTiles(int x, int y) {
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				int toRevealRow = x + i;
				int toRevealCol = y + j;
				Tile toRevealTile = getTileAt(toRevealRow, toRevealCol);
				if (!toRevealTile.canSeeTile(curPlayer))
					toRevealTile.revealTile(curPlayer);
			}
		}
	}

	/**
	 * Unit on attakcerTile attacks the Unit on defenderTile.
	 *
	 * Unit gets attack modifier based on its current terrain; defender gets to
	 * counterattack. Movement for attacker Unit is set to 0, as attack can only
	 * happen once.
	 *
	 * @param attackerTile
	 * @param defenderTile
	 * @return
	 */
	private boolean attack(Tile attackerTile, Tile defenderTile) {
		Unit attacker = attackerTile.getUnit();
		Unit defender = defenderTile.getUnit();
		double attack = attacker.getAttackValue();
		attack *= attackerTile.getAttackModifier();
		defender.takeAttack(attack);
		if (defender.getHP() <= 0) {
			defender.getOwner().removeUnit(defender);
			return true;
		}
		double counterattack = defender.getAttackValue();
		counterattack *= defenderTile.getAttackModifier();
		attacker.takeAttack(counterattack);
		attacker.move(attacker.getMovement(), attacker.getX(), attacker.getY()); // failed move
		return false;
	}

	/**
	 * Overloaded method, to be called when attacking a City
	 *
	 * Attacker attacks the city; city's health is checked. If <= 0, the City is
	 * defeated and removed from the owner's list of cities. This is how the game
	 * ends, so checks the end game condition as well.
	 *
	 * @param attackerTile
	 * @param defender
	 * @return
	 */
	private boolean attack(Tile attackerTile, City defender) {
		Unit attacker = attackerTile.getUnit();
		double attack = attacker.getAttackValue();
		attack *= attackerTile.getAttackModifier();
		defender.takeAttack(attack);
		if (defender.getRemainingHP() <= 0) {
			Player lostACity = defender.getOwner();
			lostACity.removeCity(defender);
			if (lostACity.getCities().size() == 0) {
				model.removePlayer(lostACity); // player has no cities left, remove from game
			}
		}
		attacker.move(attacker.getMovement(), attacker.getX(), attacker.getY()); // set move to 0
		return false;
	}

	/**
	 * Creates a unit on the given tile if the city has enough in its production
	 * reserve to make that unit.
	 *
	 * View should pass the correct x,y when a player tries to create a unit (i.e.,
	 * only an actual city Tile can produce a unit). Updates the tile so that it has
	 * the new unit on it.
	 *
	 * @param x        int representing the x location of new unit
	 * @param y        int representing the y location of new unit
	 * @param unitType String representing the type of unit to create
	 */
	public void createUnit(int x, int y, String unitType) {
		Tile tile = getTileAt(x, y);
		City city = tile.getOwnerCity();
		Unit newUnit = city.produceUnit(unitType);
		tile.setUnit(newUnit);
		curPlayer.addUnit(newUnit);
		model.changeAndNotify();
	}

	/**
	 * Found a city on the Tile at x, y on the board
	 *
	 * Adds city to the current player's list of cities as well. Assumes that this
	 * was only called on a valid tile (Settler on the tile).
	 *
	 * @param x int of x location of new city
	 * @param y int of y location of new city
	 */
	public void foundCity(int x, int y) {
		Tile tile = getTileAt(x, y);
		Settler settler = (Settler) tile.getUnit();
		City city = settler.foundCity();
		tile.foundCity(city);
		model.changeAndNotify();
	}

	public HashSet<int[]> getValidMoves(Unit unit) {
		HashSet<int[]> moves = new HashSet<int[]>();
		int curX = unit.getX(), curY = unit.getY();
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				int newX = curX + i, newY = curY + j;
				int movement = unit.getMovement();
				Tile moveTo = getTileAt(newX, newY);
				int cost = -moveTo.getMovementModifier();
				if (cost + 1 <= movement) {
					Unit unitOnMoveTile = moveTo.getUnit();
					if (unitOnMoveTile == null || unitOnMoveTile.getOwner() != curPlayer)
						moves.add(new int[] { newX, newY });
				}
			}
		}
		return moves;
	}

}
