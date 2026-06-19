package com.example.SMS.pojo;

// POJO Plain Class Placeholder
// Location: src/main/java/com/example/SMS/pojo/Transport.java
/**
 * Transport POJO placeholder class.
 * This class represents transport details, including route name, vehicle number, and driver contact info.
 */
public class Transport {
    private Long id;
    private String routeName;
    private String vehicleNumber;
    private String driverName;
    private String driverPhoneNumber;

    public Transport() {}

    public Transport(Long id, String routeName, String vehicleNumber, String driverName, String driverPhoneNumber) {
        this.id = id;
        this.routeName = routeName;
        this.vehicleNumber = vehicleNumber;
        this.driverName = driverName;
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public String getDriverPhoneNumber() { return driverPhoneNumber; }
    public void setDriverPhoneNumber(String driverPhoneNumber) { this.driverPhoneNumber = driverPhoneNumber; }
}
