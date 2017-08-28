package io.anuke.home.items;

import io.anuke.ucore.util.Strings;

public class Item{
	public final String name;
	public final WeaponType weapontype;
	public final ItemType type;
	public int attackbuff = 0, defensebuff = 0, speedbuff = 0;
	public String formalName;
	public String description = null;
	
	public Item(String name, ItemType type){
		this.name = name;
		this.type = type;
		this.formalName = Strings.capitalize(name);
		this.weapontype = null;
	}
	
	public Item(String name, String formal, ItemType type){
		this.name = name;
		this.type = type;
		this.formalName = formal;
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
	
	public Item(String name, String fname, ItemType type, WeaponType weapon){
		this.name = name;
		this.type = type;
		this.formalName = fname;
		this.weapontype = weapon;
	}
	
	public String getStats(){
		String out = "";
		if(weapontype != null){
			out += "\n[orange]" + weapontype.getStatString(); 
		}
		
		if(Math.abs(attackbuff) > 0){
			out += "\n" + parse(attackbuff) + " Attack";
		}
		
		if(Math.abs(defensebuff) > 0){
			out += "\n" + parse(defensebuff) + " Defense";
		}
		
		if(Math.abs(speedbuff) > 0){
			out += "\n" + parse(speedbuff) + " Speed";
		}
		
		return out;
	}
	
	private String parse(int i){
		if(i < 0){
			return "[crimson]- " + -i;
		}else{
			return "[lime]+ " + i;
		}
	}
}
