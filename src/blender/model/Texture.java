package blender.model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLContext;

/**
* An OpenGL Texture.
*
* @author Sri Harsha Chilakapati
*/
public class Texture
{

    /**
* Texture ID.
*/
    public int id;

    /**
* Texture width.
*/
    public int width;

    /**
* Texture height.
*/
    public int height;

    /**
* Creates a texture.
*/
    private Texture(int id, int width, int height)
    {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public static Texture loadTexture2D(String name){
    	TextureFile texFile = TextureFile.loadTextureFile(name);
    	
    	BufferedImage bimg = texFile.getImage();
    	ByteBuffer buffer = texFile.getBuffer();

        // Generate a texture ID
        int vboTextureID = glGenTextures();
        // Bind the ID to the context
        glBindTexture(GL_TEXTURE_2D, vboTextureID);

        // Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
    	if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic){
    		glTexParameteri(GL_TEXTURE_CUBE_MAP, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
    	}

        // Send texture data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, bimg.getWidth(), bimg.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        
    	glBindTexture(GL_TEXTURE_2D, 0);

        // Return a new Texture.
        return new Texture(vboTextureID, bimg.getWidth(), bimg.getHeight());
    }
    
    public static Texture loadTextureCubeMap(String[] names){
    	TextureFile[] texFiles = new TextureFile[6];
    	
    	for(int i=0;i<6;i++){
    		texFiles[i] = TextureFile.loadTextureFile(names[i]);
    	}

    	int vboTextureID = glGenTextures();
    	
    	glBindTexture(GL_TEXTURE_CUBE_MAP, vboTextureID);

    	//glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    	
    	if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic){
    		glTexParameteri(GL_TEXTURE_CUBE_MAP, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT);
    	}

    	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    	glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    	for(int i = 0; i < 6; i++){
    		glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_RGBA8, texFiles[i].getImage().getWidth(), texFiles[i].getImage().getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, texFiles[i].getBuffer());
    	}

    	//glGenerateMipmap(GL_TEXTURE_CUBE_MAP);

    	glBindTexture(GL_TEXTURE_CUBE_MAP, 0);
    	
    	return new Texture(vboTextureID, texFiles[0].getImage().getWidth(), texFiles[0].getImage().getHeight());
    }
    
    public void dispose(){
        glDeleteBuffers(id);
    }

}
