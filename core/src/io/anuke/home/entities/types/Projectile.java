package io.anuke.home.entities.types;

import io.anuke.home.Renderer;
import io.anuke.home.effect.LightEffect;
import io.anuke.home.entities.Prototypes;
import io.anuke.home.entities.traits.LightTrait;
import io.anuke.home.entities.traits.ParticleTrait;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.Events.CollisionFilter;
import io.anuke.ucore.ecs.extend.Events.TileCollision;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.facet.Sorter;

public class Projectile extends Prototype{
	
	public Projectile(){
		event(TileCollision.class, (spark, x, y)->{
			Tile tile = World.get(x, y);
			if(tile.wall.destructible){
				
				if(spark.get(ContactDamageTrait.class).damage < tile.wall.destoyDamage){
					if(tile.wall.hitParticle != null){
						Effects.effect(tile.wall.hitParticle, spark.pos().x, spark.pos().y);
					}
				}else{
				
					if(tile.wall.destroyParticle != null){
						Effects.effect(tile.wall.destroyParticle, tile.worldx(), tile.worldy());
					}
					
					tile.wall = tile.wall.destroyBlock;
					Renderer.updateWall(tile.x, tile.y);
					Renderer.getEffect(LightEffect.class).updateRects();
				}
			}
			
			spark.get(ProjectileTrait.class).type.removed(spark);
			spark.remove();
		});
		
		event(CollisionFilter.class, (spark, other)->{
			return other.getType() != Prototypes.projectile;
		});
	}
	
	@Override
	public void update(Spark spark){
		spark.get(ProjectileTrait.class).type.update(spark);
	}

	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(),
			new VelocityTrait(),
			new ContactDamageTrait(),
			new ColliderTrait(),
			new LifetimeTrait(),
			new ProjectileTrait(),
			new LightTrait(30),
			new ParticleTrait(10),
			new TileCollideTrait(0, 0, 2, 2),
			new FacetTrait((trait, spark)->{
				if(((Projectiles)spark.projectile().type).dark){
					trait.draw(Sorter.object, Sorter.dark, ()->{
						spark.projectile().type.draw(spark);
					});
				}
			}),
			new DrawTrait(spark->{
				if(!((Projectiles)spark.projectile().type).dark){
					spark.projectile().type.draw(spark);
				}
			})
		);
	}
	
	/**Shortcut method.*/
	public static Spark create(Projectiles type, Spark source, float x, float y, float angle){
		return create(type, source, -1, x, y, angle);
	}
	
	public static Spark create(Projectiles type, Spark source, int damage, float x, float y, float angle){
		Spark spark = new Spark(Prototypes.projectile);
		spark.get(ProjectileTrait.class).type = type;
		spark.get(ProjectileTrait.class).source = source;
		spark.get(LightTrait.class).enabled = type.light;
		spark.get(LightTrait.class).radius = type.lightsize;
		spark.get(LightTrait.class).small = true;
		if(type.particles){
			spark.get(ParticleTrait.class).setEmission(0f, 30f);
			spark.get(ParticleTrait.class).setParticles(type.particleAmount);
		}else{
			spark.get(ParticleTrait.class).setParticles(0);
		}
		spark.velocity().vector.set(1, 1).setAngle(angle);
		if(damage != -1){
			spark.get(ContactDamageTrait.class).damage = damage;
			spark.get(ProjectileTrait.class).customDamage = true;
		}
		spark.pos().set(x, y);
		spark.add();
		return spark;
	}
}
