import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Grid {
    private final int width;
    private final int height;
    private final Set<String> obstacles = new HashSet<>();

    public Grid(int width, int height, List<int[]> obstacleCoords) {
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

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean isObstacle(int x, int y) {
        return obstacles.contains(coordKey(x, y));
    }

    public GridCell getCell(int x, int y) {
        if (!inBounds(x, y)) return null;
        return isObstacle(x, y) ? new ObstacleCell() : new FreeCell();
    }

    private static String coordKey(int x, int y) {
        return x + ":" + y;
    }
}