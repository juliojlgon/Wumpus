package wumpusworld;

import java.util.ArrayList;
import java.util.HashMap;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 * Contans starting code for creating your own Wumpus World agent. Currently the
 * agent only make a random decision each turn.
 *
 * @author Johan Hagelbäck
 */
public class MyAgent implements Agent {

    private ArrayList<HashMap<String, Integer>> aprendiz = new ArrayList<HashMap<String, Integer>>();
    private final static String POS_X_KEY = "POSX";
    private final static String POS_Y_KEY = "POSY";
    private final static String SCORE_KEY = "SCORE";
    private final static String CONT_KEY = "CONT";
    private World w;

    /**
     * Creates a new instance of your solver agent.
     *
     * @param world Current world state
     */
    public MyAgent(World world) {
        w = world;
        inicializarHashMap();
    }

    public void inicializarHashMap() {
        HashMap<String, Integer> valor = new HashMap<String, Integer>();
        for (int i = 0; i < w.getSize(); i++) {
            for (int j = 0; j < w.getSize(); j++) {
                valor = new HashMap<String, Integer>();
                valor.put(POS_X_KEY, i + 1);
                valor.put(POS_Y_KEY, j + 1);
                valor.put(SCORE_KEY, 0);
                valor.put(CONT_KEY, 0);
                aprendiz.add(valor);
            }
        }
    }

    /**
     * Nos va a devolver una posicion entendible para el array
     *
     * @param x posicion X del jugador
     * @param y Posicion Y del jugador
     * @return Posicion del array
     */

    public int obtenerPosicion(int x, int y) {

        boolean encontrado = false;
        int i = 0;
        int posicion=0;
        while (!encontrado) {
            if ((aprendiz.get(i).get(POS_X_KEY) == x)
                    && (aprendiz.get(i).get(POS_Y_KEY) == y)) {
                encontrado = true;
                posicion = i;
                System.out.println(" POSX " + x + " POSY " + y + " LISTA " +posicion  + " =DEBUG");
            }
            i++;
        }
        return posicion;
    }

    public int valorNodo(int x, int y) {
        int valor = 10;
        if (w.hasBreeze(x, y)) {
            valor = valor - 50;
        }
        if (w.hasStench(x, y)) {
            valor = valor - 50;
        }
        if (w.hasPit(x, y)) {
            valor = valor - 500;
        }
        if (w.hasWumpus(x, y)) {
            valor = valor - 1000;
        }
        return valor;
    }

