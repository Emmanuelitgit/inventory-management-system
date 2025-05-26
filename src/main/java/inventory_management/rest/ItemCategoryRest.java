package inventory_management.rest;

import inventory_management.models.ItemCategory;
import inventory_management.service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
public class ItemCategoryRest {

    private final ItemCategoryService itemCategoryService;

    @Autowired
    public ItemCategoryRest(ItemCategoryService itemCategoryService) {
        this.itemCategoryService = itemCategoryService;
    }

    @PostMapping
    public ResponseEntity<ItemCategory> saveCategory(@RequestBody ItemCategory itemCategory){
        ItemCategory itemCategoryData = itemCategoryService.saveCategory(itemCategory);
        return new ResponseEntity<>(itemCategoryData, HttpStatusCode.valueOf(201));
    }
}
