package io.anuke.home.entities.enemies;

import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.util.Timers;

public class MarbleObelisk extends Enemy{
	float ang = 0f;
	
	public MarbleObelisk(){
		height = 4;
	}
	
	public void drawRenderables(){
		draw(p->{
			Draw.grect("marbleobelisk", x, y);
			p.layer = y;
		});
		
		shadow(18);
	}
	
	public void move(){
		
		if(Timers.get(this, "circle", 40)){
			shoot(Projectiles.golemwave, x, y+height, ang);
			shoot(Projectiles.golemwave, x, y+height, ang+180);
			
			ang += 15f;
		}
	}
}
