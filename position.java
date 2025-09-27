// Position value object
public final class Position {
    final int x;
    final int y;
    final Dir dir;

    public Position(int x, int y, Dir dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
    }

    public Position moveForward() {
        switch (dir) {
            case N: return new Position(x, y + 1, dir);
            case S: return new Position(x, y - 1, dir);
            case E: return new Position(x + 1, y, dir);
            case W: return new Position(x - 1, y, dir);
        }
        throw new IllegalStateException();
    }

    public Position turnLeft() {
        return new Position(x, y, dir.turnLeft());
    }

    public Position turnRight() {
        return new Position(x, y, dir.turnRight());
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + dir.toFullName() + ")";
    }
}