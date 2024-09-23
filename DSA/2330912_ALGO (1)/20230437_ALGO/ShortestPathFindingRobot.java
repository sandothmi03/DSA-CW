import java.util.Scanner;
import java.util.Random;

class Grid {
    private char[][] grid;

    public Grid(int rows, int columns) {
        grid = new char[rows][columns];
        initializeGrid();
    }

    private void initializeGrid() {
        // Initialize grid with empty cells
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = '_'; // Representing empty cell as '_'
            }
        }
    }

    public void addObstacles(double obstaclePercentage) {
        Random random = new Random();
        int obstacleCount = (int) (grid.length * grid[0].length * obstaclePercentage);
        for (int i = 0; i < obstacleCount; i++) {
            int row = random.nextInt(grid.length);
            int col = random.nextInt(grid[0].length);
            if (grid[row][col] != 'S' && grid[row][col] != 'G') {
                grid[row][col] = 'X'; // Representing obstacles as 'X'
            } else {
                i--; // Retry if obstacle is placed on start or end point
            }
        }
    }

    public char[][] getGrid() {
        return grid;
    }

    public void printGrid() {
        System.out.print("   ");
        for (int i = 0; i < grid[0].length; i++) {
            System.out.printf("%2d", i);
        }
        System.out.println();
        for (int i = 0; i < grid.length; i++) {
            System.out.printf("%2d ", i);
            for (int j = 0; j < grid[0].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
}

class Robot {
    private int currentRow;
    private int currentColumn;

    public Robot(int startRow, int startCol) {
        this.currentRow = startRow;
        this.currentColumn = startCol;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public void moveTo(int newRow, int newCol) {
        this.currentRow = newRow;
        this.currentColumn = newCol;
    }
}

class Queue {
    private int front;
    private int rear;
    private int noOfItems;
    private int maxSize;
    private int queueArray[];

    public Queue(int size) {
        maxSize = size;
        front = 0;
        rear = -1;
        noOfItems = 0;
        queueArray = new int[maxSize];
    }

    public void insert(int item) {
        if (rear == maxSize - 1) {
            rear = -1;
        }
        queueArray[++rear] = item;
        noOfItems++;
    }

    public int remove() {
        int temp = queueArray[front++];
        if (front == maxSize) {
            front = 0;
        }
        noOfItems--;
        return temp;
    }

    public boolean isEmpty() {
        return (noOfItems == 0);
    }
}

public class ShortestPathFindingRobot {
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {0, -1}, {0, 1}, {1, 0} // Non-diagonal directions only
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int rows, columns;
        do {
            rows = getPositiveInteger(scanner, "Enter the number of rows (must be more than 1): ");
            columns = getPositiveInteger(scanner, "Enter the number of columns (must be more than 1): ");
            if (rows <= 1 || columns <= 1) {
                System.out.println("Rows and columns must be more than 1. Please try again.");
            }
        } while (rows <= 1 || columns <= 1);

        Grid grid = new Grid(rows, columns);

        // Add obstacles to the grid
        grid.addObstacles(0.3); // Adjust obstacle percentage as needed

        // Print the grid with obstacles
        grid.printGrid();

        Robot robot = initializeRobot(grid);

        int endRow, endCol;
        do {
            endRow = getPointFromUser(scanner, grid.getGrid().length - 1, "Enter the row of the end point: ");
            endCol = getPointFromUser(scanner, grid.getGrid()[0].length - 1, "Enter the column of the end point: ");
            if (grid.getGrid()[endRow][endCol] == 'X') {
                System.out.println("Obstacle found at the ending point. Please choose another point.");
            }
        } while (grid.getGrid()[endRow][endCol] == 'X');

        grid.getGrid()[robot.getCurrentRow()][robot.getCurrentColumn()] = 'S'; // Mark start point on grid
        grid.getGrid()[endRow][endCol] = 'G'; // Mark end point on grid

        // Find and print the shortest path
        int[][] shortestPath = findShortestPath(grid.getGrid(), robot.getCurrentRow(), robot.getCurrentColumn(), endRow, endCol);
        if (shortestPath != null) {
            System.out.println("Shortest path points:");
            for (int[] point : shortestPath) {
                System.out.println("(" + point[0] + ", " + point[1] + ")");
            }
            System.out.println("Grid with shortest path:");
            printGridWithShortestPath(grid.getGrid(), shortestPath);
        } else {
            System.out.println("No available path!");
        }

        scanner.close();
    }

    public static Robot initializeRobot(Grid grid) {
        Scanner scanner = new Scanner(System.in);
        int startRow, startCol;
        do {
            startRow = getPointFromUser(scanner, grid.getGrid().length - 1, "Enter the row of the start point: ");
            startCol = getPointFromUser(scanner, grid.getGrid()[0].length - 1, "Enter the column of the start point: ");
            if (grid.getGrid()[startRow][startCol] == 'X') {
                System.out.println("Obstacle found at the starting point. Please choose another point.");
            }
        } while (grid.getGrid()[startRow][startCol] == 'X');

        return new Robot(startRow, startCol);
    }

    public static int getPositiveInteger(Scanner scanner, String message) {
        int num;
        do {
            System.out.print(message);
            while (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid positive integer.");
                System.out.print(message);
                scanner.next();
            }
            num = scanner.nextInt();
            if (num <= 1) {
                System.out.println("Please enter a positive integer greater than 1.");
            }
        } while (num <= 1);
        return num;
    }

    public static int getPointFromUser(Scanner scanner, int limit, String message) {
        int point;
        do {
            System.out.print(message);
            while (!scanner.hasNextInt()) {
                System.out.println("Please enter a valid integer.");
                System.out.print(message);
                scanner.next();
            }
            point = scanner.nextInt();
            if (point < 0 || point > limit) {
                System.out.println("Please enter a value between 0 and " + limit + ".");
            }
        } while (point < 0 || point > limit);
        return point;
    }

    public static int[][] findShortestPath(char[][] grid, int startRow, int startCol, int endRow, int endCol) {
        int numRows = grid.length;
        int numCols = grid[0].length;
        boolean[][] visited = new boolean[numRows][numCols];
        int[][] distances = new int[numRows][numCols];
        int[][] parentsRow = new int[numRows][numCols];
        int[][] parentsCol = new int[numRows][numCols];
        Queue queue = new Queue(numRows * numCols);

        // Initialize distances to infinity and parents to (-1, -1)
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                distances[i][j] = Integer.MAX_VALUE;
                parentsRow[i][j] = -1;
                parentsCol[i][j] = -1;
            }
        }

        // Start point has distance 0
        distances[startRow][startCol] = 0;
        queue.insert(startRow * numCols + startCol);

        while (!queue.isEmpty()) {
            int currentCell = queue.remove();
            int row = currentCell / numCols;
            int col = currentCell % numCols;
            visited[row][col] = true;

            if (row == endRow && col == endCol) {
                // Reconstruct path
                return reconstructPath(startRow, startCol, endRow, endCol, parentsRow, parentsCol);
            }

            // Explore neighbors
            for (int[] dir : DIRECTIONS) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                if (isValid(grid, visited, newRow, newCol)) {
                    queue.insert(newRow * numCols + newCol);
                    visited[newRow][newCol] = true;
                    distances[newRow][newCol] = distances[row][col] + 1;
                    parentsRow[newRow][newCol] = row;
                    parentsCol[newRow][newCol] = col;
                }
            }
        }

        return null; // No path found
    }

    private static boolean isValid(char[][] grid, boolean[][] visited, int row, int col) {
        int numRows = grid.length;
        int numCols = grid[0].length;
        return row >= 0 && row < numRows && col >= 0 && col < numCols && !visited[row][col] && grid[row][col] != 'X';
    }

    private static int[][] reconstructPath(int startRow, int startCol, int endRow, int endCol, int[][] parentsRow, int[][] parentsCol) {
        int currentRow = endRow;
        int currentCol = endCol;
        int length = 0;

        // Count the length of the path
        while (currentRow != startRow || currentCol != startCol) {
            length++;
            int newRow = parentsRow[currentRow][currentCol];
            int newCol = parentsCol[currentRow][currentCol];
            currentRow = newRow;
            currentCol = newCol;
        }

        // Reconstruct the path
        int[][] path = new int[length + 1][2];
        currentRow = endRow;
        currentCol = endCol;
        for (int i = length; i >= 0; i--) {
            path[i][0] = currentRow;
            path[i][1] = currentCol;
            int newRow = parentsRow[currentRow][currentCol];
            int newCol = parentsCol[currentRow][currentCol];
            currentRow = newRow;
            currentCol = newCol;
        }

        return path;
    }

    public static void printGridWithShortestPath(char[][] grid, int[][] shortestPath) {
        for (int[] point : shortestPath) {
            grid[point[0]][point[1]] = '*'; // Representing path as '*'
        }
        grid[shortestPath[0][0]][shortestPath[0][1]] = 'S'; // Marking start point as 'S'
        grid[shortestPath[shortestPath.length - 1][0]][shortestPath[shortestPath.length - 1][1]] = 'G'; // Marking end point as 'G'
        System.out.print("   ");
        for (int i = 0; i < grid[0].length; i++) {
            System.out.printf("%2d", i);
        }
        System.out.println();
        for (int i = 0; i < grid.length; i++) {
            System.out.printf("%2d ", i);
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'S' || grid[i][j] == 'G') {
                    System.out.print(grid[i][j] + " ");
                } else if (grid[i][j] == '*') {
                    System.out.print("* ");
                } else {
                    System.out.print(grid[i][j] + " ");
                }
            }
            System.out.println();
        }
    }
}
