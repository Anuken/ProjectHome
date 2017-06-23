package io.anuke.home.entities;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class MarbleDrone extends Enemy{
	
	public void drawRenderables(){
		setMaxHealth(60);
		
		draw(p->{
			float rise = Mathf.sin(Timers.time(), 16f, 2f)+6f;
			Draw.grect("marbledrone", x, y+rise);
			p.layer = y;
		});
		
		shadow(8);
		hitoffsety = 9;
	}
	
	public void move(){
		moveToward();
		
		if(Timers.get(this, "reload", 80)){
			for(int i = 0; i < 5; i ++){
				Timers.run(i*9, ()->{
					if(target == null || isDead()) return;
					Effects.effect("orangeblap", x, y+8);
					shoot(Projectiles.droneshot, x, y+8, angleTo(target));
				});
			}
		}
	}
}
