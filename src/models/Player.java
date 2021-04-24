package models;
import components.Unit;

import java.util.ArrayList;

import components.City;

public class Player {
	private ArrayList<Unit> units;
	private ArrayList<City> cities;
	private boolean isHuman;
	public Player(int isHuman) {
		units = new ArrayList<Unit>();
		cities = new ArrayList<City>();
		if (isHuman == 1)
			this.isHuman = true;
		else
			this.isHuman = false;
	}
	
	public void addCity(City city) {
		this.cities.add(city);
	}
	
	public void addUnit(Unit unit) {
		units.add(unit);
	}
	
	public ArrayList<Unit> getUnits() {
		return units;
	}
	public ArrayList<City> getCities() {
		return cities;
	}
	
	public boolean isHuman() {
		return this.isHuman;
	}
}
