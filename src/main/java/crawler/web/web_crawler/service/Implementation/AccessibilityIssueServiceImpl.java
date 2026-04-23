package crawler.web.web_crawler.service.Implementation;

import crawler.web.web_crawler.model.AccessibilityIssue;
import crawler.web.web_crawler.repository.AccessibilityIssueRepository;
import crawler.web.web_crawler.service.AccessibilityIssueService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccessibilityIssueServiceImpl implements AccessibilityIssueService{

    private final AccessibilityIssueRepository accessibilityIssueRepository;


    @Override
    public AccessibilityIssue createIssue(AccessibilityIssue issue) {
        return accessibilityIssueRepository.save(issue);
    }

    @Override
    public List<AccessibilityIssue> createAllIssues(List<AccessibilityIssue> issues) {
        return accessibilityIssueRepository.saveAll(issues);
    }

    @Override
    public List<AccessibilityIssue> getIssuesByPageId(Long pageId) {
        return accessibilityIssueRepository.findByPageResultId(pageId);
    }
}
