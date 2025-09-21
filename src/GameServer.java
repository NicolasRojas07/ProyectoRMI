import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class GameServer extends UnicastRemoteObject implements GameRMI {

    private Map<Integer, Player> players;
    private Map<Integer, Board> boards;
    private int currentTurn;
    private int playerCounter;

    public GameServer() throws RemoteException {
        super();
        players = new HashMap<>();
        boards = new HashMap<>();
        currentTurn = 1;
        playerCounter = 0;
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
        Board board = boards.get(playerId);
        if (board != null) {
            return board.placeShip(new Ship(size), x, y, orientation);
        }
        return false;
    }

    @Override
    public synchronized String shoot(int playerId, int x, int y) throws RemoteException {
        if (playerId != currentTurn) {
            return "No es tu turno.";
        }
        int opponentId = (playerId == 1) ? 2 : 1;
        Board opponentBoard = boards.get(opponentId);
        if (opponentBoard == null) return "Esperando al oponente...";
        boolean hit = opponentBoard.receiveShot(x, y);
        currentTurn = opponentId;
        return hit ? "¡Impacto!" : "¡Agua!";
    }

    @Override
    public synchronized char[][] getBoard(int playerId) throws RemoteException {
        Board board = boards.get(playerId);
        return (board != null) ? board.getGrid() : new char[0][0];
    }

    @Override
    public synchronized int checkWinner() throws RemoteException {
        for (Map.Entry<Integer, Board> entry : boards.entrySet()) {
            if (entry.getValue().allShipsSunk()) {
                int loserId = entry.getKey();
                int winnerId = (loserId == 1) ? 2 : 1;
                System.out.println("El jugador " + winnerId + " ha ganado la partida.");
                return winnerId;
            }
        }
        return 0;
    }

    @Override
    public synchronized List<String> listPlayers() throws RemoteException {
        List<String> lista = new ArrayList<>();
        for (Player p : players.values()) {
            lista.add("ID: " + p.getId() + " | Nombre: " + p.getName());
        }
        return lista;
    }

    @Override
    public synchronized boolean removePlayer(int playerId) throws RemoteException {
        if (players.containsKey(playerId)) {
            players.remove(playerId);
            boards.remove(playerId);
            System.out.println("Jugador con ID " + playerId + " eliminado.");
            return true;
        }
        return false;
    }
}
