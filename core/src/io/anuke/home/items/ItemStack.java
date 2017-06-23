package io.anuke.home.items;

public class ItemStack{
	public Item item;
	public int amount = 1;
	
	public ItemStack(Item item){
		this.item = item;
	}
	
	public ItemStack(Item item, int amount){
		this.item = item;
		this.amount = amount;
	}
	
	public ItemStack copy(){
		return new ItemStack(item, amount);
	}
}
