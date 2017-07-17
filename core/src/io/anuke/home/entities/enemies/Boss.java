package io.anuke.home.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Vars;
import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectile;
import io.anuke.home.entities.ecs.types.Projectiles;
import io.anuke.home.items.Items;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.entities.Entities;
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
	private float phasetime = 1200f;
	private float transitiontime = 260f;
	
	static{
		int c = 20;
		setDrops(Boss.class,
			Items.ascendarmor, c,
			Items.hellarmor, c,
			Items.juggarmor, c,
			Items.reflectarmor, c,
				
			Items.daggersword, c, 
			Items.phasesword, c, 
			Items.silversword, c,
				
			Items.aetherstaff, c,
			Items.fusionstaff, c,
			Items.orbstaff, c,
			Items.planestaff, c
		);
	}
	
	public Boss(){
		setMaxHealth(4600);
		hitsize = 20;
		hitoffsety = 10;
		
		for(int i = 0; i < particles.length; i ++){
			particles[i] = new Particle();
			particles[i].reset();
			particles[i].life = Mathf.random(plife);
		}
		
		height = 20f;
		range = 400;
	}
	
	@Override
	public void retarget(){
		
		target = Vars.control.getPlayer();
		
		if(targetValid()){
			idletime = 0;
		}else{
			target = null;
			reset();
		}
		
		if(target == null){
			Vars.control.resetBoss(this);
		}else{
			Vars.control.setBoss(this);
		}
	}
	
	//no health bar
	@Override
	public void draw(){}
	
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
	
	public void onDeath(){
		Effects.shake(10, 60f);
		Effects.effect("wraithdie", x, y+10);
		Effects.sound("bossdie", this);
		
		for(int i = 0; i < 40; i ++){
			Timers.run(Mathf.random(55), ()->{
				Effects.effect("purpleblood", x + Mathf.range(20), y + Mathf.range(20));
			});
			
		}
		
		Timers.run(120, ()->{
			Vars.ui.showVictory();
		});
		
		Entities.getNearby(x, y, 300, e->{
			if(e instanceof Projectile && e != this){
				Effects.effect("shotshrink", e);
				e.remove();
			}
		});
		
		remove();
	}
	
	public void reset(){
		phase = Phase.values()[0];
		x = startx;
		y = starty;
		heal();
		Timers.reset(this, "changephase", phasetime);
	}
	
	public void move(){
		//who needs actual state machines? /s
		
		float height = 12f;
		
		if(Timers.get(this, "changephase", phasetime)){
			Phase next = Phase.values()[(phase.ordinal() + 1) % Phase.values().length];
			if(next == Phase.idle)
				next = Phase.values()[1];
			phase = Phase.idle;
			
			Phase fin = next;
			
			Timers.run(transitiontime, ()->{
				if(target != null) 
					rotation = angleTo(target, height);
				phase = fin;
			});
		}
		if(phase != Phase.chase && phase != Phase.spam){
			x = Mathf.lerp(x, startx, 0.03f*delta);
			y = Mathf.lerp(y, starty, 0.03f*delta);
		}
		
		if(phase == Phase.swirl){
			if(Timers.get(this, "circle", 1)){
				shoot(Projectiles.tentashot, 20, x, y+height, rotation);
				rotation += 22f;
			}
		}else if(phase == Phase.blast){
			if(Timers.get(this, "blast", 30)){
				eyeflash = 1f;
				rotation += 15f;
				Geometry.circle(8, f->{
					shoot(Projectiles.golemsplitshot, x, y+height, f + rotation);
				});
			}
		}else if(phase == Phase.seek){
			if(Timers.get(this, "seek", 2)){
				shoot(Projectiles.tentashot, 12, x, y+height, angleTo(target, height)+Mathf.range(50f));
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
		}else if(phase == Phase.tentacles){
			if(Timers.get(this, "circle2", 5)){
				Geometry.circle(4,f->{
					shoot(Projectiles.tentashot, 20, x, y+height, rotation+45+f);
				});
				rotation += 7f;
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
					eyeflash = 1f;
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
			}
		}else if(phase == Phase.lanes){
			if(Timers.get(this, "lanes", 20)){
				
				float offset = rotation+360f*(Timers.getTime(this, "changephase") / phasetime);
				
				for(int i = 0; i < 2; i ++)
				Geometry.shotgun(16, 10, i*180+offset+30, f->{
					shoot(Projectiles.shadowshot, x, y+height, f);
				});
			}
		}
		
	}
	
	public Boss set(float x, float y){
		super.set(x-6f, y-12f);
		startx = this.x;
		starty = this.y;
		return this;
	}
	
	private enum Phase{
		idle, swirl, blast, seek, chase, tentacles, spam, lanes;
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
