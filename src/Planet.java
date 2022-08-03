
/*********
 * @author Yarema Dzulynsky
 * @dueDate May 27, 2022
 * @description This class is meant to be used as an object class for a planet object. It does
 * all the necessary calculations and rendering in order for the general laws of physics
 * to apply within the simulation. It is essentially the brains of the operation allowing for complex
 * cenarios to be processed. 
 *********/

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Planet {
    PApplet sketch;
    int RGB[] = {256, 0, 0};

    //Unique Planet Vectors
    PVector position = new PVector(),
            velocity = new PVector(),
            acceleration = new PVector(),
            mouseVectorTwo = new PVector(),
            mouseVectorFinal = new PVector(),
            gravity = new PVector(0, (float) 3),
            momentum = new PVector();

    //Whether the current planet has ever been rendered
    boolean activated = false;


    //general static Planet information
    float radius,
            mass,
            vLimit = 50,
            aLimit = 10;
    double planetFriction = 1.05,
            wallFriction = 1.05;


    /**
     * This is a constructor which creates a planet object.
     *
     * @param sketch_   - The PApplet object which is running the frame. This is
     *                  used to modify the frame within the planet class.
     * @param position_ - The position of the new planet.
     * @param radius_   - The radius of the planet.
     * @param gravity_  - Whether gravity is on or off.
     */
    Planet(PApplet sketch_, PVector position_, float radius_, boolean gravity_) {

        this.sketch = sketch_;

        this.position = position_;

        this.radius = radius_;

        this.mass = (PApplet.PI * radius_) / 1000;

        this.RGB[0] = (int) sketch.random(0, 255);
        this.RGB[1] = (int) sketch.random(0, 255);
        this.RGB[2] = (int) sketch.random(0, 255);

        if (gravity_) {

            this. acceleration.add(gravity);

        }

    }

    // VISUAL COMPONENT

    /**
     * This method visually renders all planets within the planets array.
     *
     * @param planets       - The array of planets that shall be rendered.
     * @param freeze_       - whether the planets movement should be frozen.
     * @param gravity_      - Whether gravity is enabled.
     * @param visualDetail_ - Whether velocity vectors are visible.
     */
    public static void renderAll(ArrayList<Planet> planets, boolean freeze_, boolean gravity_, boolean visualDetail_) {

        if (freeze_) {
            planets.stream().forEach(planet -> planet.freezeRender(visualDetail_));
        } else {
            planets.stream().forEach(planet -> planet.render(gravity_, visualDetail_));
        }

    }

    /**
     * A method to render a single planet into a frame.
     *
     * @param gravity_      - Whether gravity should be enabled.
     * @param visualDetail_ - Whether velocity vectors should be displayed.
     */
    void render(boolean gravity_, boolean visualDetail_) {

        update(gravity_);
        sketch.fill(RGB[0], RGB[1], RGB[2]);
        sketch.stroke(RGB[0], RGB[1], RGB[2]);
        sketch.ellipse(position.x, position.y, 2 * radius, 2 * radius);

        if (visualDetail_) {
            sketch.strokeWeight(2);
            sketch.line(this.position.x, this.position.y, (this.position.x + 10 * this.velocity.x), (this.position.y + 10 * this.velocity.y));
            sketch.strokeWeight(1);
        }

        activated = true;

    }

    /**
     * A method to freeze the movement of balls and render them.
     *
     * @param visualDetail_ - Whether the velocity vectors should be displayed
     */
    void freezeRender(boolean visualDetail_) {

        sketch.fill(RGB[0], RGB[1], RGB[2]);
        sketch.stroke(RGB[0], RGB[1], RGB[2]);
        sketch.ellipse(position.x, position.y, 2 * radius, 2 * radius);

        if (visualDetail_) {
            sketch.strokeWeight(2);
            sketch.line(this.position.x, this.position.y, (this.position.x + 10 * this.velocity.x), (this.position.y + 10 * this.velocity.y));
            sketch.strokeWeight(1);
        }

        activated = true;

    }

    /**
     * A method to globally set the color of the planets
     *
     * @param r - R color value.
     * @param g - G color value.
     * @param b - B color value.
     */
    void setColour(int r, int g, int b) {

        RGB[0] = r;
        RGB[1] = g;
        RGB[2] = b;

    }

    // DYNAMICS

    /**
     * This method calculates the position of the planet using its acceleration and
     * velocity.
     *
     * @param gravity_ - Whether gravity is enabled or not.
     */
    void update(boolean gravity_) {

        if (gravity_) {
            acceleration.set(gravity.copy());

        } else {
            acceleration.set(new PVector(0, 0));

        }

        velocity.add(acceleration);
        position.add(velocity);
        velocity.limit(vLimit);
        acceleration.limit(aLimit);

    }

    /**
     * This method checks whether 2 planets are colididng and if so calculates final
     * velocities.
     *
     * @param c - The other planet which should be compared too.
     */
    void enforceColisions(Planet c) {

        float dist = PVector.sub(position, c.position).mag();
        float sumOfRadei = c.radius + this.radius;

        //If the 2 Planets are colliding
        if (dist <= sumOfRadei) {

            PVector sumOfRadeiVect1 = (PVector.sub(this.position, c.position)).setMag(sumOfRadei);
            PVector sumOfRadeiVect2 = (PVector.sub(c.position, this.position)).setMag(sumOfRadei);

            //Using conservation of momentum for final velocities
            PVector v2f = velocity((new PVector(this.velocity.x, this.velocity.y)).mult(this.mass), c.mass);
            PVector v1f = velocity((new PVector(c.velocity.x, c.velocity.y)).mult(c.mass), this.mass);

            //Setting new final velocities
            this.velocity = new PVector(v1f.x, v1f.y);
            c.velocity = new PVector(v2f.x, v2f.y);


            //Reseting positions to prevent ball tunneling
            if (c.position.x < this.position.x) {

                this.position.x = c.position.x + sumOfRadeiVect1.x;
                c.position.x = this.position.x + sumOfRadeiVect2.x;

            }
            if (this.position.x < c.position.x) {

                this.position.x = c.position.x + sumOfRadeiVect1.x;
                c.position.x = this.position.x + sumOfRadeiVect2.x;

            }
            if (this.position.y < c.position.y) {

                this.position.y = c.position.y + sumOfRadeiVect1.y;
                c.position.y = this.position.y + sumOfRadeiVect2.y;

            }
            if (this.position.y > c.position.y) {

                this.position.y = c.position.y + sumOfRadeiVect1.y;
                c.position.y = this.position.y + sumOfRadeiVect2.y;

            }

        }

        //to track efficiency of the algorithm vs brute force
        MainClass.calculationsComplete++;

    }

    /**
     * This method uses nodes for efficient colision calculation.
     *
     * @param primeNode   - A list of nodes without children.
     * @param specialCase - Whether the program should check surounding nodes as
     *                    well as current node.
     */
    public static void enforceColisions(ArrayList<qNode> primeNode, boolean specialCase) {

        primeNode
                .stream()
                .forEach(node -> {
            if (specialCase) {
                node.specialColisionCase();
            } else {
                enforceColision(node.planets);
            }
        });

    }

    /**
     * This method takes 2 lists of planets and runs colisions detection on them.
     *
     * @param planets1 - A list of planets.
     * @param planets2 - Another list of planets.
     */
    public static void enforceColision(ArrayList<Planet> planets1, ArrayList<Planet> planets2) {

        ArrayList<Planet> planets = new ArrayList<Planet>();
        planets.addAll(planets1);
        planets.addAll(planets2);

        for (int x = 0; x < planets.size(); x++) {

            for (int y = x + 1; y < planets.size(); y++) {

                planets.get(x).enforceColisions(planets.get(y));

            }
        }
    }

    /**
     * This method takes in a list of planets and runs colision detection on them.
     *
     * @param planets - A list of planets.
     */
    public static void enforceColision(ArrayList<Planet> planets) {

        for (int x = 0; x < planets.size(); x++) {

            for (int y = x + 1; y < planets.size(); y++) {

                planets.get(x).enforceColisions(planets.get(y));

            }

        }

    }

    /**
     * This method takes in momentum and mass to calculate velocity.
     *
     * @param momentum - Momentum vector of the planet.
     * @param mass     - The mass of the planet.
     * @return A velocity value.
     */
    static PVector velocity(PVector momentum, float mass) {

        return momentum.copy().div(mass);

    }

    /**
     * This method takes in a list of planets and makes sure that they dont leave
     * the frame.
     *
     * @param sketch  - The PApplet object which is running the frame. This is used
     *                to modify the frame within the planet class.
     * @param planets - A list of planets.
     */
    public static void enforceBounds(PApplet sketch, ArrayList<Planet> planets) {

        for (int n = 0; n < planets.size(); n++) {

            //Creating local position variables
            //for position changes instead
            //of using the PVector object
            //(much less prone to bugs this way)
            float px = planets.get(n).position.x;
            float py = planets.get(n).position.y;
            float vx = planets.get(n).velocity.x;
            float vy = planets.get(n).velocity.y;

            //Checking if the planets are touching the bounds
            if (px < 0 + planets.get(n).radius) {

                vx = vx * -1;

                //Reseting positions to avoid tunneling through walls
                px = 0 + planets.get(n).radius;
                planets.get(n).velocity.x = (float) (vx / planets.get(n).wallFriction);

            }
            if (py < 0 + planets.get(n).radius) {

                vy = vy * -1;
                py = 0 + planets.get(n).radius;
                planets.get(n).velocity.y = (float) (vy / planets.get(n).wallFriction);

            }
            if (px > sketch.width - planets.get(n).radius) {

                vx = vx * -1;
                px = sketch.width - planets.get(n).radius;
                planets.get(n).velocity.x = (float) (vx / planets.get(n).wallFriction);

            }
            if (py > sketch.height - planets.get(n).radius) {

                vy = vy * -1;
                py = sketch.height - planets.get(n).radius;
                planets.get(n).velocity.y = (float) (vy / planets.get(n).wallFriction);

            }

            //After manipulating the local variables
            //set the PVector values to the local vars
            planets.get(n).position.x = px;
            planets.get(n).position.y = py;

        }

    }

    /**
     * This method creates unique planets with random velocities and positions.
     *
     * @param sketch          - The PApplet object which is running the frame. This
     *                        is used to modify the frame within the planet class.
     * @param numberOfPlanets - The amount of planets requested.
     * @param size            - The size of the planets requested.
     * @param maxSpeed        - The maximum speed of the planets.
     * @param gravity_        - Whether gravity is on or off.
     * @param atMousePos_     - the position of the mouse.
     * @return An array of unique planets.
     */
    public static ArrayList<Planet> createUniquePlanets(PApplet sketch, int numberOfPlanets, int size, int maxSpeed,
                                                        boolean gravity_, boolean atMousePos_) {

        ArrayList<Planet> placeHolder = new ArrayList<Planet>();

        if (atMousePos_) {
            for (int n = 0; n < numberOfPlanets; n++) {
                placeHolder.add(n, (new Planet(sketch, new PVector(sketch.mouseX, sketch.mouseY), size, gravity_)));
                placeHolder.get(n).setVelocity(
                        new PVector(sketch.random(0 - maxSpeed, maxSpeed), sketch.random(0 - maxSpeed, maxSpeed)));
            }
        } else {
            for (int n = 0; n < numberOfPlanets; n++) {

                placeHolder.add(n, (new Planet(sketch,
                        new PVector(sketch.random(0, sketch.width), sketch.random(0, sketch.height)), size, gravity_)));
                placeHolder.get(n).setVelocity(
                        new PVector(sketch.random(0 - maxSpeed, maxSpeed), sketch.random(0 - maxSpeed, maxSpeed)));

            }
        }
        return placeHolder;

    }

    /**
     * This method sets the velocity of a planet
     *
     * @param velocity_ - The requested velocity of the planet.
     */
    void setVelocity(PVector velocity_) {

        activated = true;
        velocity = velocity_;

    }

}
