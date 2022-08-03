/*********
 * @author Yarema Dzulynsky
 * @dueDate May 27, 2022
 * @description This class was a brilliant exersize in patience for me existing in order for the  
 * colision mechanics to use as little computational power as possible (as I can - theres 
 * definetally a more efficient method but this is mine). This is achieved using a quadTree data 
 * structure which partitions the frame into squares to knock out unneccesary calculation.
 *********/

import processing.core.PApplet;

import java.util.ArrayList;

public class qNode extends PApplet {

    ArrayList<Planet> planets = new ArrayList<Planet>();
    boolean divided = false;

    //Conneting the qNode class to the frame
    //in the MainClass class
    PApplet app;

    //Creating an empty object for qNode location information
    Rectangle boundary;

    //Max amount of planets per node
    int capacity;

    //Surrounding nodes
    qNode topLeft, topRight, bottomLeft, bottomRight, parent;

    //Visual attributes of the nodes
    int RGB[] = {0, 0, 0};
    int stroke;

    /**
     * This is the constructor for a qNode object.
     *
     * @param app_      - The PApplet object which is running the frame. This is
     *                  used to modify the frame within the planet class.
     * @param parent_   - The parent node.
     * @param boundary_ - The rectangle containing the bounds of the node.
     * @param capacity_ - the maximum capacity of planets per node.
     */
    public qNode(PApplet app_, qNode parent_, Rectangle boundary_, int capacity_) {

        this.boundary = boundary_;
        this.capacity = capacity_;
        this.app = app_;
        this.parent = parent_;

    }

    /**
     * This is the constructor for a qNode object that creates a certain amount of
     * layers. This is used mostly for diagnostic purposes and testing.
     *
     * @param app_      - The PApplet object which is running the frame. This is
     *                  used to modify the frame within the planet class.
     * @param parent_   - The parent node.
     * @param boundary_ - The rectangle containing the bounds of the node.
     * @param capacity_ - the maximum capacity of planets per node.
     * @param layers    - The amount of layers of the data structure requested.
     */
    public qNode(PApplet app_, qNode parent_, Rectangle boundary_, int capacity_, int layers) {

        this.boundary = boundary_;
        this.capacity = capacity_;
        this.app = app_;
        this.parent = parent_;

    }

    // DIAGNOSTIC TOOLS

    /**
     * This method builds a certain amount of layers of node requested.
     *
     * @param layers_ - How many layers of nodes.
     */
    void buildLayer(int layers_) {

        double x = this.boundary.x;
        double y = this.boundary.y;
        double w = this.boundary.width;
        double h = this.boundary.height;

        if (layers_ > 0) {
            (this.topRight = new qNode(app, this, new Rectangle(app, x + w / 2, y - h / 2, w / 2, h / 2), capacity, layers_ - 1)).buildLayer(layers_ - 1);
            (this.topLeft = new qNode(app, this, new Rectangle(app, x - w / 2, y - h / 2, w / 2, h / 2), capacity, layers_ - 1)).buildLayer(layers_ - 1);
            (this.bottomRight = new qNode(app, this, new Rectangle(app, x + w / 2, y + h / 2, w / 2, h / 2), capacity, layers_ - 1)).buildLayer(layers_ - 1);
            (this.bottomLeft = new qNode(app, this, new Rectangle(app, x - w / 2, y + h / 2, w / 2, h / 2), capacity, layers_ - 1)).buildLayer(layers_ - 1);
        }

        divided = true;

    }

    /**
     * This method displays the node onto the frame.
     */
    void show() {

        app.noFill();

        //Visualizing each qNode using recursion
        if (topLeft != null) {

            topLeft.boundary.centreRect(0, 0, 0, 1);
            topLeft.show();

        }

        if (topRight != null) {

            topRight.boundary.centreRect(0, 0, 0, 1);
            topRight.show();

        }

        if (bottomRight != null) {

            bottomRight.boundary.centreRect(0, 0, 0, 1);
            bottomRight.show();

        }

        if (bottomLeft != null) {

            bottomLeft.boundary.centreRect(0, 0, 0, 1);
            bottomLeft.show();

        }

        //Terminating case
        this.boundary.centreRect(0, 0, 0, 1);

    }

