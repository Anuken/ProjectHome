package io.anuke.home.entities.types;

import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Vars;
import io.anuke.home.entities.Prototypes;
import io.anuke.home.entities.traits.DropTrait;
import io.anuke.home.items.ItemStack;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Graphics;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.ecs.TraitList;
import io.anuke.ucore.ecs.extend.traits.*;
import io.anuke.ucore.facet.Sorter;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class ItemDrop extends Prototype{
	public static final float pickuprange = 65;

	@Override
	public TraitList traits(){
		return new TraitList(
			new PosTrait(),
			new VelocityTrait(0.08f),
			new ColliderTrait(4),
			new DropTrait(),
			new FacetTrait((trait, spark)->{
				
				trait.draw(b->{
					float raise = Mathf.sin(Timers.time(), 20f, 2f)+2f,
							x = spark.pos().x, y = spark.pos().y,
							size = spark.get(DropTrait.class).size;
					
					Spark player = Vars.control.getPlayer();
					
					if(Vector2.dst(x, y, player.pos().x, player.pos().y) < pickuprange && 
							Mathf.near2d(x, y+5, Graphics.mouseWorld().x, Graphics.mouseWorld().y, 4f)){
						
						Draw.color("purple");
						Draw.circle(x, y+raise+4f, size/1.6f);
						Draw.color();
					}
					
					Draw.grect(spark.get(DropTrait.class).stack.item.name + "-item", x, y + raise, size, size);
					
					b.layer = y-1;
				});
				
				trait.draw(b->{
					Draw.rect("shadow"+ Mathf.clamp(Mathf.roundi(spark.get(DropTrait.class).size/8f*6f, 2), 2, 10), spark.pos().x, spark.pos().y);
					b.layer = Sorter.shadow;
					b.sort(Sorter.tile);
				});
			})
		);
	}
	
	public static Spark create(ItemStack stack, float x, float y){
		Spark spark = new Spark(Prototypes.itemdrop);
		spark.get(VelocityTrait.class).vector.setToRandomDirection();
		spark.get(DropTrait.class).stack = stack;
		spark.pos().set(x, y);
		spark.add();
		return spark;
	}

}
