import java.util.List;

/**
 * Mars Rover: domain model for a rover on a grid.
 * This class is the "Rover" in the Command pattern example.
 */
public final class Rover {
    private Position pos;
    private final Grid grid;
    private boolean lastMoveBlocked = false;
    private int movesSinceLastReport = 0;

    public Rover(int startX, int startY, Dir startDir, Grid grid) {
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
        status += lastMoveBlocked
                ? "Movement blocked by obstacle or boundary."
                : "No obstacle detected in path.";
        return status;
    }

    // Optional: a simple API to get a summary
    String finalSummary() {
        Position p = pos;
        return "Final Position: (" + p.x + ", " + p.y + ", " + p.dir.toFullName() + ")";
    }
}