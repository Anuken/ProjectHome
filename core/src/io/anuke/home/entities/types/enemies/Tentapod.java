package io.anuke.home.entities.types.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.extend.traits.RenderableTrait;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;
import io.anuke.ucore.util.Tmp;

public class Tentapod extends Enemy{
	
	public Tentapod(){
		hiteffect = "blood";
		deatheffect = "purpleblood";
		
		maxhealth = 170;
		
		height = 4;
		hitoffset = height;
		hitsize = 10;
	}

	@Override
	public void move(Spark spark){
		EnemyTrait trait = spark.get(EnemyTrait.class);
		
		if(Timers.get(this, "reload", reload)){
			Effects.effect("tentashoot", spark.pos().x, spark.pos().y+height+3);
			shoot(spark, Projectiles.tentaball, spark.pos().x, spark.pos().y+height, spark.pos().angleTo(trait.target) + Mathf.range(3));
		}
	}

	@Override
	public void draw(Spark spark, RenderableTrait trait){
		trait.draw(p->{
			float x = spark.pos().x, y = spark.pos().y;
			Spark target = spark.get(EnemyTrait.class).target;
			Vector2 vector = Tmp.v1;
			
			Draw.grect("tentaguardbottom", x, y-2);
			Draw.grect("tentaguardi"+(frames(10, 4)), x, y);
			
			vector.set(0, 0);
			if(target != null){
				vector.set(target.pos().x-x, target.pos().y- (y+height));
				vector.limit(0.9f);
			}
			
			Draw.color(Color.SCARLET, Color.PURPLE, Timers.getTime(this, "reload")/reload);
			Draw.grect("tentaguardeye", x+vector.x, y+vector.y);
			Draw.alpha((Timers.getTime(this, "reload")-5f)/reload);
			Draw.grect("tentaguardeye2", x+vector.x, y+vector.y);
			
			Draw.reset();
			
			p.layer = y;
		});
		
		trait.shadow(spark, 18);
	}

}
