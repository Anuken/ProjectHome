package io.anuke.home.entities.types.enemies.library;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.traits.LightTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.ecs.extend.traits.TileCollideTrait;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class GoldFrog extends Enemy{
	
	public GoldFrog(){
		speed = 0.04f;
	}
	
	@Override
	public void init(Spark spark){
		Timers.reset(spark, "jump", Mathf.random(100));
	}

	@Override
	public void move(Spark spark){
		EnemyTrait trait = spark.get(EnemyTrait.class);
		
		if(Timers.get(spark, "jump", 100)){
			trait.time = 12f;
			trait.rot = Mathf.random(360f);
		}
		
		if(trait.time > 0){
			trait.time -= Mathf.delta();
			Angles.translation(trait.rot, speed);
			spark.get(TileCollideTrait.class).move(spark, Angles.vector.x, Angles.vector.y);
		}
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		trait.draw(spark, 10, ()->{
			//Draw.color(Hue.fromHSB((Timers.time()/30f) % 1f, 1f, 1f));
			boolean flip = enemy.rot > 90 && enemy.rot < 270 ? true : false;
			Draw.grect(enemy.time > 0 ? "frogjump" : "frog", spark.pos().x, spark.pos().y-1, flip);
			Draw.color();
		});
	}
	
	@Override
	public TraitList traits(){
		return super.traits().with(new LightTrait(20).setSmall().setOffset(0, 3));
	}
}
