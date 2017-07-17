package io.anuke.home.entities.ecs.traits;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Require;
import io.anuke.ucore.ecs.extend.traits.DrawTrait;
import io.anuke.ucore.ecs.extend.traits.HealthTrait;
import io.anuke.ucore.ecs.extend.traits.PosTrait;

/**Draws a health bar under the entity.*/
@Require({PosTrait.class, HealthTrait.class})
public class HealthBarTrait extends DrawTrait{
	public Color outline = new Color(0x3e2e50ff);
	public Color health = Color.SCARLET;

	public HealthBarTrait() {
		super(spark->{
			float x = spark.pos().x, y = spark.pos().y, hf = spark.get(HealthTrait.class).healthfrac();
			float offsety = -3f;
			float length = 9f;
			float height = 1f;
			float pad = 1f;
			
			Draw.color(spark.get(HealthBarTrait.class).outline);
			Draw.crect("blank", x-length/2f-pad, y+offsety-pad, length+pad*2f, height+pad*2f);
			
			Draw.color(Color.BLACK);
			Draw.crect("blank", x-length/2f, y+offsety, length, height);
			
			Draw.color(spark.get(HealthBarTrait.class).health);
			Draw.crect("blank", x-length/2f, y+offsety, length*hf, height);
			
			Draw.color();
		});
	}

}
