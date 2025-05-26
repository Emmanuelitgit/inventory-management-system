package inventory_management.service;

import inventory_management.dto.ItemPayload;
import inventory_management.models.Item;
import inventory_management.models.ItemCategory;
import inventory_management.models.Vendor;
import inventory_management.repo.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryService {

    private final Stack<ItemPayload> stack = new Stack<>();
    private final Queue<ItemPayload> queue = new LinkedList<>();
    private final List<ItemPayload> list = new ArrayList<>();
    private final Map<String, Integer> salesMap = new HashMap<>();
    private final Map<String, Vendor> vendorMap = new HashMap<>();


    private final ItemRepo itemRepo;
    private final ItemCategoryService itemCategoryService;

    @Autowired
    public InventoryService(ItemRepo itemRepo, ItemCategoryService itemCategoryService) {
        this.itemRepo = itemRepo;
        this.itemCategoryService = itemCategoryService;
    }

    public void addItem(ItemPayload item) {

        switch (item.getCategory()) {
            case "Beverages":
            case "Bread/Bakery":
            case "Canned/Jarred":
            case "Dairy":
                stack.push(item);
                break;
            case "Dry/Baking":
            case "Frozen ":
            case "Meat":
                queue.offer(item);
                break;
            case "Produce":
            case "Cleaners":
            case "Personal Care":
            case "Paper":
                list.add(item);
                break;
        }

        Item itemData = Item
                .builder()
                .categoryId(item.getCategoryId())
                .grossPrice(item.getGrossPrice())
                .name(item.getName())
                .buyingPrice(item.getUnitPrice())
                .sellingPrice(item.getSellingPrice())
                .build();
        itemRepo.save(itemData);
    }

    // fetching data from the database and storing them in their respective data structures
    public List<Item> getItems() {
        List<Item> itemList = itemRepo.findAll();

        for (Item item : itemList) {
            ItemPayload payload = ItemPayload
                    .builder()
                    .name(item.getName())
                    .category(itemCategoryService.getCategoryById(item.getCategoryId()))
                    .categoryId(item.getCategoryId())
                    .unitPrice(item.getBuyingPrice())
                    .grossPrice(item.getGrossPrice())

                    .sellingPrice(item.getSellingPrice())
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

        return itemList;
    }


    // removing item from a data structure based on the category and database base on the id
    public void removeItem(ItemCategory itemCategory) {
        ItemPayload removedItem = null;

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
            Optional<Item> itemFromDb = itemRepo.findById(removedItem.getId());
            itemFromDb.ifPresent(itemRepo::delete);
        }
    }


}
