package errorhandling;

import play.api.UsefulException;

/**
 * Created by florian on 18.12.15.
 */
public class OcrException extends UsefulException {
    public OcrException(String message, Throwable cause) {
        super(message, cause);
    }

    public OcrException(String message) {
        super(message);
    }
}
