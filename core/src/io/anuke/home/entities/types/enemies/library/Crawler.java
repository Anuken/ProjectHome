package io.anuke.home.entities.types.enemies.library;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.Prototypes;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.HealthBarTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectile;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.*;
import io.anuke.ucore.ecs.extend.Events.*;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Crawler extends DarkEnemy{
	Tentacle tentacle = new Tentacle();
	
	public Crawler(){
		maxhealth = 500;
		speed = 0.16f;
		passthrough = true;
		height = 0f;
		
		event(Death.class, (spark)->{
			
			Timers.runFor(80, ()->{
				if(Timers.get("crawlerspark", 8)){
					Effects.effect("shadecloud", eyeColor, spark.pos().x + Mathf.range(8f), spark.pos().y + Mathf.range(8f));
				}
				Effects.shake(0.25f, 3f);
			}, ()->{
				callSuper(Death.class, spark);
				Effects.shake(3f, 4f);
			});
			
			spark.get(Data.class).dying = true;
			
			for(Spark t : spark.get(Data.class).tentacles){
				t.remove();
			}
		});
	}
	
	@Override
	public void init(Spark spark){
		Timers.reset(spark, "dash", Mathf.random(400));
		spark.get(Data.class).speed = speed;
		
		for(int i = 0; i < 8; i ++){
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
		Data data = spark.get(Data.class);
		
		//hack
		speed = data.speed;
		enemy.moveToward(spark);
		speed = 0.16f;
		
		if(Timers.get(spark, "reload", 500) && spark.health().healthfrac() < 0.7){
			for(int i = 0; i < 16; i ++){
				int id = i;
				Timers.run(5f*i, ()->{
					shoot(spark, Projectiles.darksplit, 0, enemy.target, 4f, Mathf.sin(id, 2.5f, 35f));
				});;
			}
			
		}
		
		if(Timers.get(spark, "dash", 400)){
			Timers.runFor(80, ()->{
				data.speed += 0.003f*Mathf.delta();
			}, ()->{
				Timers.runFor(80, ()->{
					data.speed -= 0.003f*Mathf.delta();
				});
			});
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
				spark.get(EnemyTrait.class).time -= Mathf.delta();
			}
			
			if(wake <= 0.001f){
				return;
			}
			
			Draw.color(Color.BLACK, Color.DARK_GRAY, wake);
			
			float offset = spark.getID() * 125f;
			
			for(int i = 0; i < 8; i ++){
				
				float shake = data.dying ? 1f : 0f;
				
				float rot1 = i*45 + 45 +  Mathf.sin(Timers.time() + offset, 16f, 8f) + Timers.time()*0.1f + offset 
					  + Mathf.sin(Timers.time() + offset + i*3, 6f, 8f)*shake, 
					  rot2 = Mathf.sin(Timers.time() + offset + i*4, 30f, 25f) 
					  + Mathf.sin(Timers.time() + offset + i*4, 6f, 8f)*shake,
					  rot3 = Mathf.sin(Timers.time() + offset + i*4, 15f, 15f) 
					  + Mathf.sin(Timers.time() + offset + i*5, 6f, 8f)*shake;
				
				float rots = rot1 + rot2 + rot3;
				
				float scale = 0.9f + Mathf.sin(Timers.time() - i*40, 30f, 0.3f);
				
				if(data.dying){
					data.scales[i] = Mathf.lerp(data.scales[i], -1.3f, 0.034f*Mathf.delta());
				}else if(data.damages[i] > 0){
					data.damages[i] -= Mathf.delta();
					
					data.scales[i] = Mathf.lerp(data.scales[i], -0.3f, 0.06f*Mathf.delta());
				}else if(enemy.target != null){
					float angto = spark.pos().angleTo(enemy.target);
					float dst = Angles.angleDist(angto, rots);
					
					data.scales[i] = Mathf.lerp(data.scales[i], Mathf.clamp(1f-dst/60f), 0.04f*Mathf.delta());
				}else{
					data.scales[i] = Mathf.lerp(data.scales[i], 0f, 0.04f*Mathf.delta());
				}
				
				scale += data.scales[i];
				
				
				scale = Mathf.clamp(scale, 0, 10f);
				
				if(scale <= 0.1f) continue;
				
				Draw.thick(3f);
				
				Angles.translation(rot1, 8f*wake*scale);
				
				float vx = Angles.vector.x, vy = Angles.vector.y;
				
				Draw.line(spark.pos().x, spark.pos().y, 
						spark.pos().x + vx, spark.pos().y + vy);
				
				Angles.translation(rot1 + rot2, 8f*wake*scale);
				
				Draw.thick(2f);
				
				float v2x = Angles.vector.x, v2y = Angles.vector.y;
				
				Draw.line(spark.pos().x + vx, spark.pos().y + vy, 
						spark.pos().x + v2x + vx, spark.pos().y + v2y + vy);
				
				Angles.translation(rot1 + rot2 + rot3, 5f*wake*scale);
				
				Draw.thick(1f);
				
				Draw.line(spark.pos().x + vx + v2x, spark.pos().y + vy + v2y, 
						spark.pos().x + v2x + vx + Angles.x(), spark.pos().y + v2y + vy + Angles.y());
				
				data.tentacles[i].pos().set(spark.pos().x + v2x + vx + Angles.x(), spark.pos().y + v2y + vy + Angles.y());
			}
			
			Draw.thick(1f);
			Draw.color(Color.BLACK, DarkEnemy.eyeColor, wake);
			Draw.polygon(3, spark.pos().x, spark.pos().y, 4f, Timers.time()*1f + offset);
			
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
		return list.exclude(HealthBarTrait.class);
	}
	
	static class Data extends Trait{
		float[] scales = new float[8];
		float[] damages = new float[8];
		Spark[] tentacles = new Spark[8];
		float speed = 0.16f;
		boolean dying = false;
	}
	
	class Tentacle extends Prototype{
		
		public Tentacle(){
			super(true);
			
			event(CollisionFilter.class, (spark, other)->
				other.getType() instanceof Projectile && !(other.get(ProjectileTrait.class).source.getType() instanceof Enemy)
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
				//new ContactDamageTrait(3),
				new FacetTrait((trait, spark)->{
					
					trait.draw(Sorter.object, Sorter.dark - 1f, ()->{
						float wake = spark.get(TentacleData.class).parent.get(EnemyTrait.class).time/waketime;
						
						Draw.color(Color.BLACK, DarkEnemy.eyeColor, wake);
						//Draw.circle(spark.pos().x, spark.pos().y, 2f);
						//Draw.square(spark.pos().x, spark.pos().y, 4f);
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
