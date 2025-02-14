package com.freyja.hexvault.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "parts_sku")
public class PartsSku {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "part_name", nullable = false)
    private String partName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "is_service")
    private Boolean isService;

}