package ba.unze.kosnica.repository;
import ba.unze.kosnica.model.StoredFile;
import ba.unze.kosnica.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface StoredFileRepository extends JpaRepository<StoredFile,Long>{
    List<StoredFile> findByOwnerOrderByUploadedAtDesc(User owner);

    Optional<StoredFile> findByIdAndOwner(Long id, User owner);
}
