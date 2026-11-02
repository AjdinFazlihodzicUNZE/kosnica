package ba.unze.kosnica.repository;

import ba.unze.kosnica.model.UsageRecord;
import ba.unze.kosnica.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {

    List<UsageRecord> findByUserOrderByCreatedAtDesc(User user);
}