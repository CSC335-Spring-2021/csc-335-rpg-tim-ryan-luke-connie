package components;

import java.awt.Point;

import models.Player;

public class Milita extends Unit {

	public Milita(Player player, Point coord) {
		super(player, coord);
		label = "Milita";
		HP = 50;
		maxHP = HP;
		maxMovement = 1;
		resetMovement();
		sight = 1;
		attackValue = 10;
	}

}
