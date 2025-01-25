package com.freyja.hexvault.repos;

import com.freyja.hexvault.entities.Device;
import com.freyja.hexvault.entities.Note;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface NotesRepository extends CrudRepository<Note, Integer> {
    Collection<Note> findAllByDevice(Device device);

    Device getDeviceByNote(Note note);
}
