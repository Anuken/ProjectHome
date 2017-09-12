package io.anuke.home.entities.types.enemies.library;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.Prototypes;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.types.*;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.*;
import io.anuke.ucore.ecs.extend.Events.*;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

public class BigCrawler extends DarkEnemy{
	Tentacle tentacle = new Tentacle();
	static int numtentacles = 24;
	
	public BigCrawler(){
		range = 200f;
		maxhealth = 2000;
		speed = 0.1f;
		passthrough = true;
		height = 0f;
		
		deatheffect = "bigshadedeath";
		
		event(Death.class, (spark)->{
			
			Timers.runFor(105, ()->{
				if(Timers.get("crawlerspark", 4)){
					Effects.effect("shadecloud", eyeColor, spark.pos().x + Mathf.range(14f), spark.pos().y + Mathf.range(14f));
				}
				Effects.shake(0.25f, 3f);
			}, ()->{
				callSuper(Death.class, spark);
				for(int i = 0; i < 3; i ++){
					float delay = i*15f;
					Timers.run(delay, ()->{
						Effects.effect("bigshadedeath", eyeColor, spark.pos().x + Mathf.range(20), spark.pos().y + Mathf.range(20));
					});
					
				}
				
				for(int i = 0; i < 20; i ++){
					Timers.run(Mathf.random(40), ()->{
						Effects.effect("shadecloud", eyeColor, spark.pos().x + Mathf.range(14f), spark.pos().y + Mathf.range(14f));
					});
				}
				Effects.shake(4f, 5f);
			});
			
			spark.get(Data.class).dying = true;
			
		});
	}
	
	@Override
	public void removed(Spark spark){
		for(Spark t : spark.get(Data.class).tentacles){
			t.remove();
		}
	}
	
	@Override
	public void init(Spark spark){
		
		for(int i = 0; i < numtentacles; i ++){
			Spark t = new Spark(tentacle);
			t.get(TentacleData.class).parent = spark;
			t.get(TentacleData.class).index = i;
			spark.get(Data.class).tentacles[i] = t;
			t.add();
		}
	}

	@Override
	public void move(Spark spark){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		enemy.moveToward(spark);
		
		if(Timers.get(spark, "reload", 500) && spark.health().healthfrac() < 0.7){
			for(int i = 0; i < 20; i ++){
				int id = i;
				Timers.run(8f*i, ()->{
					if(enemy.target == null) return;
					
					shoot(spark, Projectiles.darkshot, 0, enemy.target, 6f, Mathf.sin(id, 1f, 40f));
				});
			}
		}
	}
	
