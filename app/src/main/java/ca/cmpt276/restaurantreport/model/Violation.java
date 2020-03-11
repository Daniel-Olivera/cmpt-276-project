package ca.cmpt276.restaurantreport.model;

public class Violation {
    private String violationCode;
    private String violationCriticality;
    private String violationDescriptor;

    public String getViolationCode() {
        return violationCode;
    }

    void setViolationCode(String violationCode) {
        this.violationCode = violationCode;
    }

    public String getViolationCriticality() {
        return violationCriticality;
    }

    void setViolationCriticality(String violationCriticality) {
        this.violationCriticality = violationCriticality;
    }

    void setViolationDescriptor(String violationDescriptor) {
        this.violationDescriptor = violationDescriptor;
    }

}
