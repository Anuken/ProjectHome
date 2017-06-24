package io.anuke.home.entities.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectiles;
import io.anuke.home.items.Items;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class Tentawarrior extends Enemy{
	float reload = 130;
	
	static{
		int c = 1;
		setDrops(Tentawarrior.class, 
			Items.densearmor, c,
			Items.hellarmor, c,
			Items.reflectarmor, c,
				
			Items.icesword, c,
			Items.daggersword, c,
			Items.silversword, c,
			Items.tentasword, c,
				
			Items.aetherstaff, c,
			Items.orbstaff, c,
			Items.scorchstaff, c
		);
		
		setDrops(Tentapod.class, Tentawarrior.class);
		setDrops(Tentafly.class, Tentawarrior.class);
	}

	public Tentawarrior() {
		hiteffect = "blood";
		deatheffect = "purpleblood";

		setMaxHealth(170);

		height = 11;
		hitoffsety = 14;
		hitsize = 14;
		speed = 0.6f;
	}
	
	public void drawRenderables(){
		draw(p -> {
			float raise = 3f + MathUtils.sin(Timers.time() / 30f) * 3f;
			Draw.grect("tentawarriori" + (frames(10, 3)), x, y + raise);

			vector.set(0, 0);
			if(target != null){
				vector.set(target.x - x, target.y - (y + 11));
				vector.limit(0.8f);
			}

			Draw.color(Color.SCARLET, Color.PURPLE, target == null ? 0f : Timers.getTime(this, "reload") / reload);
			Draw.grect("tentawarrioreye", x + vector.x, y + raise + vector.y);

			if(target != null){
				Draw.alpha((Timers.getTime(this, "reload") - 15f) / reload);
				Draw.grect("tentawarrioreye2", x + vector.x, y + raise + vector.y);
			}

			Draw.reset();

			p.layer = y;
		});

		shadow(14);
	}

	public void move(){
		moveToward();

		if(Timers.get(this, "reload", reload)){

			for(int i = 0; i < 12; i++){
				Timers.run(Mathf.random(40), () -> {
					if(target == null || isDead())
						return;
					Effects.effect("tentashoot", x, y + height + 3);
					shoot(Projectiles.tentashot, x, y + height, angleTo(target, height) + Mathf.range(24));
				});
			}
		}
	}
}
