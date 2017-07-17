package io.anuke.home.entities.ecs.types;

import io.anuke.home.entities.ecs.traits.EnemyTrait;
import io.anuke.home.entities.ecs.traits.HealthBarTrait;
import io.anuke.home.entities.ecs.traits.LootTrait;
import io.anuke.home.items.Items;
import io.anuke.ucore.ecs.*;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.ecs.extend.traits.ProjectileTrait.ProjectileType;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

/**Base enemy prototype. Extend to use.*/
public abstract class Enemy extends Prototype{
	public int maxhealth = 100;
	public float height = 8;
	public float speed = 0.5f;
	public float hitoffset = 12;
	public float hitsize = 8;
	public String hiteffect = "hit", 
			deatheffect = "death", 
			deathsound = "tentadie";

	public float range = 230, reload = 150f;
	public boolean despawn = true;
	
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
			new ColliderTrait(hitsize){{
				offsety = hitoffset;
			}},
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
}
