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
public class ItemPayload {
    private UUID id;
    private String name;
    private float unitPrice;
    private float sellingPrice;
    private float grossPrice;
    private String category;
    private UUID categoryId;
}
