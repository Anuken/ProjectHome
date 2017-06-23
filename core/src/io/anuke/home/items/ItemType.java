package io.anuke.home.items;

public enum ItemType{
	material(true), weapon, consumable(true);
	
	private ItemType(){
		
	}
	
	private ItemType(boolean stackable){
		this.stackable = stackable;
	}
	
	public boolean stackable = false;
}
