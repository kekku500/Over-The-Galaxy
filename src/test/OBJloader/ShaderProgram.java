package test.OBJloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
	
	int programID;
	int vertexShaderID;
	int fragmentShaderID;
	
	public ShaderProgram(){
		programID = glCreateProgram();
	}
	
	public void attachVertexShader(String name){
		String vertexShaderSource = ShaderProgram.readFromFile(name);
		
		vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShaderID, vertexShaderSource);
		
		glCompileShader(vertexShaderID);
		
		if(glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE){
			dispose();
		}
		
		glAttachShader(programID, vertexShaderID);
	}
	
	public void attachFragmentShader(String name){
		String fragmentShaderSource = ShaderProgram.readFromFile(name);
		
		fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderID, fragmentShaderSource);
		
		glCompileShader(fragmentShaderID);
		
		if(glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE){
			dispose();
		}
		
		glAttachShader(programID, fragmentShaderID);
	}
	
	public void setUniform(String name, float value)
    {
        glUniform1f(glGetUniformLocation(programID, name), value);
    }
	
	public void link(){
		glLinkProgram(programID);
		
		if(glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE){
			dispose();
		}
	}
	
	public void bind(){
		glUseProgram(programID);
	}
	
	public static void unbind(){
		glUseProgram(0);
	}
	
	public void dispose(){
		unbind();
		
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		
		glDeleteProgram(programID);
	}
	
	public static String readFromFile(String name){
		StringBuilder source = new StringBuilder();
		try{
			File f = new File("src\\resources\\" + name);

				        FileInputStream fis = new FileInputStream(f);

						BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

			
			String line;
			while((line = reader.readLine()) != null){
				source.append(line).append("\n");
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return source.toString();
	}
}