    /**
     * This is a diagnostic method used to highlight neigbouring nodes with planets
     * in them for colisions.
     *
     * @param center - The planet in the central node.
     * @param nodes  - A list of the surounding nodes.
     * @param COLOR  - The requested color r, g or b of the highlighted node.
     */
    public static void showCrashProtection(Planet center, ArrayList<qNode> nodes, final char COLOR) {

        nodes.stream().forEach(node -> {
            if (node != null) {

                if ((COLOR == 'r')) {
                    node.boundary.centreRect(256, 0, 0, 5);
                } else if ((COLOR == 'g')) {
                    node.boundary.centreRect(0, 256, 0, 5);
                } else if ((COLOR == 'b')) {
                    node.boundary.centreRect(0, 0, 256, 5);
                } else {
                    node.boundary.centreRect(0, 0, 0, 5);
                }

            }

            if (node != null && node.planets.size() >= 1 && node.planets.get(0) != center) {
                node.boundary.centreRect(256, 0, 0, 5);
                center.enforceColisions(node.planets.get(0));
            }

        });


    }

    // LOGICAL OPPERATORS

    /**
     * This method checks if a planet is in the current node.
     *
     * @param p - A planet.
     * @return - Whether there is planet p in the current node.
     */
    boolean inside(Planet p) {

        boolean east = this.boundary.x - this.boundary.width <= p.position.x;
        boolean west = p.position.x < this.boundary.x + this.boundary.width;
        boolean north = p.position.y <= this.boundary.y + this.boundary.height;
        boolean south = this.boundary.y - this.boundary.height < p.position.y;

        return east && west && north && south;

    }

    /**
     * This method uses surrounding nodes for colision calculations not just central
     * node.
     */
    void specialColisionCase() {

        Planet.enforceColision(this.planets);
        try {
            Planet.enforceColision(this.shiftDown().planets, this.planets);
        } catch (Exception e) {

        }
        try {
            Planet.enforceColision(this.shiftUp().planets, this.planets);
        } catch (Exception e) {

        }
        try {
            Planet.enforceColision(this.shiftLeft().planets, this.planets);
        } catch (Exception e) {

        }
        try {
            Planet.enforceColision(this.shiftRight().planets, this.planets);
        } catch (Exception e) {

        }
        try {
            Planet.enforceColision(this.shiftUp().shiftLeft().planets, this.planets);
        } catch (Exception e) {

        }
        try {
            Planet.enforceColision(this.shiftUp().shiftRight().planets, this.planets);
        } catch (Exception e) {

        }
        try {
            Planet.enforceColision(this.shiftDown().shiftLeft().planets, this.planets);
        } catch (Exception e) {

        }
        try {
            Planet.enforceColision(this.shiftDown().shiftRight().planets, this.planets);
        } catch (Exception e) {

        }

    }

    // TREE NAVIGATION

    /**
     * this method inserts planets into nodes and splits nodes if they have passed
     * planet capacity.
     *
     * @param p         - The planet to be inserted into the qNode structure.
     * @param primeNode - A list of nodes that p is to be inserted into.
     * @return true or false. This return is used for recursion.
     */
    boolean insert(Planet p, ArrayList<qNode> primeNode) {
        int n;

        //This if statment makes sure
        //that there is a parent node
        if (parent != null) {

            for (n = 0; n < parent.planets.size(); n++) {


                //This if statement makes sure that the planet is
                //inside the qNode and the planet passed into the
                //array of planets in the child node is not a copy
                if (!planets.contains(parent.planets.get(n)) && inside(parent.planets.get(n))) {

                    planets.add(parent.planets.get(n));

                }

            }

        }


        //terminating case
        if (!this.inside(p)) {
            return false;
        }


        if (this.planets.size() < this.capacity) {

            this.planets.add(p);
            primeNode.add(this);

            return true;
        } else {

            //If there are more planets than capacity
            //subdivide to make more nodes to support
            //more planets.
            if (!this.divided) {
                subDivide();
            }

            //Inserting
            if (this.topRight.insert(p, primeNode)) {

                return true;
            } else if (this.topLeft.insert(p, primeNode)) {

                return true;
            } else if (this.bottomRight.insert(p, primeNode)) {

                return true;
            } else if (this.bottomLeft.insert(p, primeNode)) {

                return true;
            }//terminating case
            else {
                return false;
            }

        }

    }

