package io.anuke.home.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import io.anuke.home.Vars;
import io.anuke.home.world.Blocks;
import io.anuke.home.world.Tile;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.renderables.Sorter;
import io.anuke.ucore.util.Mathf;

public class Rain extends RenderEffect{
	float multiplier = 1f;
	
	Color color = Color.valueOf("5075de");
	Vector2 fall = new Vector2(1f, 3f).scl(multiplier);
	Particle[] particles = new Particle[400];

	@Override
	public void init(){
		color.a = 0.7f;
		
		OrthographicCamera cam = DrawContext.camera;
		
		float xrange = cam.viewportWidth * 1.5f;

		for(int i = 0; i < particles.length; i++){
			particles[i] = new Particle();
			particles[i].lifetime = Mathf.range(1f);
			particles[i].y = Mathf.range(1000) + cam.position.y;
			particles[i].x = cam.position.x + Mathf.range(xrange);
		}
		
		draw(p -> {
			if(!enabled) return;
			
			p.sort(Sorter.tile);
			p.layer = 0;
			
			for(Particle part : particles){
				if(part.splash.lifetime > 0){
					Tile tile = World.get(Mathf.scl2(part.splash.x, Vars.tilesize), Mathf.scl2(part.splash.y, Vars.tilesize));
					
					if(tile != null && tile.floor != Blocks.air){
						Draw.color(color);
						Draw.thick(part.splash.lifetime);
						Draw.circle(part.splash.x, part.splash.y, (1f-part.splash.lifetime)*3f);
						Draw.reset();
					}
				}
			}
		});

		draw(p -> {
			if(!enabled) return;
			
			p.layer = cam.position.y - cam.viewportHeight * 2;

			for(Particle part : particles){

				 Draw.color(color); 
				 Draw.thick(1f);
				 
				 Draw.lineAngle(part.x, part.y, fall.angle() + 180, 4f);
				 
				 Draw.reset();
			}
		});
	}

	@Override
	public void update(){
		OrthographicCamera cam = DrawContext.camera;

		float xrange = cam.viewportWidth * 1.4f;

		for(Particle part : particles){

			if(part.lifetime <= 0){
				part.splash.x = part.x;
				part.splash.y = part.y;
				part.splash.lifetime = 1f;
				
				part.lifetime = 1f;
				part.x = Mathf.range(xrange) + cam.position.x;
				part.y = cam.position.y + cam.viewportHeight/2 + Mathf.range(cam.viewportHeight);
			}
			
			if(part.splash.lifetime > 0){
				part.splash.lifetime -= Mathf.delta()/10f;
			}

			part.x -= fall.x * (Mathf.delta() + part.seed / 40f);
			part.y -= fall.y * (Mathf.delta() + part.seed / 40f);
			part.lifetime -= Mathf.delta()/70f;

			if(part.y < 0){
				part.y = 1f;
				part.x = Mathf.range(1f);
			}
		}

	}

	private class Splash{
		public float x, y, lifetime;
	}

	private class Particle{
		public float x, y, lifetime, seed;
		public Splash splash = new Splash();

		public Particle() {
			seed = Mathf.random(1f);
		}
	}

}
