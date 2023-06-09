package edu.ufp.inf.sd.rmi.project.client.awgame.engine;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import edu.ufp.inf.sd.rmi.project.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.project.client.ObserverRI;
import edu.ufp.inf.sd.rmi.project.client.awgame.buildings.Base;
import edu.ufp.inf.sd.rmi.project.client.awgame.menus.MenuHandler;
import edu.ufp.inf.sd.rmi.project.server.GameFactory.GameFactoryRI;
import edu.ufp.inf.sd.rmi.project.server.GameSession.GameSessionRI;
import edu.ufp.inf.sd.rmi.project.server.Lobby.LobbyRI;

import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javax.swing.JFrame;

public class Game extends JFrame {
    public static GameFactoryRI gameFactory;
    public static GameSessionRI gameSession;
    public static ObserverImpl o;
    public static String username; // username
    public static int character;

    public static Game pointer;
    public static Channel channel;
    public static Connection con;
    public static boolean isRabbitmq = false;


    private static final long serialVersionUID = 1L;

    //Application Settings
    private static final String build = "0";
    private static final String version = "2";
    public static final String name = "Strategy Game";
    public static int ScreenBase = 32;//Bit size for the screen, 16 / 32 / 64 / 128
    public static boolean dev = true;//Is this a dev copy or not... useless? D:

    public static enum State {STARTUP, MENU, PLAYING, EDITOR}

    ;
    public static State GameState = State.STARTUP;

    //Setup the quick access to all of the other class files.
    public static Map map;
    public static Gui gui;
    public static LoadImages load;
    public static InputHandler input;
    public static Editor edit = new Editor();
    public static Battle btl = new Battle();
    public static ErrorHandler error = new ErrorHandler();
    public static Pathfinding pathing = new Pathfinding();
    public static ListData list;
    public static Save save = new Save();
    public static ComputerBrain brain = new ComputerBrain();
    public static FileFinder finder = new FileFinder();
    public static ViewPoint view = new ViewPoint();

    //Image handling settings are as follows
    public int fps;
    public int fpscount;
    public static Image[] img_menu = new Image[5];
    public static Image img_tile;
    public static Image img_char;
    public static Image img_plys;
    public static Image img_city;
    public static Image img_exts;
    public static Boolean readytopaint;


    //This handles the different players and also is used to speed logic arrays (contains a list of all characters they own)
    public static List<edu.ufp.inf.sd.rmi.project.client.awgame.players.Base> player = new ArrayList<edu.ufp.inf.sd.rmi.project.client.awgame.players.Base>();
    public static List<Base> builds = new ArrayList<Base>();
    public static List<edu.ufp.inf.sd.rmi.project.client.awgame.units.Base> units = new ArrayList<edu.ufp.inf.sd.rmi.project.client.awgame.units.Base>();
    //These are the lists that will hold commander, building, and unit data to use in the menu's
    public static List<edu.ufp.inf.sd.rmi.project.client.awgame.players.Base> displayC = new ArrayList<edu.ufp.inf.sd.rmi.project.client.awgame.players.Base>();
    public static List<Base> displayB = new ArrayList<Base>();
    public static List<edu.ufp.inf.sd.rmi.project.client.awgame.units.Base> displayU = new ArrayList<edu.ufp.inf.sd.rmi.project.client.awgame.units.Base>();

    public Game(GameFactoryRI gameFactoryRI) {
        super(name);
        pointer = this;
        gameFactory = gameFactoryRI;
        //Default Settings of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(20 * ScreenBase + 6, 12 * ScreenBase + 12));
        setBounds(0, 0, 20 * ScreenBase + 6, 12 * ScreenBase + 12);
        setUndecorated(false);
        setResizable(false);
        setLocationRelativeTo(null);

        //Creates all the gui elements and sets them up
        gui = new Gui(this);
        add(gui);
        gui.setFocusable(true);
        gui.requestFocusInWindow();

        //load images, initialize the map, and adds the input settings.
        load = new LoadImages();
        map = new Map();
        input = new InputHandler();
        list = new ListData();

        setVisible(true);//This has been moved down here so that when everything is done, it is shown.
        gui.LoginScreen();
        save.LoadSettings();
        GameLoop();
    }

