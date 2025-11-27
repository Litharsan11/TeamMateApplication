package com.teammate.exception;

/**
 * Exception thrown when input validation fails
 */
public class ValidationException extends Exception {
    private String fieldName;
    private String invalidValue;
    private String validationRule;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public ValidationException(String message, String fieldName, String invalidValue) {
        super(message);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    public ValidationException(String message, String fieldName, String invalidValue, String validationRule) {
        super(message);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.validationRule = validationRule;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public String getValidationRule() {
        return validationRule;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationException: ").append(getMessage());
        if (fieldName != null) {
            sb.append(" [Field: ").append(fieldName).append("]");
        }
        if (invalidValue != null) {
            sb.append(" [Value: ").append(invalidValue).append("]");
        }
        if (validationRule != null) {
            sb.append(" [Rule: ").append(validationRule).append("]");
        }
        return sb.toString();
    }
}