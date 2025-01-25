package com.freyja.hexvault.repos;

import com.freyja.hexvault.entities.PoItem;
import com.freyja.hexvault.entities.PurchaseOrder;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface POItemRepository extends CrudRepository<PoItem, Integer> {
    List<PoItem> findAllByPoId(Integer poId);
}
