package inventory_management.service;

import inventory_management.dto.InventoryResponse;
import inventory_management.models.Inventory;
import inventory_management.models.ItemCategory;
import inventory_management.models.Vendor;
import inventory_management.repo.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryService {

    // data structures definition here
    private final Stack<InventoryResponse> stack = new Stack<>();
    private final Queue<InventoryResponse> queue = new LinkedList<>();
    private final List<InventoryResponse> list = new ArrayList<>();
    private final Map<String, Integer> salesMap = new HashMap<>();
    private final Map<String, Vendor> vendorMap = new HashMap<>();


    private final ItemRepo itemRepo;
    private final ItemCategoryService itemCategoryService;

    @Autowired
    public InventoryService(ItemRepo itemRepo, ItemCategoryService itemCategoryService) {
        this.itemRepo = itemRepo;
        this.itemCategoryService = itemCategoryService;
    }

    /**
     * @description this method is used to add new item to inventory
     * @auther
     * @param item
     * @date 26, May 2025
     */
    public void addItem(Inventory item) {

        String category = itemCategoryService.getCategoryById(item.getCategoryId());
        // prepare data to load to the data structures
        InventoryResponse inventoryResponse = InventoryResponse
                .builder()
                .category(category)
                .grossPrice(item.getGrossPrice())
                .unitPrice(item.getBuyingPrice())
                .name(item.getName())
                .sellingPrice(item.getSellingPrice())
                .vendor("")
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

        // save data to database
        Inventory inventoryData = Inventory
                .builder()
                .grossPrice(inventoryResponse.getGrossPrice())
                .name(item.getName())
                .buyingPrice(inventoryResponse.getUnitPrice())
                .sellingPrice(inventoryResponse.getSellingPrice())
                .build();
        itemRepo.save(inventoryData);
    }


    /**
     * @description fetching data from the database and storing them in their respective data structures
     * @auther
     * @param
     * @date 26, May 2025
     */
    public List<Inventory> getItems() {
        List<Inventory> inventoryList = itemRepo.findAll();

        for (Inventory inventory : inventoryList) {
            InventoryResponse payload = InventoryResponse
                    .builder()
                    .name(inventory.getName())
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

        return inventoryList;
    }


    // removing item from a data structure based on the category and database base on the id
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
            Optional<Inventory> itemFromDb = itemRepo.findById(removedItem.getId());
            itemFromDb.ifPresent(itemRepo::delete);
        }
    }


}
