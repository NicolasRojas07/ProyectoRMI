import java.util.*;

public class Board {
    private char[][] grid;
    private List<Ship> ships;

    public Board(int rows, int cols) {
        grid = new char[rows][cols];
        ships = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            Arrays.fill(grid[i], '~'); // agua
        }
    }

    public boolean placeShip(Ship ship, int x, int y, String orientation) {
        if (orientation.equalsIgnoreCase("H")) {
            if (y + ship.getSize() > grid[0].length) return false;
            for (int i = 0; i < ship.getSize(); i++) {
                if (grid[x][y + i] == 'B') return false; // colisión
            }
            for (int i = 0; i < ship.getSize(); i++) {
                grid[x][y + i] = 'B';
            }
        } else {
            if (x + ship.getSize() > grid.length) return false;
            for (int i = 0; i < ship.getSize(); i++) {
                if (grid[x + i][y] == 'B') return false; // colisión
            }
            for (int i = 0; i < ship.getSize(); i++) {
                grid[x + i][y] = 'B';
            }
        }
        ships.add(ship);
        return true;
    }

    public boolean receiveShot(int x, int y) {
        if (grid[x][y] == 'B') {
            grid[x][y] = 'X'; // impacto
            return true;
        } else {
            grid[x][y] = 'O'; // agua
            return false;
        }
    }

    public char[][] getGrid() {
        return grid;
    }

    public boolean allShipsSunk() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 'B') {
                    return false;
                }
            }
        }
        return true;
    }
}
