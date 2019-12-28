import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 *  
 * @author Vlado Vojdanovski
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;	// the resulting masterpiece
	private ArrayList<Point> pointToColor = new ArrayList<Point>(); // points to be painted

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) 
	{
		// TODO: YOUR CODE HERE
		if (displayMode == 'w') // if we want to display the webcam
		{
			super.draw(g);
		}
		else if (displayMode == 'r') // if we want to do the recolored image
		{
			if (targetColor != null) // if we have a target color
			{
				finder.recolorImage(); // recolor the image
				g.drawImage(finder.getRecoloredImage(), 0, 0, null); // draw that image
			}
			else 
			{
				super.draw(g);
			}
		} 
		else if (displayMode == 'p')
		{
			for (Point point: pointToColor)  // for all the points in our list
			{
				image.setRGB(point.x, point.y, paintColor.getRGB()); // paint them blue
			}
			g.drawImage(painting, 0, 0, null); // draw our latest painting
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage()
	{
		if ((targetColor != null) && (displayMode != 'w'))
		{
			finder = new RegionFinder(image); // initialize finder
				// TODO: YOUR CODE HERE
			finder.findRegions(targetColor); // find all the regions of the desired color
			painting = image; // set painting to our image
			if (finder.size>0) // if we have a largest region(size represents the number of regions we have)
			{
				for (Point point: finder.largestRegion()) // for all points
					{
						painting.setRGB(point.x, point.y, paintColor.getRGB()); // paint the image
						pointToColor.add(point); // add our point to the point list
					}
			}
		 }
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// select the color at the mouse press
		targetColor = new Color(image.getRGB(x, y));
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
