package com.lab5com.model;

public class ResultCa {
    private Long id;
    private double ca;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCa() {
        return ca;
    }

    public void setCa(double ca) {
        this.ca = ca;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", ca=" + ca +
                '}';
    }
}
