package ExceptionClasses;

public class ComponentNotCompatibleException extends RuntimeException {
    public ComponentNotCompatibleException(String errorMessage) {
        super(errorMessage);
    }
}
