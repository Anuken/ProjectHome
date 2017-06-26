package io.anuke.home.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import io.anuke.home.Vars;
import io.anuke.home.entities.Player;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.scene.Element;
import io.anuke.ucore.util.Mathf;

public class HealthBar extends Element{
	private float frac = 0f;
	
	public HealthBar(){
		
	}
	
	public void draw(){
		Player entity = Vars.control.getPlayer();
		
		AtlasRegion region = (AtlasRegion)DrawContext.skin.getRegion("healthbar");
		frac = Mathf.lerp(frac, entity.healthfrac(), 0.4f*Mathf.delta());
		
		Draw.color(Color.DARK_GRAY);
		DrawContext.batch.draw(DrawContext.skin.getRegion("white"), x, y, width, height);
		
		float hit = entity.getHitTime();
		
		Draw.color(Color.RED, Color.ORANGE, hit);
		//region.setRegionWidth((int)(frac*region.getRotatedPackedWidth()));
		DrawContext.batch.draw(region, x, y, width*frac, height);
		
		Draw.color();
		
		Draw.tcolor(Hue.mix(Color.WHITE, Color.RED, hit));
		Draw.text(entity.health + "/" + entity.maxhealth, x+width/2, y+height-4);
		Draw.tcolor();
	}
}
