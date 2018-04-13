package edu.ithaca.group5;

public class Issue {
    //True if the issue has been solved, false if it has not
    boolean solved;
    //True if the issue has not been looked at by a pharmacist yet
    boolean isNew;
    //Name of the issue
    String name;
    //Name of the account that reported the issue
    String reporter;
    //Description of the issue
    String desc;

    public Issue(String name, String description, String reporter) {
        this.solved = false; this.isNew = true; this.name = name; this.desc = description; this.reporter = reporter;
    }

    public void setSolved() { this.solved = true; }

    public void setOld() { this.isNew = false; }

    public String toString() {
        String out = "";
        out += name;
        if (solved) {
            out += " (SOLVED) ";
        }
        out += "| Reported By ";
        out += reporter;
        out += ": ";
        out += desc;
        return out;
    }
}
