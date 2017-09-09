package io.anuke.home.entities.traits;

import com.badlogic.gdx.graphics.Color;

import io.anuke.ucore.ecs.Trait;

public class EffectTrait extends Trait{
	public String name;
	public Color color = Color.WHITE.cpy();
}