    /**
     * A method which takes a list of planets and inserts it into the node structure
     * contained in the primeNode list.
     *
     * @param p         - A list of planets.
     * @param primeNode - A list of nodes.
     */
    void insert(ArrayList<Planet> p, ArrayList<qNode> primeNode) {

        p.stream()
                .forEach(planet -> {
            insert(planet, primeNode);
        });

    }

    /**
     * This method subdivides nodes into 4 equal nodes.
     */
    void subDivide() {

        double x = this.boundary.x;
        double y = this.boundary.y;
        double w = this.boundary.width;
        double h = this.boundary.height;

        this.topRight = new qNode(app, this, new Rectangle(app, x + w / 2, y - h / 2, w / 2, h / 2), capacity);
        this.topLeft = new qNode(app, this, new Rectangle(app, x - w / 2, y - h / 2, w / 2, h / 2), capacity);
        this.bottomRight = new qNode(app, this, new Rectangle(app, x + w / 2, y + h / 2, w / 2, h / 2), capacity);
        this.bottomLeft = new qNode(app, this, new Rectangle(app, x - w / 2, y + h / 2, w / 2, h / 2), capacity);

        divided = true;

    }

    /**
     * This method returns the surounding nodes of a central node.
     *
     * @param q - The central node.
     * @return A list of the surounding nodes.
     */
    public static ArrayList<qNode> surroundingNodes(qNode q) {

        ArrayList<qNode> placeHolder = new ArrayList<qNode>();

        placeHolder.add(q.shiftUp());
        placeHolder.add(q.shiftUpRight());
        placeHolder.add(q.shiftRight());
        placeHolder.add(q.shiftDownRight());
        placeHolder.add(q.shiftDown());
        placeHolder.add(q.shiftDownLeft());
        placeHolder.add(q.shiftLeft());
        placeHolder.add(q.shiftUpLeft());
        placeHolder.add(q);

        return placeHolder;
    }

    /**
     * This method locates the node to the right of the current node.
     */
    qNode shiftRight() {

        ArrayList<qNode> placeHolder = new ArrayList<qNode>();

        String path = "";
        qNode clone;
        qNode current = this;

        //These if statements identify which position
        //the node is in.
        if (current.parent.topLeft == current) {

            placeHolder.add(current.parent.topRight);

        }
        if (current.parent.bottomLeft == current) {

            placeHolder.add(current.parent.bottomRight);

        }
        if (current.parent != null && (current.parent.topRight == current || current.parent.bottomRight == current)) {

            //Create a path throught the current node to the
            //biggest node and hold it in a string
            while (current.parent != null && (current.boundary.x + current.boundary.width) == (current.parent.boundary.x + current.parent.boundary.width)) {
                //
                if (current.parent.topLeft == current) {

                    path = path + "TL";

                }
                if (current.parent.bottomLeft == current) {

                    path = path + "BL";

                }
                if (current.parent.bottomRight == current) {

                    path = path + "BR";

                }

                if (current.parent.topRight == current) {

                    path = path + "TR";

                }

                //continue up the tree
                current = current.parent;

            }
            //When the parent node does not have a parent node
            if (current.parent == null) {
                clone = null;


                //Identifying which position
                //the node is in
            } else if (current == current.parent.topLeft) {

                clone = current.parent.topRight;
            } else if (current == current.parent.bottomLeft) {

                clone = current.parent.bottomRight;
            } else if (current == current.parent.topRight) {

                clone = current.parent.topLeft;
            } else if (current == current.parent.bottomRight) {

                clone = current.parent.bottomLeft;
            } else {
                clone = null;
            }

            //Reverse the process on a mirrored node
            //in order to find the correct node
            while (clone != null && path.length() >= 2) {

                if (path.substring(path.length() - 2, path.length()).compareTo("TL") == 0) {

                    clone = clone.topRight;

                }
                if (path.substring(path.length() - 2, path.length()).compareTo("BL") == 0) {

                    clone = clone.bottomRight;

                }
                if (path.substring(path.length() - 2, path.length()).compareTo("BR") == 0) {

                    clone = clone.bottomLeft;

                }

                if (path.substring(path.length() - 2, path.length()).compareTo("TR") == 0) {

                    clone = clone.topLeft;

                }

                path = path.substring(0, path.length() - 2);

            }

            placeHolder.add(clone);

        }

        //Built as future redundency if I would
        //like to make the program more efficient
        //and intern pull my hair out.
        return placeHolder.get(0);

    }

