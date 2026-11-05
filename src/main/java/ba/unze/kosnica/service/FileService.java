package ba.unze.kosnica.service;
import ba.unze.kosnica.model.StoredFile;
import ba.unze.kosnica.model.User;
import ba.unze.kosnica.repository.StoredFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
    private final StoredFileRepository fileRepository;
    private final Path uploadPath;
    private final UsageService usageService;

    public FileService(StoredFileRepository fileRepository,
                       UsageService usageService,
                       @Value("${kosnica.upload-dir}") String uploadDir) throws IOException {
        this.fileRepository = fileRepository;
        this.usageService = usageService;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadPath);
    }

    public StoredFile store(MultipartFile file, User owner) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Izaberi fajl prije uploada.");
        }
        String storedName = UUID.randomUUID().toString();
        file.transferTo(uploadPath.resolve(storedName));

        StoredFile storedFile = new StoredFile();
        storedFile.setOwner(owner);
        storedFile.setOriginalName(file.getOriginalFilename());
        storedFile.setStoredName(storedName);
        storedFile.setContentType(file.getContentType());
        storedFile.setSizeBytes(file.getSize());
        StoredFile saved = fileRepository.save(storedFile);
        usageService.record(owner, saved.getOriginalName(), saved.getSizeBytes());
        return saved;
    }

    public List<StoredFile> listFor(User owner) {
        return fileRepository.findByOwnerOrderByUploadedAtDesc(owner);
    }

    public StoredFile getOwned(Long id, User owner) {
        return fileRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new IllegalArgumentException("Ćelija nije pronađena."));
    }

    public Resource loadAsResource(StoredFile storedFile) {
        return new FileSystemResource(uploadPath.resolve(storedFile.getStoredName()));
    }

    public void delete(Long id, User owner) throws IOException {
        StoredFile storedFile = getOwned(id, owner);
        Files.deleteIfExists(uploadPath.resolve(storedFile.getStoredName()));
        fileRepository.delete(storedFile);
    }
}

