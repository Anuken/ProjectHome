package io.anuke.home.entities.types.enemies.library;

import io.anuke.home.Renderer;
import io.anuke.home.effect.LightEffect;
import io.anuke.home.effect.Shaders;
import io.anuke.home.entities.traits.HealthBarTrait;
import io.anuke.home.entities.traits.LightTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Wisp extends Enemy{
	
	@Override
	public void init(Spark spark){
		Renderer.getEffect(LightEffect.class).addLight(4f);
	}
	
	@Override
	public void move(Spark spark){
		
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		trait.draw(spark, 12, ()->{
			
			float radius = 4 + Mathf.absin(Timers.time(), 10f, 4f);
			float raise = Mathf.sin(Timers.time(), 20f, 3f) + 10;
			
			spark.get(LightTrait.class).offset.y = raise;
			
			Draw.getShader(Shaders.Wisp.class).set(spark.pos().x, spark.pos().y + raise);
			Draw.beginShaders(Shaders.Wisp.class);
			
			Draw.rect("circle", spark.pos().x, spark.pos().y + raise, radius, radius);
			
			Draw.thick(2f);
			Draw.spikes(spark.pos().x, spark.pos().y + raise, 6f, 2f, 8, -Timers.time());
			Draw.spikes(spark.pos().x, spark.pos().y + raise, 10f, 1f, 4, Timers.time());
			
			Draw.reset();
			
			Draw.endShaders();
		});
	}
	
	@Override
	public TraitList traits(){
		TraitList list = super.traits();
		list.with(new LightTrait(44));
		
		HealthBarTrait bar = list.get(HealthBarTrait.class);
		bar.outline.a = 0.4f;
		bar.empty.a = 0.4f;
		bar.health.set(1f, 1f, 1f, 1f);
		
		return list;
	}
}