    /**
     * This method locates the node to the left of the current node.
     */
    qNode shiftLeft() {

        ArrayList<qNode> placeHolder = new ArrayList<qNode>();

        String path = "";
        qNode clone;
        qNode current = this;

        if (current.parent.topRight == current) {

            placeHolder.add(current.parent.topLeft);

        }
        if (current.parent.bottomRight == current) {

            placeHolder.add(current.parent.bottomLeft);

        }
        if (current.parent != null && (current.parent.topLeft == current || current.parent.bottomLeft == current)) {

            while (current.parent != null && (current.boundary.x + current.boundary.width) == (current.parent.boundary.x)) {


                if (current.parent.topLeft == current) {

                    path = path + "TL";

                }
                if (current.parent.bottomLeft == current) {

                    path = path + "BL";

                }
                if (current.parent.bottomRight == current) {

                    path = path + "BR";

                }

                if (current.parent.topRight == current) {

                    path = path + "TR";

                }

                current = current.parent;

            }
            if (current.parent == null) {

                clone = null;

            } else if (current == current.parent.topLeft) {

                clone = current.parent.topRight;
            } else if (current == current.parent.bottomLeft) {

                clone = current.parent.bottomRight;
            } else if (current == current.parent.topRight) {

                clone = current.parent.topLeft;
            } else if (current == current.parent.bottomRight) {

                clone = current.parent.bottomLeft;
            } else {
                clone = null;
            }

            while (clone != null && path.length() >= 2) {

                if (path.substring(path.length() - 2, path.length()).compareTo("TL") == 0) {

                    clone = clone.topRight;

                }
                if (path.substring(path.length() - 2, path.length()).compareTo("BL") == 0) {

                    clone = clone.bottomRight;

                }
                if (path.substring(path.length() - 2, path.length()).compareTo("BR") == 0) {

                    clone = clone.bottomLeft;

                }

                if (path.substring(path.length() - 2, path.length()).compareTo("TR") == 0) {

                    clone = clone.topLeft;

                }

                path = path.substring(0, path.length() - 2);

            }

            placeHolder.add(clone);

        }

        return placeHolder.get(0);

    }

    /**
     * This method locates the node above the current node.
     */
    qNode shiftUp() {

        ArrayList<qNode> placeHolder = new ArrayList<qNode>();

        String path = "";
        qNode clone;
        qNode current = this;

        if (current.parent.bottomRight == current) {

            placeHolder.add(current.parent.topRight);

        }
        if (current.parent.bottomLeft == current) {

            placeHolder.add(current.parent.topLeft);

        }
        if (current.parent != null && (current.parent.topLeft == current || current.parent.topRight == current)) {


            while (current.parent != null && (current.boundary.y + current.boundary.height) == (current.parent.boundary.y)) {


                if (current.parent.topLeft == current) {

                    path = path + "TL";

                }
                if (current.parent.bottomLeft == current) {

                    path = path + "BL";

                }
                if (current.parent.bottomRight == current) {

                    path = path + "BR";

                }

                if (current.parent.topRight == current) {

                    path = path + "TR";

                }

                current = current.parent;

            }
            if (current.parent == null) {

                clone = null;

            } else if (current == current.parent.topLeft) {

                clone = current.parent.bottomLeft;
            } else if (current == current.parent.bottomLeft) {

                clone = current.parent.topLeft;
            } else if (current == current.parent.topRight) {

                clone = current.parent.bottomRight;
            } else if (current == current.parent.bottomRight) {

                clone = current.parent.topRight;
            } else {
                clone = null;
            }

            while (clone != null && path.length() >= 2) {

                if (path.substring(path.length() - 2, path.length()).compareTo("TL") == 0) {

                    clone = clone.bottomLeft;

                }
                if (path.substring(path.length() - 2, path.length()).compareTo("BL") == 0) {

                    clone = clone.topLeft;

                }
                if (path.substring(path.length() - 2, path.length()).compareTo("BR") == 0) {

                    clone = clone.topRight;

                }

                if (path.substring(path.length() - 2, path.length()).compareTo("TR") == 0) {

                    clone = clone.bottomRight;

                }

                path = path.substring(0, path.length() - 2);

            }

            placeHolder.add(clone);

        }

        return placeHolder.get(0);

    }

