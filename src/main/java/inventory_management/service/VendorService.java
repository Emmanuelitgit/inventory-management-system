package inventory_management.service;

import inventory_management.exception.NotFoundException;
import inventory_management.models.Vendor;
import inventory_management.repo.VendorRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class VendorService {

    private final VendorRepo vendorRepo;
    private final List<Object> vendors = new ArrayList<>();

    @Autowired
    public VendorService(VendorRepo vendorRepo) {
        this.vendorRepo = vendorRepo;
    }

    /**
     * @description adding new vendor record with hashmap
     * @auther
     * @param vendorPayload
     * @date 26, May 2025
     */
    public Vendor saveVendor(Vendor vendorPayload){

        // saving to database
        Vendor vendor = vendorRepo.save(vendorPayload);
        // storing data in hashmap
        HashMap<String, String> vendorHashMap = new HashMap<>();
        vendorHashMap.put("id", vendor.getId().toString());
        vendorHashMap.put("name", vendor.getName());
        vendorHashMap.put("email", vendor.getEmail());
        vendorHashMap.put("phone", vendor.getPhone());
        vendorHashMap.put("address", vendor.getAddress());
        vendorHashMap.put("city", vendor.getCity());
        vendorHashMap.put("state", vendor.getState());
        vendorHashMap.put("zipCode", vendor.getZipCode());
        vendorHashMap.put("contactPerson", vendor.getContactPerson());

        vendors.add(vendorHashMap);
        return vendor;
    }

    /**
     * @description store vendor information using hashmap
     * @auther
     * @param request
     * @date 26, May 2025
     */
    public List<Object> getVendors(boolean request) {
        if (request) {
            // Return in-memory data
            log.info("fetching from data structures");
            return vendors;
        }
        // Fetch from DB
        List<Vendor> vendorsData = vendorRepo.findAll();

        // Store in hashmap/list
        for (Vendor vendor : vendorsData) {
            HashMap<String, String> vendorHashMap = new HashMap<>();
            vendorHashMap.put("id", vendor.getId().toString());
            vendorHashMap.put("name", vendor.getName());
            vendorHashMap.put("email", vendor.getEmail());
            vendorHashMap.put("phone", vendor.getPhone());
            vendorHashMap.put("address", vendor.getAddress());
            vendorHashMap.put("city", vendor.getCity());
            vendorHashMap.put("state", vendor.getState());
            vendorHashMap.put("zipCode", vendor.getZipCode());
            vendorHashMap.put("contactPerson", vendor.getContactPerson());

            vendors.add(vendorHashMap.clone());
        }
      log.info("vendors:->>>>>>{}", vendors);
        return vendors;
    }

    /**
     * @description fetching vendor records by id from the database
     * @auther
     * @param
     * @date 26, May 2025
     */
    public Vendor findVendorById(UUID vendorId){
        Optional<Vendor> vendor = vendorRepo.findById(vendorId);
        return vendor.orElse(null);
    }

    /**
     * @description update vendor record in the database and memory
     * @auther
     * @param updatedVendor
     * @date 26, May 2025
     */
    public Vendor updateVendor(Vendor updatedVendor) {
        Optional<Vendor> existingOptional = vendorRepo.findById(updatedVendor.getId());
        if (existingOptional.isPresent()) {
            Vendor existing = existingOptional.get();

            // Update fields
            existing.setName(updatedVendor.getName());
            existing.setEmail(updatedVendor.getEmail());
            existing.setPhone(updatedVendor.getPhone());
            existing.setAddress(updatedVendor.getAddress());
            existing.setCity(updatedVendor.getCity());
            existing.setState(updatedVendor.getState());
            existing.setZipCode(updatedVendor.getZipCode());
            existing.setContactPerson(updatedVendor.getContactPerson());

            Vendor saved = vendorRepo.save(existing);

            // Refresh memory data
            vendors.clear();
            getVendors(false);

            return saved;
        }
        return null;
    }


    /**
     * @description delete vendor record by id from database and in-memory list
     * @author
     * @param vendorId
     * @date 26, May 2025
     */
    public boolean removeVendor(UUID vendorId) {
        Optional<Vendor> vendorOptional = vendorRepo.findById(vendorId);

        if (vendorOptional.isPresent()) {

            // checking if vendor exist by id
            Vendor vendor = vendorRepo.findById(vendorId)
                    .orElseThrow(()-> new NotFoundException("vendor record not found"));

            // remove from DB
            vendorRepo.deleteById(vendorId);

            // remove from in-memory list
            vendors.removeIf(v -> {
                if (v instanceof HashMap) {
                    HashMap<?, ?> map = (HashMap<?, ?>) v;
                    return vendorId.toString().equals(map.get("id"));
                }
                return false;
            });

            log.info("Vendor with ID {} deleted successfully.", vendorId);
            return true;
        }

        log.warn("Vendor with ID {} not found.", vendorId);
        return false;
    }

}