	void handleDamage(Spark spark, Spark source, int damage){
		Spark parent = spark.get(TentacleData.class).parent;
		int index = spark.get(TentacleData.class).index;
		
		parent.getType().callEvent(Damaged.class, parent, source, damage);
		
		if(spark.health().health > 0){
			parent.health().health += damage;
		}else{
		
			parent.get(Data.class).damages[index] = 100f;
		
			spark.health().heal();
		}
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		Data data = spark.get(Data.class);
		
		trait.draw(Sorter.object, Sorter.dark, ()->{
			
			float wake = spark.get(EnemyTrait.class).time/waketime;
			
			if(data.dying){
				spark.get(EnemyTrait.class).time -= Timers.delta()/2f;
			}
			
			if(wake <= 0.001f){
				return;
			}
			
			Draw.color(Color.BLACK, Color.DARK_GRAY, wake);
			
			float offset = spark.getID() * 125f;
			
			for(int i = 0; i < numtentacles; i ++){
				
				float scale = 1f + Mathf.sin(Timers.time() - i*40, 30f, 0.2f);
				
				scale += data.scales[i];
				
				scale = Mathf.clamp(scale, 0, 10f);
				
				if(scale <= 0.1f) continue;
				
				int segments = 7;
				
				float lastangle =  Timers.time()*0.1f + offset + i*(360f/numtentacles);
				float baselength = 17f;
				
				float lastx = spark.pos().x, lasty = spark.pos().y;
				
				for(int s = 0; s < segments; s ++){
					Draw.thick(segments-s);
					
					float angle = lastangle + Mathf.sin(Timers.time() + offset + i*(99+s*9) + s*99, Math.max(38f - s*4f, 6f), 10f);
					
					Angles.translation(angle, Math.max(baselength-s*2f, 4f)*wake*scale);
					
					Draw.line(lastx, lasty, lastx + Angles.x(), lasty + Angles.y());
					
					lastx += Angles.x();
					lasty += Angles.y();
					
					lastangle = angle;
					
					if(Mathf.chance(Timers.delta() * 0.001)){
						Effects.effect("shadesmoke", eyeColor, lastx, lasty);
					}
				}
				
				data.tentacles[i].pos().set(lastx, lasty);
				
				if(data.dying){
					data.scales[i] = Mathf.lerp(data.scales[i], -1.3f, 0.004f*Timers.delta());
				}else if(data.damages[i] > 0){
					data.damages[i] -= Timers.delta();
					
					data.scales[i] = Mathf.lerp(data.scales[i], -0.3f, 0.06f*Timers.delta());
				}else if(enemy.target != null){
					float angto = spark.pos().angleTo(enemy.target);
					float dst = Angles.angleDist(angto, lastangle);
					
					data.scales[i] = Mathf.lerp(data.scales[i], Mathf.clamp(1f-dst/60f)/8f, 0.04f*Timers.delta());
				}else{
					data.scales[i] = Mathf.lerp(data.scales[i], 0f, 0.04f*Timers.delta());
				}
				
				
			}
			
			Draw.thick(1f);
			Draw.color(Color.BLACK, DarkEnemy.eyeColor, wake);
			Draw.polygon(3, spark.pos().x, spark.pos().y, 8f, Timers.time()*1f + offset);
			
			Draw.reset();
		});
	}
	
	@Override
	public Trait data(){
		return new Data();
	}
	
	@Override
	public TraitList traits(){
		TraitList list = super.traits();
		list.get(TileCollideTrait.class).trigger = true;
		return list;
	}
	
	static class Data extends Trait{
		float[] scales = new float[numtentacles];
		float[] damages = new float[numtentacles];
		Spark[] tentacles = new Spark[numtentacles];
		boolean dying = false;
	}
	
	class Tentacle extends Prototype{
		
		public Tentacle(){
			super(true);
			
			event(CollisionFilter.class, (spark, other)->
				other.getType() instanceof Player || (other.getType() instanceof Projectile && !(other.get(ProjectileTrait.class).source.getType() instanceof Enemy))
			);
			
			event(Collision.class, (spark, other)->{
				if(other.getType() == Prototypes.player){
					if(Timers.get(spark, "damage", 20f)){
						other.getType().callEvent(Damaged.class, other, spark, 3);
					}
				}
			});
			
			event(Damaged.class, (spark, source, amount)->{
				handleDamage(spark, source, amount);
			});
		}

		@Override
		public TraitList traits(){
			return new TraitList(
				new PosTrait(),
				new ColliderTrait(7f),
				new TentacleData(),
				new HealthTrait(1),
				new FacetTrait((trait, spark)->{
					TentacleData data = spark.get(TentacleData.class);
					
					trait.draw(Sorter.object, Sorter.dark - 1f, ()->{
						float wake = spark.get(TentacleData.class).parent.get(EnemyTrait.class).time/waketime;
						
						Color blend = Hue.mix(Color.DARK_GRAY, DarkEnemy.eyeColor, 
								1f-data.parent.get(Data.class).damages[data.index]/100f);
						
						Draw.color(Color.BLACK, blend, wake);
						Draw.circle(spark.pos().x, spark.pos().y, 0.25f);
						Draw.color();
					});
				})
			);
		}
	}
	
	class TentacleData extends Trait{
		Spark parent;
		int index;
	}
}
