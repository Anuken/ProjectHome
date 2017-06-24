package io.anuke.home.entities.enemies;

import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectiles;
import io.anuke.home.items.Items;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class MarbleDrone extends Enemy{
	
	static{
		int c = 1;
		setDrops(MarbleGolem.class,
			Items.ascendarmor, c,
			Items.densearmor, c,
			Items.hellarmor, c,
			Items.juggarmor, c,
			Items.reflectarmor, c,
			
			Items.icesword, c, 
			Items.daggersword, c, 
			Items.phasesword, c, 
			Items.silversword, c,
			
			Items.aetherstaff, c,
			Items.fusionstaff, c,
			Items.orbstaff, c,
			Items.planestaff, c,
			Items.scorchstaff, c
		);
	}
	
	public void drawRenderables(){
		setMaxHealth(140);
		
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
