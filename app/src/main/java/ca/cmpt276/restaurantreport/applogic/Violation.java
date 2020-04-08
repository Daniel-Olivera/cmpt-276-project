package ca.cmpt276.restaurantreport.applogic;

/*
This class is use to store data of each Violation
 */
public class Violation {
    private String violationCode;
    private String violationCriticality;
    private String violationDescriptor;

    public Violation(String violationCode, String violationCriticality, String violationDescriptor) {
        this.violationCode = violationCode;
        this.violationCriticality = violationCriticality;
        this.violationDescriptor = violationDescriptor;
    }

    public Violation(){}

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

    public String getDescription() {
        return  violationDescriptor + "\n";
    }
}