    public Game(GameFactoryRI gameFactoryRI, Channel channel) {
        super(name);

        //Default Settings of the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(20 * ScreenBase + 6, 12 * ScreenBase + 12));
        setBounds(0, 0, 20 * ScreenBase + 6, 12 * ScreenBase + 12);
        setUndecorated(false);
        setResizable(false);
        setLocationRelativeTo(null);

        isRabbitmq = true;
        Game.gameFactory = gameFactoryRI;
        Game.channel = channel;
        pointer = this;

        //Creates all the gui elements and sets them up
        gui = new Gui(this);
        add(gui);
        gui.setFocusable(true);
        gui.requestFocusInWindow();

        //load images, initialize the map, and adds the input settings.
        load = new LoadImages();
        map = new Map();
        input = new InputHandler();
        list = new ListData();

        setVisible(true);//This has been moved down here so that when everything is done, it is shown.
        gui.LoginScreen();
        save.LoadSettings();
        GameLoop();
    }

    private void GameLoop() {
        boolean loop = true;
        long last = System.nanoTime();
        long lastCPSTime = 0;
        long lastCPSTime2 = 0;
        @SuppressWarnings("unused")
        int logics = 0;
        logics++;
        while (loop) {
            //Used for logic stuff
            @SuppressWarnings("unused")
            long delta = (System.nanoTime() - last) / 1000000;
            delta++;
            last = System.nanoTime();

            //FPS settings
            if (System.currentTimeMillis() - lastCPSTime > 1000) {
                lastCPSTime = System.currentTimeMillis();
                fpscount = fps;
                fps = 0;
                error.ErrorTicker();
                setTitle(name + " v" + build + "." + version + " : FPS " + fpscount);
                if (GameState == State.PLAYING) {
                    if (player.get(btl.currentplayer).npc && !btl.GameOver) {
                        brain.ThinkDamnYou(player.get(btl.currentplayer));
                    }
                }
            } else fps++;
            //Current Logic and frames per second location (capped at 20 I guess?)
            if (System.currentTimeMillis() - lastCPSTime2 > 100) {
                lastCPSTime2 = System.currentTimeMillis();
                logics = 0;
                if (GameState == State.PLAYING || GameState == State.EDITOR) {
                    view.MoveView();
                }//This controls the view-point on the map
                if (GameState == State.EDITOR) {
                    if (edit.holding && edit.moved) {
                        edit.AssButton();
                    }
                }
                Game.gui.frame++;//This is controlling the current frame of animation.
                if (Game.gui.frame >= 12) {
                    Game.gui.frame = 0;
                }
                gui.repaint();
            } else logics++;

            //Paints the scene then sleeps for a bit.
            try {
                Thread.sleep(30);
            } catch (Exception e) {
            }
            ;
        }
    }

    public void start(String l) throws RemoteException {
        boolean[] bots = {false, false, false, false};
        int[] characters = new int[4];
        LobbyRI lobby = gameSession.getLobby(l);
        List<ObserverRI> observers = lobby.getObservers();
        int i = 0;
        for (ObserverRI x : observers) {
            characters[i] = x.getCharacter();
            i++;
        }
        MenuHandler.CloseMenu();
        Game.btl.NewGame(lobby.getMap());
        Game.btl.AddCommanders(characters,bots,100,50);
        Game.gui.InGameScreen();
        if(!Game.gameFactory.hasChannel()){
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try {
                con = factory.newConnection();
                channel = con.createChannel();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * Starts a new game when launched.
     */
    public static void main(String args[]) throws Exception {
     //   new Game();
    }
}
