package com.apexdrive.dto;

public class AdminStatsResponse {

    private long totalVehicles;
    private long availableVehicles;
    private long totalBookings;
    private long activeBookings;
    private long cancelledBookings;
    private double totalRevenue;

    public AdminStatsResponse() {
    }

    public AdminStatsResponse(long totalVehicles, long availableVehicles, long totalBookings,
                              long activeBookings, long cancelledBookings, double totalRevenue) {
        this.totalVehicles = totalVehicles;
        this.availableVehicles = availableVehicles;
        this.totalBookings = totalBookings;
        this.activeBookings = activeBookings;
        this.cancelledBookings = cancelledBookings;
        this.totalRevenue = Math.round(totalRevenue * 100.0) / 100.0;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public long getAvailableVehicles() {
        return availableVehicles;
    }

    public void setAvailableVehicles(long availableVehicles) {
        this.availableVehicles = availableVehicles;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getActiveBookings() {
        return activeBookings;
    }

    public void setActiveBookings(long activeBookings) {
        this.activeBookings = activeBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
