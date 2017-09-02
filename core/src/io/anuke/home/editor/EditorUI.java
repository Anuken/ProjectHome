package io.anuke.home.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import io.anuke.home.Renderer;
import io.anuke.home.Vars;
import io.anuke.home.entities.types.Enemy;
import io.anuke.home.world.Block;
import io.anuke.home.world.MapIO;
import io.anuke.home.world.World;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.core.Settings;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.renderables.RenderableHandler;
import io.anuke.ucore.scene.builders.*;
import io.anuke.ucore.scene.ui.*;
import io.anuke.ucore.scene.ui.layout.Stack;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.scene.utils.Elements;

public class EditorUI extends SceneModule{
	ImageButton wallbutton;
	Dialog importDialog, exportDialog;
	String imtext = Settings.getString("lastimport"), extext = Settings.getString("lastexport");
	
	@Override
	public void init(){
		DrawContext.skin.font().setUseIntegerPositions(true);
		Dialog.closePadT -= 1;
		TooltipManager.getInstance().animations = false;
		
		importDialog = new Dialog("Import Map");
		importDialog.content().pad(10);
		importDialog.content().add("Import path: ");
		importDialog.content().addField(Settings.getString("lastimport"), text->{
			imtext = text;
			Settings.putString("lastimport", imtext);
			Settings.save();
		}).size(550, 50);
		
		importDialog.getButtonTable().addButton("Cancel", ()->{
			importDialog.hide();
		});
		
		importDialog.getButtonTable().addButton("OK", ()->{
			
			try{
				MapIO.load(Gdx.files.absolute(imtext));
				Renderer.clearAll();
				DrawContext.camera.position.set(World.getStartX() * Vars.tilesize, World.getStartY() * Vars.tilesize, 0);
			}catch (Exception e){
				e.printStackTrace();
				new TextDialog("Error loading file, ", e.getClass().getSimpleName() + ": "+ e.getCause() + "").show();
			}
			
			importDialog.hide();
		});
		
		exportDialog = new Dialog("Export Map");
		exportDialog.content().pad(10);
		exportDialog.content().add("Export path:");
		exportDialog.content().addField(Settings.getString("lastexport"), text->{
			extext = text;
			Settings.putString("lastexport", extext);
			Settings.save();
		}).size(550, 50);
		
		exportDialog.getButtonTable().addButton("Cancel", ()->{
			exportDialog.hide();
		});
		
		exportDialog.getButtonTable().addButton("OK", ()->{
			
			try{
				MapIO.save(World.getTiles(), Gdx.files.absolute(extext));
			}catch (Exception e){
				e.printStackTrace();
				new TextDialog("Error writing file, ", e.getClass().getSimpleName() + ": "+ e.getCause() + "").show();
			}
			
			exportDialog.hide();
		});
		
		build.begin();
		
		//block select
		new table(){{
			aleft();
			atop();
			
			new table(){{
			
				new table("button"){{
					atop();
					growY();
					
					tabs(get(), 
						"Blocks", new Array<ImageButton>(){{
							for(Block block : Block.getAllBlocks()){
								
								//ecksdee
								TextureRegion region = Draw.hasRegion(block.name) ? 
										Draw.region(block.name) : 
										Draw.hasRegion(block.name + "1") ? 
										Draw.region(block.name + "1") :  Draw.region("blank");
								
								ImageButton i = new ImageButton(region, "toggle");
								i.resizeImage(42);
								i.clicked(()->{
									Evar.control.seltype = null;
									Evar.control.selected = block;
									Evar.control.blocktype = block.type;
									updateWallButton();
								});
								
								Table info = new Table();
								info.background("button");
								info.add("[yellow]" + block.name);
								
								Tooltip tip = new Tooltip(info);
								
								i.addListener(tip);
								
								add(i);
							}
						}},
						"Mobs", new Array<ImageButton>(){{
							for(Prototype type : Prototype.getAllTypes()){
								if(!(type instanceof Enemy)) continue;
								
								String uname = ClassReflection.getSimpleName(type.getClass());
								String name = uname.toLowerCase();
								
								TextureRegion region = Draw.hasRegion(name) ? 
										Draw.region(name) : 
										Draw.hasRegion(name + "i1") ? 
										Draw.region(name + "i1") :  Draw.region("blank");
								
								ImageButton i = new ImageButton(region, "toggle");
								i.resizeImage(42);
								i.clicked(()->{
									Evar.control.seltype = type;
									Evar.control.selected = null;
								});
								
								Table info = new Table();
								info.background("button");
								info.add("[yellow]" + uname);
								
								Tooltip tip = new Tooltip(info);
								
								i.addListener(tip);
								
								add(i);
							}
						}}
					);
					
				}}.end();
				
				row();
				
				new button("Import", ()->{
					importDialog.show();
				}).fill();
				
				row();
				new button("Export", ()->{
					exportDialog.show();
				}).fill();
			
			}}.end();
			
			new table(){{
				atop();
				
				wallbutton = new imagebutton("icon-wall", 32, ()->{
					Evar.control.switchBlockType();
					updateWallButton();
				}).size(50).get();
				
				row();
				
				ButtonGroup<ImageButton> group = new ButtonGroup<>();
				
				for(Tool tool : Tool.values()){
					ImageButton button = new ImageButton("icon-"+tool.name(), "toggle");
					button.clicked(()->{
						Evar.control.tool = tool;
					});
					button.resizeImage(32);
					add(button).size(50);
					group.add(button);
					
					row();
				}
			}}.top().end();
			
			new table(){{
				top();
				
				Slider slider = new Slider(1, 9, 1, true);
				slider.moved(f->{
					Evar.control.brushsize = (int)(float)f;
				});
				
				new label(()->(int)slider.getValue() + "").center();
				row();
				add(slider);
			}}.pad(4).end();
			
		}}.end();
		
		//fps and info
		new table(){{
			abottom().aright();
			
			new table("button"){{
			
				new label(()->Gdx.graphics.getFramesPerSecond() + " FPS")
				.color(Color.CORAL).right();
				
				row();
				
				new label(()->RenderableHandler.instance().getSize() + " renderables"){{
					get().setAlignment(Align.right);
				}}
				.color(Color.CORAL).minWidth(200).right();
				
				row();
				
				new label(()->World.width() + "x" + World.height())
				.color(Color.CORAL).right();
			
			}}.end();
		}}.end();
		
		new table(){{
			atop().aright();
			new table(){{
				ButtonGroup group = new ButtonGroup();
				for(View view : View.values()){
					ImageButton button = new ImageButton("view-" + view.name(), "toggle");
					button.resizeImage(24);
					button.clicked(()->{
						Evar.control.view = view;
					});
					group.add(button);
					add(button).size(42);
				}
			}}.right().end();
			row();
			
			new label("Properties").right();
			row();
			
			defaults().right().size(42);
			
			add(Elements.newToggleImageButton("icon-eye", 32, World.data().dark, b->{
				World.data().dark = b;
			}));
			
			row();
			
			add(Elements.newToggleImageButton("icon-sky", 32, World.data().sky, b->{
				World.data().sky = b;
			}));
		}}.end();
		
		new table(){{
			abottom();
		}}.end();
		
		build.end();
	}
	
