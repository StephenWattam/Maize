package maize.ui;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
public class BotTileSet{

	public BufferedImage botN;
	public BufferedImage botE;
	public BufferedImage botS;
	public BufferedImage botW;

	public BotTileSet(BufferedImage botN){
		this.botN = botN;
		this.botE = createRotatedCopy(botN, 1);
		this.botW = createRotatedCopy(botN, 2);
		this.botS = createRotatedCopy(botN, 3);
	}

	// Rotate the bot image to form 4 images, north south east west.
	private BufferedImage createRotatedCopy(BufferedImage img, int rotationMode){
		int w = img.getWidth();
		int h = img.getHeight();

		BufferedImage output = new BufferedImage(h, w, img.getType());

		double theta;
		switch(rotationMode){
			case 1:
				theta = Math.PI/2;
				break;
			case 2:
				theta = -Math.PI/2;
				break;
			default:
				theta = Math.PI;
		}

		AffineTransform xform = AffineTransform.getRotateInstance(theta, w / 2, h / 2);
		Graphics2D g = (Graphics2D) output.createGraphics();
		g.drawImage(img, xform, null);
		g.dispose();


		return output;
	}
}	

