package io.anuke.home.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectiles;
import io.anuke.home.items.Items;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Tentacolumn extends Enemy{
	float reload = 35;
	int eye = 0;
	Vector2[] offsets = {new Vector2(0, 0), new Vector2(-4, -7), new Vector2(4, -7)};
	
	static{
		int c = 5;
		setDrops(Tentacolumn.class,
			Items.ascendarmor, c,
			Items.densearmor, c,
			Items.hellarmor, c,
			Items.juggarmor, c,
			Items.reflectarmor, c,
				
			Items.icesword, c, 
			Items.daggersword, c, 
			Items.phasesword, c, 
			Items.silversword, c,
				
			Items.aetherstaff, c,
			Items.fusionstaff, c,
			Items.orbstaff, c,
			Items.planestaff, c,
			Items.scorchstaff, c
		);
	}

	public Tentacolumn() {
		hiteffect = "blood";
		deatheffect = "purpleblood";

		setMaxHealth(320);

		height = 10;
		hitoffsety = height;
		hitsize = 16;
	}

	public void drawRenderables(){
		draw(p -> {
			Draw.grect("tentacolumn", x, y - 2);
			
			float raise = Mathf.sin(Timers.time(), 10f, 0.9f);
			
			Draw.grect("tentacolumntop", x, y-3-raise/1.5f);
			Draw.grect("tentacolumnright", x, y-3+raise);
			Draw.grect("tentacolumnleft", x, y-3+raise);

			vector.set(0, 0);
			if(target != null){
				vector.set(target.x - x, target.y - (y + height));
				vector.limit(0.9f);
			}
			
			for(int i = 0; i < 3; i ++){
				Vector2 offset = offsets[i];
				
				//hahahaha
				if(eye == i){
					Draw.color(Color.SCARLET, Color.PURPLE, Timers.getTime(this, "reload") / reload);
				}else{
					Draw.color("purple");
				}
				
				Draw.grect("tentacolumneye", x + vector.x + offset.x, y + vector.y + offset.y);
				
				if(eye == i){
					Draw.alpha((Timers.getTime(this, "reload") - 5f) / reload);
					Draw.grect("tentacolumneye2", x + vector.x + offset.x, y + vector.y + offset.y);
				}
			}

			Draw.reset();

			p.layer = y;
		});

		shadow(32);
	}

	public void move(){
		//moveToward();

		if(Timers.get(this, "reload", reload)){
			Vector2 offset = offsets[eye];
			
			Effects.effect("tentashoot", x + offset.x, y + height + 3 + offset.y);
			shoot(Projectiles.tentaball, x + offset.x, y + height + offset.y, angleTo(target, offset.x, offset.y + height) + Mathf.range(3));
			
			eye ++;
			eye = eye%3;
		}
		
		if(Timers.get(this, "wave", 500)){
			Timers.runFor(30f, ()->{
				if(Timers.get(this, "swave", 9f)){
					Effects.effect("golemflash", this);
					Geometry.circle(18, f->{
						shoot(Projectiles.tentashot2, x, y+height, f);
					});
				}
			});
		}
	}
}
