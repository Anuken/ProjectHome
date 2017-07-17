package io.anuke.home.entities.ecs.traits;

import com.badlogic.gdx.utils.Array;

import io.anuke.home.Vars;
import io.anuke.home.entities.ecs.traits.LootTrait.Drop;
import io.anuke.home.entities.ecs.types.*;
import io.anuke.home.world.Blocks;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.Trait;
import io.anuke.ucore.ecs.extend.Events.CollisionFilter;
import io.anuke.ucore.ecs.extend.Events.Damaged;
import io.anuke.ucore.ecs.extend.Events.Death;
import io.anuke.ucore.ecs.extend.traits.HealthTrait;
import io.anuke.ucore.ecs.extend.traits.ProjectileTrait;
import io.anuke.ucore.function.Consumer;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Tmp;

/**Base enemy trait. Not absolutely needed in an enemy, yet still convenient. Requires type to be an Enemy!*/
public class EnemyTrait extends Trait{
	static final float timeout = 600;
	
	public Spark target;

	public float idletime = 0f;
	
	public float rot = 0f;
	public float reload = 0f;
	
	public Consumer<Spark> mover;
	
	public EnemyTrait(Consumer<Spark> mover){
		this.mover = mover;
	}
	
	@Override
	public void registerEvents(Prototype type){
		type.traitEvent(Damaged.class, (spark, source, damage)->{
			Effects.effect(((Enemy)spark.getType()).hiteffect, source);
		});
		
		type.traitEvent(CollisionFilter.class, (spark, other)->
			other.getType() instanceof Projectile && other.get(ProjectileTrait.class).source.getType() instanceof Player
		);
		
		type.traitEvent(Death.class, spark->{
			Enemy enemy = (Enemy)spark.getType();
			//TODO addKill fix
			//Vars.control.addKill(this);
			for(int i = 0; i < 4; i++){
				Effects.effect("hit", spark.pos().x + Mathf.range(5), spark.pos().y + Mathf.range(5) + enemy.height);
			}

			Effects.effect(enemy.deatheffect, spark.pos().x, spark.pos().y + enemy.height);
			Effects.sound(enemy.deathsound, spark);

			spark.remove();
			
			dropStuff(spark);
		});
	}
	
	@Override
	public void update(Spark spark){
		retarget(spark);

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
	
	//TODO
	@SuppressWarnings("null")
	public void retarget(Spark spark){
		Enemy enemy = (Enemy)spark.getType();
		
		if(targetValid(spark)){
			idletime = 0;
			return;
		}else{
			target = null;
		}
		
		Spark player = null;//Vars.control.getPlayer();

		//TODO player var change
		float dst = spark.pos().dst(player.pos());

		//optimization
		if(dst < enemy.range && !player.get(HealthTrait.class).dead){
			target = player;
		}else{
			target = null;

			if(dst > 300 && enemy.despawn){
				idletime += Mathf.delta();

				if(idletime >= timeout){
					Tile tile = World.get(Mathf.scl(spark.pos().x, Vars.tilesize), Mathf.scl(spark.pos().y, Vars.tilesize));
					
					if(tile != null && tile.wall == Blocks.air){
						tile.data = this;
						tile.wall = Blocks.spawner;
						spark.health().heal();
						spark.remove();
						reset(spark);
					}else{
						idletime = 0f;
					}
				}
			}
		}

		//TODO uncomment this for multiplayer, if needed
		//target = (DestructibleEntity)Entities.getClosest(x, y, 100, e->{
		//	return e instanceof Player;
		//});
	}
	
	public void reset(Spark spark){
		spark.health().heal();
	}
	
	public void moveToward(Spark spark){
		Tmp.v1.set(target.pos().x - spark.pos().x, target.pos().y - spark.pos().y);
		Tmp.v1.setLength(((Enemy)spark.getType()).speed * Mathf.delta());
		//TODO actually move somewhere
		//move(Tmp.v1.x, Tmp.v1.y);
	}
	
	protected boolean targetValid(Spark spark){
		return target != null && !target.get(HealthTrait.class).dead && spark.pos().dst(target.pos()) < ((Enemy)spark.getType()).range;
	}
}
