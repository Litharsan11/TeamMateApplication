/** Indicates an error while reading or writing files during application processes.
 *  Wraps detailed messages or underlying causes for easier debugging. */
package com.teammate.exception;

public class FileProcessingException extends Exception {
    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}