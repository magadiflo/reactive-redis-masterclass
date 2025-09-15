package dev.magadiflo.performance.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "products")
public class Product {
    @Id
    private Integer id;
    private String description;
    private double price;
}
