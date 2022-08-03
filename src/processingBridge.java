import processing.core.PApplet;

public class processingBridge {

	public static void main(String[] args) {

		String[] processingArgs = { "MainClass" };
		MainClass mySketch = new MainClass();
		PApplet.runSketch(processingArgs, mySketch);

	}

}
