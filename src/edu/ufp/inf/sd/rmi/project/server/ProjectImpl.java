package edu.ufp.inf.sd.rmi.project.server;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Game;
import edu.ufp.inf.sd.rmi.project.server.ProjectRI;
import edu.ufp.inf.sd.rmi.project.shared.User;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ProjectImpl extends UnicastRemoteObject implements ProjectRI {

    private HashMap<String, User> hashMap;
    private HashMap<String, Lobby> lobbies;
    private ArrayList<Lobby> arrayLobbies;
    private String secret;

    private void saveHashMap() {
        String filePath = "/Users/brunomiguel/IdeaProjects/SD1/src/edu/ufp/inf/sd/rmi/project/server/Users.txt";
        File file = new File(filePath);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);

            for (String key : hashMap.keySet()) {
                User user = hashMap.get(key);
                String line = key + "," + user.getPasswordHash() + "," + user.getToken() + "\n";
                writer.write(line);
            }

            writer.close();
            System.out.println("HashMap salvo com sucesso em " + filePath);
        } catch (IOException e) {
            System.err.println("Erro ao salvar HashMap: " + e.getMessage());
        }
    }

    private void loadHashMap() {
        String filePath = "/Users/brunomiguel/IdeaProjects/SD1/src/edu/ufp/inf/sd/rmi/project/server/Users.txt";
        File file = new File(filePath);

        try {
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String key = parts[0];
                        int passwordHash = Integer.parseInt(parts[1]);
                        String token = parts[2];
                        User user = new User(passwordHash, token);
                        hashMap.put(key, user);
                    }
                }
                reader.close();
                System.out.println("HashMap carregado com sucesso de " + filePath);
            } else {
                System.out.println("O arquivo " + filePath + " não existe.");
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar HashMap: " + e.getMessage());
        }
    }

    public ProjectImpl() throws RemoteException {
        super();
        hashMap = new HashMap<>();
        lobbies = new HashMap<>();
        arrayLobbies = new ArrayList<>();
        secret = "mysecretkey";
        loadHashMap(); // Carrega os dados do HashMap quando o objeto é criado
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveHashMap)); // Salva os dados do HashMap quando o programa é finalizado
    }

    @Override
    public String registerUser(String username, String password) throws RemoteException {
        if (hashMap.containsKey(username)) {
            return "400"; // User already exists
        } else {
            int passwordHash = password.hashCode();
            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
            String token = generateToken(username, password, expirationDate);

            User user = new User(passwordHash, token);
            hashMap.put(username, user);
            return "TOKEN : " + token;
        }
    }

    @Override
    public String loginUser(String username, String password) throws RemoteException {
        User user = hashMap.get(username);
        if (user != null && user.getPasswordHash() == password.hashCode()) {
            String storedToken = user.getToken();
            if (storedToken != null) {
                try {
                    DecodedJWT decodedJWT = JWT.decode(storedToken);
                    Algorithm algorithm = Algorithm.HMAC256(secret);
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    verifier.verify(decodedJWT);  // Isso verifica se o token é válido
                    return "Login bem sucedido.";
                } catch (JWTVerificationException e) {
                    // Token inválido ou expirado, gerar um novo token
                    Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
                    String newToken = generateToken(username, password, expirationDate);
                    user.setToken(newToken);
                    return "Login bem sucedido. Novo token gerado: " + newToken;
                }
            }
            return "Login bem sucedido.";
        } else {
            return "Login falhou.";
        }
    }

    private String generateToken(String username, String password, Date expirationDate) {
         String token = JWT.create()
             .withClaim("username", username)
             .withClaim("password", password)
             .withExpiresAt(expirationDate)
             .sign(Algorithm.HMAC256(secret));
         return token;
    }

    public ArrayList<Lobby> getArrayLobbies(){
        return arrayLobbies;
    }

    public ArrayList<String> getLobbies() throws RemoteException{
        ArrayList<String> x = new ArrayList<>();
        String s;
        for(Lobby l : getArrayLobbies()){
            s = l.getMap() + "#" + l.getId();
            x.add(s);
        }
        return x;
    }

    public void createLobby(String mapName) throws RemoteException {
        Lobby lobby = new Lobby(mapName);
        String lobbyName = mapName + "#" + lobby.getId();
        lobbies.put(lobbyName, lobby);
        arrayLobbies.add(lobby);
        System.out.println("Lobby created: " + lobbyName);
    }

    public void joinLobby(String lobbyName) throws RemoteException{
        Lobby lobby = lobbies.get(lobbyName);
        lobby.incrementNumPlayers();
        //lobby.notifyObservers();
         System.out.println("Player joined lobby: " + lobbyName);
    }
}
