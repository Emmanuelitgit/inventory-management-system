package inventory_management.repo;

import inventory_management.models.IssueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IssueItemRepo extends JpaRepository<IssueItem, UUID> {
}
