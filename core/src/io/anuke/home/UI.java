package io.anuke.home;

import static io.anuke.home.Vars.*;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import io.anuke.home.GameState.State;
import io.anuke.home.ui.HealthBar;
import io.anuke.home.ui.Inventory;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.entities.Entities;
import io.anuke.ucore.function.VisibilityProvider;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.builders.*;
import io.anuke.ucore.scene.ui.*;
import io.anuke.ucore.scene.ui.layout.Cell;

public class UI extends SceneModule{
	VisibilityProvider play = ()->{return GameState.is(State.playing);};
	VisibilityProvider inmenu = ()->{return GameState.is(State.menu);};
	
	KeybindDialog keybind;
	SettingsDialog settings;
	TextDialog about;
	TextDialog tutorial;
	Dialog paused, death;
	Inventory inventory;
	
	@Override
	public void init(){
		DrawContext.font.setUseIntegerPositions(true);
		Dialog.closePadT = -1;
		
		death = new Dialog("You have died.", "dialog");
		death.getTitleLabel().setColor(Color.SCARLET);
		death.content().add("Press [green][[SPACE][] to respawn at your last checkpoint,\n or [green][[ESCAPE][] to return to the menu.");
		death.content().pad(10);
		death.keyDown(k->{
			if(k == Keys.SPACE){
				death.hide();
				Vars.control.respawn();
			}
		});
		
		paused = new Dialog("Paused", "dialog");
		
		about = new TextDialog("About", aboutText).padText(10);
		about.left();
		for(Cell cell : about.content().getCells()){
			cell.left();
		}
		tutorial = new TextDialog("Tutorial", tutorialText).padText(10);
		
		keybind = new KeybindDialog();
		settings = new SettingsDialog();
		
		settings.volumePrefs();
		settings.screenshakePref();
		
		build.begin(paused.content());
		
		paused.content().defaults().width(200);
		
		new button("Resume", ()->{
			GameState.set(State.playing);
			paused.hide();
		});
		
		paused.content().row();
		
		new button("Settings", ()->{
			settings.show();
		});
		
		paused.content().row();
		
		new button("Controls", ()->{
			keybind.show();
		});
		
		paused.content().row();
		
		new button("Back to Menu", ()->{
			paused.hide();
			//TODO back to menu code
			GameState.set(State.menu);
		});
		
		build.end();
		
		build.begin(scene);
		
		new table(){{
			defaults().width(300).height(60);
			
			new button("Play", "clear", ()->{
				//TODO playing setup code, call reset()
				GameState.set(State.playing);
			});
			
			row();
			
			new button("Settings", "clear", ()->{
				settings.show();
			});
			
			row();
			
			new button("Controls", "clear", ()->{
				keybind.show();
			});
			
			row();
			
			new button("About", "clear", ()->{
				about.show();
			});
			
			row();
			
			if(Gdx.app.getType() == ApplicationType.Desktop)
			new button("Exit", "clear", ()->{
				Gdx.app.exit();
			});
			
			visible(inmenu);
		}}.end();
		
		new table(){{
			atop();
			
			String text = "< Project Home >";
			
			new label(text, "title").color(Color.GRAY).scale(2).padBottom(-80);
			row();
			new label(text, "title").scale(2);
			
			visible(inmenu);
		}}.end();
		
		new table(){{
			atop();
			aright();
			
			new label(()->{
				return Gdx.graphics.getFramesPerSecond() + " FPS";
			});
			
			row();
			
			new label(()->{
				return Entities.amount() + " entities";
			});
		}}.end().visible(play);;
		
		new table(){{
			abottom().aleft();
			
			inventory = new Inventory();
			
			add(inventory);
			
			visible(play);
		}}.end();
		
		new table(){{
			HealthBar bar = new HealthBar(Vars.control.player);
			atop().aleft();
			
			new table("button"){{
				add(bar).size(370, 24);
				row();
					
			}}.end();
			
			visible(play);
		}}.end();
		
		build.end();
	}
	
	public Inventory getInventory(){
		return inventory;
	}
	
	public void showDeath(){
		death.show();
	}
	
	public void hidePaused(){
		paused.hide();
	}
	
	public void showPaused(){
		paused.show();
	}
	
}
