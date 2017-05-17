package com.models;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by karan on 5/14/2017.
 */
public class SearchClass {

    private ArrayList<Long> company;
    private ArrayList<String> location;
    private ArrayList<String> search;
    private ArrayList<String> salary;

    public ArrayList<Long> getCompany() {
        return company;
    }

    public void setCompany(ArrayList<Long> company) {
        this.company = company;
    }

    public ArrayList<String> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<String> location) {
        this.location = location;
    }

    public ArrayList<String> getSearch() {
        return search;
    }

    public void setSearch(ArrayList<String> search) {
        this.search = search;
    }

    public ArrayList<String> getSalary() {
        return salary;
    }

    public void setSalary(ArrayList<String> salary) {
        this.salary = salary;
    }
}
