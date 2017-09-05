package io.anuke.home.entities.traits;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Require;
import io.anuke.ucore.ecs.extend.traits.DrawTrait;
import io.anuke.ucore.ecs.extend.traits.HealthTrait;
import io.anuke.ucore.ecs.extend.traits.PosTrait;

/**Draws a health bar under the entity.*/
@Require({PosTrait.class, HealthTrait.class})
public class HealthBarTrait extends DrawTrait{
	public Color outline = new Color(0x3e2e50ff);
	public Color health = Color.SCARLET.cpy();
	public Color empty = Color.BLACK.cpy();
	public Vector2 offset = new Vector2();

	public HealthBarTrait() {
		super(spark->{
			HealthBarTrait hb = spark.get(HealthBarTrait.class);
			
			drawBar(hb.health, hb.empty, hb.outline, 5f, spark.pos().x, spark.pos().y - 5f, spark.health().healthfrac());
		});
	}
	
	static void drawBar(Color color, Color empty, Color outline, float length, float x, float y, float fraction){
		float w = (int)(length * 2 * fraction) + 0.5f;
		
		x -= 0.5f;
		y += 0.5f;
		
		Draw.thickness(3f);
		Draw.color(outline);
		Draw.line(x - length + 1, y, x + length + 1.5f, y);
		Draw.thickness(1f);
		Draw.color(Color.BLACK);
		Draw.line(x - length + 1, y, x + length + 0.5f, y);
		Draw.color(color);
		if(w >= 1)
			Draw.line(x - length + 1, y, x - length+ w, y);
		Draw.reset();
	}

}
