package io.anuke.home.entities.types.enemies.library;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.ecs.extend.traits.TileCollideTrait;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Crawler extends DarkEnemy{
	
	public Crawler(){
		speed = 0.14f;
	}

	@Override
	public void move(Spark spark){
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		
		enemy.moveToward(spark);
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		
		EnemyTrait enemy = spark.get(EnemyTrait.class);
		Data data = spark.get(Data.class);
		
		trait.draw(Sorter.object, Sorter.dark, ()->{
			
			float wake = spark.get(EnemyTrait.class).rot/waketime;
			
			Draw.color(Color.BLACK, Color.DARK_GRAY, wake);
			
			for(int i = 0; i <8; i ++){
				
				float rot1 = i*45 + 45 +  Mathf.sin(Timers.time(), 16f, 8f) + Timers.time()*0.1f, 
					  rot2 = Mathf.sin(Timers.time() + i*4, 30f, 25f),
					  rot3 = Mathf.sin(Timers.time() + i*4, 15f, 15f);
				
				float rots = rot1 + rot2 + rot3;
				
				float scale = 0.9f + Mathf.sin(Timers.time() - i*40, 30f, 0.3f);
				
				if(enemy.target != null){
					float angto = spark.pos().angleTo(enemy.target);
					float dst = Angles.angleDist(angto, rots);
					
					data.scales[i] = Mathf.lerp(data.scales[i], Mathf.clamp(1f-dst/60f), 0.04f*Mathf.delta());
				}else{
					data.scales[i] = Mathf.lerp(data.scales[i], 0f, 0.04f*Mathf.delta());
				}
				
				scale += data.scales[i];
				
				Draw.thick(3f);
				
				Angles.translation(rot1, 8f*wake*scale);
				
				float vx = Angles.vector.x, vy = Angles.vector.y;
				
				Draw.line(spark.pos().x, spark.pos().y, 
						spark.pos().x + vx, spark.pos().y + vy);
				
				Angles.translation(rot1 + rot2, 8f*wake*scale);
				
				Draw.thick(2f);
				
				float v2x = Angles.vector.x, v2y = Angles.vector.y;
				
				Draw.line(spark.pos().x + vx, spark.pos().y + vy, 
						spark.pos().x + v2x + vx, spark.pos().y + v2y + vy);
				
				Angles.translation(rot1 + rot2 + rot3, 5f*wake*scale);
				
				Draw.thick(1f);
				
				Draw.line(spark.pos().x + vx + v2x, spark.pos().y + vy + v2y, 
						spark.pos().x + v2x + vx + Angles.vector.x, spark.pos().y + v2y + vy + Angles.vector.y);
			}
			
			Draw.thick(1f);
			Draw.color(Color.BLACK, Color.PURPLE, wake);
			Draw.polygon(3, spark.pos().x, spark.pos().y, 4f, Timers.time()*1f);
			
			Draw.reset();
		});
	}
	
	@Override
	public Trait data(){
		return new Data();
	}
	
	@Override
	public TraitList traits(){
		TraitList list = super.traits();
		list.get(TileCollideTrait.class).trigger = true;
		return list;
	}
	
	static class Data extends Trait{
		public float[] scales = new float[8];
	}
}
