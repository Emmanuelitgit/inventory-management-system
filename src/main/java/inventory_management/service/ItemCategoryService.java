package inventory_management.service;

import inventory_management.models.ItemCategory;
import inventory_management.repo.ItemCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ItemCategoryService {

    private final ItemCategoryRepo itemCategoryRepo;

    @Autowired
    public ItemCategoryService(ItemCategoryRepo itemCategoryRepo) {
        this.itemCategoryRepo = itemCategoryRepo;
    }

    // fetching categories by id from the database
    public String getCategoryById(UUID categoryId){
        Optional<ItemCategory> itemCategory = itemCategoryRepo.findById(categoryId);
        if (itemCategory.isEmpty()){
            return "no category record found";
        }
        return itemCategory.get().getName();
    }

    public ItemCategory saveCategory(ItemCategory itemCategory){
        return itemCategoryRepo.save(itemCategory);
    }
}
