package io.anuke.home.entities.types;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.Vars;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.HealthBarTrait;
import io.anuke.home.entities.traits.LootTrait;
import io.anuke.home.items.Items;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.home.world.blocks.Blocks;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.*;
import io.anuke.ucore.ecs.extend.Events.CollisionFilter;
import io.anuke.ucore.ecs.extend.Events.Damaged;
import io.anuke.ucore.ecs.extend.Events.Death;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

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
	public Color effectColor = Color.WHITE.cpy();
	
	/**The number to multiply the range by to get the range at which the player is un-targeted.*/
	public float untargetMultiplier = 1.4f;
	public float range = 230, reload = 150f;
	public boolean despawn = true;
	public boolean passthrough = false;
	
	public Enemy(){
		event(Damaged.class, (spark, source, damage)->{
			Effects.effect(hiteffect, effectColor, source.pos().x, source.pos().y);
		});
		
		event(CollisionFilter.class, (spark, other)->
			other.getType() instanceof Projectile && !(other.get(ProjectileTrait.class).source.getType() instanceof Enemy)
		);
		
		event(Death.class, spark->{
			Vars.control.addKill(spark);
			for(int i = 0; i < 4; i++){
				Effects.effect(hiteffect, effectColor, spark.pos().x + Mathf.range(5), spark.pos().y + Mathf.range(5) + height);
			}
			
			Effects.effect(deatheffect, effectColor, spark.pos().x, spark.pos().y + height);
			Effects.sound(deathsound, spark);
			
			spark.remove();
			
			spark.get(EnemyTrait.class).dropStuff(spark);
		});
	}
	
	public void init(Spark spark){
		
	}
	
	@Override
	public TraitList typeTraits(){
		//TODO is this bad design?
		return new TraitList(
			new LootTrait(Items.aetherstaff, 0)
		);
	}
	
	@Override
	public TraitList traits(){
		Trait data = data();
		TraitList list = new TraitList(
			new PosTrait(),
			new ColliderTrait(hitsize, hitsize, 0, hitsize/2 + hitoffset),
			new TileCollideTrait(1f, 2f, 5, 4, passthrough),
			new HealthTrait(maxhealth),
			new HealthBarTrait(),
			new EnemyTrait(spark->{
				move(spark);
			}),
			new FacetTrait((trait, spark)->{
				draw(spark, trait);
			})
		);
		if(data != null)
			list.with(data);
		
		return list;
	}
	
	public abstract void move(Spark spark);
	public abstract void draw(Spark spark, FacetTrait trait);
	
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
				enemy.idletime += Timers.delta();

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
	
	public static void shoot(Spark spark, Projectiles type, float offsety, Spark target, float translation, float angoffset){
		float angle = Angles.angle(spark.pos().x, spark.pos().y + offsety, target.pos().x, target.pos().y + 3) + angoffset;
		Angles.translation(angle, translation);
		Projectile.create(type, spark, spark.pos().x + Angles.vector.x, spark.pos().y + offsety + Angles.vector.y, angle);
	}
	
	public static void shoot(Spark spark, Projectiles type, float angle){
		Projectile.create(type, spark, spark.pos().x, spark.pos().y, angle);
	}
	
	public static void shoot(Spark spark, Projectiles type, float x, float y, float angle){
		Projectile.create(type, spark, x, y, angle);
	}
	
	public static void shoot(Spark spark, Projectiles type, int damage, float x, float y, float angle){
		//TODO
		Projectile.create(type, spark, x, y, angle);
	}
}
