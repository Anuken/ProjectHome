package io.anuke.home.items.types;

import io.anuke.home.items.Item;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.util.Strings;

public abstract class Weapon extends Item{
	public int damage = 1;
	public float speed;
	
	public Weapon(String name, String formal) {
		super(name, formal, "Weapon");
	}
	
	public abstract void draw(Spark player, Item item);
	
	public abstract void update(Spark player);
	
	public String getStatString(){
		return "\n[orange]Damage: " + damage + 
				"\n[FIREBRICK]Attack Speed: " + Strings.toFixed(60f/speed, 1);
	}
	
	@Override
	public String getStats(){
		return super.getStats() + getStatString();
	}
	
	public float getAngleOffset(){
		return 0f;
	}

}
