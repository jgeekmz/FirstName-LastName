package com.martin.zlatkov.models;

import java.util.Date;

public class Employee {
    private int ID1;
    private int projectID;
    private Date startDate;
    private Date endDate;

    public Employee() { }

    public int getID1() {
        return ID1;
    }

    public void setID1(int ID1) {
        this.ID1 = ID1;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) { this.projectID = projectID; }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEdnDate() {
        return endDate;
    }

    public void setEdnDate(Date ednDate) {
        this.endDate = ednDate;
    }
}
