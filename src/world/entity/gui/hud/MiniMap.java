package world.entity.gui.hud;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;

import state.Game;
import threading.RenderThread;
import utils.math.Vector3f;
import world.World;
import world.entity.Entity;
import world.entity.VisualEntity;
import world.entity.dumb.StaticEntity;
import world.entity.gui.AbstractComponent;
import world.entity.smart.Player;
import world.graphics.BufferSubData;
import world.graphics.Graphics2D;

public class MiniMap extends AbstractComponent{
	private static int vboVertexID;
	private static int vboTexVertexID;
	
	private static int width = 250;
	private static int heigth = 138;
	
	private World world;
	private Player player;
	
	private Set<VisualEntity> Entitys;
	private BufferSubData verticesChange = new BufferSubData(BufferUtils.createFloatBuffer(2 * 6), 0).setOffsetByFloat(12);
	private Vector3f PlayerLocation;
	
	public MiniMap(World world, Player player){
		this.world = world;
		this.player = player;
		
		PlayerLocation = player.getPosition();
		Entitys = world.getVisualEntities();
		setPosition(250, Game.height-heigth);
	}

	@Override
	public void render() {
		int textureid = RenderThread.spritesheet.getTex().getID();
		Vector2f pos = getPosition();
		
		glColor3f(1,1,1);
		
		glPushMatrix();
		
		glTranslatef(pos.x, pos.y, 0);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		Graphics2D.drawVBO(8, vboVertexID, vboTexVertexID, textureid);
		
		glDisable(GL_BLEND);
		
		glPopMatrix();
	}

	@Override
	//Lisada tekstuuri kordinaatide muutuste buffer.
	public void update(float dt) {
		Entitys = world.getVisualEntities();
		PlayerLocation = player.getPosition();
		
		verticesChange.put(new float []{
			width/2 - 2.5F, heigth/2 - 2.5F,
			width/2 - 2.5F, heigth/2 + 2.5F,
			width/2 + 2.5F, heigth/2 - 2.5F,
			width/2 + 2.5F, heigth/2 + 2.5F
		});
		
		for(VisualEntity e: Entitys){
			if(e instanceof StaticEntity){
				Vector3f pos = e.getPosition();
				if(Entitydistance(PlayerLocation, pos) < 300){
					//Kordinaatide teisendus luua
					verticesChange.put(new float[]{
						pos.x, pos.y - 2.5F,
						pos.x - 2.5F, pos.y + 2.5F,
						pos.x + 2.5F, pos.y + 2.5F,
						pos.x, pos.y - 2.5F
					});
				}
			}
		}
	}

	public Entity setLink(Entity t) {
		if(t instanceof MiniMap){
			MiniMap wt = (MiniMap)t;			
		}
		return this;
	}
	
	@Override
	public Entity getLinked() {
		return new MiniMap(world, player).setLink(this);
	}

	public float Entitydistance(Vector3f Player, Vector3f Entity){
		return (float)(Math.sqrt(Math.pow((Player.x - Entity.x), 2)+ Math.pow((Player.y - Entity.y), 2)));
	}
	
	@Override
	public void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(2 * 8);
		FloatBuffer texVertices = BufferUtils.createFloatBuffer(2 * 8);
		
		vertices.put(new float[]{
				0,0,
				0,heigth,
				width,heigth,
				width,0,
				
				0,0,
				0,heigth,
				width,heigth,
				width,0		
		});
		vertices.rewind();
		
		texVertices.put(new float[]{
				RenderThread.spritesheet.getUpLeftCoordNormal(52)[0],
				RenderThread.spritesheet.getUpLeftCoordNormal(52)[1],
				RenderThread.spritesheet.getBottomLeftCoordNormal(52)[0],
				RenderThread.spritesheet.getBottomLeftCoordNormal(52)[1],
				RenderThread.spritesheet.getBottomRightCoordNormal(52)[0],
				RenderThread.spritesheet.getBottomRightCoordNormal(52)[1],
				RenderThread.spritesheet.getUpRightCoordNormal(52)[0],
				RenderThread.spritesheet.getUpRightCoordNormal(52)[1],

				RenderThread.spritesheet.getUpLeftCoordNormal(53)[0],
				RenderThread.spritesheet.getUpLeftCoordNormal(53)[1],
				RenderThread.spritesheet.getBottomLeftCoordNormal(53)[0],
				RenderThread.spritesheet.getBottomLeftCoordNormal(53)[1],
				RenderThread.spritesheet.getBottomRightCoordNormal(53)[0],
				RenderThread.spritesheet.getBottomRightCoordNormal(53)[1],
				RenderThread.spritesheet.getUpRightCoordNormal(53)[0],
				RenderThread.spritesheet.getUpRightCoordNormal(53)[1]

		});
		texVertices.rewind();
		
		vboVertexID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertexID);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
		vboTexVertexID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboTexVertexID);
        glBufferData(GL_ARRAY_BUFFER, texVertices, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
		
	}

	@Override
	public void dispose() {
	    glDeleteBuffers(vboVertexID);
		glDeleteBuffers(vboTexVertexID);		
	}

}
