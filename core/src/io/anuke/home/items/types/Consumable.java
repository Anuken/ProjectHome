package io.anuke.home.items.types;

import io.anuke.home.items.Item;

public class Consumable extends Item{

	public Consumable(String name, String formal) {
		super(name, formal, "Consumable");
		stackable = true;
	}

}
