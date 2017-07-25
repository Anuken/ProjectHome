package io.anuke.home.entities.types;

import io.anuke.home.Vars;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.HealthBarTrait;
import io.anuke.home.entities.traits.LootTrait;
import io.anuke.home.items.Items;
import io.anuke.home.world.Blocks;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.*;
import io.anuke.ucore.ecs.extend.Events.CollisionFilter;
import io.anuke.ucore.ecs.extend.Events.Damaged;
import io.anuke.ucore.ecs.extend.Events.Death;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.ecs.extend.traits.ProjectileTrait.ProjectileType;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

/**Base enemy prototype. Extend to use.*/
public abstract class Enemy extends Prototype{
	public int maxhealth = 100;
	public float height = 8;
	public float speed = 0.5f;
	public float hitoffset = 0;
	public float hitsize = 10;
	public float timeout = 600;
	public String hiteffect = "hit", 
			deatheffect = "death", 
			deathsound = "tentadie";

	public float range = 230, reload = 150f;
	public boolean despawn = true;
	
	public Enemy(){
		event(Damaged.class, (spark, source, damage)->{
			Effects.effect(((Enemy)spark.getType()).hiteffect, source);
		});
		
		event(CollisionFilter.class, (spark, other)->
			other.getType() instanceof Projectile && !(other.get(ProjectileTrait.class).source.getType() instanceof Enemy)
		);
		
		event(Death.class, spark->{
			Vars.control.addKill(spark);
			for(int i = 0; i < 4; i++){
				Effects.effect("hit", spark.pos().x + Mathf.range(5), spark.pos().y + Mathf.range(5) + height);
			}
			
			Effects.effect(deatheffect, spark.pos().x, spark.pos().y + height);
			Effects.sound(deathsound, spark);
			
			spark.remove();
			
			spark.get(EnemyTrait.class).dropStuff(spark);
		});
	}
	
	@Override
	public TraitList typeTraits(){
		return new TraitList(
			new LootTrait(Items.aetherstaff, 0)
		);
	}
	
	@Override
	public TraitList traits(){
		Trait data = data();
		TraitList list = new TraitList(
			new PosTrait(),
			new TileCollideTrait(),
			new ColliderTrait(hitsize, hitsize, 0, hitsize/2 + hitoffset),
			new TileCollideTrait(1f, 2f, 5, 4),
			new HealthTrait(maxhealth),
			new HealthBarTrait(),
			new EnemyTrait(spark->{
				move(spark);
			}),
			new RenderableTrait((trait, spark)->{
				draw(spark, trait);
			})
		);
		if(data != null)
			list.with(data);
		
		return list;
	}
	
	public abstract void move(Spark spark);
	public abstract void draw(Spark spark, RenderableTrait trait);
	
	public void reset(Spark spark){
		spark.health().heal();
	}
	
	public void retarget(Spark spark){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		if(enemy.targetValid(spark)){
			enemy.idletime = 0;
			return;
		}else{
			enemy.target = null;
		}
		
		Spark player = Vars.control == null ? null : Vars.control.getPlayer();
		
		if(player == null) return;

		//TODO player var change
		float dst = spark.pos().dst(player.pos());

		//optimization
		if(dst < range && !player.get(HealthTrait.class).dead){
			enemy.target = player;
		}else{
			enemy.target = null;

			if(dst > 300 && despawn){
				enemy.idletime += Mathf.delta();

				if(enemy.idletime >= timeout){
					Tile tile = World.get(Mathf.scl(spark.pos().x, Vars.tilesize), Mathf.scl(spark.pos().y, Vars.tilesize));
					
					if(tile != null && tile.wall == Blocks.air){
						World.placeSpawner(tile.x, tile.y, spark.getType());
						spark.health().heal();
						spark.remove();
						((Enemy)spark.getType()).reset(spark);
					}else{
						enemy.idletime = 0f;
					}
				}
			}
		}
	}
	
	public Trait data(){
		return null;
	}
	
	public static int frames(float scale, int amount){
		return Mathf.scl(Timers.time(), scale)%amount+1;
	}
	
	public static void shoot(Spark spark, ProjectileType type, float angle){
		Projectile.create(type, spark, spark.pos().x, spark.pos().y, angle);
	}
	
	public static void shoot(Spark spark, ProjectileType type, float x, float y, float angle){
		Projectile.create(type, spark, x, y, angle);
	}
	
	public static void shoot(Spark spark, ProjectileType type, int damage, float x, float y, float angle){
		//TODO
		Projectile.create(type, spark, x, y, angle);
	}
}
