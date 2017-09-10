package io.anuke.home.world.blocks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;

import io.anuke.home.Renderer;
import io.anuke.home.Vars;
import io.anuke.home.world.*;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.ecs.Prototype;
import io.anuke.ucore.ecs.Spark;
import io.anuke.ucore.facet.*;
import io.anuke.ucore.graphics.Caches;
import io.anuke.ucore.util.Geometry;
import io.anuke.ucore.util.Mathf;
import io.anuke.ucore.util.Timers;

public class BlockTypes{
	
	public static class Floor extends Block{
		private static TextureRegion temp = new TextureRegion();

		public Floor(String name) {
			super(name, BlockType.floor);
		}
		
		@Override
		public void drawCache(Tile tile){
			if(tile.wall instanceof Wall) return;
			
			if(this != Blocks.air)
				Caches.draw(name + (vary ? tile.rand(variants) : ""), tile.worldx(), tile.worldy());
			
			for(int dx = -1; dx <= 1; dx ++){
				for(int dy = -1; dy <= 1; dy ++){
					
					if(dx == 0 && dy == 0) continue;
					
					Tile other = World.get(tile.x+dx, tile.y+dy);
					
					if(other == null) continue;
					
					Block floor = other.floor;
					
					if(floor.id <= id || other.wall instanceof Wall) continue;
					
					TextureRegion region = Draw.region(floor.name + "edge");
					
					if(region == null)
						region = Draw.region(floor.edge + "edge");
					
					int tsize = 12;
					
					int sx = -dx*tsize+2, sy = -dy*tsize+2;
					int x = Mathf.clamp(sx, 0, tsize+4);
					int y = Mathf.clamp(sy, 0, tsize+4);
					int w = Mathf.clamp(sx+tsize, 0, tsize+4)-x, h = Mathf.clamp(sy+tsize, 0, tsize+4)-y;
					
					float rx = Mathf.clamp(dx*tsize, 0, tsize-w);
					float ry = Mathf.clamp(dy*tsize, 0, tsize-h);
					
					temp.setTexture(region.getTexture());
					temp.setRegion(region.getRegionX()+x, region.getRegionY()+y+h, w, -h);
					
					Caches.draw(temp, tile.worldx()-tsize/2 + rx, tile.worldy()-tsize/2 + ry, w, h);
					
				}
			}
		}
		
	}
	
	public static class Wall extends Block{
		String edge;

		public Wall(String name) {
			super(name, BlockType.wall);
			edge = name;
			vary = false;
			solid = true;
			hitbox.setSize(12, 18);
			hitbox.y = 3;
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			new SpriteFacet(name + (vary ? tile.rand(variants) : "")).set(tile.worldx(), tile.worldy()-Vars.tilesize/2f)
			.centerX().addShadow(list, "wallshadow", 6).sort(Sorter.object).add(list);
			
			if(Draw.hasRegion(edge + "edge")){
				new BaseFacet(p->{
					p.layer = tile.worldy()-Vars.tilesize/2f - 0.001f;
					float posx = tile.x * Vars.tilesize, posy = tile.y * Vars.tilesize + height;
					
					int dir = 0;
					
					for(GridPoint2 point : Geometry.getD4Points()){
						if(!World.isWall(tile.x + point.x, tile.y + point.y, this)){
							Draw.rect(edge + "edge", posx, posy, dir*90);
						}
						
						dir ++;
					}
					
					dir = 0;
					
					for(GridPoint2 point : Geometry.getD8EdgePoints()){
						if(!World.isWall(tile.x + point.x, tile.y + point.y, this)){
							Draw.rect(edge + "edgecorner", posx, posy, dir*90);
						}
						
						dir ++;
					}
					
				}).sort(Sorter.object).add(list);
			}
		}
	}
	
	public static class Tree extends Block{

		public Tree(String name) {
			super(name, BlockType.wall);
			hitbox.setSize(12, 2);
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			new SpriteFacet(name + (vary ? tile.rand(variants) : "")).set(tile.worldx(), tile.worldy()-offset)
			.layer(tile.worldy())
			.centerX().addShadow(list, offset).sort(Sorter.object).add(list);
		}
	}
	
	public static class Prop extends Block{

