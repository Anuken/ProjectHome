package io.anuke.home.items;

import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.Player;
import io.anuke.ucore.modules.Module;

public class WeaponType{
	protected static Vector2 vector = Module.vector;
	public int damage = 1;
	public float speed;
	
	public String getStatString(){
		return "Damage: " + damage + 
				"\n[firebrick]Attack Speed: " + String.format("%.1f", 60f/speed);
	}
	
	public float getAngleOffset(){
		return 0f;
	}
	
	public void draw(Player player, Item item){
		
	}
	
	public void update(Player player){
		
	}
}
