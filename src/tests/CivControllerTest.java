package tests;

import controllers.CivController;
import models.CivModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the methods of CivController.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivControllerTest {

	@Test
	void testBasics() {
		CivModel model = new CivModel();
		CivController controller = new CivController(model);

		assertNotNull(controller);
	}

}
