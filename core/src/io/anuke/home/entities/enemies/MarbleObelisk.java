package io.anuke.home.entities.enemies;

import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Timers;

public class MarbleObelisk extends Enemy{
	float ang = 0f;
	
	static{
		setDrops(MarbleObelisk.class, MarbleGolem.class);
	}
	
	public MarbleObelisk(){
		height = 4;
		setMaxHealth(250);
		deathsound = "blockdie";
	}
	
	public void drawRenderables(){
		draw(p->{
			Draw.grect("marbleobelisk", x, y);
			p.layer = y;
		});
		
		shadow(18);
	}
	
	public void move(){
		
		if(Timers.get(this, "circle", 18)){
			Effects.effect("golemwave", this);
			shoot(Projectiles.golemwave, x, y+height, ang);
			shoot(Projectiles.golemwave, x, y+height, ang+180);
			
			ang += 20f;
		}
		
		if(Timers.get(this, "bigshot", 300)){
			Effects.effect("golemwave", this);
			Effects.shake(3, 4f);
			
			for(int i = 0; i < 4; i ++)
				Effects.effect("purpleeyeflash", x, y+i+6);
			
			Geometry.circle(8, f->{
				shoot(Projectiles.golemsplitshot, x, y+height, f);
			});
		}
	}
}
