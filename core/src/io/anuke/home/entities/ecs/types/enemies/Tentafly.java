package io.anuke.home.entities.ecs.types.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.ecs.traits.EnemyTrait;
import io.anuke.home.entities.ecs.types.Enemy;
import io.anuke.home.entities.ecs.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.extend.traits.RenderableTrait;
import io.anuke.ucore.util.*;

public class Tentafly extends Enemy{
	
	public Tentafly(){
		maxhealth = 100;
		hiteffect = "blood";
		deatheffect = "purpleblood";
		
		height = 8;
		speed = 1f;
		hitoffset = 12;
		reload = 135;
	}

	@Override
	public void move(Spark spark){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		enemy.moveToward(spark);
		
		if(Timers.get(spark, "reload", reload)){
			Effects.effect("tentashoot", spark.pos().x, spark.pos().y + height);
			Geometry.shotgun(7, 9f, spark.pos().angleTo(enemy.target), f->{
				shoot(spark, Projectiles.tentashot, spark.pos().x, spark.pos().y+8, f);
			});
			
			Geometry.circle(8, f->{
			//	shoot(Projectiles.tentashot, x, y+8, f);
			});
		}
	}

	@Override
	public void draw(Spark spark, RenderableTrait trait){
		trait.draw(p->{
			EnemyTrait enemy = spark.get(EnemyTrait.class);
			float x = spark.pos().x, y = spark.pos().y;
			Spark target = enemy.target;
			Vector2 vector = Tmp.v1;
			
			float sin = Mathf.sin(Timers.time(), 4, 0.75f)+0.75f;
			float raise = 6f+Mathf.sin(Timers.time(), 12f, 2f);
			
			Draw.grect("tentafly", x, y+raise);
			Draw.grect("tentaflywingsbot", x, y-sin+raise);
			Draw.grect("tentaflywingstop", x, y+sin+raise);
			
			vector.set(0, 0);
			if(target != null){
				vector.set(target.pos().x - x, target.pos().y - (y+height));
				vector.limit(0.9f);
			}
			
			Draw.color(Color.SCARLET, Color.PURPLE, target == null ? 0f : Timers.getTime(spark, "reload") / reload);
			Draw.grect("tentaflyeye", x+vector.x, y+vector.y+raise);
			if(target != null){
				Draw.alpha((Timers.getTime(spark, "reload") - 15f) / enemy.reload);
				Draw.grect("tentaflyeye2", x + vector.x, y + raise + vector.y);
			}
			Draw.color();
			p.layer = y;
		});
		
		trait.shadow(spark, 14);
	}

}
