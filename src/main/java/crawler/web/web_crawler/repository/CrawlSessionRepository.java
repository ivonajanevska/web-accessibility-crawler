package crawler.web.web_crawler.repository;

import crawler.web.web_crawler.model.CrawlSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlSessionRepository extends JpaRepository<CrawlSession, Long> {
}
