package com.freyja.hexvault.repos;

import com.freyja.hexvault.entities.PurchaseOrder;
import org.springframework.data.repository.CrudRepository;

public interface PurchaseOrderRepository extends CrudRepository<PurchaseOrder, Integer> {
}
