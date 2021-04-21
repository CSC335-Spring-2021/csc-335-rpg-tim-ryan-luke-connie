package controllers;

import models.CivModel;

/**
 * Provides methods to calculate data about game state or act as a computer player that updates
 * game state.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivController {

	private final CivModel model;


	/**
	 * Constructor for controller
	 *
	 * @param model model that the controller will interact with to store the results of the
	 *              operations it performs
	 */
	public CivController(CivModel model) {
		this.model = model;
	}

}
