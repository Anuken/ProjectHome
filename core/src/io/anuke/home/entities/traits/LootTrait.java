package io.anuke.home.entities.traits;

import com.badlogic.gdx.utils.Array;

import io.anuke.home.items.Item;
import io.anuke.home.items.ItemStack;
import io.anuke.ucore.ecs.Trait;

/**<i>Note: This is a type trait.</i>*/
public class LootTrait extends Trait{
	public Array<Drop> drops;
	
	private LootTrait(){}
	
	//**TODO make this more modular*/
	public LootTrait(Object... objects){
		drops = new Array<Drop>();

		for(int i = 0; i < objects.length; i += 2){
			Drop drop = new Drop();

			if(objects[i] instanceof Item){
				drop.stack = new ItemStack((Item) objects[i], 1);
			}else{
				drop.stack = (ItemStack) objects[i];
			}
			
			if(objects[i + 1] instanceof Double){
				drop.chance = (double)(objects[i + 1])/100.0;
			}else if(objects[i + 1] instanceof Float){
				drop.chance = (float)(objects[i + 1])/100.0;
			}else{
				drop.chance = (int)(objects[i + 1])/100.0;
			}
			
			drops.add(drop);
		}
	}
	
	static class Drop{
		public double chance;
		public ItemStack stack;

		public String toString(){
			return stack.item.name + ": " + chance * 100 + "%";
		}
	}
}
