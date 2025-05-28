package inventory_management.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory_tb")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private Float buyingPrice;
    private Float sellingPrice;
    private Integer quantity;
    private String description;
    private Float grossPrice;
    private UUID categoryId;
    private UUID vendorId;
}