	void tabs(Table table, Object... objects){
		Stack stack = new Stack();
		ButtonGroup<TextButton> group = new ButtonGroup<>();
		
		for(int i = 0; i < objects.length; i += 2){
			String tabname = (String)objects[i];
			TextButton button = new TextButton(tabname, "toggle");
			group.add(button);
			table.add(button).fillX().height(40);
			
			ButtonGroup<ImageButton> sgroup = new ButtonGroup<>();
			
			Array<ImageButton> buttons = (Array)objects[i+1];
			
			//begin
			Table content = new Table();
			content.top().left();
			content.padRight(34);
			
			ScrollPane pane = new ScrollPane(content);
			pane.setFadeScrollBars(false);
			pane.setScrollingDisabled(true, false);
			
			stack.add(pane);
			
			int maxblocks = 18;
			
			int counter = 0;
			Table current = new Table();
			
			for(ImageButton ib : buttons){
				current.top().left();
				
				sgroup.add(ib);
				current.add(ib).size(60);
				current.row();
				
				counter ++;
				
				if(counter % maxblocks == 0){
					content.add(current).top();
					current = new Table();
				}
			}
			
			content.add(current).top();
			pane.setVisible(()->button.isChecked());
			
			//end
		}
		
		table.row();
		table.add(stack).colspan(group.getButtons().size).growY().fillX().fillY();
	}
	
	public void updateWallButton(){
		wallbutton.getStyle().imageUp = DrawContext.skin.getDrawable("icon-" + Evar.control.blocktype.name());
	}
}
