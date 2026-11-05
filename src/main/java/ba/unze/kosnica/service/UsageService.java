package ba.unze.kosnica.service;

import ba.unze.kosnica.model.UsageRecord;
import ba.unze.kosnica.model.User;
import ba.unze.kosnica.repository.UsageRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsageService {

    private final UsageRecordRepository usageRecordRepository;

    public UsageService(UsageRecordRepository usageRecordRepository) {
        this.usageRecordRepository = usageRecordRepository;
    }

    public UsageRecord record(User user, String fileName, long sizeBytes) {
        long megabytes = (long) Math.ceil(sizeBytes / 1048576.0);
        if (megabytes < 1) {
            megabytes = 1;
        }
        UsageRecord record = new UsageRecord();
        record.setUser(user);
        record.setFileName(fileName);
        record.setMegabytes(megabytes);
        return usageRecordRepository.save(record);
    }

    public List<UsageRecord> listFor(User user) {
        return usageRecordRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long totalMegabytes(User user) {
        long total = 0;
        for (UsageRecord record : listFor(user)) {
            total += record.getMegabytes();
        }
        return total;
    }
}