package io.anuke.home.entities;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Timers;

public class Golem extends Enemy{
	float armraise = 0;
	
	public Golem(){
		maxhealth = 10;
		heal();
		
		speed = 0.4f;
		
		height = 6;
		hitoffsety = height;
		
	}
	
	public void drawRenderables(){

		draw(p->{
			Draw.grect("golem-armless", x, y);
			
			Draw.grect("golem-arms", x, y + armraise);
			
			p.layer = y;
		});
		
		drawShadow(13, 0);
	}
	
	public void move(){
		vector.set(target.x-x, target.y-y);
		vector.setLength(speed*delta);
		
		if(Timers.get(this, "smash", 100)){
			float rdir = 30;
			Timers.runFor(rdir, ()->{
				armraise += 3f/rdir*delta;
			}, ()->{
				if(isDead()) return;
				
				armraise = -2f;
				Effects.shake(2f, 4f, this);
				Effects.effect("eyeflash", x, y+11);
				Effects.effect("golemwave", this);
				
				Geometry.circle(8, ang->{
					shoot(Projectiles.golemshot, ang);
				});
				
				Timers.run(20f, ()->{
					armraise = 0f;
				});
			});
		}
		
		//move(vector.x, vector.y);
	}
}
