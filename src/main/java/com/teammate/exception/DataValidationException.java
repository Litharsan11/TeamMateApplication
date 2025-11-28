/** Custom exception for handling invalid or improperly formatted data inputs.
 *  Stores the affected field name and invalid value for clearer error reporting. */

package com.teammate.exception;

public class DataValidationException extends Exception {
    private String fieldName;
    private String invalidValue;

    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(String message, String fieldName, String invalidValue) {
        super(message);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    @Override
    public String toString() {
        if (fieldName != null && invalidValue != null) {
            return "DataValidationException: " + getMessage() +
                    " [Field: " + fieldName + ", Value: " + invalidValue + "]";
        }
        return super.toString();
    }
}