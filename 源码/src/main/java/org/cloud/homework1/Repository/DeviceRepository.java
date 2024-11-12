package org.cloud.homework1.Repository;

import org.cloud.homework1.Entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findById(Long Id);
}