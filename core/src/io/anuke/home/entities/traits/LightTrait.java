package io.anuke.home.entities.traits;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Renderer;
import io.anuke.home.effect.LightEffect;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.lights.Light;

public class LightTrait extends Trait{
	public Light light;
	public Vector2 offset = new Vector2();
	public float radius = 30;
	public Color color = Color.WHITE;
	
	public LightTrait(float radius){
		this.radius = radius;
	}
	
	public LightTrait(float radius, Color color){
		this.radius = radius;
		this.color = color;
	}
	
	@Override
	public void init(Spark spark){
		light = Renderer.getEffect(LightEffect.class).addLight(radius, color);
		light.setActive(false);
	}
	
	@Override
	public void update(Spark spark){
		light.setPosition(spark.pos().x + offset.x, spark.pos().y + offset.y);
	}
	
	@Override
	public void added(Spark spark){
		light.setActive(true);
	}
	
	@Override
	public void removed(Spark spark){
		light.setActive(false);
	}
}
