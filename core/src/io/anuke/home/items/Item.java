package io.anuke.home.items;

import io.anuke.ucore.util.Strings;

public class Item{
	public final String name;
	public final WeaponType weapontype;
	public final ItemType type;
	public String formalName;
	public String description = null;
	
	public Item(String name, ItemType type){
		this.name = name;
		this.type = type;
		this.formalName = Strings.capitalize(name);
		this.weapontype = null;
	}
	
	public Item(String name, WeaponType weapon){
		this.name = name;
		this.type = ItemType.weapon;
		this.formalName = Strings.capitalize(name);
		this.weapontype = weapon;
	}
	
	public Item(String name, String fname, WeaponType weapon){
		this.name = name;
		this.type = ItemType.weapon;
		this.formalName = fname;
		this.weapontype = weapon;
	}
}
