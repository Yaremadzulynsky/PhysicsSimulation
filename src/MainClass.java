
/*********
 * @author Yarema Dzulynsky
 * @dueDate May 27, 2022
 * @description This class is the main class in which the settings, setup and draw methods 
 * are added to in order to satisfy Proccessings requirments. This class also implements 
 * hotkeys and the creation of a information JFrame
 *********/

import processing.core.PApplet;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MainClass extends PApplet {

	// parent node
	qNode parent;
	// max planets per node
	int capacity = 2;

	ArrayList<Planet> planets = new ArrayList<Planet>();
	ArrayList<qNode> primeNode = new ArrayList<qNode>();

	// starting conditions of simulation
	int planetNumber = 1000,
			planetRadius = 3,
			planetMaxVel = 3;

	long pausedFrames = 0;

	// information
	public JLabel simulatedConsole;

	// boolean logic
	boolean developerMode = false,
			gravity = false,
			efficient = true,
			accountForSpecialCase = true,
			freezeRender = false,
			visualDetail = false,
			previewPlanet = true,
			bypassUI = true;

	// calculation tracker for efficiency information
	public static long calculationsComplete = 0;

	public void settings() {

		size(800, 800);

	}

	/**
	 * Setup
	 */
	public void setup() {

		buildInfoFrame();

		if (!bypassUI) {
			Scanner in = new Scanner(System.in);

			System.out.println("Number of starting planets?");
			planetNumber = in.nextInt();
			System.out.println("Radius of starting planets?");
			planetRadius = in.nextInt();
			System.out.println("Maximum velocity of starting planets?");
			planetMaxVel = in.nextInt();
		}

		initializeVariables();
		initializeObjects();
		background(255);

	}

	/**
	 * Continuous draw loop
	 */
	public void draw() {

		reInitialize();
		createVisuals();

	}

	/**
	 * Create any objects necessary for the program.
	 */
	void initializeObjects() {

		planets = Planet.createUniquePlanets(this, planetNumber, planetRadius, planetMaxVel, gravity, false);

	}

	/**
	 * Initialize any variables necessary for the program
	 */
	void initializeVariables() {

		parent = new qNode(this, null, new Rectangle(this, 0, 0, width, height), 2);

	}

	/**
	 * Continuously initiate variables every frame.
	 */
	void reInitialize() {

		if (!freezeRender) {

			enforcePhysics();
			previewPlanet();

			if (efficient) {

				primeNode.clear();
				parent = new qNode(this, null, new Rectangle(this, 0, 0, width, height), capacity);
				parent.insert(planets, primeNode);
				Planet.enforceColisions(primeNode, accountForSpecialCase);

			} else {

				Planet.enforceColision(planets);

			}
		} else {

			pausedFrames++;

		}

		background(255);

	}

	/**
	 * An estimation of calculations per frame required with brute force collision
	 * detection.
	 * 
	 * @param numberOfPlanets - The amount of planets in frame.
	 * @return estimated calculations per frame.
	 */
	long getM(int numberOfPlanets) {

		return (31365 * numberOfPlanets);

	}

	/**
	 * Create any extra visuals that apear on the screen.
	 */
	void createVisuals() {

		Planet.renderAll(planets, freezeRender, gravity, visualDetail);

		previewPlanet();

		if (developerMode) {

			parent.show();
			Planet.enforceBounds(this, planets);

		}

	}

	/**
	 * Enforce boundaries.
	 */
	void enforcePhysics() {

		Planet.enforceBounds(this, planets);

	}

	/**
	 * 
	 * @return - The predicted amount of colisions per frame using brute force
	 *         methods.
	 */
	long bruteForceColisions() {

		return (long) ((sq(planetNumber) / 2));

	}

	/**
	 * 
	 * @return - The predicted amount of colisions per frame using my algorithm.
	 */
	long algorithmicColisions() {

		return (long) (calculationsComplete / (frameCount - pausedFrames));

	}

	/**
	 * hotKeyAction
	 */
	public void keyPressed() {

		if (key == 'e') {

			eClicked();

		}
		if (key == 'v') {

			vClicked();

		}
		if (key == 'g') {

			gClicked();

		}
		if (key == 'i') {

			iClicked();

		}
		if (key == 'd') {

			dClicked();

		}
		if (keyCode == ENTER) {

			enterClicked();

		}
		if (key == 'r') {

			rClicked();

		}
		if (keyCode == UP) {

			upClicked();

		}
		if (keyCode == DOWN) {

			downClicked();

		}
		if (keyCode == RIGHT) {

			rightClicked();

		}
		if (keyCode == LEFT) {

			leftClicked();

		}
		if (key == ' ') {

			spaceClicked();

		}

	}

	/**
	 * previewPlanet
	 */
	void previewPlanet() {

		if (previewPlanet && freezeRender) {

			ellipse(mouseX, mouseY, planetRadius * 2, planetRadius * 2);

		}

	}

	/**
	 * engageGravity
	 */
	void gClicked() {

		if (gravity) {

			gravity = false;

		} else {

			gravity = true;

		}

	}

	/**
	 * increaseMaxVelocity
	 */
	void rightClicked() {

		planetMaxVel++;

	}

	/**
	 * decreaseMaxVelocity
	 */
	void leftClicked() {

		if (planetMaxVel != 0) {

			planetMaxVel--;

		}

	}

	/**
	 * pause
	 */
	void spaceClicked() {

		if (freezeRender) {

			freezeRender = false;

		} else {

			freezeRender = true;

		}

	}

	/**
	 * developerMode
	 */
	void developerMode() {

		if (developerMode) {

			developerMode = false;

		} else {

			developerMode = true;

		}

	}

	/**
	 * visualDetail
	 */
	void vClicked() {

		if (visualDetail) {

			visualDetail = false;

		} else {

			visualDetail = true;

		}

	}

	/**
	 * showNodes
	 */
	// Hotkeys

	// developerMode
	void dClicked() {

		developerMode();

	}

	// newPlanet

	/**
	 * newPlanet
	 */
	void enterClicked() {
		planets.addAll(Planet.createUniquePlanets(this, 1, planetRadius, planetMaxVel, gravity, true));

	}

	// resetCanvas
	/**
	 * clearCanvas
	 */
	void rClicked() {

		planets.clear();
		primeNode.clear();

	}

	/**
	 * increaseRadius
	 */
	void upClicked() {

		planetRadius++;

	}

	/**
	 * decreaseRadius
	 */
	void downClicked() {

		if (planetRadius != 0) {

			planetRadius--;

		}

	}

	/**
	 * efficientAlgorithm
	 */
	void eClicked() {

		if (efficient) {

			efficient = false;

		} else {

			efficient = true;

		}

	}

	/**
	 * Information
	 */
	void iClicked() {

		simulatedConsole.setText("<html><h3>Developer Mode: " + developerMode + "<br>" + "Planets: " + planets.size()
				+ "<br>" + "Size: " + planetRadius + "<br>" + "Maxiumum Velocity: " + planetMaxVel + "<br>"
				+ "Efficient Mode: " + efficient + "<br>" + /* + "Efficiency: "
				+ bruteForceColisions() / algorithmicColisions() + "x more efficient vs brute force*/"<h3><html>");
	}

	/**
	 * Method to create JFrame displaying important information regarding the
	 * simulation
	 */
	void buildInfoFrame() {

		JFrame frame = new JFrame();
		JPanel panel = new JPanel();

		JLabel ledgend = new JLabel();
		JLabel ledgendTitle = new JLabel();
		simulatedConsole = new JLabel();
		JLabel simulatedConsoleTitle = new JLabel();
		ledgendTitle.setText("<html> <h1> Ledgend <h1> <html>");

		ledgend.setText("<html> <h3>" + "d: Toggle developer mode (subdivision visibility).<br>"
				+ "e: Toggle efficient vs brute force colision algorithm.<br>" + "i: General information.<br>"
				+ "v: Toggle velocity vectors (visibility).<br>"
				+ "r: Reset simulation (delete all planets, create <br> blank simulation).<br>"
				+ "g: Toggle gravity.<br>" + "<br>"
				+ "SPACE: Pause simulation<br>" + "ENTER: Insert new planet at mouse position.<br>"
				+ "RIGHT: Increase starting planet velocity.<br>" + "LEFT: Decrease starting planet velocity.<br>"
				+ "UP: Increase starting planet radius.<br>" + "DOWN: Decrease starting planet radius.<br>"
				+ "<h3><html>");

		simulatedConsoleTitle.setText("<html><h1>Information<h1><html>");
		simulatedConsole.setText("<html><h3> - Press \"i\" to populate data.<h3><html>");

		panel.add(ledgendTitle, JPanel.CENTER_ALIGNMENT);
		panel.add(ledgend, JPanel.CENTER_ALIGNMENT);
		panel.add(simulatedConsoleTitle, JPanel.CENTER_ALIGNMENT);
		panel.add(simulatedConsole, JPanel.CENTER_ALIGNMENT);
		frame.pack();
		frame.add(panel);
		frame.setBounds(0, 0, 400, 800);
		frame.setVisible(true);

	}
}
