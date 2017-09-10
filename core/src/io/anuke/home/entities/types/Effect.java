package io.anuke.home.entities.types;

import io.anuke.home.entities.traits.EffectTrait;
import io.anuke.home.entities.types.enemies.library.Shade;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.FacetTrait;
import io.anuke.ucore.ecs.extend.traits.LifetimeTrait;
import io.anuke.ucore.ecs.extend.traits.PosTrait;
import io.anuke.ucore.facet.Sorter;

public class Effect extends Prototype{
	
	public Effect(){
		
	}

	@Override
	public TraitList traits(){
		return new TraitList(
			new EffectTrait(),
			new PosTrait(),
			new LifetimeTrait(),
			new FacetTrait((trait, spark)->{
				EffectTrait effect = spark.get(EffectTrait.class);
				
				//TODO make this cleaner
				if(!effect.color.equals(Shade.eyeColor)){
					trait.draw(Sorter.object, spark.pos().y - 8, ()->{
						Effects.renderEffect(spark.getID(), Effects.getEffect(effect.name), 
							effect.color, spark.life().life, spark.pos().x, spark.pos().y);
					});
				}else{
					trait.draw(Sorter.object, Sorter.dark, ()->{
						Effects.renderEffect(spark.getID(), Effects.getEffect(effect.name), 
							effect.color, spark.life().life, spark.pos().x, spark.pos().y);
					});
				}
			})
		);
	}

}