		public Prop(String name) {
			super(name, BlockType.wall);
			hitbox.setSize(12, 2);
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			new SpriteFacet(name).set(tile.worldx(), tile.worldy()-offset)
			.layer(tile.worldy())
			.centerX().addShadow(list, offset).sort(Sorter.object).add(list);
		}
	}
	
	public static class Overlay extends Block{
		boolean shadow = true;

		public Overlay(String name) {
			super(name, BlockType.wall);
			hitbox.setSize(8, 8);
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			String name = this.name + (vary ? tile.rand(variants) : "");
			SpriteFacet sprite = new SpriteFacet(name).set(tile.worldx(), tile.worldy()-offset)
			.layer(tile.worldy()+5)
			.center().sort(Sorter.object).sprite();
			
			if(shadow)
				sprite.addShadow(list, name, offset+8);
			
			sprite.add(list);
		}
	}
	
	public static class WallOverlay extends Block{
		Color color = Color.WHITE.cpy();

		public WallOverlay(String name) {
			super(name, BlockType.wall);
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			new SpriteFacet(name + (vary ? tile.rand(variants) : "")).color(color).set(tile.worldx(), tile.worldy()+Vars.tilesize/2f-0.001f)
			.centerX().sort(Sorter.object).add(list);
		}
		
	}
	
	public static abstract class SpellCircle extends Block{
		
		public SpellCircle(String name){
			super(name, BlockType.wall);
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			new BaseFacet(0, Sorter.tile, b->{
				draw(tile, tile.worldx(), tile.worldy());
			}).add(list);
		}
		
		public TextureRegion randRune(int index, Tile tile){
			return Renderer.getRune(tile.rand(index, 15)-1);
		}
		
		public abstract void draw(Tile tile, float x, float y);
	}
	
	public static class Checkpoint extends Block{
		public Color darkColor = new Color(0xd5bd7cff), lightColor = new Color(0xffeab2ff);
		public int sides = 3;

		public Checkpoint(String name) {
			super(name, BlockType.wall);
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			if(list != null) return;
			
			new SpriteFacet(name).set(tile.worldx(), tile.worldy())
			.center().sort(Sorter.tile).add(list);
			
			new BaseFacet(p->{
				Draw.color(darkColor);
				Draw.polygon(sides, tile.worldx(), tile.worldy() + Mathf.sin(Timers.time(), 16f, 2f)+10f, 4f, Timers.time()/1f);
				Draw.color(lightColor);
				Draw.polygon(sides, tile.worldx(), tile.worldy() + Mathf.sin(Timers.time(), 16f, 2f)+11f, 4f, Timers.time()/1f);
				Draw.color();
				p.layer = tile.worldy() - 2;
			}).add(list);
		}
	}
	
	public static class Spawner extends Block{

		public Spawner(String name) {
			super(name, BlockType.wall);
		}
		
		@Override
		public void draw(FacetList list, Tile tile){
			if(tile.data1 == -1){
				throw new IllegalArgumentException("Spawner tile detected without any valid spawn data!");
			}
			
			Spark enemy = new Spark(Prototype.getAllTypes().get(tile.data1));
			enemy.pos().set(tile.worldx(), tile.worldy());
			enemy.add();
			tile.data2 = (short)enemy.getID();
			tile.wall = Blocks.emptySpawner;
		}
	}
	
	public static class Decal extends Block{

		protected Decal(String name) {
			super(name, BlockType.decal);
			vary = false;
		}
		
		@Override
		public void drawCache(Tile tile){
			Caches.draw(name + (vary ? tile.rand(variants) : ""), tile.worldx(), tile.worldy());
		}
	}
	
	public static class Moss extends Decal{
		Color color = new Color();
		boolean rotate = true;

		protected Moss(String name) {
			super(name);
		}
		
		@Override
		public void drawCache(Tile tile){
			int i = 0;
			boolean any = false;
			
			Caches.color(color);
			
			for(GridPoint2 point : Geometry.getD4Points()){
				Tile other = World.get(point.x + tile.x, point.y + tile.y);
				
				if(other != null && other.decal == this){
					int rot = tile.rand(i*2, 5)-1;
					
					Caches.draw(name + "" + (i + (rotate ? rot : 0)) % 4, tile.worldx(), tile.worldy(), rotate ? -rot*90 : 0);
					any = true;
				}
				i ++;
			}
			
			if(!any){
				Caches.draw(name + "none", tile.worldx(), tile.worldy());
			}
			
			Caches.color();
		}
	}
}
