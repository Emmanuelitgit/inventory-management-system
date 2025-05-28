package inventory_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupLoader implements CommandLineRunner {

    private final InventoryService inventoryService;
    private final VendorService vendorService;
    private final IssueItemService issueItemService;

    @Autowired
    public StartupLoader(InventoryService inventoryService, VendorService vendorService, IssueItemService issueItemService) {
        this.inventoryService = inventoryService;
        this.vendorService = vendorService;
        this.issueItemService = issueItemService;
    }

    @Override
    public void run(String... args) {
        inventoryService.getItems(false);
        vendorService.getVendors(false);
        issueItemService.getAllIssuedItems(false);
        System.out.println("✅ Items loaded into data structures at startup.");
    }
}
