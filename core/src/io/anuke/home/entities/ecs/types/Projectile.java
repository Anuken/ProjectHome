package io.anuke.home.entities.ecs.types;

import io.anuke.home.entities.ecs.Prototypes;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.ecs.extend.traits.ProjectileTrait.ProjectileType;

public class Projectile extends Prototype{

	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(),
			new VelocityTrait(),
			new ContactDamageTrait(),
			new ColliderTrait(),
			new LifetimeTrait(),
			new ProjectileTrait(),
			new DrawTrait(spark->{
				spark.get(ProjectileTrait.class).type.draw(spark);
			})
		);
	}
	
	/**Shortcut method.*/
	public static Spark create(ProjectileType type, Spark source, float x, float y, float angle){
		return create(type, source, -1, x, y, angle);
	}
	
	public static Spark create(ProjectileType type, Spark source, int damage, float x, float y, float angle){
		Spark spark = new Spark(Prototypes.projectile);
		spark.get(ProjectileTrait.class).type = type;
		spark.get(ProjectileTrait.class).source = source;
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
