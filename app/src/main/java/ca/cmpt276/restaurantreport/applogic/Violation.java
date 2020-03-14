package ca.cmpt276.restaurantreport.applogic;

/*
This class is use to store data of each Violation
 */
public class Violation {
    private String violationCode;
    private String violationCriticality;
    private String violationDescriptor;

    public Violation(String violationCode, String violationCriticality, String violationDescriptor) {
        this.violationCode = "";
        this.violationCriticality = "";
        this.violationDescriptor = "";
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

    public boolean isCritical(){
        return violationCriticality.equals("Critical");
    }

    public String getDescription()
    {
        return  violationDescriptor + "\n";
    }
    @Override
    public String toString() {
        return "Violation{" +
                "violationCode='" + violationCode + '\'' +
                ", violationCriticality='" + violationCriticality + '\'' +
                ", violationDescriptor='" + violationDescriptor + '\'' +
                '}';
    }
}
