package inventory_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private UUID id;
    private String name;
    private Float unitPrice;
    private Float sellingPrice;
    private Float buyingPrice;
    private Float grossPrice;
    private Integer quantity;
    private String description;
    private String category;
    private String vendor;
}
