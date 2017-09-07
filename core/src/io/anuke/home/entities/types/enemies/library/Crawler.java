package io.anuke.home.entities.types.enemies.library;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Crawler extends DarkEnemy{

	@Override
	public void move(Spark spark){
		
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		
		trait.draw(Sorter.object, Sorter.dark, ()->{
			
			float wake = spark.get(EnemyTrait.class).rot/waketime;
			
			Draw.alpha(wake);
			Draw.tint(Color.DARK_GRAY);
			
			for(int i = 0; i <8; i ++){
				//Draw.thick(3f);
				//Draw.lineAngle(spark.pos().x, spark.pos().y, i*90 + 45, 8f);
				
				
				//Draw.lineAngle(spark.pos().x, spark.pos().y, i*90 + 45, 16f);
				
				//Draw.thick(1f);
				//Draw.lineAngle(spark.pos().x, spark.pos().y, i*90 + 45, 20f);
				
				float rot1 = i*45 + 45 +  Mathf.sin(Timers.time(), 16f, 8f) + Timers.time()*0.1f, 
				//+ ((i == 0 || i == 2) ? Mathf.sin(Timers.time(), 10f, 20f) : -Mathf.sin(Timers.time(), 10f, 20f)), 
					  rot2 = Mathf.sin(Timers.time() + i*4, 30f, 25f),
					  rot3 = Mathf.sin(Timers.time() + i*4, 15f, 15f);
				
				Draw.thick(3f);
				
				Angles.translation(rot1, 8f);
				
				float vx = Angles.vector.x, vy = Angles.vector.y;
				
				Draw.line(spark.pos().x, spark.pos().y, 
						spark.pos().x + vx, spark.pos().y + vy);
				
				Angles.translation(rot1 + rot2, 8f);
				
				Draw.thick(2f);
				
				float v2x = Angles.vector.x, v2y = Angles.vector.y;
				
				Draw.line(spark.pos().x + vx, spark.pos().y + vy, 
						spark.pos().x + v2x + vx, spark.pos().y + v2y + vy);
				
				Angles.translation(rot1 + rot2 + rot3, 5f);
				
				Draw.thick(1f);
				
				Draw.line(spark.pos().x + vx + v2x, spark.pos().y + vy + v2y, 
						spark.pos().x + v2x + vx + Angles.vector.x, spark.pos().y + v2y + vy + Angles.vector.y);
				
				//Draw.thick(2f);
				//Draw.lineAngle(spark.pos().x, spark.pos().y, i*90 + 45, 20f);
			}
			
			Draw.thick(1f);
			Draw.tint(Color.PURPLE);
			Draw.polygon(3, spark.pos().x, spark.pos().y, 4f, Timers.time()*1f);
			
			Draw.reset();
		});
	}

}
