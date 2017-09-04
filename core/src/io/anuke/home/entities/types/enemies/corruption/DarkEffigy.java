package io.anuke.home.entities.types.enemies.corruption;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Vars;
import io.anuke.home.entities.traits.BossTrait;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.HealthBarTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectile;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.*;
import io.anuke.ucore.ecs.extend.Events.Death;
import io.anuke.ucore.ecs.extend.processors.CollisionProcessor;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class DarkEffigy extends Enemy{
	private static final Color tent = new Color(0x500680ff);
	private static final float plife = 80;
	
	public DarkEffigy(){
		maxhealth = 4600;
		hitsize = 20;
		hitoffset = 0;
		height = 20;
		range = 400;
		
		event(Death.class, spark->{
			float x = spark.pos().x, y = spark.pos().y;
			
			Effects.shake(10, 60f);
			Effects.effect("wraithdie", x, y+10);
			Effects.sound("bossdie", spark);
			
			for(int i = 0; i < 40; i ++){
				Timers.run(Mathf.random(55), ()->{
					Effects.effect("purpleblood", x + Mathf.range(20), y + Mathf.range(20));
				});
				
			}
			
			Timers.run(120, ()->{
				Vars.ui.showVictory();
			});
			
			Basis.instance().getProcessor(CollisionProcessor.class)
			.getNearby(x, y, 300, e->{
				if(e.getType() instanceof Projectile){
					Effects.effect("shotshrink", e);
					e.remove();
				}
			});
			
			spark.remove();
		});
	}
	
	@Override
	public void retarget(Spark spark){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		if(Vars.control == null) return;
		
		enemy.target = Vars.control.getPlayer();
		
		if(enemy.targetValid(spark)){
			enemy.idletime = 0;
		}else{
			enemy.target = null;
			reset(spark);
		}
	}
	
	@Override
	public TraitList traits(){
		return super.traits().with(new BossTrait("Dark Effigy")).exclude(HealthBarTrait.class);
	}

	@Override
	public void move(Spark spark){
		final float height = 12f;
		final float x = spark.pos().x, y = spark.pos().y;
		Data data = spark.get(Data.class);
		Spark target = spark.get(EnemyTrait.class).target;
		
		if(Timers.get(this, "changephase", data.phasetime)){
			Phase next = Phase.values()[(data.phase.ordinal() + 1) % Phase.values().length];
			if(next == Phase.idle)
				next = Phase.values()[1];
			data.phase = Phase.idle;
			
			Phase fin = next;
			
			Timers.run(data.transitiontime, ()->{
				if(target != null) 
					data.rotation = spark.pos().angleTo(target, 0, height);
				data.phase = fin;
			});
		}
		if(data.phase != Phase.chase && data.phase != Phase.spam){
			spark.pos().x = Mathf.lerp(x, data.startx, 0.03f*Mathf.delta());
			spark.pos().y = Mathf.lerp(y, data.starty, 0.03f*Mathf.delta());
		}
		
		if(data.phase == Phase.swirl){
			if(Timers.get(this, "circle", 1)){
				shoot(spark, Projectiles.tentashot, 20, x, y+height, data.rotation);
				data.rotation += 22f;
			}
		}else if(data.phase == Phase.blast){
			if(Timers.get(this, "blast", 30)){
				data.eyeflash = 1f;
				data.rotation += 15f;
				Geometry.circle(8, f->{
					shoot(spark, Projectiles.golemsplitshot, x, y+height, f + data.rotation);
				});
			}
		}else if(data.phase == Phase.seek){
			if(Timers.get(this, "seek", 2)){
				shoot(spark, Projectiles.tentashot, 12, x, y+height, spark.pos().angleTo(target, 0, height)+Mathf.range(50f));
			}
		}else if(data.phase == Phase.chase){
			if(Timers.get(this, "chase", 90)){
				
				Timers.runFor(40f, ()->{
					float a = 0.05f*Mathf.delta();
					spark.pos().x = Mathf.lerp(spark.pos().x, target.pos().x, a);
					spark.pos().y = Mathf.lerp(spark.pos().y, target.pos().y, a);
					if(Timers.get(this, "chases", 1f))
						Effects.effect("darkdash", spark.pos().x, spark.pos().y+10);
				}, ()->{
					float cx = spark.pos().x, cy = spark.pos().y;
					data.eyeflash = 1f;
					Effects.shake(4f, 4f);
					
					Geometry.circle(4, f->{
						shoot(spark, Projectiles.shadowsplit, cx, cy+height, f);
					});
					
					Geometry.circle(25, f->{
						shoot(spark, Projectiles.shadowshot, cx, cy+height, f);
					});
				});
				
			}
		}else if(data.phase == Phase.tentacles){
			if(Timers.get(this, "circle2", 5)){
				Geometry.circle(4,f->{
					shoot(spark, Projectiles.tentashot, 20, x, y+height, data.rotation+45+f);
				});
				data.rotation += 7f;
			}
		}else if(data.phase == Phase.spam){
			if(Timers.get(this, "spam", 50)){
				
				float targetx = data.startx + Mathf.range(120);
				float targety = data.starty + Mathf.range(120);
				Timers.runFor(20f, ()->{
					float a = 0.1f*Mathf.delta();
					
					spark.pos().x = Mathf.lerp(spark.pos().x, targetx, a);
					spark.pos().y = Mathf.lerp(spark.pos().y, targety, a);
					
					if(Timers.get(this, "chases", 1f))
						Effects.effect("darkdash", spark.pos().x, spark.pos().y+10);
				}, ()->{
					float cx = spark.pos().x, cy = spark.pos().y;
					data.eyeflash = 1f;
					if(Mathf.chance(0.5)){
						Geometry.circle(8, f->{
							shoot(spark, Projectiles.shadowsplit, cx, cy+height, f);
						});
					}else{
						Geometry.circle(20, f->{
							shoot(spark, Projectiles.shadowshot, cx, cy+height, f);
						});
					}
					
				});
			}
		}else if(data.phase == Phase.lanes){
			if(Timers.get(this, "lanes", 20)){
				
				float offset = data.rotation+360f*(Timers.getTime(this, "changephase") / data.phasetime);
				
				for(int i = 0; i < 2; i ++)
				Geometry.shotgun(16, 10, i*180+offset+30, f->{
					shoot(spark, Projectiles.shadowshot, x, y+height, f);
				});
			}
		}
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		Data data = spark.get(Data.class);
		data.startx = spark.pos().x;
		data.starty = spark.pos().y;
		
		for(int i = 0; i < data.particles.length; i ++){
			data.particles[i] = new Particle();
			data.particles[i].reset();
			data.particles[i].life = Mathf.random(plife);
		}
		
		trait.draw(p->{
			float x = spark.pos().x, y = spark.pos().y;
			
			if(data.eyeflash > 0){
				data.eyeflash -= Mathf.delta()*0.05f;
				data.eyeflash = Mathf.clamp(data.eyeflash);
			}
			
			float si = Mathf.sin(Timers.time(), 30f, 4f)-2f;
			
			Draw.color(Color.BLACK, tent, 0.5f);
			Draw.thick(2f);
			Draw.circle(x, y+10f, 10f+si);
			Draw.spikes(x, y+10f, 8f+si, 6f, 8, Timers.time());
			
			for(Particle part : data.particles){
				part.life += Mathf.delta();
				part.y += Mathf.delta()*0.4f;
				
				float size = part.fract()*10f;
				Draw.color(tent, Color.CLEAR, 1f-part.sfract());
				Draw.rect("circle", part.x+x, part.y+y, size, size);
				
				if(part.fract() <= 0f){
					part.reset();
				}
			}
			
			Draw.thick(4f);
			Draw.color(new Color(0xa020f000), Color.WHITE, data.eyeflash);
			Draw.polygon(3, x, y+height + Mathf.sin(Timers.time(), 20f, 2f), 4);
			
			Draw.color(Color.PURPLE, Color.ORANGE, data.eyeflash);
			Draw.thick(1f);
			Draw.polygon(3, x, y+height + Mathf.sin(Timers.time(), 20f, 2f), 4);
			
			Draw.reset();
			
			p.layer = y;
		});
	}
	
	@Override
	public void reset(Spark spark){
		Data data = spark.get(Data.class);
		data.phase = Phase.values()[0];
		//TODO
		//spark.pos().set(data.startx, data.starty);
		spark.health().heal();
		Timers.reset(this, "changephase", data.phasetime);
	}
	
	@Override
	public Trait data(){
		return new Data();
	}
	
	static class Data extends Trait{
		private Particle[] particles = new Particle[100];
		private float plife = 80;
		private float eyeflash = 0f;
		private float startx, starty;
		
		private float rotation = 0f;
		private Phase phase = Phase.values()[0];
		private float phasetime = 1200f;
		private float transitiontime = 260f;
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
