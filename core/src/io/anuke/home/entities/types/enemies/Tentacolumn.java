package io.anuke.home.entities.types.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.traits.RenderableTrait;
import io.anuke.ucore.util.*;

public class Tentacolumn extends Enemy{
	static Vector2[] offsets = {new Vector2(0, 0), new Vector2(-4, -7), new Vector2(4, -7)};
	
	public Tentacolumn(){
		hiteffect = "blood";
		deatheffect = "purpleblood";

		maxhealth = 450;

		height = 10;
		hitoffset = height;
		hitsize = 16;
	}

	@Override
	public void move(Spark spark){
		EnemyTrait trait = spark.get(EnemyTrait.class);
		Data data = spark.get(Data.class);
		
		if(Timers.get(this, "reload", reload)){
			Vector2 offset = offsets[data.eye];
			
			Effects.effect("tentashoot", spark.pos().x + offset.x, spark.pos().y + height + 3 + offset.y);
			shoot(spark, Projectiles.tentaball, spark.pos().x + offset.x, spark.pos().y + height + offset.y, spark.pos().angleTo(trait.target, offset.x, offset.y + height) + Mathf.range(3));
			
			data.eye ++;
			data.eye = data.eye%3;
		}
		
		if(Timers.get(this, "wave", 500)){
			Timers.runFor(30f, ()->{
				if(Timers.get(this, "swave", 9f)){
					Effects.effect("golemflash", spark);
					Geometry.circle(18, f->{
						shoot(spark, Projectiles.tentashot2, spark.pos().x, spark.pos().y+height, f);
					});
				}
			});
		}
	}

	@Override
	public void draw(Spark spark, RenderableTrait trait){
		trait.draw(p -> {
			float x = spark.pos().x, y = spark.pos().y;
			EnemyTrait enemy = spark.get(EnemyTrait.class);
			Data data = spark.get(Data.class);
			Vector2 vector = Tmp.v1;
			
			Draw.grect("tentacolumn", x, y - 2);
			
			float raise = Mathf.sin(Timers.time(), 10f, 0.9f);
			
			Draw.grect("tentacolumntop", x, y-3-raise/1.5f);
			Draw.grect("tentacolumnright", x, y-3+raise);
			Draw.grect("tentacolumnleft", x, y-3+raise);

			vector.set(0, 0);
			if(enemy.target != null){
				vector.set(enemy.target.pos().x - x, enemy.target.pos().y - (y + height));
				vector.limit(0.9f);
			}
			
			for(int i = 0; i < 3; i ++){
				Vector2 offset = offsets[i];
				
				//hahahaha
				if(data.eye == i){
					Draw.color(Color.SCARLET, Color.PURPLE, enemy.target == null ? 0 : Timers.getTime(this, "reload") / reload);
				}else{
					Draw.color("purple");
				}
				
				Draw.grect("tentacolumneye", x + vector.x + offset.x, y + vector.y + offset.y);
				
				if(data.eye == i){
					Draw.alpha(enemy.target == null ? 0 : (Timers.getTime(this, "reload") - 5f) / reload);
					Draw.grect("tentacolumneye2", x + vector.x + offset.x, y + vector.y + offset.y);
				}
			}

			Draw.reset();

			p.layer = y;
		});

		trait.shadow(spark, 32);
	}
	
	public Trait data(){
		return new Data();
	}
	
	static private class Data extends Trait{
		public int eye;
	}

}
