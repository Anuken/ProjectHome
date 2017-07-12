package io.anuke.home.editor;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import io.anuke.home.world.Block;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.modules.SceneModule;
import io.anuke.ucore.scene.builders.build;
import io.anuke.ucore.scene.builders.label;
import io.anuke.ucore.scene.builders.table;
import io.anuke.ucore.scene.ui.*;
import io.anuke.ucore.scene.ui.layout.Table;

public class EditorUI extends SceneModule{
	
	@Override
	public void init(){
		TooltipManager.getInstance().animations = false;
		
		build.begin();
		
		//block select
		new table(){{
			aleft();
			atop();
			
			new table("button"){{
				atop();
				growY();
				
				new label("Blocks");
				row();
				
				Table table = new Table();
				table.top().left();
				
				ScrollPane pane = new ScrollPane(table);
				
				add(pane).growY().fillX().fillY();
				
				Array<Block> blocks = Block.getAllBlocks();
				ButtonGroup<ImageButton> group = new ButtonGroup<>();
				
				int maxblocks = 12;
				
				int counter = 0;
				Table current = new Table();
				
				for(Block block : blocks){
					current.top().left();
					
					//ecksdee
					TextureRegion region = Draw.hasRegion(block.name) ? 
							Draw.region(block.name) : 
							Draw.hasRegion(block.name + "1") ? 
							Draw.region(block.name + "1") :  Draw.region("blank");
					
					ImageButton i = new ImageButton(region, "toggle");
					i.resizeImage(42);
					i.clicked(()->{
						Evar.control.selected = block;
					});
					
					Table info = new Table();
					info.background("button");
					info.add("[yellow]" + block.name);
					
					Tooltip tip = new Tooltip(info);
					
					i.addListener(tip);
					
					group.add(i);
					current.add(i).padBottom(4).size(60);
					current.row();
					
					counter ++;
					
					if(counter % maxblocks == 0){
						table.add(current).top();
						current = new Table();
					}
				}
				
				table.add(current).top();
				
			}}.end();
			
		}}.end();
		
		build.end();
	}
}
