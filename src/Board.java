public class Board {
    private char[][] grid;
    private Ship[][] ships;

    public Board(int rows, int cols) {
        grid = new char[rows][cols];
        ships = new Ship[rows][cols];

        // inicializar tablero con agua
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '~';
            }
        }
    }

    // Sobrecarga 1: colocar barco con size + orientation
    public boolean placeShip(int x, int y, int size, String orientation) {
        Ship ship = new Ship(size, orientation);
        return placeShip(x, y, ship);
    }

    // Sobrecarga 2: colocar barco con objeto Ship
    public boolean placeShip(int x, int y, Ship ship) {
        int size = ship.getSize();
        String orientation = ship.getOrientation();

        if (orientation.equals("H")) {
            if (y + size > grid[0].length) return false;
            for (int j = y; j < y + size; j++) {
                if (grid[x][j] != '~') return false;
            }
            for (int j = y; j < y + size; j++) {
                grid[x][j] = 'B';
                ships[x][j] = ship;
            }
        } else if (orientation.equals("V")) {
            if (x + size > grid.length) return false;
            for (int i = x; i < x + size; i++) {
                if (grid[i][y] != '~') return false;
            }
            for (int i = x; i < x + size; i++) {
                grid[i][y] = 'B';
                ships[i][y] = ship;
            }
        } else {
            return false;
        }
        return true;
    }

    // disparar a una celda
    public String receiveShot(int x, int y) {
        if (grid[x][y] == 'B') {
            grid[x][y] = 'X'; // impacto
            return "Tocado";
        } else if (grid[x][y] == '~') {
            grid[x][y] = 'O'; // agua
            return "Agua";
        } else {
            return "Ya disparaste aquí";
        }
    }

    public char[][] getBoard() {
        return grid;
    }

    public boolean allShipsSunk() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 'B') return false;
            }
        }
        return true;
    }
}
