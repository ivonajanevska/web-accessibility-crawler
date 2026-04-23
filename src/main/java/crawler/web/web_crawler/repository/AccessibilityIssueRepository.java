package crawler.web.web_crawler.repository;

import crawler.web.web_crawler.model.AccessibilityIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessibilityIssueRepository extends JpaRepository<AccessibilityIssue, Long> {

    List<AccessibilityIssue> findByPageResultId(Long pageResultId);


}
