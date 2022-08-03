
/*********
 * @author Yarema Dzulynsky
 * @dueDate May 27, 2022
 * @description This class is simply to create an object to hold position information
 * for each qNode. It is also used to visualize the qNode tree. 
 *********/
import processing.core.PApplet;

public class Rectangle {

	double x, y, height, width;
	PApplet app;

	/**
	 * Constructor to make a rectangle.
	 * @param app_ - The PApplet object which is running the frame. This is
	 *                  used to modify the frame within the planet class.
	 * @param x	- The x position of the middle of the rectangle
	 * @param y- The y position of the middle of the rectangle
	 * @param width - The width of rectangle
	 * @param height- The height of rectangle
	 */
	public Rectangle(PApplet app_, double x, double y, double width, double height) {

		this.app = app_;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

	}

	/**
	 * This method draws a visual representation of each node.
	 * @param r - Char r for red ink.
	 * @param g - Char g for green ink.
	 * @param b - Char b for blue ink.
	 * @param weight
	 */
	void centreRect(int r, int g, int b, int weight) {

		app.stroke(r, g, b);
		app.noFill();
		app.strokeWeight(weight);
		app.rectMode(app.CENTER);

		app.rect((float) this.x, (float) this.y, (float) this.width * 2, (float) this.height * 2);

	}

}