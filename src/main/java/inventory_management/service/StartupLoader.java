package inventory_management.service;

import inventory_management.models.Item;
import inventory_management.service.InventoryService;
import inventory_management.dto.ItemPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupLoader implements CommandLineRunner {

    private final InventoryService inventoryService;

    @Autowired
    public StartupLoader(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void run(String... args) {
        inventoryService.getItems();
        System.out.println("✅ Items loaded into data structures at startup.");
    }
}
