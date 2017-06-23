package io.anuke.home.entities.enemies;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Tentafly extends Enemy{
	float reload = 120;
	
	public Tentafly(){
		setMaxHealth(70);
		
		hiteffect = "blood";
		deatheffect = "purpleblood";
		
		height = 8;
		speed = 1f;
		hitoffsety = 12;
	}
	
	public void drawRenderables(){
		draw(p->{
			float sin = Mathf.sin(Timers.time(), 4, 0.75f)+0.75f;
			float raise = 6f+Mathf.sin(Timers.time(), 12f, 2f);
			
			Draw.grect("tentafly", x, y+raise);
			Draw.grect("tentaflywingsbot", x, y-sin+raise);
			Draw.grect("tentaflywingstop", x, y+sin+raise);
			
			vector.set(0, 0);
			if(target != null){
				vector.set(target.x-x, target.y- (y+height));
				vector.limit(0.9f);
			}
			
			Draw.color(Color.SCARLET, Color.PURPLE, target == null ? 0f : Timers.getTime(this, "reload") / reload);
			Draw.grect("tentaflyeye", x+vector.x, y+vector.y+raise);
			if(target != null){
				Draw.alpha((Timers.getTime(this, "reload") - 15f) / reload);
				Draw.grect("tentaflyeye2", x + vector.x, y + raise + vector.y);
			}
			Draw.color();
			p.layer = y;
		});
		
		shadow(14);
	}
	
	public void move(){
		moveToward();
		
		if(Timers.get(this, "reload", reload)){
			Effects.effect("tentashoot", x, y + height);
			Geometry.shotgun(7, 9f, angleTo(target), f->{
				shoot(Projectiles.tentashot, x, y+8, f);
			});
			
			Geometry.circle(8, f->{
			//	shoot(Projectiles.tentashot, x, y+8, f);
			});
		}
		
	}
}
