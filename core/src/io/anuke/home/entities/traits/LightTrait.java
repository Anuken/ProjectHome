package io.anuke.home.entities.traits;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Renderer;
import io.anuke.home.effect.LightEffect;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.lights.Light;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class LightTrait extends Trait{
	public boolean enabled = true;
	public boolean small = false;
	public boolean instant = false;
	public Light light;
	public Vector2 offset = new Vector2();
	public float radius = 30;
	public Color color = Color.WHITE;
	
	private float shrink = 0f;
	
	public LightTrait(float radius, boolean instant){
		this.radius = radius;
		this.instant = true;
	}
	
	public LightTrait(float radius){
		this.radius = radius;
	}
	
	public LightTrait(float radius, Color color){
		this.radius = radius;
		this.color = color;
	}
	
	@Override
	public void init(Spark spark){
		
	}
	
	@Override
	public void update(Spark spark){
		if(enabled){
			light.setPosition(spark.pos().x + offset.x, spark.pos().y + offset.y);
		}
	}
	
	@Override
	public void added(Spark spark){
		if(enabled && light == null){
			light = small ? Renderer.getEffect(LightEffect.class).addSmallLight(radius) 
					: Renderer.getEffect(LightEffect.class).addLight(radius, color);
			light.setActive(false);
		}
		
		shrink = 0f;
		if(enabled){
			light.setActive(true);
		}
	}
	
	@Override
	public void removed(Spark spark){
		if(enabled && light != null){
			if(!instant){
				Timers.runFor(30f, ()->{
					shrink += Mathf.delta();
					light.setColor(Hue.lightness(1f-shrink/30f));
				}, ()->{
					light.setActive(false);
				});
			}else{
				light.setActive(false);
			}
		}
	}
}
