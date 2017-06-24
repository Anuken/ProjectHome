package io.anuke.home.entities.enemies;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Tentapod  extends Enemy{
	float reload = 70;
	
	public Tentapod(){
		hiteffect = "blood";
		deatheffect = "purpleblood";
		
		setMaxHealth(190);
		
		height = 4;
		hitoffsety = height;
		hitsize = 10;
	}
	
	public void drawRenderables(){
		draw(p->{
			Draw.grect("tentaguardbottom", x, y-2);
			Draw.grect("tentaguardi"+(frames(10, 4)), x, y);
			
			vector.set(0, 0);
			if(target != null){
				vector.set(target.x-x, target.y- (y+height));
				vector.limit(0.9f);
			}
			
			Draw.color(Color.SCARLET, Color.PURPLE, Timers.getTime(this, "reload")/reload);
			Draw.grect("tentaguardeye", x+vector.x, y+vector.y);
			Draw.alpha((Timers.getTime(this, "reload")-5f)/reload);
			Draw.grect("tentaguardeye2", x+vector.x, y+vector.y);
			
			Draw.reset();
			
			p.layer = y;
		});
		
		shadow(18);
	}
	
	public void move(){
		//moveToward();
		
		if(Timers.get(this, "reload", reload)){
			Effects.effect("tentashoot", x, y+height+3);
			shoot(Projectiles.tentaball, x, y+height, angleTo(target) + Mathf.range(3));
		}
	}
}
