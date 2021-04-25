package tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import controllers.CivController;
import models.CivModel;

/**
 * Tests the methods of CivController.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivControllerTest {

	@Test
	void testBasics() {
		CivModel model = new CivModel(2);
		CivController controller = new CivController(model);

		assertNotNull(controller);
	}

}
