public class Board {
    private final char[][] grid = new char[10][10];
    private int totalShipCells = 0;   // total de casillas con barcos
    private int hitShipCells = 0;     // total de casillas impactadas

    public Board() {
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                grid[i][j] = '~';
    }

    public boolean placeShip(int x, int y, int size, String orientation) {
        if (orientation.equals("H")) {
            if (y + size > 10) return false;
            for (int j = 0; j < size; j++) if (grid[x][y + j] != '~') return false;
            for (int j = 0; j < size; j++) {
                grid[x][y + j] = 'B';
                totalShipCells++;
            }
        } else {
            if (x + size > 10) return false;
            for (int i = 0; i < size; i++) if (grid[x + i][y] != '~') return false;
            for (int i = 0; i < size; i++) {
                grid[x + i][y] = 'B';
                totalShipCells++;
            }
        }
        return true;
    }

    public String shoot(int x, int y) {
        if (grid[x][y] == 'B') {
            grid[x][y] = 'X';
            hitShipCells++;
            return "💥 Impacto!";
        } else if (grid[x][y] == '~') {
            grid[x][y] = 'O';
            return "🌊 Agua.";
        } else {
            return "Ya disparaste aquí.";
        }
    }

    public char[][] getGrid() {
        return grid;
    }

    public boolean allShipsSunk() {
        return hitShipCells >= totalShipCells && totalShipCells > 0;
    }
}
