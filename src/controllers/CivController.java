package controllers;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;

import components.City;
import components.Settler;
import components.Tile;
import components.Unit;
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
//		Scout scout = new Scout(model.getCurPlayer(), new Point(13, 8));
//		model.getTileAt(13, 8).setUnit(scout);
//		curPlayer.addUnit(scout);

		// Warrior warrior = new Warrior(model.getCurPlayer(), new Point(16, 7));
		// model.getTileAt(16, 7).setUnit(warrior);
		// curPlayer.addUnit(warrior);

		Settler settler = new Settler(model.getCurPlayer(), new Point(5, 9));
		model.getTileAt(5, 9).setUnit(settler);
		curPlayer.addUnit(settler);

		// second player
		model.nextPlayer();
		curPlayer = model.getCurPlayer();

//		Warrior warrior2 = new Warrior(model.getCurPlayer(), new Point(16, 5));
//		model.getTileAt(16, 5).setUnit(warrior2);
//		curPlayer.addUnit(warrior2);

		Settler settler2 = new Settler(model.getCurPlayer(), new Point(15, 9));
		model.getTileAt(15, 9).setUnit(settler2);
		curPlayer.addUnit(settler2);

//		Warrior warrior3 = new Warrior(model.getCurPlayer(), new Point(17, 6));
//		model.getTileAt(17, 6).setUnit(warrior3);
//		curPlayer.addUnit(warrior3);
//
//		Warrior warrior4 = new Warrior(model.getCurPlayer(), new Point(16, 4));
//		model.getTileAt(16, 4).setUnit(warrior4);
//		curPlayer.addUnit(warrior4);

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

	public void startGame() {
		// place starting Settlers using model getStartingCoords
		// remember to loop through the players until back to #1
		startTurn();
		model.changeAndNotify();
	}

	/**
	 * Starts a player's turn by doing all of the "housekeeping" automatic game
	 * events for a player turn
	 *
	 * All Units have their movement reset, all Cities owned by a Player are
	 * incremented and updated. Do computer turn if it is the computer's turn.
	 */
	public void startTurn() {
		curPlayer = model.getCurPlayer();
		for (Unit u : curPlayer.getUnits()) {
			u.resetMovement();
			u.healUnit();
		}
		for (City c : curPlayer.getCities()) {
			c.cityIncrement();
			updateCity(c);
		}
		if (!curPlayer.isHuman())
			computerTurn();
		model.changeAndNotify();
	}

	/**
	 * Player ends their turn, the model moves on to the next player. This will
	 * update the curPlayer in model to be retrieved by controller for when the next
	 * turn begins. Notify the model if the game is over so that view is updated
	 * accordingly.
	 */
	public void endTurn() {
		if (gameOver())
			model.changeAndNotify();
		model.nextPlayer();
		startTurn();
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
	 * To determine if it is currently a human's turn or not
	 *
	 * @return true if it's a human turn, false otherwise
	 */
	public boolean isHumanTurn() {
		return curPlayer.isHuman();
	}

	/**
	 * Perform AI turn actions.
	 * 
	 * Right now, the computer will loop through all the cities and do city actions,
	 * then loop through all its units and do unit actions. Settlers found cities,
	 * the first few units stay by their origin city and defend it, and the rest
	 * move towards enemy cities to attack them.
	 *
	 */
	public void computerTurn() {
		for (City c : curPlayer.getCities()) {
			computerCityActions(c);
		}
		int firstFew = 2;
		int i = 0;
		while (!gameOver() && i < curPlayer.getUnits().size()) {
			int oldSize = curPlayer.getUnits().size();
			Unit u = curPlayer.getUnits().get(i);
			if (u instanceof Settler) {
				computerSettlerActions((Settler) u);
			}
			// these ones are defending the city
			else if (firstFew > 0) {
				computerDefenderActions(u);
				firstFew--;
			} else {
				// move towards enemy city/attack it
				computerAttackerActions(u);
			}
			if (curPlayer.getUnits().size() == oldSize)
				i++;
		}
		model.changeAndNotify();
		endTurn();
	}

	/**
	 * Actions for computer's settlers. Try to found a city as soon as possible.
	 * Otherwise, move randomly and avoid attacking (this is only necessary if the
	 * computer is producing settlers, as these new settlers must move out of the
	 * city radius of control to found a new city).
	 *
	 * @param s a Settler owned by the computer player
	 */
	private void computerSettlerActions(Settler s) {
		boolean founded = foundCity(s.getX(), s.getY()); // try to found a city
		if (!founded) {
			HashSet<int[]> validMoves = getValidMoves(s);
			boolean moved = true;
			while (validMoves.size() != 0 && moved) { // continue moving while able
				moved = false;
				for (int[] move : validMoves) { // random set of moves
					if (getTileAt(move[0], move[1]).getUnit() == null) { // don't want to attack
						moveUnit(s, move[0], move[1]);
						moved = true;
						break;
					}
				}
				validMoves = getValidMoves(s);
			}
		}
	}

	/**
	 * Actions that the computer takes for the first two non-settler units. These
	 * units remain close to the city and defend it against attackers. They move out
	 * of the city if there is no enemy attacking. Otherwise, they just don't move.
	 *
	 * @param u a Unit owned by the computer player
	 */
	private void computerDefenderActions(Unit u) {
		HashSet<int[]> validMoves = getValidMoves(u);
		for (int[] move : validMoves) { // random set of moves
			if (getTileAt(move[0], move[1]).getUnit() != null) { // attack enemy unit
				moveUnit(u, move[0], move[1]);
				break;
			}
		}
		// move the unit out of the city by 1 space if there is no enemy attacking
		if (getTileAt(u.getX(), u.getY()).isCityTile()) {
			validMoves = getValidMoves(u);
			for (int[] move : validMoves) { // random set of moves
				moveUnit(u, move[0], move[1]);
				break;
			}
		}
	}

	/**
	 * Units that move towards and attack human player's cities. Find the closest
	 * enemy city and move towards it. Movement heuristic is to find the best move
	 * if possible and do that move; otherwise, do a good move; if these are both
	 * impossible, make a random move. This is done until movement is depleted.
	 *
	 * @param u a Unit owned by the computer player
	 */
	private void computerAttackerActions(Unit u) {
		// search the entire map for the user's closest city
		Integer[] closest = new Integer[] { -1, -1 };
		int minDist = Integer.MAX_VALUE;
		for (int i = 0; i < model.getSize(); i++) {
			for (int j = 0; j < model.getSize(); j++) {
				Tile t = getTileAt(i, j);
				if (t.isCityTile()) {
					City c = t.getOwnerCity();
					if (c.getOwner() != curPlayer) {
						int dist = Math.max(Math.abs(c.getX() - u.getX()), Math.abs(c.getY() - u.getY()));
						if (dist < minDist) {
							minDist = dist;
							closest[0] = c.getX();
							closest[1] = c.getY();
						}
					}
				}
			}
		}
		if (closest[0] == -1) // no cities left to attack
			return;
		// if y distance to city is greater than x, move in the y direction
		// and vice versa
		// if equal, move diagonal
		// if no move exists for these "better" choices, choose a move that
		// gets the unit at all closer
		HashSet<int[]> validMoves = getValidMoves(u);
		while (validMoves.size() != 0) {
			if (getTileAt(closest[0], closest[1]).getOwnerCity() == null)
				return;
			int xDiff = closest[0] - u.getX();
			int yDiff = closest[1] - u.getY();
			int priority = 0; // give x direction priority
			if (Math.abs(yDiff) > Math.abs(xDiff))
				priority = 1; // y diff is greater, so give y priority
			int goodX = Integer.signum(xDiff) + u.getX(); // ideal x to go to
			int goodY = Integer.signum(yDiff) + u.getY(); // ideal y to go to
			boolean moved = false;
			for (int[] move : validMoves) { // loop for the ideal move
				if ((move[0] == closest[0] && move[1] == closest[1]) || // city, attack
						move[0] == goodX && move[1] == goodY) { // ideal move
					moveUnit(u, move[0], move[1]);
					moved = true;
					break;
				}
			}
			if (!moved) { // if not moved, loop again for good move
				for (int[] move : validMoves) {
					if (priority == 0) {
						if (move[0] == goodX) {
							moveUnit(u, move[0], move[1]);
							moved = true;
							break;
						}
					}
					if (priority == 1) {
						if (move[1] == goodY) {
							moveUnit(u, move[0], move[1]);
							moved = true;
							break;
						}
					}
				}
			}
			// got through all the moves and didn't move, just make a random move
			if (!moved) {
				Iterator<int[]> iterator = validMoves.iterator();
				int[] move = iterator.next();
				moveUnit(u, move[0], move[1]);
			}
			validMoves = getValidMoves(u);
		}

	}

	/**
	 * For now, computer cities will crank out warrior fodder
	 *
	 * @param c computer city attempting to create units
	 */
	private void computerCityActions(City c) {
		Tile tile = getTileAt(c.getX(), c.getY());
		Unit unit = tile.getUnit();
		// if unit on this city, move it out
		if (unit != null) {
			HashSet<int[]> validMoves = getValidMoves(unit);
			Iterator<int[]> iterator = validMoves.iterator();
			int[] move = iterator.next();
			moveUnit(unit, move[0], move[1]);
		}
		createUnit(c.getX(), c.getY(), "Warrior");
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
			revealTiles(toMove); // reveal tiles around unit
		}
		model.changeAndNotify();
		if (moveTo.getUnit() != null) { // died in counterattack
			if (moveTo.getUnit().getOwner() != curPlayer && moveFrom.getUnit() == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Set all the tiles in a 1-tile radius around the given location as revealed
	 * for the current player.
	 *
	 * @param x int of x location middle tile
	 * @param y int of y location middle tile
	 */
	private void revealTiles(Unit unit) {
		int sight = unit.getSight();
		for (int i = -sight; i <= sight; i++) {
			for (int j = -sight; j <= sight; j++) {
				int toRevealRow = unit.getX() + i;
				int toRevealCol = unit.getY() + j;
				Tile toRevealTile = getTileAt(toRevealRow, toRevealCol);
				if (toRevealTile != null && !toRevealTile.canSeeTile(curPlayer))
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
		if ((int) defender.getHP() <= 0) {
			defenderTile.setUnit(null);
			defender.getOwner().removeUnit(defender);
			return !defenderTile.isCityTile();
		}
		double counterattack = defender.getAttackValue();
		counterattack *= defenderTile.getAttackModifier();
		attacker.takeAttack(counterattack);
		if ((int) attacker.getHP() <= 0) {
			attacker.move(attacker.getMovement(), attacker.getX(), attacker.getY());
			curPlayer.removeUnit(attacker);
			attackerTile.setUnit(null);
			return false;
		}
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
		if ((int) defender.getRemainingHP() <= 0) {
			getTileAt(defender.getX(), defender.getY()).destroyCity();
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
	 * Creates a unit on the given tile.
	 *
	 * View should pass the correct x,y when a player tries to create a unit (i.e.,
	 * only an actual city Tile can produce a unit). Updates the tile so that it has
	 * the new unit on it.
	 *
	 * @param x        int representing the x location of new unit
	 * @param y        int representing the y location of new unit
	 * @param unitType String representing the type of unit to create
	 * @return true if unit was successfully created; false otherwise
	 */
	public boolean createUnit(int x, int y, String unitType) {
		Tile tile = getTileAt(x, y);
		City city = tile.getOwnerCity();
		if (city.getProductionReserve() >= Unit.unitCosts.get(unitType) && tile.getUnit() == null
				&& city.getProducableUnits().contains(unitType)) {
			Unit newUnit = city.produceUnit(unitType);
			tile.setUnit(newUnit);
			curPlayer.addUnit(newUnit);
			model.changeAndNotify();
			return true;
		}
		return false;
	}

	/**
	 * Found a city on the Tile at x, y on the board
	 *
	 * Adds city to the current player's list of cities as well. Assumes that this
	 * was only called on a valid tile (Settler on the tile).
	 *
	 * @param x int of x location of new city
	 * @param y int of y location of new city
	 * @return true if a city was sucessfully founded; false otherwise
	 */
	public boolean foundCity(int x, int y) {
		Tile tile = getTileAt(x, y);
		Settler settler = (Settler) tile.getUnit();
		if (settler.getCharges() > 0 && tile.getOwnerCity() == null) {
			City city = settler.foundCity();
			tile.foundCity(city);
			curPlayer.removeUnit(settler);
			tile.setUnit(null);
			model.changeAndNotify();
			return true;
		}
		return false;
	}

	/**
	 * Expand the city's influence and check for resources on each added tile
	 * 
	 * @param c the City whose resources are to be updated
	 */
	private void updateCity(City c) {
		int range = c.getControlRadius();
		int top = c.getY() - range;
		int bottom = c.getY() + range;
		int left = c.getX() - range;
		int right = c.getX() + range;
		int[] dirs = new int[] { top, bottom, left, right };
		for (int i = -range; i <= range; i++) {
			int x = c.getX() + i;
			int y = c.getY() + i;
			for (int j = 0; j < 4; j++) {
				Tile t = null;
				if (j == 0 || j == 1)
					t = getTileAt(x, dirs[j]);
				else
					t = getTileAt(dirs[j], y);
				if (t != null && t.getOwnerCity() == null) {
					t.setOwnerCity(c);
					t.checkForNewResource();
				}
			}
		}
	}

	/**
	 * Returns a set of all the valid moves that the unit can currently make.
	 *
	 * A unit can move onto a tile if it has enough movement left based on the cost
	 * of moving (1) and the movement modifier for the tile. A unit can "move" onto
	 * an tile with an enemy unit but cannot move onto a tile with a friendly unit.
	 *
	 * @param unit the Unit whose valid moves are to be retrieved
	 * @return HashSet of int[]s representing all the valid moves for the given unit
	 */
	public HashSet<int[]> getValidMoves(Unit unit) {
		HashSet<int[]> moves = new HashSet<int[]>();
		int curX = unit.getX(), curY = unit.getY();
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				int newX = curX + i, newY = curY + j;
				int movement = unit.getMovement();
				Tile moveTo = getTileAt(newX, newY);
				if (moveTo != null) {
					int cost = -moveTo.getMovementModifier();
					if (cost + 1 <= movement) {
						Unit unitOnMoveTile = moveTo.getUnit();
						if (unitOnMoveTile == null || unitOnMoveTile.getOwner() != curPlayer)
							moves.add(new int[] { newX, newY });
					}
				}
			}
		}
		return moves;
	}

}
