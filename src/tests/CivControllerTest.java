package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;

import org.junit.jupiter.api.Test;

import components.City;
import components.Scout;
import components.Settler;
import components.Unit;
import components.Warrior;
import controllers.CivController;
import models.CivModel;
import models.Player;

/**
 * Tests the methods of CivController. This is setup to work with current
 * default map so please dont change it.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivControllerTest {

	@Test
	void testBasics() {
		CivModel model = new CivModel(1, 2, 0);
		CivController controller = new CivController(model);
		controller.placeStartingUnits();
		// basic checks
		assertEquals(model.getTileAt(4, 7), controller.getTileAt(4, 7));
		controller.startTurn();
		assertFalse(controller.gameOver());
		assertTrue(controller.isHumanTurn());
		assertTrue(controller.foundCity(3, 2));
		assertFalse(controller.foundCity(3, 2));
		// give me production now
		for (int i = 0; i < 8; i++) {
			controller.getTileAt(3, 2).getOwnerCity().cityIncrement();
		}
		// create a scout and advance it forward, enemies will be defending so this is
		// fine
		assertTrue(controller.createUnit(3, 2, "Scout"));
		assertFalse(controller.createUnit(3, 2, "Warrior"));
		controller.endTurn();
		// add a computer city that's easier to get to
		model.nextPlayer();
		City c = new City(model.getCurPlayer(), 12, 3);
		controller.getTileAt(12, 3).foundCity(c);
		model.getCurPlayer().addCity(c);
		model.nextPlayer();
		assertTrue(controller.moveUnit(model.getTileAt(3, 2).getUnit(), 4, 2));
		assertTrue(controller.moveUnit(model.getTileAt(4, 2).getUnit(), 5, 2));
		assertFalse(controller.moveUnit(model.getTileAt(5, 2).getUnit(), 4, 2));
		controller.endTurn();
		assertTrue(controller.moveUnit(model.getTileAt(5, 2).getUnit(), 6, 2));
		assertTrue(controller.moveUnit(model.getTileAt(6, 2).getUnit(), 7, 2));
		controller.endTurn();
		for (int i = 7; i < 11; i++) {
			assertTrue(controller.moveUnit(model.getTileAt(i, 2).getUnit(), i + 1, 2));
		}
		controller.endTurn();

		// attack enemy city
		assertTrue(controller.moveUnit(model.getTileAt(11, 2).getUnit(), 12, 3));
		controller.endTurn();
		assertTrue(controller.moveUnit(model.getTileAt(11, 2).getUnit(), 12, 3));
		controller.endTurn();
		// scout attacked and should eventually be killed by a defending unit
		// insert enemy unit and move it onto the city
		model.nextPlayer();
		for (int i = 0; i < 40; i++) {
			controller.getTileAt(12, 3).getOwnerCity().cityIncrement();
			controller.getTileAt(3, 2).getOwnerCity().cityIncrement();
		}
		assertTrue(controller.createUnit(12, 3, "Settler"));
		model.nextPlayer();
		assertTrue(controller.createUnit(3, 2, "Settler"));
		assertFalse(controller.createUnit(3, 2, "Settler"));
		controller.endTurn();
		// System.out.println(model.getTileAt(14, 9).getUnit());
		// create new warriors and defend so the AI can exercise its logic
		for (int i = 6; i < 12; i++) {
			controller.getTileAt(6, i).setUnit(new Warrior(model.getCurPlayer(), new Point(6, i)));
			model.getCurPlayer().addUnit(model.getTileAt(6, i).getUnit());
			controller.getTileAt(7, i).setUnit(new Warrior(model.getCurPlayer(), new Point(7, i)));
			model.getCurPlayer().addUnit(model.getTileAt(7, i).getUnit());
		}
		// hit a few more branches in moveUnit
		Unit u = controller.getTileAt(6, 8).getUnit();
		assertFalse(controller.moveUnit(u, 6, 9));
		assertFalse(controller.moveUnit(u, 10, 10));

		assertFalse(controller.gameOver());
		// TODO: make the game end painlessly
		for (int i = 0; i < 25; i++) {
			controller.endTurn();
		}
		assertTrue(controller.gameOver());
		controller.close();
	}

	// game over should work just fine
	@Test
	void testComputerDestroyCity() {
		CivModel model = new CivModel(1, 2, 0);
		CivController controller = new CivController(model);
		City city = new City(model.getCurPlayer(), 5, 9);
		model.getCurPlayer().addCity(city);
		model.getTileAt(5, 9).foundCity(city);
		model.nextPlayer();
		assertFalse(model.getCurPlayer().isHuman());
		Player computer = model.getCurPlayer();
		// these warriors are in horny jail
		Warrior w1 = new Warrior(computer, new Point(1, 1));
		Warrior w2 = new Warrior(computer, new Point(0, 0));
		// this warrior will take over human city
		Warrior w3 = new Warrior(computer, new Point(5, 10));
		model.getCurPlayer().addUnit(w1);
		model.getCurPlayer().addUnit(w2);
		model.getCurPlayer().addUnit(w3);
		model.getTileAt(1, 1).setUnit(w1);
		model.getTileAt(0, 0).setUnit(w2);
		model.getTileAt(5, 10).setUnit(w3);
		model.nextPlayer(); // back to the first player
		controller.startTurn();
		assertFalse(controller.gameOver());
		for (int i = 0; i < 4; i++)
			controller.endTurn();
	}

	@Test
	void attackOwnKillEnemy() {
		CivModel model = new CivModel(1, 2, 0);
		CivController controller = new CivController(model);
		Player human = model.getCurPlayer();
		model.nextPlayer();
		Player computer = model.getCurPlayer();
		model.nextPlayer();
		City city = new City(model.getCurPlayer(), 2, 9);
		human.addCity(city);
		Settler se = new Settler(human, new Point(2, 10));
		Warrior w1 = new Warrior(human, new Point(2, 11));
		human.addUnit(se);
		human.addUnit(w1);
		Warrior w2 = new Warrior(computer, new Point(3, 10));
		// these warriors are in horny jail
		Warrior w3 = new Warrior(computer, new Point(1, 1));
		Warrior w4 = new Warrior(computer, new Point(0, 0));
		computer.addUnit(w3);
		computer.addUnit(w4);
		computer.addUnit(w2);
		Scout s = new Scout(human, new Point(0, 1));
		human.addUnit(s);
		model.getTileAt(2, 9).foundCity(city);
		model.getTileAt(2, 10).setUnit(se);
		model.getTileAt(2, 11).setUnit(w1);
		model.getTileAt(3, 10).setUnit(w2);
		model.getTileAt(1, 1).setUnit(w3);
		model.getTileAt(0, 0).setUnit(w4);
		model.getTileAt(0, 1).setUnit(s);
		controller.startTurn();
		assertFalse(controller.moveUnit(controller.getTileAt(2, 10).getUnit(), 2, 11));
		assertFalse(controller.moveUnit(controller.getTileAt(2, 10).getUnit(), 3, 10));
		for (int i = 0; i < 6; i++)
			controller.endTurn();
	}

	@Test
	void testNicheUnits() {
		assertFalse(false);
	}

	@Test
	void testResourcesMaybe() {
		assertFalse(false);
	}
}
