import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class GameServer extends UnicastRemoteObject implements GameRMI {
    private final Map<Integer, String> players = new HashMap<>();
    private final Map<Integer, Board> boards = new HashMap<>();
    private final Map<Integer, Boolean> readyPlayers = new HashMap<>();
    private int nextId = 1;
    private int currentTurn = -1;

    protected GameServer() throws RemoteException { super(); }

    @Override
    public synchronized int registerPlayer(String name) throws RemoteException {
        int id = nextId++;
        players.put(id, name);
        boards.put(id, new Board());
        readyPlayers.put(id, false);
        System.out.println("Jugador registrado: " + name + " (ID: " + id + ")");
        return id;
    }

    @Override
    public synchronized boolean placeShip(int playerId, int x, int y, int size, String orientation) throws RemoteException {
        return boards.get(playerId).placeShip(x, y, size, orientation);
    }

    @Override
    public synchronized String shoot(int playerId, int x, int y) throws RemoteException {
        if (currentTurn != playerId) return "No es tu turno.";
        int opponentId = players.keySet().stream().filter(id -> id != playerId).findFirst().orElse(-1);
        if (opponentId == -1) return "Esperando a un oponente...";
        String result = boards.get(opponentId).shoot(x, y);
        if (result.contains("Impacto") || result.contains("Agua")) {
            currentTurn = opponentId;
        }
        return result;
    }

    @Override
    public synchronized char[][] getBoard(int playerId) throws RemoteException {
        return boards.get(playerId).getGrid();
    }

    @Override
    public synchronized String getCurrentTurn() throws RemoteException {
        if (currentTurn == -1) return "Esperando a que todos estén listos...";
        return "Turno de: " + players.get(currentTurn);
    }

    @Override
    public synchronized List<String> listPlayers() throws RemoteException {
        List<String> list = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : players.entrySet()) {
            list.add("ID: " + entry.getKey() + " | Nombre: " + entry.getValue() +
                    (readyPlayers.get(entry.getKey()) ? " ✅ Listo" : " ⏳ No listo"));
        }
        return list;
    }

    @Override
    public synchronized boolean removePlayer(int playerId) throws RemoteException {
        if (players.containsKey(playerId)) {
            players.remove(playerId);
            boards.remove(playerId);
            readyPlayers.remove(playerId);
            return true;
        }
        return false;
    }

    @Override
    public synchronized void setPlayerReady(int playerId) throws RemoteException {
        readyPlayers.put(playerId, true);
        System.out.println("Jugador listo: " + players.get(playerId));

        if (allPlayersReady() && currentTurn == -1) {
            currentTurn = players.keySet().iterator().next();
            System.out.println("La partida comienza. Turno inicial: " + players.get(currentTurn));
        }
    }

    @Override
    public synchronized boolean allPlayersReady() throws RemoteException {
        return !readyPlayers.isEmpty() && readyPlayers.values().stream().allMatch(r -> r);
    }

    // 👇 Nuevo método: tablero enemigo filtrado
    @Override
    public synchronized char[][] getEnemyBoard(int playerId) throws RemoteException {
        // Buscar oponente
        int opponentId = players.keySet().stream().filter(id -> id != playerId).findFirst().orElse(-1);
        if (opponentId == -1) {
            return new char[10][10]; // vacío si no hay oponente
        }

        char[][] fullBoard = boards.get(opponentId).getGrid();
        char[][] visibleBoard = new char[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                char c = fullBoard[i][j];
                if (c == 'X' || c == 'O') {
                    visibleBoard[i][j] = c; // mostrar solo disparos
                } else {
                    visibleBoard[i][j] = '~'; // lo demás oculto
                }
            }
        }
        return visibleBoard;
    }
}
