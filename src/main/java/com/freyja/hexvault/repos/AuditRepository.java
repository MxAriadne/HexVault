package com.freyja.hexvault.repos;

import com.freyja.hexvault.entities.Audit;
import org.springframework.data.repository.CrudRepository;

public interface AuditRepository extends CrudRepository<Audit, Integer> {
}
