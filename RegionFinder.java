import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 *  
 * @author Vlado Vojdanovski
 */
public class RegionFinder {
	private static final double maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;     						// the image in which to find regions
	protected int size; 										// the size of the regions array
	private BufferedImage recoloredImage;                   // the image with identified regions recolored
	
	
	
	private ArrayList<ArrayList<Point>> regions = new ArrayList<ArrayList<Point>>();			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
		this.size = 0;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;	
		this.size = 0;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) 
	{
		// create a new blanc image
		BufferedImage blanco = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < image.getWidth(); x++) // go through all the pixels in the image
		{
			for (int y = 0; y < image.getHeight(); y++)
			{
				// if the pixel has not been visited and is the wanted color
				if ((blanco.getRGB(x, y) == 0) && colorMatch(new Color(image.getRGB(x,y)), targetColor))
				{
					ArrayList<Point> searchList = new ArrayList<Point>(); // create a new deque
					ArrayList<Point> region = new ArrayList<Point>(); // create a new region
					Point firstPoint = new Point(x,y); // create a new point
					region.add(firstPoint); // add this point to the region
					searchList.add(firstPoint); // add it to the deque as well
					blanco.setRGB(x, y, 1);
					
					// while our deque is not empty
					while (searchList.size() > 0)
					{
						for (int i=-1; i<=1; i++)
						{
							for (int j=-1; j<=1; j++)
							{
								if (firstPoint.x + i <0) firstPoint.x = firstPoint.x+1; // if we go off the screen to the left, increment x by 1
								if (firstPoint.y + j <0) firstPoint.y = firstPoint.y+1;
								if (firstPoint.x + i >image.getWidth() -1 ) firstPoint.x = firstPoint.x-1; //vice versa
								if (firstPoint.y + j >image.getHeight() -1) firstPoint.y = firstPoint.y -1;
								if (colorMatch(new Color(image.getRGB(firstPoint.x + i, firstPoint.y +j)), targetColor) && blanco.getRGB(firstPoint.x + i, firstPoint.y +j) == 0)
								 { // if the color is correct AND the item is not in the region already
								        blanco.setRGB(firstPoint.x + i, firstPoint.y + j, 1); 
								        // change the color of the blank image to white
								        region.add(new Point(firstPoint.x + i, firstPoint.y +j ));
								        // add this pixel to our region
								        searchList.add(new Point(firstPoint.x +i, firstPoint.y +j));
								        // add this pixel to the search list
								 }
							}
						}
						searchList.remove(0); // remove the first image from the searchlist
						if (searchList.size() > 0) 
							{
								firstPoint = new Point((int)(searchList.get(0).getX()), (int)(searchList.get(0).getY()));
								// assign a new point to analyze
							}
				    }	
					   if (region.size() >= minRegion) 
						   {
						   regions.add(region);
						   size+=1;
						   }
					   // add the region if the size is big enough
				}	
			}
		}
	}


	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		if ((Math.abs(c1.getRed() - c2.getRed()) < maxColorDiff) && (Math.abs(c1.getBlue() - c2.getBlue()) < maxColorDiff)
				&& (Math.abs(c1.getGreen() - c2.getGreen()) < maxColorDiff)) 
			{
			return true;
			}
		else 
			{
				return false;
			}
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		if (regions.size() == 0)
			{
			return null;
			}
		else
		{
			ArrayList<Point> max = regions.get(0);
			for (ArrayList<Point> currentRegion: regions)
			{
				if(currentRegion.size() > max.size()) max = currentRegion;
			}
			return max;
		}
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		for (ArrayList<Point> region: regions)
		{
			int randomColor = (int)(Math.random()*16777217);
			for (Point point: region)
			{
				recoloredImage.setRGB(point.x, point.y, randomColor);
			}
		}
	}
}
