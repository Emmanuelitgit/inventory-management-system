package inventory_management.service;

import inventory_management.dto.InventoryResponse;
import inventory_management.exception.NotFoundException;
import inventory_management.models.Inventory;
import inventory_management.models.ItemCategory;
import inventory_management.models.Vendor;
import inventory_management.repo.InventoryRepo;
import inventory_management.repo.ItemCategoryRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class InventoryService {

    // data structures definition here
    private final Stack<Inventory> stack = new Stack<>();
    private final Queue<Inventory> queue = new LinkedList<>();
    private final List<Inventory> list = new ArrayList<>();
    private final Map<String, Integer> salesMap = new HashMap<>();
    private final Map<String, Vendor> vendorMap = new HashMap<>();


    private final InventoryRepo inventoryRepo;
    private final ItemCategoryService itemCategoryService;
    private final VendorService vendorService;
    private final ItemCategoryRepo itemCategoryRepo;

    @Autowired
    public InventoryService(InventoryRepo inventoryRepo, ItemCategoryService itemCategoryService, VendorService vendorService, ItemCategoryRepo itemCategoryRepo) {
        this.inventoryRepo = inventoryRepo;
        this.itemCategoryService = itemCategoryService;
        this.vendorService = vendorService;
        this.itemCategoryRepo = itemCategoryRepo;
    }

    /**
     * @description this method is used to add new item to inventory
     * @auther
     * @param item
     * @return
     * @createdAt 26, May 2025
     */
    public Inventory addItem(Inventory item) {

        // save data to database
        Inventory inventoryItem = inventoryRepo.save(item);

        // getting category name by id
        String category = "";
        if (item.getCategoryId() !=null){
            category = itemCategoryService.getCategoryById(item.getCategoryId());
        }
        // prepare data to load to the data structures
        Inventory inventoryResponse = Inventory
                .builder()
                .id(inventoryItem.getId())
                .grossPrice(inventoryItem.getGrossPrice())
                .name(inventoryItem.getName())
                .sellingPrice(inventoryItem.getSellingPrice())
                .buyingPrice(inventoryItem.getBuyingPrice())
                .description(inventoryItem.getDescription())
                .quantity(inventoryItem.getQuantity())
                .categoryId(inventoryItem.getCategoryId())
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
            case "Frozen":
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
     * @param request
     * @return
     * @createdAt  26, May 2025
     */
    public List<Inventory> getItems(boolean request) {
        // Return from in-memory data structures if request is true
        if (request) {
            List<Inventory> combined = new ArrayList<>();

            // Combine all data structures into one list
            combined.addAll(stack);
            combined.addAll(queue);
            combined.addAll(list);

            return combined;
        }

        // Else, fetch from the database and populate the data structures
        List<Inventory> inventoryList = inventoryRepo.findAll();

        // Clear old data
        stack.clear();
        queue.clear();
        list.clear();

        for (Inventory inventoryItem : inventoryList) {
            UUID categoryId = inventoryItem.getCategoryId();
            String category = "";

            // getting category name given the id
            if (categoryId != null){
                category = itemCategoryService.getCategoryById(inventoryItem.getCategoryId());
            }

            Inventory payload = Inventory
                    .builder()
                    .id(inventoryItem.getId())
                    .grossPrice(inventoryItem.getGrossPrice())
                    .name(inventoryItem.getName())
                    .sellingPrice(inventoryItem.getSellingPrice())
                    .buyingPrice(inventoryItem.getBuyingPrice())
                    .description(inventoryItem.getDescription())
                    .quantity(inventoryItem.getQuantity())
                    .categoryId(inventoryItem.getCategoryId())
                    .build();

            switch (category) {
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
                    System.out.println("⚠️ Unknown category: " + category);
            }
        }

        // Combine the populated data structures and return
        List<Inventory> result = new ArrayList<>();
        result.addAll(stack);
        result.addAll(queue);
        result.addAll(list);
       log.info("inventory:->>>>{}", result);
        return result;
    }


    /**
     * @description removing item from a data structure based on the category and database base on the id
     * @auther
     * @param itemCategory
     * @return
     * @createdAt 26, May 2025
     */
    public void removeItem(ItemCategory itemCategory) {

        // getting category name given the id
        ItemCategory category = itemCategoryRepo.findById(itemCategory.getId())
                .orElseThrow(()-> new NotFoundException("category record not found"));

        Inventory removedItem = null;

        switch (category.getName()) {
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
     * @return
     * @createdAt 26, May 2025
     */
    public void updateItem(Inventory updatedItem) {
        Optional<Inventory> existingItemOpt = inventoryRepo.findById(updatedItem.getId());

        if (existingItemOpt.isEmpty()) {
            throw new NotFoundException("Item record not found");
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

        for (Inventory inventoryItem : inventoryList) {

            // getting category name given the id
            UUID categoryId = inventoryItem.getCategoryId();
            String category = "";
            if (categoryId != null){
                category = itemCategoryService.getCategoryById(inventoryItem.getCategoryId());
            }


            Inventory payload = Inventory
                    .builder()
                    .id(inventoryItem.getId())
                    .grossPrice(inventoryItem.getGrossPrice())
                    .name(inventoryItem.getName())
                    .sellingPrice(inventoryItem.getSellingPrice())
                    .buyingPrice(inventoryItem.getBuyingPrice())
                    .description(inventoryItem.getDescription())
                    .quantity(inventoryItem.getQuantity())
                    .build();

            switch (category) {
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
                    System.out.println("⚠️ Unknown category: " + category);
            }
        }
    }

}