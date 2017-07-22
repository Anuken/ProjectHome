package io.anuke.home.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import io.anuke.home.entities.types.Enemy;
import io.anuke.home.world.Block;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.DrawContext;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.renderables.RenderableHandler;
import io.anuke.ucore.scene.builders.*;
import io.anuke.ucore.scene.ui.*;
import io.anuke.ucore.scene.ui.layout.Stack;
import io.anuke.ucore.scene.ui.layout.Table;

public class EditorUI extends SceneModule{
	
	@Override
	public void init(){
		DrawContext.skin.font().setUseIntegerPositions(true);
		TooltipManager.getInstance().animations = false;
		
		build.begin();
		
		//block select
		new table(){{
			aleft();
			atop();
			
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
			
			new table(){{
				atop();
				
				new imagebutton("view-hitboxes", "toggle", 32, ()->{
					Evar.control.walls = !Evar.control.walls;
				}).size(50).get();
				
				row();
				
				ButtonGroup<ImageButton> group = new ButtonGroup<>();
				
				for(Tool tool : Tool.values()){
					ImageButton button = new ImageButton("view-none", "toggle");
					button.clicked(()->{
						Evar.control.tool = tool;
					});
					button.resizeImage(32);
					add(button).size(50);
					group.add(button);
					
					row();
				}
			}}.top().end();
			
		}}.end();
		
		//fps and info
		new table(){{
			abottom().aright();
			
			new label(()->Gdx.graphics.getFramesPerSecond() + " FPS")
			.color(Color.CORAL).right();
			
			row();
			
			new label(()->RenderableHandler.instance().getSize() + " renderables")
			.color(Color.CORAL);
		}}.end();
		
		new table(){{
			atop().aright();
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
			
			ScrollPane pane = new ScrollPane(content);
			
			stack.add(pane);
			
			int maxblocks = 12;
			
			int counter = 0;
			Table current = new Table();
			
			for(ImageButton ib : buttons){
				current.top().left();
				
				sgroup.add(ib);
				current.add(ib).padBottom(4).size(60);
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
}
