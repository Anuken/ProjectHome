package io.anuke.home.entities.ecs;

import io.anuke.home.entities.ecs.types.ItemDrop;
import io.anuke.home.entities.ecs.types.Player;
import io.anuke.home.entities.ecs.types.Projectile;
import io.anuke.ucore.ecs.Prototype;

public class Prototypes{
	public static final Prototype
	
	player = new Player(),
	projectile = new Projectile(),
	itemdrop = new ItemDrop();
}
