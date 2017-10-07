package io.anuke.home.items;

import com.badlogic.gdx.utils.reflect.ClassReflection;

public class ItemFilter{
	private Class<? extends Item>[] types;
	
	public ItemFilter(Class<? extends Item>... types){
		this.types = types;
	}
	
	public Class<? extends Item> getType(int index){
		return index >= types.length ? null : types[index];
	}
	
	public boolean accept(int index, Item item){
		return index >= types.length || ClassReflection.isAssignableFrom(types[index], item.getClass());
	}
}
