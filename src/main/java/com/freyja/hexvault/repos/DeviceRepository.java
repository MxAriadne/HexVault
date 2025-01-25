package com.freyja.hexvault.repos;

import com.freyja.hexvault.entities.Device;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface DeviceRepository extends CrudRepository<Device, Integer> {
    Collection<Device> findAllByStatusIsLike(String complete);
}
