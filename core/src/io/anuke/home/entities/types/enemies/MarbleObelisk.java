package io.anuke.home.entities.types.enemies;

import io.anuke.home.entities.traits.EnemyTrait;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.Projectiles;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.extend.traits.RenderableTrait;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Timers;

public class MarbleObelisk extends Enemy{
	
	public MarbleObelisk(){
		height = 4;
		maxhealth = 260;
		deathsound = "blockdie";
	}

	@Override
	public void move(Spark spark){
		float x = spark.pos().x, y = spark.pos().y;
		EnemyTrait trait = spark.get(EnemyTrait.class);
		
		if(Timers.get(spark, "circle", 18)){
			Effects.effect("golemwave", spark);
			shoot(spark, Projectiles.golemwave, x, y+height, trait.rot);
			shoot(spark, Projectiles.golemwave, x, y+height, trait.rot+180);
			
			trait.rot += 20f;
		}
		
		if(Timers.get(spark, "bigshot", 240)){
			Effects.effect("golemwave", spark);
			Effects.shake(3, 4f);
			
			for(int i = 0; i < 4; i ++)
				Effects.effect("purpleeyeflash", x, y+i+6);
			
			Geometry.circle(8, f->{
				shoot(spark, Projectiles.golemsplitshot, x, y+height, f);
			});
		}
	}

	@Override
	public void draw(Spark spark, RenderableTrait trait){
		trait.draw(p->{
			Draw.grect("marbleobelisk", spark.pos().x, spark.pos().y);
			p.layer = spark.pos().y;
		});
		
		trait.shadow(spark, 18);
	}

}
