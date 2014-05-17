package shader;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glUseProgram;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

import utils.Utils;

/**
* The vertex and fragment shaders are setup when the box object is
* constructed. They are applied to the GL state prior to the box
* being drawn, and released from that state after drawing.
* @author Stephen Jones
*/
public class Shader {
	
	private String vertex, fragment;
	private int vertShader, fragShader;
	private int program;
	public int[] uniformLocations;
	public int[] attribLocations;
	
	public Shader(){
		
	}
	
	public Shader(String vertex, String fragment){
		this.vertex = vertex;
		this.fragment = fragment;
		createShaderProgram(vertex, fragment);
	}
	
	public boolean load(String vertex, String fragment){
		this.vertex = vertex;
		this.fragment = fragment;
		createShaderProgram(vertex, fragment);
		if(program == 0)
			return false;
		return true;
	}
	
	public void createShader(){
		createShaderProgram(vertex, fragment);
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
	
	public int i(){
		return program;
	}
	
	private void setDefault(){
		vertShader = 0;
		fragShader = 0;
		program = 0;
		uniformLocations = null;
		attribLocations = null;
	}

	public int createShaderProgram(String vertex, String fragment){
    	int vertShader = 0, fragShader = 0;
    	
    	try {
            vertShader = createShaderFromFile(vertex,GL_VERTEX_SHADER);
            fragShader = createShaderFromFile(fragment,GL_FRAGMENT_SHADER);
    	}
    	catch(Exception exc) {
    		exc.printStackTrace();
    		return 0;
    	}
    	finally {
    		if(vertShader == 0 || fragShader == 0)
    			return 0;
    	}
    	
    	program = ARBShaderObjects.glCreateProgramObjectARB();
    	
    	if(program == 0)
    		return 0;
        
        ARBShaderObjects.glAttachObjectARB(program, vertShader);
        ARBShaderObjects.glAttachObjectARB(program, fragShader);
        
        ARBShaderObjects.glLinkProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(program));
            return 0;
        }
        

        return program;
    }
	
	public void validate(){
        ARBShaderObjects.glValidateProgramARB(program);
        if (ARBShaderObjects.glGetObjectParameteriARB(program, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
        	System.err.println(getLogInfo(program));
        	program = 0;
        }
	}
     
    /*
    * With the exception of syntax, setting up vertex and fragment shaders
    * is the same.
    * @param the name and path to the vertex shader
    */
    private int createShaderFromFile(String filename, int shaderType) throws Exception {
    	return createShader(Utils.readFileAsString(filename), shaderType);
    	/*int shader = 0;
    	try {
	        shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
	        
	        if(shader == 0)
	        	return 0;
	        
	        ARBShaderObjects.glShaderSourceARB(shader, Utils.readFileAsString(filename));
	        ARBShaderObjects.glCompileShaderARB(shader);
	        glCompileShader(shader);
	        
	        if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
	            throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
	        
	        return shader;
    	}
    	catch(Exception exc) {
    		ARBShaderObjects.glDeleteObjectARB(shader);
    		throw exc;
    	}*/
    }
    
    private int createShader(String data, int shaderType) throws Exception {
    	int shader = 0;
    	try {
	        shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
	        
	        if(shader == 0)
	        	return 0;
	        
	        ARBShaderObjects.glShaderSourceARB(shader, data);
	        ARBShaderObjects.glCompileShaderARB(shader);
	        glCompileShader(shader);
	        
	        if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
	            throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
	        
	        return shader;
    	}
    	catch(Exception exc) {
    		ARBShaderObjects.glDeleteObjectARB(shader);
    		throw exc;
    	}
    }
    
    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
    
}
