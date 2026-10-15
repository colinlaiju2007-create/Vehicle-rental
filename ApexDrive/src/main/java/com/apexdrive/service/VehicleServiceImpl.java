package com.apexdrive.service;

import com.apexdrive.dto.VehicleDto;
import com.apexdrive.exception.ResourceNotFoundException;
import com.apexdrive.model.*;
import com.apexdrive.repository.BookingRepository;
import com.apexdrive.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of VehicleService managing fleet database operations and polymorphic instantiation.
 */
@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, BookingRepository bookingRepository) {
        this.vehicleRepository = vehicleRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByAvailableTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> filterVehicles(VehicleCategory category, Double maxPrice, LocalDate pickupDate, LocalDate returnDate) {
        if (pickupDate != null && returnDate != null) {
            return vehicleRepository.filterAvailableVehicles(category, maxPrice, pickupDate, returnDate);
        }
        
        List<Vehicle> list = vehicleRepository.findByAvailableTrue();
        return list.stream()
                .filter(v -> category == null || v.getCategory() == category)
                .filter(v -> maxPrice == null || v.getDailyPrice() <= maxPrice)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID " + id + " was not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVehicleAvailableForDates(Long vehicleId, LocalDate pickupDate, LocalDate returnDate) {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (!Boolean.TRUE.equals(vehicle.getAvailable())) {
            return false;
        }
        if (pickupDate == null || returnDate == null) {
            return true;
        }
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(vehicleId, pickupDate, returnDate);
        return overlapping.isEmpty();
    }

    @Override
    public Vehicle createVehicle(VehicleDto dto) {
        Vehicle vehicle = instantiatePolymorphicVehicle(dto);
        mapDtoToVehicle(dto, vehicle);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle updateVehicle(Long id, VehicleDto dto) {
        Vehicle existing = getVehicleById(id);
        mapDtoToVehicle(dto, existing);
        return vehicleRepository.save(existing);
    }

    @Override
    public Vehicle toggleVehicleAvailability(Long id) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setAvailable(!Boolean.TRUE.equals(vehicle.getAvailable()));
        return vehicleRepository.save(vehicle);
    }

    @Override
    public void deleteVehicle(Long id) {
        Vehicle vehicle = getVehicleById(id);
        vehicleRepository.delete(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalVehicles() {
        return vehicleRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countAvailableVehicles() {
        return vehicleRepository.findByAvailableTrue().size();
    }

    /**
     * OOP Factory Method: Instantiates specialized subclasses demonstrating polymorphism.
     */
    private Vehicle instantiatePolymorphicVehicle(VehicleDto dto) {
        if (dto.getCategory() == null) {
            return new Vehicle();
        }
        return switch (dto.getCategory()) {
            case LUXURY -> {
                LuxuryVehicle luxury = new LuxuryVehicle();
                if (dto.getConciergeFee() != null) luxury.setConciergeFee(dto.getConciergeFee());
                if (dto.getChauffeurAvailable() != null) luxury.setChauffeurAvailable(dto.getChauffeurAvailable());
                yield luxury;
            }
            case SPORTS -> {
                SportsVehicle sports = new SportsVehicle();
                if (dto.getTopSpeedKph() != null) sports.setTopSpeedKph(dto.getTopSpeedKph());
                if (dto.getTrackPackage() != null) sports.setTrackPackage(dto.getTrackPackage());
                if (dto.getPerformanceInsuranceFee() != null) sports.setPerformanceInsuranceFee(dto.getPerformanceInsuranceFee());
                yield sports;
            }
            case ELECTRIC -> {
                ElectricVehicle ev = new ElectricVehicle();
                if (dto.getBatteryRangeKm() != null) ev.setBatteryRangeKm(dto.getBatteryRangeKm());
                if (dto.getFastChargingSupport() != null) ev.setFastChargingSupport(dto.getFastChargingSupport());
                yield ev;
            }
            default -> new Vehicle();
        };
    }

    private void mapDtoToVehicle(VehicleDto dto, Vehicle vehicle) {
        vehicle.setName(dto.getName());
        vehicle.setCategory(dto.getCategory());
        vehicle.setDailyPrice(dto.getDailyPrice());
        vehicle.setSpecifications(dto.getSpecifications());
        vehicle.setTransmission(dto.getTransmission());
        vehicle.setSeatingCapacity(dto.getSeatingCapacity());
        vehicle.setFuelType(dto.getFuelType());
        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            vehicle.setImageUrl(dto.getImageUrl());
        }
        if (dto.getAvailable() != null) {
            vehicle.setAvailable(dto.getAvailable());
        }
        if (dto.getMileageLimitKmPerDay() != null) {
            vehicle.setMileageLimitKmPerDay(dto.getMileageLimitKmPerDay());
        }

        if (vehicle instanceof LuxuryVehicle luxury) {
            if (dto.getConciergeFee() != null) luxury.setConciergeFee(dto.getConciergeFee());
            if (dto.getChauffeurAvailable() != null) luxury.setChauffeurAvailable(dto.getChauffeurAvailable());
        } else if (vehicle instanceof SportsVehicle sports) {
            if (dto.getTopSpeedKph() != null) sports.setTopSpeedKph(dto.getTopSpeedKph());
            if (dto.getTrackPackage() != null) sports.setTrackPackage(dto.getTrackPackage());
            if (dto.getPerformanceInsuranceFee() != null) sports.setPerformanceInsuranceFee(dto.getPerformanceInsuranceFee());
        } else if (vehicle instanceof ElectricVehicle ev) {
            if (dto.getBatteryRangeKm() != null) ev.setBatteryRangeKm(dto.getBatteryRangeKm());
            if (dto.getFastChargingSupport() != null) ev.setFastChargingSupport(dto.getFastChargingSupport());
        }
    }
}
