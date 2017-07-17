package io.anuke.home.entities.ecs.types.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.ecs.traits.EnemyTrait;
import io.anuke.home.entities.ecs.types.Enemy;
import io.anuke.home.entities.ecs.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.extend.traits.RenderableTrait;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;
import io.anuke.ucore.util.Tmp;

public class Tentawarrior extends Enemy{
	
	public Tentawarrior(){
		hiteffect = "blood";
		deatheffect = "purpleblood";

		maxhealth = 150;

		height = 11;
		hitoffset = 14;
		hitsize = 14;
		speed = 0.6f;
	}

	@Override
	public void move(Spark spark){
		EnemyTrait trait = spark.get(EnemyTrait.class);
		
		trait.moveToward(spark);

		if(Timers.get(this, "reload", reload)){

			for(int i = 0; i < 12; i++){
				Timers.run(Mathf.random(40), () -> {
					if(trait.target == null || spark.health().dead)
						return;
					Effects.effect("tentashoot", spark.pos().x, spark.pos().y + height + 3);
					shoot(spark, Projectiles.tentashot, spark.pos().x, spark.pos().y + height, spark.pos().angleTo(trait.target, 0, height) + Mathf.range(24));
				});
			}
		}
	}

	@Override
	public void draw(Spark spark, RenderableTrait trait){
		trait.draw(p -> {
			float x = spark.pos().x, y = spark.pos().y;
			Spark target = spark.get(EnemyTrait.class).target;
			Vector2 vector = Tmp.v1;
			
			float raise = 3f + MathUtils.sin(Timers.time() / 30f) * 3f;
			Draw.grect("tentawarriori" + (frames(10, 3)), x, y + raise);

			vector.set(0, 0);
			if(target != null){
				vector.set(target.pos().x - x, target.pos().y - (y + 11));
				vector.limit(0.8f);
			}

			Draw.color(Color.SCARLET, Color.PURPLE, target == null ? 0f : Timers.getTime(this, "reload") / reload);
			Draw.grect("tentawarrioreye", x + vector.x, y + raise + vector.y);

			if(target != null){
				Draw.alpha((Timers.getTime(this, "reload") - 15f) / reload);
				Draw.grect("tentawarrioreye2", x + vector.x, y + raise + vector.y);
			}

			Draw.reset();

			p.layer = y;
		});

		trait.shadow(spark, 14);
	}

}
