package ExceptionClasses;

public class ComputerNotInitializedException extends RuntimeException {
    public ComputerNotInitializedException(String errorMessage) {
        super(errorMessage);
    }
}
