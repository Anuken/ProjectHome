package io.anuke.home.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Boss extends Enemy{
	static final Color tent = new Color(0x500680ff);
	private Particle[] particles = new Particle[100];
	private float plife = 80;
	private float eyeflash = 0f;
	private float startx, starty;
	
	private float rotation = 0f;
	private Phase phase = Phase.values()[0];
	private float phasetime = 800f;
	
	public Boss(){
		setMaxHealth(1000);
		hitsize = 20;
		hitoffsety = 10;
		
		for(int i = 0; i < particles.length; i ++){
			particles[i] = new Particle();
			particles[i].reset();
			particles[i].life = Mathf.random(plife);
		}
		
		height = 20f;
		range = 260;
	}
	
	public void drawRenderables(){
		draw(p->{
			
			if(eyeflash > 0){
				eyeflash -= delta*0.05f;
				eyeflash = Mathf.clamp(eyeflash);
			}
			
			float si = Mathf.sin(Timers.time(), 30f, 4f)-2f;
			
			Draw.color(Color.BLACK, tent, 0.5f);
			Draw.thick(2f);
			Draw.circle(x, y+10f, 10f+si);
			Draw.spikes(x, y+10f, 8f+si, 6f, 8, Timers.time());
			
			for(Particle part : particles){
				part.life += delta;
				part.y += delta*0.4f;
				
				float size = part.fract()*10f;
				Draw.color(tent, Color.CLEAR, 1f-part.sfract());
				Draw.rect("circle", part.x+x, part.y+y, size, size);
				
				if(part.fract() <= 0f){
					part.reset();
				}
			}
			
			Draw.thick(4f);
			Draw.color(new Color(0xa020f000), Color.WHITE, eyeflash);
			Draw.polygon(3, x, y+height + Mathf.sin(Timers.time(), 20f, 2f), 4);
			
			Draw.color(Color.PURPLE, Color.ORANGE, eyeflash);
			Draw.thick(1f);
			Draw.polygon(3, x, y+height + Mathf.sin(Timers.time(), 20f, 2f), 4);
			
			Draw.reset();
			
			p.layer = y;
		});
	}
	
	public void reset(){
		phase = Phase.values()[0];
		set(startx, starty);
	}
	
	public void move(){
		//who needs actual state machines? /s
		
		if(Timers.get(this, "changephase", phasetime)){
			Phase next = Phase.values()[(phase.ordinal() + 1) % Phase.values().length];
			phase = Phase.idle;
			
			Timers.run(120, ()->{
				phase = next;
			});
		}
		
		if(phase != Phase.chase && phase != Phase.spam){
			x = Mathf.lerp(x, startx, 0.03f*delta);
			y = Mathf.lerp(y, starty, 0.03f*delta);
		}
		
		float height = 12f;
		
		if(phase == Phase.swirl){
			if(Timers.get(this, "circle", 1)){
				shoot(Projectiles.tentashot, x, y+height, rotation);
				rotation += 22f;
			}
		}else if(phase == Phase.blast){
			if(Timers.get(this, "blast", 40)){
				eyeflash = 1f;
				Geometry.circle(6, f->{
					shoot(Projectiles.golemsplitshot, x, y+height, f);
				});
			}
		}else if(phase == Phase.seek){
			if(Timers.get(this, "seek", 2)){
				shoot(Projectiles.tentashot, x, y+height, angleTo(target, height)+Mathf.range(40f));
			}
		}else if(phase == Phase.chase){
			if(Timers.get(this, "chase", 90)){
				
				Timers.runFor(40f, ()->{
					float a = 0.05f*delta;
					x = Mathf.lerp(x, target.x, a);
					y = Mathf.lerp(y, target.y, a);
					if(Timers.get(this, "chases", 1f))
						Effects.effect("darkdash", x, y+10);
				}, ()->{
					eyeflash = 1f;
					Effects.shake(4f, 4f);
					
					Geometry.circle(4, f->{
						shoot(Projectiles.shadowsplit, x, y+height, f);
					});
					
					Geometry.circle(25, f->{
						shoot(Projectiles.shadowshot, x, y+height, f);
					});
				});
				
			}
		}else if(phase == Phase.spam){
			if(Timers.get(this, "spam", 50)){
				
				float targetx = startx + Mathf.range(120);
				float targety = starty + Mathf.range(120);
				Timers.runFor(20f, ()->{
					float a = 0.1f*delta;
					
					x = Mathf.lerp(x, targetx, a);
					y = Mathf.lerp(y, targety, a);
					
					if(Timers.get(this, "chases", 1f))
						Effects.effect("darkdash", x, y+10);
				}, ()->{
					Timers.run(0, ()->{
						if(Mathf.chance(0.5)){
							Geometry.circle(8, f->{
								shoot(Projectiles.shadowsplit, x, y+height, f);
							});
						}else{
							Geometry.circle(20, f->{
								shoot(Projectiles.shadowshot, x, y+height, f);
							});
						}
					});
				});
			}
		}else if(phase == Phase.lanes){
			if(Timers.get(this, "lanes", 20)){
				
				float offset = 360f*(Timers.getTime(this, "changephase") / phasetime);
				
				
				for(int i = 0; i < 2; i ++)
				Geometry.shotgun(16, 10, i*180+offset, f->{
					shoot(Projectiles.shadowshot, x, y+height, f);
				});
			}
		}
		
	}
	
	public Boss set(float x, float y){
		super.set(x-6f, y-12f);
		startx = x;
		starty = y;
		return this;
	}
	
	private enum Phase{
		idle, swirl, blast, seek, chase, spam, lanes;
	}
	
	private class Particle extends Vector2{
		float life;
		
		public float fract(){
			return 1f-life/plife;
		}
		
		public float ifract(){
			return life/plife;
		}
		
		public float sfract(){
			return (0.5f-Math.abs(life/plife-0.5f))*2f;
		}
		
		void reset(){
			set(Mathf.range(6), Mathf.random(0f, 20f));
			life = 0f;
		}
	}
}
