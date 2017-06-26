package io.anuke.home.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.anuke.home.entities.Enemy;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.util.Mathf;

public class BossHealthBar extends Element{
	private Enemy entity;
	private float frac;
	private String name = "Dark Effigy";
	
	public void setEntity(Enemy e){
		this.entity = e;
	}
	
	public void draw(){
		if(entity == null){
			frac = 0f;
			return;
		}
		
		TextureRegion region = DrawContext.skin.getRegion("healthbar");
		frac = Mathf.lerp(frac, entity.healthfrac(), 0.3f*Mathf.delta());
		
		Draw.color(Color.DARK_GRAY);
		DrawContext.batch.draw(DrawContext.skin.getRegion("white"), x, y, width, height);
		
		Draw.color(Color.PURPLE);
		DrawContext.batch.draw(region, x, y, width*frac, height);
		
		Draw.tcolor(Color.DARK_GRAY);
		Draw.text(name, x+width/2, y+height-6);
		Draw.tcolor(Color.WHITE);
		Draw.text(name, x+width/2, y+height-4);
		
		Draw.color();
	}
}
