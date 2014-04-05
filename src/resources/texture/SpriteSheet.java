package resources.texture;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class SpriteSheet {
	
	public SpriteSheet(){
		SAXBuilder saxBuilder = new SAXBuilder();  
		       
		// obtain file object   
		File file = new File("res\\textures\\smiley.ss");  
		  
		try {  
			// converted file to document object  
			Document document = saxBuilder.build(file);  
			     
			// get root node from xml  
			Element rootNode = document.getRootElement();  
			
			// got all xml elements into a list  
			List<Element> stuff = rootNode.getChildren();
			        
			for(int i=0;i<=stuff.size()-1;i++){  
				Content element = stuff.get(i);  
				System.out.println("Is " + element.getValue());
			}  
			       
		} catch (JDOMException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		} catch (IOException e) {  
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		}    
	}

}
