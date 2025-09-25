import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class GameServer extends UnicastRemoteObject implements GameRMI {
    private Map<Integer, char[][]> boards = new HashMap<>();
    private Map<Integer, Integer> shipCells = new HashMap<>();  // total casillas ocupadas
    private Map<Integer, Integer> hits = new HashMap<>();       // aciertos por jugador
    private Map<Integer, String> players = new HashMap<>();
    private int currentTurn = 1;
    private int nextId = 1;

    public GameServer() throws RemoteException {}

    @Override
    public int registerPlayer(String name) {
        int id = nextId++;
        boards.put(id, new char[10][10]);
        players.put(id, name);
        shipCells.put(id, 0);
        hits.put(id, 0);

        // Inicializamos tablero vacío
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                boards.get(id)[i][j] = '~';

        return id;
    }

    @Override
    public boolean placeShip(int playerId, int x, int y, int size, String orientation) {
        char[][] board = boards.get(playerId);
        if (board == null) return false;

        if (orientation.equals("H")) {
            for (int j = 0; j < size; j++) {
                board[x][y + j] = 'B';
            }
        } else {
            for (int i = 0; i < size; i++) {
                board[x + i][y] = 'B';
            }
        }
        shipCells.put(playerId, shipCells.get(playerId) + size); // sumamos casillas
        return true;
    }

    @Override
    public String shoot(int playerId, int x, int y) {
        int enemyId = getEnemyId(playerId);
        char[][] enemyBoard = boards.get(enemyId);
        if (enemyBoard == null) return "❌ No hay enemigo";

        if (enemyBoard[x][y] == 'B') {
            enemyBoard[x][y] = 'X';
            hits.put(playerId, hits.get(playerId) + 1);
            return "💥 Impacto!";
        } else if (enemyBoard[x][y] == '~') {
            enemyBoard[x][y] = 'O';
            return "🌊 Agua";
        } else {
            return "⚠️ Ya disparaste aquí";
        }
    }

    @Override
    public char[][] getBoard(int playerId) { return boards.get(playerId); }

    @Override
    public String getCurrentTurn() { return players.get(currentTurn); }

    @Override
    public List<String> listPlayers() { return new ArrayList<>(players.values()); }

    @Override
    public boolean removePlayer(int playerId) {
        if (!players.containsKey(playerId)) return false;
        players.remove(playerId);
        boards.remove(playerId);
        shipCells.remove(playerId);
        hits.remove(playerId);
        return true;
    }

    @Override
    public void setPlayerReady(int playerId) { /* lógica de ready */ }

    @Override
    public boolean allPlayersReady() { return true; }

    @Override
    public char[][] getEnemyBoard(int playerId) {
        int enemyId = getEnemyId(playerId);
        char[][] board = boards.get(enemyId);
        if (board == null) return null;

        // Solo mostrar X (acierto), O (agua) y ~
        char[][] filtered = new char[10][10];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == 'X' || board[i][j] == 'O') {
                    filtered[i][j] = board[i][j];
                } else {
                    filtered[i][j] = '~';
                }
            }
        return filtered;
    }

    @Override
    public int checkWinner() {
        for (int playerId : players.keySet()) {
            int total = shipCells.get(playerId);
            int enemyId = getEnemyId(playerId);
            if (hits.get(enemyId) == total) {
                return enemyId; // este jugador destruyó todos los barcos del rival
            }
        }
        return -1; // no hay ganador aún
    }

    private int getEnemyId(int playerId) {
        for (int id : players.keySet()) {
            if (id != playerId) return id;
        }
        return -1;
    }
}
