package io.anuke.home.entities.traits;

import com.badlogic.gdx.utils.Array;

import io.anuke.home.items.Item;
import io.anuke.home.items.ItemStack;
import io.anuke.ucore.ecs.Trait;

public class InventoryTrait extends Trait{
	public Array<Item> melee, ranged, armor;
	public Array<ItemStack> consumables;
	public int capacity;
	
	public int weaponEquip;
	public int armorEquip;
	public boolean isRanged;
	
	public InventoryTrait(int capacity){
		this.capacity = capacity;
		
		melee = new Array<>();
		ranged = new Array<>();
		armor = new Array<>();
		consumables = new Array<>();
	}
	
	public Array<Item> currentWeapons(){
		return isRanged ? ranged : melee;
	}
	
	public Item weapon(){
		Array<Item> items = isRanged ? ranged : melee;
		
		return items.size == 0 ? null : items.get(capacity);
	}
}
