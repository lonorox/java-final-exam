package Exceptions;

public class ValidationResult {
    private boolean valid;
    private String reason;

    ValidationResult(boolean valid, String reason) {
        this.valid = valid;
        this.reason = reason;
    }
    public boolean isValid() {
        return valid;
    }
    public String getReason() {
        return reason;
    }
    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult failure(String reason) {
        return new ValidationResult(false, reason);
    }
}