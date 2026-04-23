package crawler.web.web_crawler.repository;

import crawler.web.web_crawler.model.PageResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PageResultRepository extends JpaRepository<PageResult, Long> {

    List<PageResult> findByCrawlSessionId(Long crawlSessionId);

}
