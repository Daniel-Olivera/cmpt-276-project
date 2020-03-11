package ca.cmpt276.restaurantreport.model;
/*
* Serves as the backend for violation code lookup
* Used for displaying abridged versions of each relevant violation
* */
public class ShortViolation {
    private int violationCode;
    private String shortDescriptor;

    ShortViolation(int violationCode, String shortDescriptor) {
        this.violationCode = violationCode;
        this.shortDescriptor = shortDescriptor;
    }

    public int getViolationCode() {
        return violationCode;
    }

    public String getShortDescriptor() {
        return shortDescriptor;
    }

}
