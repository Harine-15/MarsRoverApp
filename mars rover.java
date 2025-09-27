import java.util.*;  

// -- Domain: Mars Rover on a grid with obstacles --  

// Direction enum for clarity  
enum Dir {  
    N, E, S, W;  

    // Helpers to rotate  
    public Dir turnLeft() {  
        switch (this) {  
            case N: return W;  
            case W: return S;  
            case S: return E;  
            case E: return N;  
        }  
        throw new IllegalStateException();  
    }  

    public Dir turnRight() {  
        switch (this) {  
            case N: return E;  
            case E: return S;  
            case S: return W;  
            case W: return N;  
        }  
        throw new IllegalStateException();  
    }  

    public String toFullName() {  
        switch (this) {  
            case N: return "North";  
            case E: return "East";  
            case S: return "South";  
            case W: return "West";  
        }  
        return "";  
    }  
}  

// Position value object  
final class Position {  
    final int x;  
    final int y;  
    final Dir dir;  

    Position(int x, int y, Dir dir) {  
        this.x = x;  
        this.y = y;  
        this.dir = dir;  
    }  

    Position moveForward() {  
        switch (dir) {  
            case N: return new Position(x, y + 1, dir);  
            case S: return new Position(x, y - 1, dir);  
            case E: return new Position(x + 1, y, dir);  
            case W: return new Position(x - 1, y, dir);  
        }  
        throw new IllegalStateException();  
    }  

    Position turnLeft() {  
        return new Position(x, y, dir.turnLeft());  
    }  

    Position turnRight() {  
        return new Position(x, y, dir.turnRight());  
    }  

    @Override  
    public String toString() {  
        return "(" + x + ", " + y + ", " + dir.toFullName() + ")";  
    }  
}  

// -- Structural: Grid with obstacles (Composite-like) --  

// GridCell concept (could be extended for FreeCell/ObstacleCell if desired)  
interface GridCell {  
    boolean isObstacle();  
}  

// Concrete cells  
final class FreeCell implements GridCell {  
    public boolean isObstacle() { return false; }  
}  
final class ObstacleCell implements GridCell {  
    public boolean isObstacle() { return true; }  
}  

// Grid holds bounds and obstacles  
final class Grid {  
    private final int width;  
    private final int height;  
    // store obstacles for O(1) lookup  
    private final Set<String> obstacles = new HashSet<>();  

    Grid(int width, int height, List<int[]> obstacleCoords) {  
        this.width = width;  
        this.height = height;  
        if (obstacleCoords != null) {  
            for (int[] p : obstacleCoords) {  
                if (p != null && p.length >= 2) {  
                    obstacles.add(coordKey(p[0], p[1]));  
                }  
            }  
        }  
    }  

    boolean inBounds(int x, int y) {  
        return x >= 0 && x < width && y >= 0 && y < height;  
    }  

    boolean isObstacle(int x, int y) {  
        return obstacles.contains(coordKey(x, y));  
    }  

    GridCell getCell(int x, int y) {  
        if (!inBounds(x, y)) return null;  
        return isObstacle(x, y) ? new ObstacleCell() : new FreeCell();  
    }  

    private static String coordKey(int x, int y) {  
        return x + ":" + y;  
    }  
}  

// -- Behavioral: Command Pattern --  

// Command interface  
interface Command {  
    void execute();  
}  

// Rover that uses Grid  
final class Rover {  
    private Position pos;  
    private final Grid grid;  
    private boolean lastMoveBlocked = false;  
    private int movesSinceLastReport = 0;  

    Rover(int startX, int startY, Dir startDir, Grid grid) {  
        this.pos = new Position(startX, startY, startDir);  
        this.grid = grid;  
    }  

    // Expose internal state for command objects to mutate  
    void applyMove() {  
        Position next = pos.moveForward();  
        if (grid.inBounds(next.x, next.y) && !grid.isObstacle(next.x, next.y)) {  
            pos = next;  
            lastMoveBlocked = false;  
        } else {  
            lastMoveBlocked = true;  
        }  
        movesSinceLastReport++;  
    }  

    void applyTurnLeft() {  
        pos = pos.turnLeft();  
    }  

    void applyTurnRight() {  
        pos = pos.turnRight();  
    }  

    Position getPosition() {  
        return pos;  
    }  

    boolean wasLastMoveBlocked() {  
        return lastMoveBlocked;  
    }  

    String statusReport() {  
        String status = "Rover is at " + pos.toString() + ". ";  
        status += lastMoveBlocked ? "Movement blocked by obstacle or boundary." : "No obstacle detected in path.";  
        return status;  
    }  

    // Optional: a simple API to get a summary  
    String finalSummary() {  
        Position p = pos;  
        return "Final Position: (" + p.x + ", " + p.y + ", " + p.dir.toFullName() + ")";  
    }  
}  

// Concrete commands  
final class MoveCommand implements Command {  
    private final Rover rover;  

    MoveCommand(Rover rover) { this.rover = rover; }  

    @Override  
    public void execute() { rover.applyMove(); }  
}  

final class TurnLeftCommand implements Command {  
    private final Rover rover;  

    TurnLeftCommand(Rover rover) { this.rover = rover; }  

    @Override  
    public void execute() { rover.applyTurnLeft(); }  
}  

final class TurnRightCommand implements Command {  
    private final Rover rover;  

    TurnRightCommand(Rover rover) { this.rover = rover; }  

    @Override  
    public void execute() { rover.applyTurnRight(); }  
}  

// -- Runner / Demo --  

public class MarsRoverDemo {  
    public static void main(String[] args) {  
        // 1. Grid: 10x10 with obstacles at (2,2) and (3,5)  
        List<int[]> obstacles = Arrays.asList(  
                new int[]{2, 2},  
                new int[]{3, 5}  
        );  
        Grid grid = new Grid(10, 10, obstacles);  

        // 2. Starting state: (0,0) facing North  
        Rover rover = new Rover(0, 0, Dir.N, grid);  

        // 3. Commands: ['M','M','R','M','L','M']  
        List<Command> commands = Arrays.asList(  
                new MoveCommand(rover),     // M  
                new MoveCommand(rover),     // M  
                new TurnRightCommand(rover),// R  
                new MoveCommand(rover),     // M  
                new TurnLeftCommand(rover), // L  
                new MoveCommand(rover)      // M  
        );  

        // 4. Execute  
        for (Command c : commands) {  
            c.execute();  
        }  

        // 5. Status / final outputs  
        System.out.println("Final Position: " + rover.getPosition().