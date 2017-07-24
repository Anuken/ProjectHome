package io.anuke.home.editor;

import java.util.Stack;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntSet;

import io.anuke.home.Renderer;
import io.anuke.home.Vars;
import io.anuke.home.world.*;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.ecs.Basis;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.function.SegmentConsumer;
import io.anuke.ucore.util.Mathf;

public enum Tool{
	pencil{
		public void clicked(int x, int y){

			if(World.get(x, y) == null)
				return;

			Block block = Evar.control.selected;
			if(block.type.floor){
				if(World.get(x, y).floor != block){
					World.get(x, y).floor = block;
					Renderer.updateFloor(x, y);
				}
			}else{
				if(World.get(x, y).wall != block){
					World.get(x, y).wall = block;
					Renderer.updateWall(x, y);
					Renderer.updateFloor(x, y);
				}
			}

		}

		public void spawnClicked(int x, int y){
			Tile tile = World.get(x, y);

			if(tile.wall == Blocks.emptySpawner){
				Basis.instance().removeSpark(tile.data2);
			}

			Prototype type = Evar.control.seltype;
			tile.data1 = (short) type.getTypeID();
			tile.wall = Blocks.spawner;
			Renderer.updateWall(x, y);
		}
	},
	fill{
		public void clicked(int x, int y){

			if(World.get(x, y) == null)
				return;

			Tile tile = World.get(x, y);

			boolean wall = !Evar.control.selected.type.floor;

			Block dest = wall ? tile.wall : tile.floor;

			if(dest == Evar.control.selected)
				return;

			int width = World.width();

			IntSet set = new IntSet();
			Stack<GridPoint2> points = new Stack<GridPoint2>();
			points.add(new GridPoint2(x, y));

			while(!points.isEmpty()){
				GridPoint2 pos = points.pop();
				set.add(asInt(pos.x, pos.y, width));

				Block block = wall ? World.get(pos.x, pos.y).wall : World.get(pos.x, pos.y).floor;

				if(block == dest){
					World.get(pos.x, pos.y).setBlock(Evar.control.selected);

					if(pos.x > 0 && !set.contains(asInt(pos.x - 1, pos.y, width)))
						points.add(new GridPoint2(pos).cpy().add(-1, 0));
					if(pos.y > 0 && !set.contains(asInt(pos.x, pos.y - 1, width)))
						points.add(new GridPoint2(pos).cpy().add(0, -1));
					if(pos.x < World.width() - 1 && !set.contains(asInt(pos.x + 1, pos.y, width)))
						points.add(new GridPoint2(pos).cpy().add(1, 0));
					if(pos.y < World.height() - 1 && !set.contains(asInt(pos.x, pos.y + 1, width)))
						points.add(new GridPoint2(pos).cpy().add(0, 1));
				}
			}

			Renderer.clearWorld();
			Renderer.updateWalls();
		}

		int asInt(int x, int y, int width){
			return x + y * width;
		}

		void set(int x, int y, Block block){
			if(block.type.floor){
				World.get(x, y).floor = block;
			}else{
				World.get(x, y).wall = block;
			}
		}

		public boolean radius(){
			return false;
		}

	},
	erase{
		public void clicked(int x, int y){

			if(World.get(x, y) == null)
				return;

			if(Evar.control.walls){
				World.get(x, y).wall = Blocks.air;
				Renderer.updateFloor(x, y);
				Renderer.updateWall(x, y);

			}else{
				World.get(x, y).floor = Blocks.air;
				Renderer.updateFloor(x, y);
			}
		}

		public void spawnClicked(int x, int y){
			Tile tile = World.get(x, y);

			if(tile.wall == Blocks.emptySpawner){
				Basis.instance().removeSpark(tile.data2);
				tile.data1 = -1;
				tile.wall = Blocks.air;
			}
		}
	},
	select{
		int x1, y1, x2, y2, lastx, lasty;
		boolean up, selecting, cleared;
		Tile[][] selection;
		Tile[][] over;

		public boolean radius(){
			return false;
		}

		public void down(int x, int y){
			if(Vector2.dst((x1+x2)/2, (y1+y2)/2, x, y) < 3f && up && !(x1 == x2 || y1 == y2)){
				selecting = true;
				lastx = x;
				lasty = y;
			}else{
				x1 = x;
				y1 = y;
				up = false;
			}
		}

		public void clicked(int x, int y){
			if(selecting){
				if(!cleared){
					clearWorldSel();
					updateOver();
					cleared = true;
				}
				
				placeOver();
				
				x1 += x-lastx;
				y1 += y-lasty;
				x2 += x-lastx;
				y2 += y-lasty;
				
				updateOver();
				placeSelection();
				lastx = x;
				lasty = y;
				
				Renderer.updateWalls();
				Renderer.clearWorld();
			}else{
				x2 = x + 1;
				y2 = y - 1;
			}
		}

		public void up(int x, int y){
			//TODO fix grabbing clearing tiles on top
			if(!up){
				selection = new Tile[Math.abs(x1-x2)][Math.abs(y1-y2)];
				over = new Tile[Math.abs(x1-x2)][Math.abs(y1-y2)];
				grabSelection();
				up = true;
			}
			
			cleared = false;
			selecting = false;
		}

		public void draw(int x, int y){
			Draw.color(Color.CORAL);
			Draw.thick(2f);
			Draw.linerect(x1*Vars.tilesize - Vars.tilesize/2, y1*Vars.tilesize + Vars.tilesize/2, 
					-x1*Vars.tilesize+x2*Vars.tilesize, -y1*Vars.tilesize+y2*Vars.tilesize);
			Draw.alpha(0.2f);
			Draw.fillcrect(x1*Vars.tilesize - Vars.tilesize/2, y1*Vars.tilesize + Vars.tilesize/2, -x1*Vars.tilesize+x2*Vars.tilesize, -y1*Vars.tilesize+y2*Vars.tilesize);
			
			if(up && !(x1 == x2 || y1 == y2)){
				Draw.color(selecting ? Color.PURPLE : Color.WHITE);
				float px = (x1+x2)*Vars.tilesize/2 - Vars.tilesize/2, py = (y1+y2)*Vars.tilesize/2 + Vars.tilesize/2;
				Draw.polygon(4, px, py, 12, 0);
				Draw.lineAngleCenter(px, py, 0, 22);
				Draw.lineAngleCenter(px, py, 90, 22);
			}
			
			Draw.reset();
		}
		
		public void update(){
			if(Inputs.keyUp(Keys.FORWARD_DEL)){
				iterateRect((x, y, wx, wy)->{
					Tile tile = World.get(wx, wy);
					
					if(tile != null){
						if(tile.wall == Blocks.emptySpawner)
							Basis.instance().removeSpark(tile.data2);
						
						tile.wall = Blocks.air;
						tile.floor = Blocks.air;
					}
				});
				Renderer.updateWalls();
				Renderer.clearWorld();
				selecting = false;
				x1 = x2 = y1 = y2 = 0;
			}
		}
		
		void clearWorldSel(){
			iterateRect((x, y, wx, wy)->{
				Tile tile = World.get(wx, wy);
				
				World.set(wx, wy, new Tile(wx, wy));
				
				if(tile != null && tile.wall.type == BlockType.emptySpawner){
					Basis.instance().removeSpark(tile.data2);
				}
			});
		}
		
		void placeSelection(){
			iterateRect((x, y, wx, wy)->{
				Tile tile = selection[x][y];
				
				if(tile != null){
					Tile prev = World.get(wx, wy);
					
					World.set(wx, wy, tile);
					
					if(tile.wall.type == BlockType.emptySpawner){
						tile.wall = Blocks.spawner;
					}
					
					if(prev != null && prev.wall.type == BlockType.emptySpawner){
						Basis.instance().removeSpark(prev.data2);
					}
				}
			});
		}
		
		void placeOver(){
			iterateRect((x, y, wx, wy)->{
				Tile tile = over[x][y];
				
				if(tile != null){
					Tile prev = World.get(wx, wy);
					
					World.set(wx, wy, tile);
					
					if(tile.wall.type == BlockType.emptySpawner){
						tile.wall = Blocks.spawner;
					}
					
					if(prev != null && prev.wall.type == BlockType.emptySpawner){
						Basis.instance().removeSpark(prev.data2);
					}
				}
			});
		}
		
		void grabSelection(){
			iterateRect((x, y, wx, wy)->{
				selection[x][y] = World.get(wx, wy);
			});
		}
		
		void updateOver(){
			iterateRect((x, y, wx, wy)->{
				Tile tile = World.get(wx, wy);
				
				over[x][y] = tile;
			});
		}
		
		void iterateRect(SegmentConsumer cons){
			int w = Math.abs(x1-x2), h = Math.abs(y1-y2);
			int mulx = Mathf.sign(x2 > x1), muly = Mathf.sign(y2 > y1);
			
			for(int x = 0; x < w; x ++){
				for(int y = 0; y < h; y ++){
					int wx = x1 + x*mulx;
					int wy = y1 + y*muly;
					cons.accept(x, y, wx, wy);
				}
			}
		}
	};

	public boolean radius(){
		return true;
	}

	public void down(int x, int y){}

	public void up(int x, int y){}

	public void spawnClicked(int x, int y){}

	public void draw(int x, int y){
		int brushsize = (!radius() || Evar.control.seltype != null ? 1 : Evar.control.brushsize);

		for(int rx = -brushsize; rx <= brushsize; rx++){
			for(int ry = -brushsize; ry <= brushsize; ry++){
				if(Vector2.dst(rx, ry, 0, 0) < brushsize)
					Draw.rect("place", rx * Vars.tilesize + x * Vars.tilesize, ry * Vars.tilesize + y * Vars.tilesize);
			}
		}
	}

	public void clicked(int x, int y){}
	
	public void moved(){}
	
	public void update(){}
}
