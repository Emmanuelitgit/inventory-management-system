package inventory_management.service;

import inventory_management.exception.BadRequestException;
import inventory_management.exception.NotFoundException;
import inventory_management.models.Inventory;
import inventory_management.models.IssueItem;
import inventory_management.models.Vendor;
import inventory_management.repo.InventoryRepo;
import inventory_management.repo.IssueItemRepo;
import inventory_management.repo.VendorRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
public class IssueItemService {

    private final IssueItemRepo issueItemRepo;
    private final InventoryRepo inventoryRepo;
    private final VendorRepo vendorRepo;
    private final List<Map<String, Object>> itemList = new ArrayList<>();
    private final InventoryService inventoryService;

    @Autowired
    public IssueItemService(IssueItemRepo issueItemRepo, InventoryRepo inventoryRepo, VendorRepo vendorRepo, InventoryService inventoryService) {
        this.issueItemRepo = issueItemRepo;
        this.inventoryRepo = inventoryRepo;
        this.vendorRepo = vendorRepo;
        this.inventoryService = inventoryService;
    }

    /**
     * @description Issue (create) a new item and update inventory quantity accordingly
     * @author
     * @param issueItemPayload the issued item to be stored
     * @return IssueItem
     * @date 26, May 2025
     */
    public IssueItem issueItem(IssueItem issueItemPayload) {

        if (issueItemPayload == null){
            throw new BadRequestException("payload cannot be null");
        }

        issueItemPayload.setCreatedAt(ZonedDateTime.now());
        IssueItem issueItem = issueItemRepo.save(issueItemPayload);

        Vendor vendor = vendorRepo.findById(UUID.fromString(issueItemPayload.getVendorId()))
                .orElseThrow(() -> new NotFoundException("Vendor record not found"));

        Inventory inventory = updateInventory(issueItem.getQuantity(), issueItem.getProductId());

        Map<String, Object> itemMap = createItemMap(issueItemPayload, inventory, vendor);
        itemList.add(itemMap);

        // refresh inventory data structures
        inventoryService.refreshMemoryStructures();

        return issueItem;
    }

    /**
     * @description Fetch all issued items either from memory or from database
     * @author
     * @param fromMemory boolean flag to determine data source
     * @return List of issued item maps
     * @date 26, May 2025
     */
    public List<Map<String, Object>> getAllIssuedItems(boolean fromMemory) {
        if (fromMemory) {
            log.info("fetching from data structures{}", itemList);
            return itemList;
        }
        itemList.clear();
        List<IssueItem> dbItems = issueItemRepo.findAll();
        log.info("fetching from database{}", dbItems);
        for (IssueItem item : dbItems) {
            Vendor vendor = vendorRepo.findById(UUID.fromString(item.getVendorId())).orElse(null);
            Inventory inventory = inventoryRepo.findById(item.getProductId()).orElse(null);
            itemList.add(createItemMap(item, inventory, vendor));
        }
        return itemList;
    }

    /**
     * @description Fetch a specific issued item by ID and return it as a map object
     * @author
     * @param id UUID of the issued item
     * @return Map<String, Object> representing issued item details
     * @date 26, May 2025
     */
    public Map<String, Object> getIssuedItemById(UUID id) {
        IssueItem item = issueItemRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Issued item not found"));

        Vendor vendor = vendorRepo.findById(UUID.fromString(item.getVendorId())).orElse(null);
        Inventory inventory = inventoryRepo.findById(item.getProductId()).orElse(null);

        return createItemMap(item, inventory, vendor);
    }

    /**
     * @description Update details of an existing issued item and refresh in-memory list
     * @author
     * @param updatedItem IssueItem object with updated fields
     * @return Updated IssueItem
     * @date 26, May 2025
     */
    public IssueItem updateIssuedItem(IssueItem updatedItem) {
        IssueItem existingItem = issueItemRepo.findById(updatedItem.getId())
                .orElseThrow(() -> new NotFoundException("Issued item not found"));

        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setProductId(updatedItem.getProductId());
        existingItem.setVendorId(updatedItem.getVendorId());
        existingItem.setTotalPrice(updatedItem.getTotalPrice());

        IssueItem savedItem = issueItemRepo.save(existingItem);

        itemList.removeIf(item -> item.get("id").equals(savedItem.getId()));

        Vendor vendor = vendorRepo.findById(UUID.fromString(savedItem.getVendorId())).orElse(null);
        Inventory inventory = inventoryRepo.findById(savedItem.getProductId()).orElse(null);
        itemList.add(createItemMap(savedItem, inventory, vendor));

        // refresh inventory data structures
        inventoryService.refreshMemoryStructures();

        return savedItem;
    }

    /**
     * @description Delete a specific issued item by ID and remove it from in-memory list
     * @author
     * @param id UUID of the issued item to delete
     * @date 26, May 2025
     */
    public void removeIssuedItem(UUID id) {
        IssueItem item = issueItemRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Issued item not found"));

        issueItemRepo.deleteById(item.getId());
        itemList.removeIf(i -> i.get("id").equals(id));
    }

    /**
     * @description Helper method to update inventory quantity after item is issued
     * @author
     * @param quantity quantity issued
     * @param itemId UUID of the inventory item
     * @return Updated Inventory item
     * @date 26, May 2025
     */
    private Inventory updateInventory(Integer quantity, UUID itemId) {
        Inventory existingItem = inventoryRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Inventory item not found"));

        if (quantity > existingItem.getQuantity()) {
            throw new BadRequestException("Quantity exceeds available stock");
        }

        existingItem.setQuantity(existingItem.getQuantity() - quantity);
        return inventoryRepo.save(existingItem);
    }

    /**
     * @description Helper method to create a structured map of issued item details
     * @author
     * @param item IssueItem
     * @param inventory Inventory
     * @param vendor Vendor
     * @return Map<String, Object>
     * @date 26, May 2025
     */
    private Map<String, Object> createItemMap(IssueItem item, Inventory inventory, Vendor vendor) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", item.getId());
        map.put("quantity", item.getQuantity());
        map.put("totalPrice", item.getTotalPrice());
        map.put("vendor", vendor != null ? vendor.getName() : "Unknown Vendor");
        map.put("itemName", inventory != null ? inventory.getName() : "Unknown Item");
        map.put("createdAt", item.getCreatedAt());
        return map;
    }
}