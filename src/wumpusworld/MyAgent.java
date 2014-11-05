package wumpusworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
    private final static int MAXCONT = 5;

    private final static int[] incrX = new int[]{0, 1, 0, -1};
    private final static int[] incrY = new int[]{1, 0, -1, 0};
    private boolean[] posMoves = new boolean[4];
    //UP = 0; RIGHT = 1; DOWN = 2; Left = 3

    private World w;

    /**
     * Creates a new instance of your solver agent.
     *
     * @param world Current world state
     */
    public MyAgent(World world) {
        w = world;
        inicializarHashMap();
        aprendiz.get(0).put(CONT_KEY, 1);
        aprendiz.get(0).put(SCORE_KEY, 1);
    }

    public MyAgent(World world, ArrayList<HashMap<String, Integer>> hash) {
        w = world;
        aprendiz = (ArrayList<HashMap<String, Integer>>) hash.clone();
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
     * @return Posicion del ArrayList<HashMap>
     */
    public int obtenerPosicion(int x, int y) {

        boolean encontrado = false;
        int i = 0;
        int posicion = 0;
        while (!encontrado) {
            if ((aprendiz.get(i).get(POS_X_KEY) == x)
                    && (aprendiz.get(i).get(POS_Y_KEY) == y)) {
                encontrado = true;
                posicion = i;
                //System.out.println(" POSX " + x + " POSY " + y + " LISTA " + posicion + " =DEBUG");
            }
            i++;
        }
        return posicion;
    }

    /**
     * Nos va a devolver la utilidad del nodo.
     *
     * @param x Posicion X
     * @param y Posicion Y
     * @return Utilidad -50 en perceciones, -500 en pit y -1000 en wumpus
     */
    public int valorNodo(int x, int y) {
        int valor = 10;
        if (w.hasBreeze(x, y)) {
            valor = valor - 60;
        }
        if (w.hasStench(x, y)) {
            valor = valor - 60;
        }
        if (w.hasPit(x, y)) {
            valor = valor - 510;
        }
        if (w.hasWumpus(x, y)) {
            valor = valor - 1010;
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
        for (int i = 0; i < posMoves.length; i++) {
            posMoves[i] = true;
        }

        //Ponemos a false los movimientos que no se pueden realizar
        if (w.getPlayerY() == 4) {
            moveUp = false;
            posMoves[0] = false;
        }
        if (w.getPlayerX() == 4) {
            moveRight = false;
            posMoves[1] = false;
        }
        if (w.getPlayerY() == 1) {
            moveDown = false;
            posMoves[2] = false;
        }

        if (w.getPlayerX() == 1) {
            moveLeft = false;
            posMoves[3] = false;
        }

        //Decido que movimiento Realizar mediante una funcion (Hacer la funcion)
        int score = -1;
        int maxScore = -1000;
        int counter = -1;
        int minCounter = 7;
        int bestX = 1;
        int bestY = 1;
        int moveToDo = -1;
        int moveToDoAux = -1;
        ArrayList<Integer> aMoveToDo = new ArrayList<>();
        ArrayList<Integer> aMoveToDoAux = new ArrayList<>();
        int index = 0;
        while (index < posMoves.length) {
            //Si el movimiento está en true se puede realizar.
            //s tres arrays declarados se corresponden con los indices, por tanto
            //si queremos mover arriba nos valdría con usar el indice 0, evitando
            //multiples casos de if.
            if (posMoves[index]) {
                counter = aprendiz.get(obtenerPosicion(cX + incrX[index], cY + incrY[index])).get(CONT_KEY);
                score = aprendiz.get(obtenerPosicion(cX + incrX[index], cY + incrY[index])).get(SCORE_KEY);
                /*
                 Si el contador es contador es menor que tres comprobamos  
                 directamente las puntuaciones, si por el contrario el contador no lo es
                 primero comprobamos el contador, ya que buscaremos aquel en el que menos veces
                 hayamos estado previo a buscar puntuaciones.
                 */
                if ((counter <= MAXCONT) && (counter < minCounter)) {
                    minCounter = counter;
                    moveToDo = index;
                    aMoveToDo.add(index);
                }
                if (score >= maxScore) {
                    maxScore = score;
                    moveToDoAux = index;
                    aMoveToDoAux.add(index);
                }

            }
            index++;

            //Comparar puntuaciones para moverme. ESO FALLA!
        }
        //CUANDO QUIERO QUE SEA RANDOM?? cuando todos los posibles movimientos sean iguales.
        //añadir a un arraylist y sin son todos iguales entrar por aquí.

//        if (aMoveToDo.equals(aMoveToDoAux)) { //Si los posibles movimientos son iguales tiramos un random.
//
//            int i = 0;
//            int primeraVez = 0;
//            while (i < posMoves.length) {
//                if (posMoves[i]) {
//                    if (primeraVez == 0) {
//                        moveToDo = i;
//                        primeraVez = 1;
//                    }
//                    if (primeraVez == 1) {
//                        Random rand = new Random();
//                        if (rand.nextBoolean()) {
//                            moveToDo = i;
//                        }
//                    }
//
//                }
//                i++;
//            }
//        }
        if (aprendiz.get(obtenerPosicion(cX + incrX[moveToDo], cY + incrY[moveToDo])).get(SCORE_KEY) < -50) {
            moveToDo = moveToDoAux;
//        Si el movimiento que va a hacer es muy malo se va por el movimiento por puntos.
        }
        System.out.println("Movimiento a realizar: " + moveToDo);

        // Le digo que moviento realizar
        if (moveToDo == 0) {
            moveDown = false;
            moveUp = true;
            moveLeft = false;
            moveRight = false;
        }
        if (moveToDo == 1) {
            moveDown = false;
            moveUp = false;
            moveLeft = false;
            moveRight = true;
        }
        if (moveToDo == 2) {
            moveDown = true;
            moveUp = false;
            moveLeft = false;
            moveRight = false;
        }
        if (moveToDo == 3) {
            moveDown = false;
            moveUp = false;
            moveLeft = true;
            moveRight = false;
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

//            puntuacionActual = aprendiz.get(
//                    obtenerPosicion(w.getPlayerX() + 1, w.getPlayerY())).get(
//                            SCORE_KEY);//Obtenemos la puntuacion guardada en el Hashmap de la posicion de su derecha
//            if (puntuacionActual >= mejorPuntuacion) {
//                mejorPuntuacion = puntuacionActual;
            mejorX = w.getPlayerX() + 1;
            mejorY = w.getPlayerY();
//            }
        }
        if (moveUp) {
//            puntuacionActual = aprendiz.get(
//                    obtenerPosicion(w.getPlayerX(), w.getPlayerY() + 1)).get(
//                            SCORE_KEY);
//            if (puntuacionActual >= mejorPuntuacion) {
//                mejorPuntuacion = puntuacionActual;
            mejorX = w.getPlayerX();
            mejorY = w.getPlayerY() + 1;
//            }

        }
        if (moveLeft) {
//            puntuacionActual = aprendiz.get(
//                    obtenerPosicion(w.getPlayerX() - 1, w.getPlayerY())).get(
//                            SCORE_KEY);
//            if (puntuacionActual > mejorPuntuacion) {
//                mejorPuntuacion = puntuacionActual;
            mejorX = w.getPlayerX() - 1;
            mejorY = w.getPlayerY();

//            }
        }
        if (moveDown) {
//            puntuacionActual = aprendiz.get(
//                    obtenerPosicion(w.getPlayerX(), w.getPlayerY() - 1)).get(
//                            SCORE_KEY);
//            if (puntuacionActual >= mejorPuntuacion) {
//                mejorPuntuacion = puntuacionActual;
            mejorX = w.getPlayerX();
            mejorY = w.getPlayerY() - 1;
//            }

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

//        for (int i = 0;
//                i < 6; i++) {
//            System.out.println(aprendiz.get(i));
//        }
        //System.out.println(w.getPlayerX());
        w.doAction(World.A_MOVE);
        System.out.println("DEBUG--> " + "X: " + mejorX + " Y: " + mejorY);

        aprendiz.get(obtenerPosicion(mejorX, mejorY)).put(SCORE_KEY, valorNodo(mejorX, mejorY));
        int cont = aprendiz.get(obtenerPosicion(mejorX, mejorY)).get(CONT_KEY);
        cont++;
        aprendiz.get(obtenerPosicion(mejorX, mejorY)).put(CONT_KEY, cont);

        return;
    }

    public ArrayList<HashMap<String, Integer>> getAprendiz() {
        return aprendiz;
    }

    public void setAprendiz(ArrayList<HashMap<String, Integer>> aprendiz) {
        this.aprendiz = aprendiz;
    }

}
