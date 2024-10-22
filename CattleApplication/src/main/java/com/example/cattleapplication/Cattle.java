package com.example.cattleapplication;

public class Cattle {
    private String cattleId;
    private String cattleType;
    private String breed;
    private int age;
    private double weight;
    private String healthStatus;
    private String ownerName; // Owner name property

    // Updated constructor to include ownerName
    public Cattle(String cattleId, String cattleType, String breed, int age, double weight, String healthStatus, String ownerName) {
        this.cattleId = cattleId;
        this.cattleType = cattleType;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.healthStatus = healthStatus;
        this.ownerName = ownerName; // Initialize ownerName
    }

    // Getters
    public String getCattleId() {
        return cattleId;
    }

    public String getCattleType() {
        return cattleType;
    }

    public String getBreed() {
        return breed;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public String getOwnerName() {
        return ownerName; // Getter for ownerName
    }

    // Optional: Override toString for better readability
    @Override
    public String toString() {
        return "Cattle{" +
                "cattleId='" + cattleId + '\'' +
                ", cattleType='" + cattleType + '\'' +
                ", breed='" + breed + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", healthStatus='" + healthStatus + '\'' +
                ", ownerName='" + ownerName + '\'' +
                '}';
    }
}
