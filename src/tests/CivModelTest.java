package tests;

import models.CivModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the methods of CivModel.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivModelTest {

  @Test
  void testBasics() {
    CivModel model = new CivModel();

    assertNotNull(model);
  }
}
