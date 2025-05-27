package inventory_management.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

public class IssueItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private Float sellingPrice;
    private Integer quantity;
    private Float totalPrice;
    private String customer;
    private ZonedDateTime createdAt;
}
