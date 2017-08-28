package io.anuke.home.items;

public enum ItemType{
	material(true), consumable(true), weapon, ranged_weapon, armor, soul /*soul???*/;
	
	private ItemType(){
		
	}
	
	private ItemType(boolean stackable){
		this.stackable = stackable;
	}
	
	public boolean stackable = false;
}