    /**
     * This method locates the node below the current node.
     */
    qNode shiftDown() {

        ArrayList<qNode> placeHolder = new ArrayList<qNode>();

        String path = "";
        qNode clone;
        qNode current = this;

        if (current.parent != null && current.parent.topRight == current) {

            placeHolder.add(current.parent.bottomRight);

        }
        if (current.parent != null && current.parent.topLeft == current) {

            placeHolder.add(current.parent.bottomLeft);

        }
        if (current.parent != null && current.parent != null && (current.parent.bottomLeft == current || current.parent.bottomRight == current)) {


            while (current.parent != null && (current.boundary.y - current.boundary.height) == (current.parent.boundary.y)) {


                if (current.parent.topLeft == current) {

                    path = path + "TL";

                }
                if (current.parent.bottomLeft == current) {

                    path = path + "BL";

                }
                if (current.parent.bottomRight == current) {

                    path = path + "BR";

                }

                if (current.parent.topRight == current) {

                    path = path + "TR";

                }

                current = current.parent;

            }
            if (current.parent == null) {

                clone = null;

            } else if (current == current.parent.topLeft) {

                clone = current.parent.bottomLeft;
            } else if (current == current.parent.bottomLeft) {

                clone = current.parent.topLeft;
            } else if (current == current.parent.topRight) {

                clone = current.parent.bottomRight;
            } else if (current == current.parent.bottomRight) {

                clone = current.parent.topRight;
            } else {
                clone = null;
            }

            while (clone != null && path.length() >= 2) {

                if (path.substring(path.length() - 2, path.length()).compareTo("TL") == 0) {

                    clone = clone.bottomLeft;

                }
                if (path.substring(path.length() - 2, path.length()).compareTo("BL") == 0) {

                    clone = clone.topLeft;

                }
                if (path.substring(path.length() - 2, path.length()).compareTo("BR") == 0) {

                    clone = clone.topRight;

                }

                if (path.substring(path.length() - 2, path.length()).compareTo("TR") == 0) {

                    clone = clone.bottomRight;

                }

                path = path.substring(0, path.length() - 2);

            }

            placeHolder.add(clone);

        }

        return placeHolder.get(0);
    }

    /**
     * This method locates the node up left of the current node.
     */
    qNode shiftUpLeft() {

        if (this.shiftUp() != null) {

            return this.shiftUp().shiftLeft();

        } else return null;

    }

    /**
     * This method locates the node up right of the current node.
     */
    qNode shiftUpRight() {

        if (this.shiftUp() != null) {
            return this.shiftUp().shiftRight();
        } else return null;

    }

    /**
     * This method locates the node down right of the current node.
     */
    qNode shiftDownRight() {

        if (this.shiftDown() != null) {

            return this.shiftDown().shiftRight();

        } else return null;
    }

    /**
     * This method locates the node down left of the current node.
     */
    qNode shiftDownLeft() {

        if (this.shiftDown() != null) {

            return this.shiftDown().shiftLeft();

        } else return null;

    }
}
