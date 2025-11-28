/** Thrown when a personality score is out of valid range or logically incorrect.
 *  Extends IllegalArgumentException for clearer validation-specific errors. */

package com.teammate.exception;

public class InvalidPersonalityScoreException extends IllegalArgumentException {
    public InvalidPersonalityScoreException(String message) {
        super(message);
    }

    public InvalidPersonalityScoreException(String message, Throwable cause) {
        super(message, cause);
    }
}