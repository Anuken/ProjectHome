package io.anuke.home;

import static io.anuke.home.Vars.*;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import io.anuke.home.GameState.State;
import io.anuke.home.ui.BossHealthBar;
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
	Dialog paused, death, victory;
	Inventory inventory;
	BossHealthBar bossbar;
	
	boolean hasreset = true;
	boolean showntutorial = false;
	
	@Override
	public void init(){
		DrawContext.font.setUseIntegerPositions(true);
		Dialog.closePadT = -1;
		
		victory = new Dialog("Congratulations!", "dialog");
		victory.getTitleLabel().setColor(Color.YELLOW);
		victory.content().pad(10);
		victory.content().add("[green]You have beaten the game!\n[]Thanks for playing.");
		victory.getButtonTable().addButton("Back to Menu", ()->{
			victory.hide();
			GameState.set(State.menu);
		});
		
		victory.getButtonTable().addButton("Continue Playing", ()->{
			victory.hide();
		});
		
		death = new Dialog("You have died.", "dialog");
		death.getTitleLabel().setColor(Color.SCARLET);
		death.content().add("Press [green][[SPACE][] to respawn at your last checkpoint,\n or [green][[ESCAPE][] to return to the menu.");
		death.content().pad(10);
		death.keyDown(k->{
			if(k == Keys.SPACE){
				death.hide();
				Vars.control.respawn();
			}else if(k == Keys.ESCAPE){
				death.hide();
				GameState.set(State.menu);
			}
		});
		
		paused = new Dialog("Paused", "dialogclear");
		paused.getTitleLabel().setColor(Color.SKY);
		
		about = new TextDialog("About", aboutText).padText(10);
		about.left();
		for(Cell cell : about.content().getCells()){
			cell.left();
		}
		tutorial = new TextDialog("Tutorial", tutorialText).padText(10);
		tutorial.getButtonTable().addButton("OK", ()->{
			GameState.set(State.playing);
			tutorial.hide();
		}).size(80, 40);
		
		keybind = new KeybindDialog();
		settings = new SettingsDialog();
		
		settings.volumePrefs();
		settings.screenshakePref();
		
		build.begin(paused.content());
		
		paused.content().defaults().width(300).height(60);
		
		new button("Resume", "clear",()->{
			GameState.set(State.playing);
			paused.hide();
		});
		
		paused.content().row();
		
		new button("Settings", "clear",()->{
			settings.show();
		});
		
		paused.content().row();
		
		new button("Controls", "clear",()->{
			keybind.show();
		});
		
		paused.content().row();
		
		new button("Back to Menu", "clear", ()->{
			paused.hide();
			//TODO back to menu code
			GameState.set(State.menu);
		});
		
		build.end();
		
		build.begin(scene);
		
		new table("window-clear"){{
			defaults().width(300).height(60);
			
			new button("Play", "clear", ()->{
				//TODO playing setup code, call reset()
				if(hasreset){
					hasreset = false;
				}else{
					Vars.control.reset();
				}
				
				if(!showntutorial && !debug){
					tutorial.show();
					showntutorial = true;
				}else{
					GameState.set(State.playing);
				}
				
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
		
		if(Vars.debug)
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
			
			new table("button"){{
				HealthBar bar = new HealthBar();
				
				add(bar).fillX().expandX().height(24);//.size(370, 24);
			}}.left().end().fillX();
			
			row();
			
			inventory = new Inventory();
			
			add(inventory);
			
			visible(play);
		}}.end();
		
		new table(){{
			
			bossbar = new BossHealthBar();
			atop().aleft();
			
			new table("button"){{
				add(bossbar).height(28).growX();
				get().setVisible(false);	
			}}.end().growX();
			
			visible(play);
		}}.end();
		
		build.end();
	}
	
	public void setBossBarVisible(boolean visible){
		bossbar.getParent().setVisible(visible);
	}
	
	public BossHealthBar getBossBar(){
		return bossbar;
	}
	
	public void clearInventory(){
		inventory.clearItems();
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
	
	public void showVictory(){
		victory.show();
	}
	
}
