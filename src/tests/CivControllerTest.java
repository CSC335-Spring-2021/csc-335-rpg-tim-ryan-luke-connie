package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;

import org.junit.jupiter.api.Test;

import components.City;
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
	void testMovementOfPlayerAndAIAction() {
		CivModel model = new CivModel(1);
		CivController controller = new CivController(model);
		controller.placeStartingUnits();
		// basic checks
		assertEquals(model.getTileAt(4, 7), controller.getTileAt(4, 7));
		controller.startGame();
		controller.startTurn();
		assertFalse(controller.gameOver());
		assertTrue(controller.isHumanTurn());
		assertTrue(controller.foundCity(5, 9));
		assertFalse(controller.foundCity(5, 9));
		// give me production now
		for (int i = 0; i < 8; i++) {
			controller.getTileAt(5, 9).getOwnerCity().cityIncrement();
		}
		// create a scout and advance it forward, enemies will be defending so this is
		// fine
		assertTrue(controller.createUnit(5, 9, "Scout"));
		assertFalse(controller.createUnit(5, 9, "Warrior"));
		controller.endTurn();
		assertTrue(controller.moveUnit(model.getTileAt(5, 9).getUnit(), 6, 9));
		assertTrue(controller.moveUnit(model.getTileAt(6, 9).getUnit(), 7, 9));
		assertFalse(controller.moveUnit(model.getTileAt(7, 9).getUnit(), 8, 9));
		controller.endTurn();
		assertTrue(controller.moveUnit(model.getTileAt(7, 9).getUnit(), 8, 9));
		assertTrue(controller.moveUnit(model.getTileAt(8, 9).getUnit(), 9, 9));
		controller.endTurn();
		for (int i = 9; i < 13; i++) {
			assertTrue(controller.moveUnit(model.getTileAt(i, 9).getUnit(), i + 1, 9));
		}
		controller.endTurn();
		// move in front of enemy city, they should not have the production to have any
		// units yet
		assertTrue(controller.moveUnit(model.getTileAt(13, 9).getUnit(), 14, 9));
		// attack enemy city
		assertTrue(controller.moveUnit(model.getTileAt(14, 9).getUnit(), 15, 9));
		controller.endTurn();
		assertTrue(controller.moveUnit(model.getTileAt(14, 9).getUnit(), 15, 9));
		controller.endTurn();
		// scout attacked and should eventually be killed by a defending unit
		// insert enemy unit and move it onto the city

		model.nextPlayer();
		for (int i = 0; i < 40; i++) {
			controller.getTileAt(15, 9).getOwnerCity().cityIncrement();
		}
		assertTrue(controller.createUnit(15, 9, "Settler"));
		assertFalse(controller.createUnit(15, 9, "Settler"));
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
		// assertTrue(controller.gameOver());
	}

	// game over should work just fine
	@Test
	void testComputerDestroyCity() {
		CivModel model = new CivModel(1);
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
		controller.startGame();
		assertFalse(controller.gameOver());
		for (int i = 0; i < 4; i++)
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
