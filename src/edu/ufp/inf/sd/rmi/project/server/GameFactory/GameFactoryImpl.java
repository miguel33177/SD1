package edu.ufp.inf.sd.rmi.project.server.GameFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rabbitmq.client.Channel;
import edu.ufp.inf.sd.rmi.project.server.GameSession.GameSessionImpl;
import edu.ufp.inf.sd.rmi.project.server.GameSession.GameSessionRI;
import edu.ufp.inf.sd.rmi.project.server.Lobby.LobbyImpl;
import edu.ufp.inf.sd.rmi.project.shared.User;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class GameFactoryImpl extends UnicastRemoteObject implements GameFactoryRI {
    private final HashMap<String, User> hashMap;
    private final ArrayList<String> loggedUsers;
    private HashMap<String, LobbyImpl> hash;
    private ArrayList<LobbyImpl> array;
    private Channel channel;

    public GameFactoryImpl() throws RemoteException{
        super();
        this.hashMap = new HashMap<>();
        this.hash = new HashMap<>();
        this.array = new ArrayList<>();
        this.loggedUsers = new ArrayList<>();
        loadHashMap();
        addShutdownHook();
    }

    public GameFactoryImpl(Channel c) throws RemoteException{
        this();
        this.channel = c;
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveHashMap();
            } catch (RemoteException e) {
                System.err.println("Erro ao salvar HashMap: " + e.getMessage());
            }
        }));
    }

    @Override
    public void saveHashMap() throws RemoteException{
        String filePath = "../../../src/edu/ufp/inf/sd/rmi/project/server/Users.txt";
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

    @Override
    public void loadHashMap() throws RemoteException{
        String filePath = "../../../src/edu/ufp/inf/sd/rmi/project/server/Users.txt";
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

    @Override
    public String generateToken(String username, String password, Date expirationDate) throws RemoteException {
        String token = JWT.create()
                .withClaim("username", username)
                .withClaim("password", password)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256("mysecretkey"));
        return token;
    }

    @Override
    public boolean login(String username,String password) throws RemoteException {
        User user = hashMap.get(username);
        if (user != null && user.getPasswordHash() == password.hashCode() && !this.loggedUsers.contains(username)) {
            String storedToken = user.getToken();
            if (storedToken != null) {
                try {
                    DecodedJWT decodedJWT = JWT.decode(storedToken);
                    Algorithm algorithm = Algorithm.HMAC256("mysecretkey");
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    verifier.verify(decodedJWT);
                } catch (JWTVerificationException e) {
                    // Token inválido ou expirado, gerar um novo token
                    Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
                    String newToken = generateToken(username, password, expirationDate);
                    user.setToken(newToken);
                }
            }
            this.loggedUsers.add(username);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean register(String username,String password) throws RemoteException {
        if (hashMap.containsKey(username)) {
          return false;
        } else {
            int passwordHash = password.hashCode();
            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
            String token = generateToken(username, password, expirationDate);

            User user = new User(passwordHash, token);
            hashMap.put(username, user);
            return true;
        }
    }

    @Override
    public GameSessionRI getSession() throws RemoteException {
        if(this.channel != null){
            return new GameSessionImpl(hash,array,this.channel);
        }else{
            return new GameSessionImpl(hash,array);
        }
    }

    @Override
    public void logout(String u) throws RemoteException{
        this.loggedUsers.remove(u);
    }

    @Override
    public boolean hasChannel() throws RemoteException{
        return this.channel != null;
    }
}
