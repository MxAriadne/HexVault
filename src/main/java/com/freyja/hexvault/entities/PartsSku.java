package com.freyja.hexvault.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "parts_sku")
public class PartsSku {
    @Id
    @ColumnDefault("nextval('parts_sku_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "part_name", nullable = false)
    private String partName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

}