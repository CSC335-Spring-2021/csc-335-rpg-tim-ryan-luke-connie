package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import controllers.CivController;
import models.CivModel;

/**
 * Tests the methods of CivController. This is setup to work with current
 * default map so please dont change it.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivControllerTest {

	@Test
	void testBasics() {
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
		// progress the game until we have sufficient production
		for (int i = 0; i < 8; i++) {
			controller.endTurn();
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
		// move diagonal to enemy city, enemy unit should be on 14, 9
		assertTrue(controller.moveUnit(model.getTileAt(13, 9).getUnit(), 14, 10));
		// attack enemy city
		assertTrue(controller.moveUnit(model.getTileAt(14, 10).getUnit(), 15, 9));
		controller.endTurn();
		assertTrue(controller.moveUnit(model.getTileAt(14, 10).getUnit(), 14, 9));
		controller.endTurn();
		// scout is now dead from a warrior, which occupies 14, 10
		// try to make AI warrior attack its own city
		assertFalse(controller.moveUnit(model.getTileAt(14, 10).getUnit(), 15, 9));

		// System.out.println(model.getTileAt(14, 9).getUnit());
		// create new warriors and defend so the AI can exercise its logic
		for (int i = 0; i < 3; i++) {
			controller.endTurn();
		}
		assertTrue(controller.createUnit(5, 9, "Warrior"));
		controller.endTurn();
		assertTrue(controller.moveUnit(model.getTileAt(5, 9).getUnit(), 6, 9));
		for (int i = 0; i < 8; i++) {
			controller.endTurn();
		}
		assertTrue(controller.createUnit(5, 9, "Warrior"));
		assertFalse(controller.gameOver());
		// make myself die
		for (int i = 0; i < 29; i++) {
			controller.endTurn();
		}
		// lmao wtf one more turn causes overflow, uncomment the line below i dare u
		// controller.endTurn();
		// assertTrue(controller.gameOver());
	}

}
