package io.anuke.home.entities.traits;

import com.badlogic.gdx.utils.Array;

import io.anuke.home.entities.traits.LootTrait.Drop;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.entities.types.ItemDrop;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.traits.HealthTrait;
import io.anuke.ucore.ecs.extend.traits.TileCollideTrait;
import io.anuke.ucore.function.Consumer;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

/**Base enemy trait. Not absolutely needed in an enemy, yet still convenient. Requires type to be an Enemy!*/
public class EnemyTrait extends Trait{
	
	public Spark target;

	public float idletime = 0f;
	
	public float rot = 0f;
	public float time = 0f;
	public float reload = 0f;
	
	public Consumer<Spark> mover;
	
	public EnemyTrait(Consumer<Spark> mover){
		this.mover = mover;
	}
	
	@Override
	public void registerEvents(Prototype type){
		
	}
	
	@Override
	public void update(Spark spark){
		((Enemy)spark.getType()).retarget(spark);

		if(targetValid(spark)){
			mover.accept(spark);
		}
	}
	
	public void dropStuff(Spark spark){
		Array<Drop> drops = spark.getType().getTypeTrait(LootTrait.class).drops;

		for(Drop drop : drops){
			if(Mathf.chance(drop.chance)){
				ItemDrop.create(drop.stack, spark.pos().x, spark.pos().y);
			}
		}
	}
	
	public void moveToward(Spark spark){
		Tmp.v1.set(target.pos().x - spark.pos().x, target.pos().y - spark.pos().y);
		Tmp.v1.setLength(((Enemy)spark.getType()).speed * Mathf.delta());
		spark.get(TileCollideTrait.class).move(spark, Tmp.v1.x, Tmp.v1.y);
	}
	
	public void moveTowardDeltaless(Spark spark){
		Tmp.v1.set(target.pos().x - spark.pos().x, target.pos().y - spark.pos().y);
		Tmp.v1.setLength(((Enemy)spark.getType()).speed);
		spark.get(TileCollideTrait.class).move(spark, Tmp.v1.x, Tmp.v1.y);
	}
	
	public boolean targetValid(Spark spark){
		return target != null && !target.get(HealthTrait.class).dead 
				&& spark.pos().dst(target.pos()) < ((Enemy)spark.getType()).range;
	}
}
