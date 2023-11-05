package exception;

/**
 * This method throws an exception if there is a problem with the searcher
 */
public class SearchException extends Exception{
    private final Throwable cause;

    public SearchException(String message) {
        super(message);
        this.cause = null;
    }
    public SearchException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }
}

