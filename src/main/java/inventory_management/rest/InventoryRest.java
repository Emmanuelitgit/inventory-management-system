package inventory_management.rest;

import inventory_management.dto.InventoryResponse;
import inventory_management.models.Inventory;
import inventory_management.models.ItemCategory;
import inventory_management.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryRest {

 private final InventoryService inventoryService;

 @Autowired
    public InventoryRest(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody Inventory item) {
        inventoryService.addItem(item);
        if (item == null ){
            return new ResponseEntity<>("payload cannot be null", HttpStatusCode.valueOf(400));
        }
        return new ResponseEntity<>(item, HttpStatusCode.valueOf(200));
    }

    @GetMapping
    public ResponseEntity<Object> getItems(){
     return new ResponseEntity<>(inventoryService.getItems(),HttpStatus.valueOf(200));
    }

    @DeleteMapping
    public ResponseEntity<String > removeItem(@RequestBody ItemCategory itemCategory){
     inventoryService.removeItem(itemCategory);
     return new ResponseEntity<>("item remove successfully", HttpStatus.OK);
    }

}
