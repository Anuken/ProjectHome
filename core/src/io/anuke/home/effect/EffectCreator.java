package io.anuke.home.effect;

import com.badlogic.gdx.graphics.Color;

import io.anuke.home.entities.types.enemies.library.Shade;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.util.Geometry;

public class EffectCreator{
	static final Color tent = new Color(0x500680ff);
	
	public static void load(){
		
		Effects.create("shadecloud", 20, e->{
			Draw.color(Color.DARK_GRAY);
			
			float rad = e.fract()*5f + 1f;
			Geometry.randLenVectors(e.id, 5, e.powfract()*16f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.reset();
		});
		
		Effects.create("shadedeath", 24, e->{
			Draw.thickness(3f);
			Draw.color(Shade.eyeColor, Color.DARK_GRAY, e.ifract());
			Draw.spikes(e.x, e.y, 1f+e.ifract()*8f, 1, 5);
			
			float rad = e.fract()*6f;
			Geometry.randLenVectors(e.id, 9, e.ifract()*40f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.reset();
		});
		
		Effects.create("bigshadedeath", 60, e->{
			Draw.thickness(3f);
			
			Draw.color(Color.DARK_GRAY);
			
			float rad = e.fract()*12f;
			Geometry.randLenVectors(e.id+9999, 40, e.ifract()*60f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.color(Shade.eyeColor, Color.DARK_GRAY, e.ifract());
			
			float rad1 = e.fract()*9f;
			
			Geometry.randLenVectors(e.id, 20, e.ifract()*50f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad1, rad1);
			});
			
			Draw.spikes(e.x, e.y, 1f+e.ifract()*15f, 1, 5);
			
			
			Draw.reset();
		});
		
		Effects.create("shadehit", 10, e->{
			Draw.thickness(1f);
			Draw.color(Shade.eyeColor, Color.DARK_GRAY, e.ifract());
			float rad = e.fract()*3f+1f;
			Geometry.randLenVectors(e.id, 6, e.ifract()*14f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			//Draw.spikes(e.x, e.y, e.ifract()*4f, 2, 8);
			Draw.reset();
		});
		
		Effects.create("shadesmoke", 70, e->{
			Draw.thickness(1f);
			Draw.color(Color.DARK_GRAY);
			float rad = e.sfract()*5f+1f;
			Geometry.randLenVectors(e.id, 6, e.ifract()*20f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			//Draw.spikes(e.x, e.y, e.ifract()*4f, 2, 8);
			Draw.reset();
		});
		
		Effects.create("rockbreak", 38, e->{
			Draw.color(Color.LIGHT_GRAY, Color.DARK_GRAY, e.ifract());
			
			float rad = e.fract()*7f;
			Geometry.randLenVectors(e.id, 5, e.powfract()*24f, (x, y)->{
				Draw.rect("rockparticle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.reset();
		});
		
		Effects.create("rockspark", 18, e->{
			Draw.color(Color.WHITE, Color.DARK_GRAY, e.ifract());
			
			float rad = e.fract()*3f;
			Geometry.randLenVectors(e.id, 5, e.powfract()*12f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.reset();
		});
		
		Effects.create("scorchblap", 8, e->{
			Draw.thickness(2f);
			Draw.color(Hue.mix(Color.CORAL, Color.WHITE, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*5f, 1, 6);
			Draw.reset();
		});
		
		Effects.create("aetherblap", 8, e->{
			Draw.thickness(1f);
			Draw.color(Hue.mix(Color.PURPLE, Color.WHITE, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*5f, 1, 4);
			Draw.circle(e.x, e.y, e.ifract()*5f);
			Draw.reset();
		});
		
		Effects.create("planeblap", 8, e->{
			Draw.thickness(1f);
			Draw.color(Hue.mix(Color.LIME, Color.WHITE, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*5f, 2, 4);
			Draw.polygon(4, e.x, e.y, e.ifract()*5f);
			Draw.reset();
		});
		
		Effects.create("orbblap", 9, e->{
			Draw.thickness(2f);
			Draw.color(Hue.mix(Color.PINK, Color.WHITE, e.ifract()));
			Draw.circle(e.x, e.y, e.ifract()*6f);
			Draw.reset();
		});
		
		Effects.create("orbshrink", 9, e->{
			Draw.color(Color.PINK, Color.WHITE, e.ifract());
			Draw.circle(e.x, e.y, 8-e.ifract()*8f);
			Draw.color();
		});
		
		Effects.create("fusionblap", 9, e->{
			Draw.thickness(1f);
			Draw.color(Hue.mix(Color.CORAL, Color.BLACK, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*5f, 2, 5, 90);
			Draw.polygon(5, e.x, e.y, e.ifract()*6f, 90);
			Draw.reset();
		});
		
		Effects.create("explosion", 10, e->{
			Draw.color(Color.YELLOW);
			Draw.circle(e.x, e.y, 20*e.ifract());
			Draw.reset();
		});
		
		Effects.create("hit", 10, e->{
			Draw.thickness(1f);
			Draw.color(Hue.mix(Color.ORANGE, Color.PURPLE, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*4f, 2, 8);
			Draw.reset();
		});
		
		Effects.create("checkpoint", 35, e->{
			Draw.thickness(2f);
			Draw.color(Color.WHITE, new Color(0xffe29400), e.ifract());
			Draw.spikes(e.x, e.y, 4+e.ifract()*9f, 3, 8);
			Draw.polygon(4, e.x, e.y, 2+e.ifract()*9f);
			Draw.reset();
		});
		
		Effects.create("tentablip", 8, e->{
			Draw.thickness(2f);
			Draw.color(Hue.mix(Color.BLACK, Color.CLEAR, e.ifract()));
			Draw.spikes(e.x, e.y, 2+e.ifract()*4f, 2, 3, 90);
			Draw.reset();
		});
		
		Effects.create("yellowblap", 8, e->{
			Draw.thickness(2f);
			Draw.color(Hue.mix(Color.WHITE, Color.YELLOW, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*4f, 1, 5);
			Draw.reset();
		});
		
		Effects.create("orangeblap", 8, e->{
			Draw.thickness(1f);
			Draw.color(Hue.mix(Color.WHITE, Color.ORANGE, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*4f, 1, 5);
			Draw.reset();
		});
		
		Effects.create("shotshrink", 13, e->{
			Draw.thickness(1f);
			Draw.color(Color.BLACK, Color.CLEAR, e.ifract());
			Draw.circle(e.x, e.y, 3-e.ifract()*3f);
			Draw.reset();
		});
		
		Effects.create("gshotshrink", 13, e->{
			Draw.thickness(1f);
			Draw.color(Color.WHITE, Color.ORANGE, e.ifract());
			Draw.circle(e.x, e.y, 2-e.ifract()*2f);
			Draw.reset();
		});
		
		Effects.create("tentashoot", 9, e->{
			Draw.thickness(2f);
			Draw.color(Hue.mix(Color.PURPLE, new Color(1, 0, 0, 0f), e.ifract()));
			float rad = e.ifract()*6f;
			Draw.spikes(e.x, e.y, rad, 2, 5);
			Draw.thickness(1f);
			Draw.spikes(e.x, e.y, 2+rad, 1, 5);
			Draw.reset();
		});
		
		Effects.create("blood", 23, e->{
			Draw.color(Color.SCARLET, new Color(0xff341c00), e.ifract());
			
			float rad = e.fract()*4f;
			Geometry.randLenVectors(e.id, 5, e.ifract()*30f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.reset();
		});
		
		Effects.create("dust", 23, e->{
			Draw.color(Color.LIGHT_GRAY, Color.DARK_GRAY, e.ifract());
			
			float rad = e.fract()*4f;
			Geometry.randVectors(e.id, 5, e.ifract()*30f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.reset();
		});
		
		Effects.create("purpleblood", 25, e->{
			Draw.color(new Color(0x411e4bff), Color.BLACK, e.ifract());
			
			float rad = e.fract()*8f;
			Geometry.randVectors(e.id, 8, e.ifract()*40f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.reset();
		});
		
		Effects.create("golemwave", 14, e->{
			
			Draw.color(Hue.mix(Color.WHITE, new Color(0x53000000), e.ifract()));
			float rad = 3f+e.ifract()*9f;
			Draw.thickness(2f);
			Draw.spikes(e.x, e.y, rad, 2f, 8);
			Draw.thickness(1f);
			Draw.spikes(e.x, e.y, rad+3f, 2f, 8);
			Draw.reset();
		});
		
		Effects.create("eyeflash", 10, e->{
			Draw.thickness(1f);
			Draw.color(Hue.mix(Color.SCARLET, Color.BLACK, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*6f, 2, 2);
			Draw.reset();
		});
		
		Effects.create("purpleeyeflash", 10, e->{
			Draw.thickness(1f);
			Draw.color(Hue.mix(Color.PURPLE, Color.BLACK, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*6f, 2, 2);
			Draw.reset();
		});
		
		Effects.create("golemflash", 7, e->{
			Draw.thickness(2f);
			Draw.color(Hue.mix(Color.SCARLET, Color.BLACK, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*7f, 1, 3, 90);
			Draw.reset();
		});
		
		Effects.create("tentaflash", 7, e->{
			Draw.thickness(2f);
			Draw.color(Hue.mix(Color.PURPLE, Color.BLACK, e.ifract()));
			Draw.spikes(e.x, e.y, e.ifract()*7f, 1, 3, 90);
			Draw.reset();
		});
		
		Effects.create("death", 20, e->{
			Draw.thickness(4f);
			Draw.color(Hue.mix(Color.ORANGE, Color.GRAY, e.ifract()));
			Draw.spikes(e.x, e.y, 1f+e.ifract()*8f, 1, 5);
			
			float rad = e.fract()*6f;
			Geometry.randLenVectors(e.id, 8, e.ifract()*40f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.reset();
		});
		
		Effects.create("wraithdie", 40, e->{
			Draw.thickness(4f);
			Draw.color(Hue.mix(tent, Color.CLEAR, e.ifract()));
			
			float rad = e.fract()*23f;
			Geometry.randVectors(e.id, 8, e.ifract()*70f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad, rad);
			});
			
			Draw.color(Hue.mix(Color.BLACK, tent, e.ifract()));
			
			float rad2 = e.fract()*18f;
			Geometry.randVectors(e.id+1, 8, e.ifract()*60f, (x, y)->{
				Draw.rect("circle", e.x+x, e.y+y, rad2, rad2);
			});
			
			Draw.reset();
		});
		
		Effects.create("dash", 7, e->{
			Draw.thickness(1f);
			Draw.color(Color.WHITE, Color.GRAY, e.ifract());
			float s = e.fract()*7;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("darkdash", 7, e->{
			Draw.thickness(1f);
			Draw.color(Color.BLACK, Color.CLEAR, e.ifract());
			float s = e.fract()*10;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("swing", 7, e->{
			Draw.thickness(1f);
			Draw.color(Color.WHITE, new Color(0.5f, 0.5f, 0.5f, 0f), e.ifract());
			float s = e.fract()*6;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("swingalt", 8, e->{
			Draw.thickness(1f);
			Draw.color(Color.ORANGE, new Color(0.5f, 0.5f, 0.5f, 0f), e.ifract());
			float s = e.fract()*7;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("swingcryo", 7, e->{
			Draw.thickness(1f);
			Draw.color(Color.WHITE, Color.SKY, e.ifract());
			float s = e.fract()*6;
			Draw.polygon(5, e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("swingaltcryo", 8, e->{
			Draw.thickness(1f);
			Draw.color(Color.SKY, Color.ORANGE, e.ifract());
			float s = e.fract()*7;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("swingdagger", 7, e->{
			Draw.thickness(1f);
			Draw.color(Color.SCARLET, Color.WHITE, e.ifract());
			float s = e.fract()*5;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("swingaltdagger", 8, e->{
			Draw.thickness(1f);
			Draw.color(Color.PURPLE, Color.ORANGE, e.ifract());
			float s = e.fract()*7;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("swingsilver", 7, e->{
			Draw.color(Color.LIGHT_GRAY, Color.DARK_GRAY, e.ifract());
			float s = e.fract()*8;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("swingaltsilver", 8, e->{
			Draw.color(Color.LIGHT_GRAY, Color.DARK_GRAY, e.ifract());
			float s = e.fract()*8;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.color();
			Draw.polygon(3, e.x, e.y, 2-e.ifract()*2f);
			Draw.reset();
		});
		
		Effects.create("swingsilver", 7, e->{
			Draw.color(Color.LIGHT_GRAY, Color.DARK_GRAY, e.ifract());
			float s = e.fract()*8;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.reset();
		});
		
		Effects.create("swingaltsilver", 8, e->{
			Draw.color(Color.LIGHT_GRAY, Color.DARK_GRAY, e.ifract());
			float s = e.fract()*8;
			Draw.rect("circle", e.x, e.y, s, s);
			Draw.color();
			Draw.polygon(3, e.x, e.y, 2-e.ifract()*2f);
			Draw.reset();
		});
		
		Effects.create("swingtenta", 7, e->{
			Draw.color(tent, Color.BLACK, e.ifract());
			float s = e.fract()*8;
			Draw.rect("circle", e.x, e.y, s/2f, s/2f);
			Draw.polygon(3, e.x, e.y, s);
			Draw.reset();
		});
		
		Effects.create("swingalttenta", 8, e->{
			Draw.color(Color.PURPLE, Color.BLACK, e.ifract());
			float s = e.fract()*8;
			Draw.rect("circle", e.x, e.y, s, s);
			//Draw.color();
			//Draw.polygon(3, e.x, e.y, 2-e.ifract()*2f);
			Draw.reset();
		});
		
		Effects.create("swingphase", 7, e->{
			Draw.color(Color.ROYAL, Color.WHITE, e.ifract());
			float s = e.fract()*5;
			Draw.thick(2);
			Draw.polygon(4, e.x, e.y, s);
			Draw.reset();
		});
		
		Effects.create("swingaltphase", 8, e->{
			Draw.color(Color.ROYAL, Color.WHITE, e.ifract());
			float s = e.fract()*7;
			Draw.thick(2);
			Draw.polygon(4, e.x, e.y, s);
			Draw.thick(1);
			Draw.polygon(4, e.x, e.y, s+3);
			Draw.reset();
		});
		
	}
	
}
