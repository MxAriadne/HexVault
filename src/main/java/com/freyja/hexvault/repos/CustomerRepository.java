package com.freyja.hexvault.repos;

import com.freyja.hexvault.entities.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
}
