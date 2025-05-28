package inventory_management.service;

import inventory_management.dto.InventoryResponse;
import inventory_management.models.Inventory;
import inventory_management.models.ItemCategory;
import inventory_management.models.Vendor;
import inventory_management.repo.InventoryRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class InventoryService {

    // data structures definition here
    private final Stack<InventoryResponse> stack = new Stack<>();
    private final Queue<InventoryResponse> queue = new LinkedList<>();
    private final List<InventoryResponse> list = new ArrayList<>();
    private final Map<String, Integer> salesMap = new HashMap<>();
    private final Map<String, Vendor> vendorMap = new HashMap<>();


    private final InventoryRepo inventoryRepo;
    private final ItemCategoryService itemCategoryService;
    private final VendorService vendorService;

    @Autowired
    public InventoryService( InventoryRepo inventoryRepo, ItemCategoryService itemCategoryService, VendorService vendorService) {
        this.inventoryRepo = inventoryRepo;
        this.itemCategoryService = itemCategoryService;
        this.vendorService = vendorService;
    }

    /**
     * @description this method is used to add new item to inventory
     * @auther
     * @param item
     * @date 26, May 2025
     */
    public InventoryResponse addItem(Inventory item) {

        // save data to database
        Inventory inventoryItem = inventoryRepo.save(item);

        // getting vendor name by id
        String vendor = "";
        if (item.getVendorId() != null){
            vendor = vendorService.findVendorById(item.getVendorId()).getName();
        }
        // getting category name by id
        String category = "";
        if (item.getCategoryId() !=null){
            category = itemCategoryService.getCategoryById(item.getCategoryId());
        }
        // prepare data to load to the data structures
        InventoryResponse inventoryResponse = InventoryResponse
                .builder()
                .id(inventoryItem.getId())
                .category(category)
                .grossPrice(inventoryItem.getGrossPrice())
                .unitPrice(inventoryItem.getBuyingPrice())
                .name(inventoryItem.getName())
                .sellingPrice(inventoryItem.getSellingPrice())
                .buyingPrice(inventoryItem.getBuyingPrice())
                .description(inventoryItem.getDescription())
                .quantity(inventoryItem.getQuantity())
                .vendor(vendor)
                .build();

        // store data in data structures base on categories
        switch (category) {
            case "Beverages":
            case "Bread/Bakery":
            case "Canned/Jarred":
            case "Dairy":
                stack.push(inventoryResponse);
                break;
            case "Dry/Baking":
            case "Frozen ":
            case "Meat":
                queue.offer(inventoryResponse);
                break;
            case "Produce":
            case "Cleaners":
            case "Personal Care":
            case "Paper":
                list.add(inventoryResponse);
                break;
        }

        return inventoryResponse;
    }

    /**
     * @description fetching data from the database and storing them in their respective data structures
     * @auther
     * @param
     * @date 26, May 2025
     */
    public List<InventoryResponse> getItems(boolean request) {
        // Return from in-memory data structures if request is true
        if (request) {
            List<InventoryResponse> combined = new ArrayList<>();

            // Combine all data structures into one list
            combined.addAll(stack);
            combined.addAll(queue);
            combined.addAll(list);

            log.info("fetching from data structures");
            return combined;
        }

        // Else, fetch from the database and populate the data structures
        List<Inventory> inventoryList = inventoryRepo.findAll();

        // Clear old data
        stack.clear();
        queue.clear();
        list.clear();

        for (Inventory inventory : inventoryList) {
            UUID categoryId = inventory.getCategoryId();
            UUID vendorId = inventory.getVendorId();
            String category = "";
            String vendor = "";
            if (categoryId != null){
                category = itemCategoryService.getCategoryById(inventory.getCategoryId());
            }
            if (vendorId != null){
                vendor = vendorService.findVendorById(vendorId).getName();
            }
            InventoryResponse payload = InventoryResponse
                    .builder()
                    .name(inventory.getName())
                    .id(inventory.getId())
                    .category(category)
                    .vendor(vendor)
                    .unitPrice(inventory.getBuyingPrice())
                    .grossPrice(inventory.getGrossPrice())
                    .sellingPrice(inventory.getSellingPrice())
                    .build();

            switch (payload.getCategory()) {
                case "Beverages":
                case "Bread/Bakery":
                case "Canned/Jarred":
                case "Dairy":
                    stack.push(payload);
                    break;
                case "Dry/Baking":
                case "Frozen":
                case "Meat":
                    queue.offer(payload);
                    break;
                case "Produce":
                case "Cleaners":
                case "Personal Care":
                case "Paper":
                    list.add(payload);
                    break;
                default:
                    System.out.println("⚠️ Unknown category: " + payload.getCategory());
            }
        }

        // Combine the populated data structures and return
        List<InventoryResponse> result = new ArrayList<>();
        result.addAll(stack);
        result.addAll(queue);
        result.addAll(list);
log.info("inventory:->>>>{}", result);
        return result;
    }

    /**
     * @description removing item from a data structure based on the category and database base on the id
     * @auther
     * @param
     * @date 26, May 2025
     */
    public void removeItem(ItemCategory itemCategory) {
        InventoryResponse removedItem = null;

        switch (itemCategory.getName()) {
            case "Beverages":
            case "Bread/Bakery":
            case "Canned/Jarred":
            case "Dairy":
                if (!stack.isEmpty()) {
                    removedItem = stack.pop();
                }
                break;

            case "Dry/Baking":
            case "Frozen":
            case "Meat":
                if (!queue.isEmpty()) {
                    removedItem = queue.poll();
                }
                break;

            case "Produce":
            case "Cleaners":
            case "Personal Care":
            case "Paper":
                if (!list.isEmpty()) {
                    removedItem = list.remove(0);
                }
                break;
        }

        // removing item from database
        if (removedItem != null) {
            Optional<Inventory> itemFromDb = inventoryRepo.findById(removedItem.getId());
            itemFromDb.ifPresent(inventoryRepo::delete);
        }
    }

    /**
     * @description this method edits an existing item in the inventory
     * @param updatedItem Inventory object containing updated data
     * @date 26, May 2025
     */
    public void updateItem(Inventory updatedItem) {
        Optional<Inventory> existingItemOpt = inventoryRepo.findById(updatedItem.getId());

        if (existingItemOpt.isEmpty()) {
            throw new NoSuchElementException("Item not found");
        }

        Inventory existingItem = existingItemOpt.get();

        // Update values
        existingItem.setName(updatedItem.getName());
        existingItem.setBuyingPrice(updatedItem.getBuyingPrice());
        existingItem.setGrossPrice(updatedItem.getGrossPrice());
        existingItem.setSellingPrice(updatedItem.getSellingPrice());
        existingItem.setCategoryId(updatedItem.getCategoryId());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setVendorId(updatedItem.getVendorId());

        inventoryRepo.save(existingItem);

        // Update in-memory structures
        refreshMemoryStructures();
    }

    // a helper method for refreshing data in the data structures with the updated data
    private void refreshMemoryStructures() {
        List<Inventory> inventoryList = inventoryRepo.findAll();

        stack.clear();
        queue.clear();
        list.clear();

        for (Inventory inventory : inventoryList) {
            InventoryResponse payload = InventoryResponse
                    .builder()
                    .name(inventory.getName())
                    .id(inventory.getId())
                    .category(itemCategoryService.getCategoryById(inventory.getCategoryId()))
                    .unitPrice(inventory.getBuyingPrice())
                    .grossPrice(inventory.getGrossPrice())
                    .sellingPrice(inventory.getSellingPrice())
                    .build();

            switch (payload.getCategory()) {
                case "Beverages":
                case "Bread/Bakery":
                case "Canned/Jarred":
                case "Dairy":
                    stack.push(payload);
                    break;
                case "Dry/Baking":
                case "Frozen":
                case "Meat":
                    queue.offer(payload);
                    break;
                case "Produce":
                case "Cleaners":
                case "Personal Care":
                case "Paper":
                    list.add(payload);
                    break;
                default:
                    System.out.println("⚠️ Unknown category: " + payload.getCategory());
            }
        }
    }

}
