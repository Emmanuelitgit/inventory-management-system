package inventory_management.rest;

import inventory_management.dto.ResponseDTO;
import inventory_management.models.Inventory;
import inventory_management.models.IssueItem;
import inventory_management.models.ItemCategory;
import inventory_management.repo.IssueItemRepo;
import inventory_management.service.IssueItemService;
import inventory_management.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/issue-item")
public class IssueItemRest {

  private final IssueItemService issueItemService;

  @Autowired
    public IssueItemRest(IssueItemService issueItemService) {
        this.issueItemService = issueItemService;
    }

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestBody IssueItem issueItem) {
        IssueItem inventoryRes = issueItemService.issueItem(issueItem);
        ResponseDTO responseDTO = AppUtils.getResponseDto("Item issued successfully", HttpStatus.CREATED, inventoryRes);
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(200));    }

    @GetMapping
    public ResponseEntity<ResponseDTO> findAll(){
        ResponseDTO responseDTO = AppUtils.getResponseDto("Issued items records", HttpStatus.OK, issueItemService.getAllIssuedItems(true));
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO > removeItem(@PathVariable UUID id){
        issueItemService.removeIssuedItem(id);
        ResponseDTO responseDTO = AppUtils.getResponseDto("item removed successfully", HttpStatus.OK);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Object> updateItem(@RequestBody IssueItem item) {
        ResponseDTO responseDTO = AppUtils.getResponseDto("Inventory updated successfully", HttpStatus.OK, issueItemService.updateIssuedItem(item));
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(200));    }
}
