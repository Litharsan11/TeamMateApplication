/** Exception thrown when team formation fails due to invalid data or logic issues.
 *  Provides clear reporting for errors encountered during the formation process. */

package com.teammate.exception;

public class TeamFormationException extends Exception {
    public TeamFormationException(String message) {
        super(message);
    }

    public TeamFormationException(String message, Throwable cause) {
        super(message, cause);
    }
}