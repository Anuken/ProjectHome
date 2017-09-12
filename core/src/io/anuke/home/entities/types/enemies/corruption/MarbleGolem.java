package io.anuke.home.entities.types.enemies.corruption;

import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Timers;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.util.*;

public class MarbleGolem extends Enemy{

	@Override
	public void move(Spark spark){
		Data data = spark.get(Data.class);
		
		if(Timers.get(this, "smash", 100)){

			if(data.smashes < 5){

				float rdir = 30;
				Timers.runFor(rdir, () -> {
					data.armraise += 3f / rdir * Timers.delta();
				}, () -> {
					if(spark.health().dead)
						return;

					data.armraise = -2f;
					Effects.shake(2f, 4f, spark);
					Effects.effect("purpleeyeflash", spark.pos().x, spark.pos().y + 8);
					Effects.effect("golemwave", spark);

					Angles.circle(4, ang -> {
						shoot(spark, Projectiles.golemsplitshot, ang);
					});

					Timers.run(20f, () -> {
						data.armraise = 0f;
					});
				});
			}else{
				for(int i = 0; i < 3; i++){
					
					float rdir = 20;
					Timers.run(i * (rdir*2), () -> {
						
						Timers.runFor(rdir, () -> {
							data.armraise += 3f / rdir * Timers.delta();
						}, () -> {
							if(spark.health().dead)
								return;

							data.armraise = -2f;
							Effects.shake(2f, 4f, spark);
							Effects.effect("purpleeyeflash", spark.pos().x, spark.pos().y + 8);
							Effects.effect("purpleeyeflash", spark.pos().x, spark.pos().y + 7);
							Effects.effect("purpleeyeflash", spark.pos().x, spark.pos().y + 9);
							Effects.effect("golemwave", spark);
							
							Angles.circle(4, ang -> {
								shoot(spark, Projectiles.golemsplitshot, ang+45);
							});

							Angles.circle(4, ang -> {
								Angles.shotgun(5, 9f, ang, out -> {
									shoot(spark, Projectiles.golemwave, out);
								});
							});

							Timers.run(rdir, () -> {
								data.armraise = 0f;
							});
						});
					});

					data.smashes = 0;
				}
			}

			data.smashes++;
		}
	}

	@Override
	public void draw(Spark spark, FacetTrait trait){
		trait.draw(p -> {
			Data data = spark.get(Data.class);
			
			Draw.grect("marblegolem-armless", spark.pos().x, spark.pos().y);

			Draw.grect("marblegolem-arms", spark.pos().x, spark.pos().y + data.armraise);

			p.layer = spark.pos().y;
		});

		trait.drawShadow(spark, 13, 0);
	}
	
	public Trait data(){
		return new Data();
	}
	
	private static class Data extends Trait{
		public float armraise;
		public int smashes;
	}
}
