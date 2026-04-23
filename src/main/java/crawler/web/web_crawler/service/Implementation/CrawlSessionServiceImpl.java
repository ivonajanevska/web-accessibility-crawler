package crawler.web.web_crawler.service.Implementation;

import crawler.web.web_crawler.model.CrawlSession;
import crawler.web.web_crawler.repository.CrawlSessionRepository;
import crawler.web.web_crawler.service.CrawlSessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CrawlSessionServiceImpl implements CrawlSessionService {

    private final CrawlSessionRepository crawlSessionRepository;

    @Override
    public CrawlSession createSession(String baseUrl) {
        CrawlSession session = new CrawlSession();
        session.setBaseUrl(baseUrl);
        session.setStartedAt(LocalDateTime.now());
        session.setStatus("IN PROGRESS");
        return crawlSessionRepository.save(session);
    }

    @Override
    public CrawlSession getSessionById(Long id) {
        return crawlSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Тhe session with id: " + id + " was not found"));
    }

    @Override
    public List<CrawlSession> getAllSessions() {
        return crawlSessionRepository.findAll();
    }

    @Override
    public CrawlSession updateSession(CrawlSession session) {
        return crawlSessionRepository.save(session);
    }
}
