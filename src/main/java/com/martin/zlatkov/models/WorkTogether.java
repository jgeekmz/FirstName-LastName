package com.martin.zlatkov.models;

public class WorkTogether {
    public int ID1;
    public int ID2;
    public int projectID;
    public long daysTotalTogether;

    public WorkTogether(int ID1, int ID2, int projectID, int daysTotalTogether) {
        this.ID1 = ID1;
        this.ID2 = ID2;
        this.projectID = projectID;
        this.daysTotalTogether = daysTotalTogether;
    }

    public int getID1() {
        return ID1;
    }

    public void setID1(int ID1) {
        this.ID1 = ID1;
    }

    public int getID2() {
        return ID2;
    }

    public void setID2(int ID2) {
        this.ID2 = ID2;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getDaysTotalTogether() {
        return (int) daysTotalTogether;
    }

    public void setDaysTotalTogether(int daysTotalTogether) {
        this.daysTotalTogether = daysTotalTogether;
    }

    @Override
    public String toString() {
        return "ID1=" + ID1 + ", ID2=" + ID2 + ", projectID=" + projectID + ", daysTotalTogether=" + daysTotalTogether;
    }
}
