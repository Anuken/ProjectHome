package io.anuke.home.entities.enemies;

import io.anuke.home.entities.Enemy;
import io.anuke.home.entities.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Timers;

public class MarbleGolem extends Enemy{
	float armraise = 0;
	int smashes = 0;

	public MarbleGolem() {
		maxhealth = 200;
		heal();

		speed = 0.4f;

		height = 6;
		hitoffsety = height;

	}

	public void drawRenderables(){

		draw(p -> {
			Draw.grect("marblegolem-armless", x, y);

			Draw.grect("marblegolem-arms", x, y + armraise);

			p.layer = y;
		});

		drawShadow(13, 0);
	}

	public void move(){
		vector.set(target.x - x, target.y - y);
		vector.setLength(speed * delta);

		if(Timers.get(this, "smash", 100)){

			if(smashes < 5){

				float rdir = 30;
				Timers.runFor(rdir, () -> {
					armraise += 3f / rdir * delta;
				}, () -> {
					if(isDead())
						return;

					armraise = -2f;
					Effects.shake(2f, 4f, this);
					Effects.effect("purpleeyeflash", x, y + 8);
					Effects.effect("golemwave", this);

					Geometry.circle(4, ang -> {
						shoot(Projectiles.golemsplitshot, ang);
					});

					Timers.run(20f, () -> {
						armraise = 0f;
					});
				});
			}else{
				for(int i = 0; i < 3; i++){
					
					float rdir = 20;
					Timers.run(i * (rdir*2), () -> {
						
						Timers.runFor(rdir, () -> {
							armraise += 3f / rdir * delta;
						}, () -> {
							if(isDead())
								return;

							armraise = -2f;
							Effects.shake(2f, 4f, this);
							Effects.effect("purpleeyeflash", x, y + 8);
							Effects.effect("purpleeyeflash", x, y + 7);
							Effects.effect("purpleeyeflash", x, y + 9);
							Effects.effect("golemwave", this);

							Geometry.circle(4, ang -> {
								Geometry.shotgun(5, 9f, ang, out -> {
									shoot(Projectiles.golemwave, out);
								});
							});

							Timers.run(rdir, () -> {
								armraise = 0f;
							});
						});
					});

					smashes = 0;
				}
			}

			smashes++;
		}

		//move(vector.x, vector.y);
	}
}
