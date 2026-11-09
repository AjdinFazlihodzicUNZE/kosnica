package ba.unze.kosnica.service;

import ba.unze.kosnica.model.UsageRecord;
import ba.unze.kosnica.model.User;
import ba.unze.kosnica.repository.UsageRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsageService {

    private static final Logger log = LoggerFactory.getLogger(UsageService.class);
    private final UsageRecordRepository usageRecordRepository;
    private final StripeService stripeService;

    public UsageService(UsageRecordRepository usageRecordRepository, StripeService stripeService) {
        this.usageRecordRepository = usageRecordRepository;
        this.stripeService = stripeService;
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
        if (user.getStripeCustomerId() != null && !user.getStripeCustomerId().isEmpty()) {
            try {
                stripeService.sendMeterEvent(user.getStripeCustomerId(), megabytes);
                record.setReportedToStripe(true);
            } catch (Exception e) {
                log.error("Nije moguće poslati potrošnju na Stripe za {}: {}", user.getEmail(), e.getMessage());
            }
        }
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