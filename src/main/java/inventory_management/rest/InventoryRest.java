package inventory_management.rest;

import inventory_management.dto.InventoryResponse;
import inventory_management.dto.ResponseDTO;
import inventory_management.models.Inventory;
import inventory_management.models.ItemCategory;
import inventory_management.service.InventoryService;
import inventory_management.utils.AppUtils;
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
    public ResponseEntity<Object> saveItem(@RequestBody Inventory item) {
        inventoryService.addItem(item);
        ResponseDTO responseDTO = AppUtils.getResponseDto("Inventory created successfully", HttpStatus.CREATED);
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(200));    }

    @GetMapping
    public ResponseEntity<ResponseDTO> findAll(){
        ResponseDTO responseDTO = AppUtils.getResponseDto("Inventory records", HttpStatus.OK, inventoryService.getItems(true));
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(200));
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO > removeItem(@RequestBody ItemCategory itemCategory){
     inventoryService.removeItem(itemCategory);
     ResponseDTO responseDTO = AppUtils.getResponseDto("item remove successfully", HttpStatus.OK);
     return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Object> updateItem(@RequestBody Inventory item) {
        inventoryService.addItem(item);
        if (item == null ){
            return new ResponseEntity<>("payload cannot be null", HttpStatusCode.valueOf(400));
        }
        ResponseDTO responseDTO = AppUtils.getResponseDto("Inventory created successfully", HttpStatus.CREATED);
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(200));    }

}
