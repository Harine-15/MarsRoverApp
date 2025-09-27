// Direction enum for clarity
public enum Dir {
    N, E, S, W;

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