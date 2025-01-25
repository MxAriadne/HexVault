package com.freyja.hexvault.repos;

import com.freyja.hexvault.entities.Device;
import com.freyja.hexvault.entities.Note;
import com.freyja.hexvault.entities.PartsIndividual;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface PartsRepository extends CrudRepository<PartsIndividual, Integer> {
    Collection<PartsIndividual> findAllByDevice(Device device);
}
