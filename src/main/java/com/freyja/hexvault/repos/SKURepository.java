package com.freyja.hexvault.repos;

import com.freyja.hexvault.entities.PartsSku;
import com.freyja.hexvault.entities.PoItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SKURepository extends CrudRepository<PartsSku, Integer> {
}

