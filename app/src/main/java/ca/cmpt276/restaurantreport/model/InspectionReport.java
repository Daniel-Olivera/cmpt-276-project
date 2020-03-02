package ca.cmpt276.restaurantreport.model;

public class InspectionReport {
    private String trackNum;
    private String inspectDate;
    private String inspectType;
    private String numCriticalIssue;
    private String numNonCriticalIssue;
    private String hazardRating;
    private String violLump;

    public String getHazardRating() {
        return hazardRating;
    }

    public void setHazardRating(String hazardRating) {
        this.hazardRating = hazardRating;
    }

    public String getTrackNum() {
        return trackNum;
    }

    public void setTrackNum(String trackNum) {
        this.trackNum = trackNum;
    }

    public String getInspectDate() {
        return inspectDate;
    }

    public void setInspectDate(String inspectDate) {
        this.inspectDate = inspectDate;
    }

    public String getInspectType() {
        return inspectType;
    }

    public void setInspectType(String inspectType) {
        this.inspectType = inspectType;
    }

    public String getNumCriticalIssue() {
        return numCriticalIssue;
    }

    public void setNumCriticalIssue(String numCriticalIssue) {
        this.numCriticalIssue = numCriticalIssue;
    }

    public String getNumNonCriticalIssue() {
        return numNonCriticalIssue;
    }

    public void setNumNonCriticalIssue(String numNonCriticalIssue) {
        this.numNonCriticalIssue = numNonCriticalIssue;
    }

    public String getViolLump() {
        return violLump;
    }

    public void setViolLump(String violLump) {
        this.violLump = violLump;
    }
}
