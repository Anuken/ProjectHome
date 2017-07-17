package io.anuke.home.entities.ecs.types.enemies;

import io.anuke.home.entities.ecs.traits.EnemyTrait;
import io.anuke.home.entities.ecs.types.Enemy;
import io.anuke.home.entities.ecs.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.traits.RenderableTrait;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class MarbleGolem extends Enemy{

	@Override
	public void move(Spark spark){
		Data data = spark.get(Data.class);
		
		if(Timers.get(this, "smash", 100)){

			if(data.smashes < 5){

				float rdir = 30;
				Timers.runFor(rdir, () -> {
					data.armraise += 3f / rdir * Mathf.delta();
				}, () -> {
					if(spark.health().dead)
						return;

					data.armraise = -2f;
					Effects.shake(2f, 4f, spark);
					Effects.effect("purpleeyeflash", spark.pos().x, spark.pos().y + 8);
					Effects.effect("golemwave", spark);

					Geometry.circle(4, ang -> {
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
							data.armraise += 3f / rdir * Mathf.delta();
						}, () -> {
							if(spark.health().dead)
								return;

							data.armraise = -2f;
							Effects.shake(2f, 4f, spark);
							Effects.effect("purpleeyeflash", spark.pos().x, spark.pos().y + 8);
							Effects.effect("purpleeyeflash", spark.pos().x, spark.pos().y + 7);
							Effects.effect("purpleeyeflash", spark.pos().x, spark.pos().y + 9);
							Effects.effect("golemwave", spark);
							
							Geometry.circle(4, ang -> {
								shoot(spark, Projectiles.golemsplitshot, ang+45);
							});

							Geometry.circle(4, ang -> {
								Geometry.shotgun(5, 9f, ang, out -> {
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
	public void draw(Spark spark, RenderableTrait trait){
		trait.draw(p -> {
			Draw.grect("marblegolem-armless", spark.pos().x, spark.pos().y);

			Draw.grect("marblegolem-arms", spark.pos().x, spark.pos().y + spark.get(EnemyTrait.class).rot);

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
