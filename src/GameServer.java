import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class GameServer extends UnicastRemoteObject implements GameRMI {
    private Map<Integer, Player> players = new HashMap<>();
    private Map<Integer, Board> boards = new HashMap<>();
    private int playerCounter = 0;
    private int currentTurn = 1; // 🔹 empieza el jugador 1

    protected GameServer() throws RemoteException {
        super();
    }

    @Override
    public synchronized int registerPlayer(String name) throws RemoteException {
        playerCounter++;
        int id = playerCounter;
        players.put(id, new Player(id, name));
        boards.put(id, new Board(10, 10));
        System.out.println("Jugador registrado: " + name + " con ID: " + id);
        return id;
    }

    @Override
    public synchronized boolean placeShip(int playerId, int x, int y, int size, String orientation) throws RemoteException {
        return boards.get(playerId).placeShip(x, y, size, orientation);
    }

    @Override
    public synchronized char[][] getBoard(int playerId) throws RemoteException {
        return boards.get(playerId).getBoard();
    }

    @Override
    public synchronized String shoot(int playerId, int x, int y) throws RemoteException {
        if (playerId != currentTurn) {
            return "⏳ No es tu turno. Es el turno de " + players.get(currentTurn).getName();
        }

        // identificar oponente
        int opponentId = players.keySet().stream()
                .filter(id -> id != playerId)
                .findFirst()
                .orElse(-1);

        if (opponentId == -1) {
            return "⚠️ No hay oponente disponible.";
        }

        String result = boards.get(opponentId).receiveShot(x, y);

        if (result.equals("Agua") || result.equals("Tocado")) {
            currentTurn = opponentId;
        }

        return "🎯 " + result + ". Ahora es el turno de " + players.get(currentTurn).getName();
    }

    @Override
    public synchronized int checkWinner() throws RemoteException {
        for (Map.Entry<Integer, Board> entry : boards.entrySet()) {
            if (entry.getValue().allShipsSunk()) {
                return entry.getKey();
            }
        }
        return 0;
    }

    @Override
    public synchronized List<String> listPlayers() throws RemoteException {
        List<String> list = new ArrayList<>();
        for (Player p : players.values()) {
            list.add("ID: " + p.getId() + " | Nombre: " + p.getName());
        }
        return list;
    }

    @Override
    public synchronized boolean removePlayer(int playerId) throws RemoteException {
        if (players.containsKey(playerId)) {
            players.remove(playerId);
            boards.remove(playerId);
            return true;
        }
        return false;
    }

    @Override
    public synchronized String getCurrentTurn() throws RemoteException {
        if (!players.containsKey(currentTurn)) {
            return "⚠️ No hay turno activo.";
        }
        return "👉 Es el turno de: " + players.get(currentTurn).getName();
    }
}
