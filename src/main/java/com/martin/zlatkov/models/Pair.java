package com.martin.zlatkov.models;

import javax.persistence.*;

@Entity
public class Pair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int emp1;
    private int emp2;
    private int projectID;
    private int totalDaysWorkedTogether;

    private String fileDBName;

    public Pair() {
    }

    public String getFileDBName() { return fileDBName; }
    public void setFileDBName(String fileDBName) { this.fileDBName = fileDBName; }

    public int getEmp1() {
        return emp1;
    }

    public void setEmp1(int emp1) {
        this.emp1 = emp1;
    }

    public int getEmp2() {
        return emp2;
    }

    public void setEmp2(int emp2) {
        this.emp2 = emp2;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public int getTotalDaysWorkedTogether() {
        return totalDaysWorkedTogether;
    }

    public void setTotalDaysWorkedTogether(int totalDaysWorkedTogether) {
        this.totalDaysWorkedTogether = totalDaysWorkedTogether;
    }

}