    /**
     * Asks your solver agent to execute an action.
     */
    public void doAction() {

        // Location of the player
        int cX = w.getPlayerX();
        int cY = w.getPlayerY();

		// Basic action:
        // Grab Gold if we can.
        if (w.hasGlitter(cX, cY)) {
            w.doAction(World.A_GRAB);
            return;
        }

		// Basic action:
        // We are in a pit. Climb up.
        if (w.isInPit()) {
            w.doAction(World.A_CLIMB);
            return;
        }

        // Test the environment
        if (w.hasBreeze(cX, cY)) {
            System.out.println("I am in a Breeze");
        }
        if (w.hasStench(cX, cY)) {
            System.out.println("I am in a Stench");
        }
        if (w.hasPit(cX, cY)) {
            System.out.println("I am in a Pit");
        }
        if (w.getDirection() == World.DIR_RIGHT) {
            System.out.println("I am facing Right");
        }
        if (w.getDirection() == World.DIR_LEFT) {
            System.out.println("I am facing Left");
        }
        if (w.getDirection() == World.DIR_UP) {
            System.out.println("I am facing Up");
        }
        if (w.getDirection() == World.DIR_DOWN) {
            System.out.println("I am facing Down");
        }

        // Basic moves for starting and some constraints
        boolean moveLeft = true;
        boolean moveRight = true;
        boolean moveUp = true;
        boolean moveDown = true;

        if (w.getPlayerY() == 1) {
            moveDown = false;
            System.out.println("NOABAJO");
        }
        if (w.getPlayerY() == 4) {
            moveUp = false;
            System.out.println("NOUP");
        }
        if (w.getPlayerX() == 1) {
            moveLeft = false;
            System.out.println("NOizq");
        }
        if (w.getPlayerX() == 4) {
            moveRight = false;
            System.out.println("NODER");
        }

        // declaro puntuacion actual
        int puntuacionActual = 0;
        // dweclaro puntuacion mejor
        int mejorPuntuacion = 0;

        // declaro mejor x
        int mejorX = 0;
        // declaro mejor y
        int mejorY = 0;
        // comprobamos a la derecha

        if (moveRight) {

            puntuacionActual = aprendiz.get(
                    obtenerPosicion(w.getPlayerX() + 1, w.getPlayerY())).get(
                            SCORE_KEY);//Obtenemos la puntuacion guardada en el Hashmap de la posicion de su derecha
            if (puntuacionActual >= mejorPuntuacion) {
                mejorPuntuacion = puntuacionActual;
                mejorX = w.getPlayerX() + 1;
                mejorY = w.getPlayerY();
            }
        }
        if (moveUp) {
            puntuacionActual = aprendiz.get(
                    obtenerPosicion(w.getPlayerX(), w.getPlayerY() + 1)).get(
                            SCORE_KEY);
            if (puntuacionActual >= mejorPuntuacion) {
                mejorPuntuacion = puntuacionActual;
                mejorX = w.getPlayerX();
                mejorY = w.getPlayerY() + 1;
            }

        }
        if (moveLeft) {
            puntuacionActual = aprendiz.get(
                    obtenerPosicion(w.getPlayerX() - 1, w.getPlayerY())).get(
                            SCORE_KEY);
            if (puntuacionActual > mejorPuntuacion) {
                mejorPuntuacion = puntuacionActual;
                mejorX = w.getPlayerX() - 1;
                mejorY = w.getPlayerY();

            }

        }
        if (moveDown) {
            puntuacionActual = aprendiz.get(
                    obtenerPosicion(w.getPlayerX(), w.getPlayerY() - 1)).get(
                            SCORE_KEY);
            if (puntuacionActual >= mejorPuntuacion) {
                mejorPuntuacion = puntuacionActual;
                mejorX = w.getPlayerX();
                mejorY = w.getPlayerY() - 1;
            }

        }
        int direction = w.getDirection();

        // TODO orientar()
        if (mejorX < cX) {
            switch (direction) {
                case 0: //UP
                    w.doAction(World.A_TURN_LEFT);
                    break;
                case 1: //Right
                    w.doAction(World.A_TURN_RIGHT);
                    w.doAction(World.A_TURN_RIGHT);
                    break;
                case 2: //Down
                    w.doAction(World.A_TURN_RIGHT);
                    break;
            }

        }
        if (mejorX > cX) {
            switch (direction) {
                case 0:
                    w.doAction(World.A_TURN_RIGHT);
                    break;
                case 2:
                    w.doAction(World.A_TURN_LEFT);
                    break;
                case 3://LEFT
                    w.doAction(World.A_TURN_RIGHT);
                    w.doAction(World.A_TURN_RIGHT);
                    break;
            }

        }
        if (mejorY < cY) {
            switch (direction) {
                case 0:
                    w.doAction(World.A_TURN_RIGHT);
                    w.doAction(World.A_TURN_RIGHT);
                    break;

                case 1:
                    w.doAction(World.A_TURN_RIGHT);
                    break;
                case 3:
                    w.doAction(World.A_TURN_LEFT);
                    break;
            }

        }
        if (mejorY > cY) {
            switch (direction) {
                case 1:
                    w.doAction(World.A_TURN_LEFT);
                    break;

                case 2:
                    w.doAction(World.A_TURN_RIGHT);
                    w.doAction(World.A_TURN_RIGHT);
                    break;
                case 3:
                    w.doAction(World.A_TURN_RIGHT);
                    break;
            }

        }
        aprendiz.get(obtenerPosicion(mejorX, mejorY)).put(SCORE_KEY, valorNodo(mejorX, mejorY));

        for (int i = 0; i < 6; i++) {
            System.out.println(aprendiz.get(i));
        }
        //System.out.println(w.getPlayerX());

        w.doAction(World.A_MOVE);
        return;

    }

    public ArrayList<HashMap<String, Integer>> getAprendiz() {
        return aprendiz;
    }

    public void setAprendiz(ArrayList<HashMap<String, Integer>> aprendiz) {
        this.aprendiz = aprendiz;
    }

}
