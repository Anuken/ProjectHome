package io.anuke.home.entities.types.enemies.library;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.Prototypes;
import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.Events.Death;
import io.anuke.ucore.ecs.extend.traits.ColliderTrait;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

public class DarkBlob extends DarkEnemy{

	public DarkBlob() {
		speed = 0.2f;
		
		range = 140f;
		
		deathsound = "blobdie";
		
		event(Death.class, spark->{
			callSuper(Death.class, spark);
			
			Data data = spark.get(Data.class);
			
			for(int i = 0; i < data.size/4f; i ++){
				Effects.effect("shadecloud", eyeColor,  spark.pos().x + Mathf.range(data.size/2f), spark.pos().y + Mathf.range(data.size/2f));
			}
			
			if(data.size > 5)
				split(spark);
		});
	}
	
	public void split(Spark spark){
		Data data = spark.get(Data.class);
		
		for(int i = 0; i < 2; i ++){
			Spark split = new Spark(Prototypes.darkblob);
			split.get(Data.class).size = data.size/2f;
			split.get(EnemyTrait.class).time = waketime;
			split.pos().set(spark.pos().x + Mathf.range(data.size/3f), spark.pos().y + Mathf.range(data.size/3f));
			split.add();
		}
		
		spark.remove();
	}
	
	@Override
	public void init(Spark spark){
		Timers.reset(spark, "split", Mathf.random(300f));
		
		spark.get(ColliderTrait.class).setSize(spark.get(Data.class).size/1.2f);
	}

	@Override
	public void move(Spark spark){
		EnemyTrait trait = spark.get(EnemyTrait.class);
		Data data = spark.get(Data.class);
		
		float wake = trait.time/waketime;

		//TODO
		trait.rot += Mathf.range(50f) * Timers.delta();
		Tmp.v1.set(speed * Timers.delta(), 0).rotate(trait.rot);

		//spark.get(TileCollideTrait.class).move(spark, Tmp.v1.x, Tmp.v1.y);
		
		if(Timers.get(spark, "shotgun", 60) && wake > 0.9f){
			Angles.circle(4, f->{
				Angles.shotgun(Math.max((int)(data.size/5f), 1), 4f, f, o->{
					float ang = o + Timers.time()/2f + spark.getID()*99512;
					Angles.translation(ang, data.size/5f);
					shoot(spark, Projectiles.darkorb2, spark.pos().x + Angles.x(), spark.pos().y + Angles.y(), ang);
				});
			});
		}
		
		if(data.phase == 0){
			if(Timers.get(spark, "reload1", 200)){
				Timers.runFor(60f, () -> {
					data.targetgrow -= Timers.delta() * 0.3f;
				}, () -> {
					data.targetgrow = 16f;

					Angles.circle(4, f -> {
						shoot(spark, Projectiles.darkorb, f);
					});

					Timers.run(20, () -> {
						data.targetgrow = 0f;
					});
				});
			}
		}else if(data.phase == 1){
			if(Timers.get(spark, "reload1", 5)){
				float angle = spark.pos().angleTo(spark.get(EnemyTrait.class).target);

				float offset = Mathf.sin(Timers.time(), 15f, 80f);

				shoot(spark, Projectiles.darkorb2, spark.pos().x, spark.pos().y, angle + offset);
				shoot(spark, Projectiles.darkorb2, spark.pos().x, spark.pos().y, angle - offset);

				offset += 180f;

				shoot(spark, Projectiles.darkorb2, spark.pos().x, spark.pos().y, angle + offset);
				shoot(spark, Projectiles.darkorb2, spark.pos().x, spark.pos().y, angle - offset);

			}
		}else if(data.phase == 2){
			if(Timers.get(spark, "reload1", 10)){
				float angle = spark.pos().angleTo(spark.get(EnemyTrait.class).target);

				float offset = Mathf.sin(Timers.time(), 100f, 360f);
				shoot(spark, Projectiles.darkshot, spark.pos().x, spark.pos().y, angle + offset);

				shoot(spark, Projectiles.darkshot, spark.pos().x, spark.pos().y, angle + offset + 180f);

			}
		}else if(data.phase == 3){
			float angle = spark.pos().angleTo(spark.get(EnemyTrait.class).target);
			float sin = Mathf.sin(Timers.time(), 17f, 90f);

			if(Timers.time() % 200 < 50 && Timers.get(spark, "reload4", 17)){
				shoot(spark, Projectiles.darkorb, spark.pos().x, spark.pos().y, angle + sin);
				shoot(spark, Projectiles.darkorb3, spark.pos().x, spark.pos().y, angle - sin);
			}

		}

	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		Data data = spark.get(Data.class);

		trait.draw(Sorter.object, Sorter.dark, () -> {
			
			float wake = spark.get(EnemyTrait.class).time/waketime;

			float offset = spark.getID() * 59f;

			data.grow = Mathf.lerp(data.grow, data.targetgrow, 0.05f * Timers.delta());

			float rad = data.size + Mathf.sin(Timers.time() + offset, 25f, 3f) + data.grow;
			
			rad *= wake;

			Draw.color(Color.BLACK, Color.DARK_GRAY, wake);

			Draw.rect("circle", spark.pos().x, spark.pos().y, rad, rad);

			int blobs = 10;

			for(int i = 0; i < blobs; i++){
				float rotation = i * 100;
				float radius = 3 + (Mathf.randomSeed(spark.getID() * 3 + i)/2f + 0.5f) * (data.size/3f) + Mathf.sin(Timers.time() + offset + i * 77, 20f, 2f);
				float len = rad / 2.3f + Mathf.sin(Timers.time() + i * 30f, 30f + Mathf.randomSeed(spark.getID() * 46 + i), 1f);
				
				radius *= wake;
				
				Angles.translation(rotation + Timers.time()/2f + spark.getID()*999, len);
				
				Draw.rect("circle", spark.pos().x + Angles.x(), spark.pos().y + Angles.y(), radius, radius);
			}

			Draw.color(Color.BLACK, DarkEnemy.eyeColor, wake);
			Draw.polygon(3, spark.pos().x, spark.pos().y, 4f, Timers.time());

			Draw.reset();
		});
	}

	@Override
	public Trait data(){
		return new Data();
	}

	static class Data extends Trait{
		float grow, targetgrow;
		float size = 36f;
		int phase = -1;
	}

}
