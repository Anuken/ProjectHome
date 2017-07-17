package io.anuke.home.entities;

import io.anuke.home.entities.ecs.types.Projectiles;
import io.anuke.ucore.entities.BulletEntity;
import io.anuke.ucore.entities.Entity;

public class Projectile extends BulletEntity{
	
	public Projectile(Projectiles type, Entity owner, float angle){
		this.type = type;
		this.owner = owner;
		
		velocity.set(0, type.speed).setAngle(angle);
		hitsize = 4;
	}
	
	@Override
	public void update(){
		super.update();
		if(collidesTile()){
			type.removed(this);
			remove();
		}
	}
}
