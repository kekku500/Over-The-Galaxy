package shader;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import game.Game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class GLSLProgram {
	
	private String vertex;
	private String fragment;
	private int vertShader, fragShader;
	private int program;
	public int[] uniformLocations;
	
	public boolean load(String vertex, String fragment){
		this.vertex = vertex;
		this.fragment = fragment;
		createShader();
		if(program == 0)
			return false;
		return true;
	}
	
	public int i(){
		return program;
	}
	
	public void createShader(){
		program = createShaderProgram(vertex, fragment);
	}
	
	public int createShaderProgram(String vertex, String fragment){
    	try {
            vertShader = createShader(vertex, GL_VERTEX_SHADER);
            fragShader = createShader(fragment, GL_FRAGMENT_SHADER);
    	}
    	catch(Exception exc) {
    		exc.printStackTrace();
    		return 0;
    	}
    	finally {
    		if(vertShader == 0 || fragShader == 0)
    			return 0;
    	}
    	
    	int program = glCreateProgram();

    	
    	if(program == 0)
    		return 0;
        
        glAttachShader(program, vertShader);
        glAttachShader(program, fragShader);
        
        glLinkProgram(program);
        
        if (glGetProgrami(program, GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(program));
            return 0;
        }
        return program;
	}
	
	public void validate(){
		glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
        	System.err.println(getLogInfo(program));
        }
	}
	
	public void bind(){
		glUseProgram(program);
	}
	
	public static void unbind(){
		glUseProgram(0);
	}
	
	public void destroy(){
		glDetachShader(program, vertShader);
		glDetachShader(program, fragShader);
		glDeleteShader(vertShader);
		glDeleteShader(fragShader);
		glDeleteProgram(program);
		
		setDefault();
	}
	
	private void setDefault(){
		vertShader = 0;
		fragShader = 0;
		program = 0;
		uniformLocations = null;
	}
	
	private int createShader(String filename, int shaderType) throws Exception{
    	int shader = 0;
    	try {
    		shader = glCreateShader(shaderType);
	        
	        if(shader == 0)
	        	return 0;
	        glShaderSource(shader, readFileAsString(filename));
	        glCompileShader(shader);
	        
	        if (glGetProgrami(shader, GL_COMPILE_STATUS) == GL11.GL_FALSE)
	            throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
	        return shader;
    	}
    	catch(Exception exc) {
    		glDeleteShader(fragShader);
    		throw exc;
    	}
	}
	
    private String getLogInfo(int obj) {
        return glGetProgramInfoLog(obj, glGetProgrami(obj, GL_INFO_LOG_LENGTH));
    }
	
    private String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();
        
		File f = new File(Game.RESOURCESPATH + Game.SHADERPATH + filename);
        
        FileInputStream in = new FileInputStream(f);
        
        Exception exception = null;
        
        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            
            Exception innerExc= null;
            try {
            	String line;
                while((line = reader.readLine()) != null)
                    source.append(line).append('\n');
            }
            catch(Exception exc) {
            	exception = exc;
            }
            finally {
            	try {
            		reader.close();
            	}
            	catch(Exception exc) {
            		if(innerExc == null)
            			innerExc = exc;
            		else
            			exc.printStackTrace();
            	}
            }
            
            if(innerExc != null)
            	throw innerExc;
        }
        catch(Exception exc) {
        	exception = exc;
        }
        finally {
        	try {
        		in.close();
        	}
        	catch(Exception exc) {
        		if(exception == null)
        			exception = exc;
        		else
					exc.printStackTrace();
        	}
        	
        	if(exception != null)
        		throw exception;
        }
        
        return source.toString();
    }
	

}
