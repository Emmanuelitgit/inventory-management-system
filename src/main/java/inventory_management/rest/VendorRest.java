package inventory_management.rest;

import inventory_management.dto.ResponseDTO;
import inventory_management.models.ItemCategory;
import inventory_management.models.Vendor;
import inventory_management.service.VendorService;
import inventory_management.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendors")
public class VendorRest {

    private final VendorService vendorService;

    @Autowired
    public VendorRest(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public ResponseEntity<Object> findAl(){
        List<Object> vendors = vendorService.getVendors(true);
        ResponseDTO responseDTO = AppUtils.getResponseDto("Vendors records", HttpStatus.OK, vendors);
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(200));    }

    @PostMapping
    public ResponseEntity<Object> saveVendor(@RequestBody Vendor vendor){
        Vendor vendorRes = vendorService.saveVendor(vendor);
        ResponseDTO responseDTO = AppUtils.getResponseDto("Vendor record saved", HttpStatus.OK, vendorRes);
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(201));    }

    @PutMapping
    public ResponseEntity<Object> updateVendor(@RequestBody Vendor vendor){
        Vendor vendorRes = vendorService.updateVendor(vendor);
        ResponseDTO responseDTO = AppUtils.getResponseDto("Vendor record saved", HttpStatus.OK, vendorRes);
        return new ResponseEntity<>(responseDTO,HttpStatus.valueOf(201));    }

    @DeleteMapping("/{vendorId}")
    public ResponseEntity<ResponseDTO > removeItem(@PathVariable UUID vendorId){
        vendorService.removeVendor(vendorId);
        ResponseDTO responseDTO = AppUtils.getResponseDto("vendor remove successfully", HttpStatus.OK);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
