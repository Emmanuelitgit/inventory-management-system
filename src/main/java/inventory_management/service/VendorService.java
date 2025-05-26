package inventory_management.service;

import inventory_management.models.Vendor;
import inventory_management.repo.VendorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class VendorService {

    private final VendorRepo vendorRepo;
    private final List<Object> vendors = new ArrayList<>();
    private final HashMap<String, String> vendorHashMap = new HashMap<>();

    @Autowired
    public VendorService(VendorRepo vendorRepo) {
        this.vendorRepo = vendorRepo;
    }

    // adding new inventory record with hashmap
    public Vendor vendor(Vendor vendor){
        vendorHashMap.put("name", vendor.getName());
        vendorHashMap.put("email", vendor.getEmail());
        vendorHashMap.put("zipCode", vendor.getZipCode());
        vendorHashMap.put("address", vendor.getAddress());

        vendors.add(vendorHashMap);
        vendorRepo.save(vendor);
        return vendor;
    }


    // store inventory information using hashmap
    public List<Object > getVendors(){
        // data from database
        List<Vendor> vendorsData = vendorRepo.findAll();

        // storing in hashmaps inside a list
        for (Vendor vendor:vendorsData){
            vendorHashMap.put("name", vendor.getName());
            vendorHashMap.put("email", vendor.getEmail());
            vendorHashMap.put("zipCode", vendor.getZipCode());
            vendorHashMap.put("address", vendor.getAddress());

            vendors.add(vendor);
        }

        return vendors;
    }
}
