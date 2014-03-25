package blender.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class TextureFile {
	
	private ByteBuffer buffer;
	private BufferedImage image;
	
	
    public TextureFile(ByteBuffer buffer, BufferedImage image) {
		super();
		this.buffer = buffer;
		this.image = image;
	}
    
	public ByteBuffer getBuffer() {
		return buffer;
	}


	public BufferedImage getImage() {
		return image;
	}


	public static TextureFile loadTextureFile(String name){
        // Load the image
        BufferedImage bimg = null;
        try
        {	
          File f = new File(name);
          bimg = ImageIO.read(f);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Unable to load texture file: " + name);
        }

        // Gather all the pixels
        int[] pixels = new int[bimg.getWidth() * bimg.getHeight()];
        bimg.getRGB(0, 0, bimg.getWidth(), bimg.getHeight(), pixels, 0, bimg.getWidth());

        // Create a ByteBuffer
        ByteBuffer buffer = BufferUtils.createByteBuffer(bimg.getWidth() * bimg.getHeight() * 4);

        // Iterate through all the pixels and add them to the ByteBuffer
        for (int y = 0; y < bimg.getHeight(); y++)
        {
            for (int x = 0; x < bimg.getWidth(); x++)
            {
                // Select the pixel
                int pixel = pixels[y * bimg.getWidth() + x];
                // Add the RED component
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                // Add the GREEN component
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                // Add the BLUE component
                buffer.put((byte) (pixel & 0xFF));
                // Add the ALPHA component
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        // Reset the read location in the buffer so that GL can read from
        // beginning.
        buffer.flip();
        return new TextureFile(buffer, bimg);
    }

}
