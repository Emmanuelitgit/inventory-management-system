package inventory_management.rest;

import inventory_management.dto.ResponseDTO;
import inventory_management.models.ItemCategory;
import inventory_management.service.ItemCategoryService;
import inventory_management.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<ResponseDTO> findAll(){
        ResponseDTO responseDTO = AppUtils.getResponseDto("Inventory records", HttpStatus.OK, itemCategoryService.findAll());
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(200));
    }
}
