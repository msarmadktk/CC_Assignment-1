public class ErrorHandler {
    public void reportError(String message, int line, int column) {
        System.err.printf("Error at line %d, column %d: %s%n", line, column, message);
    }
}
