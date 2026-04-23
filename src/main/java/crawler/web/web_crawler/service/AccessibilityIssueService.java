package crawler.web.web_crawler.service;

import crawler.web.web_crawler.model.AccessibilityIssue;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AccessibilityIssueService {

    AccessibilityIssue createIssue(AccessibilityIssue issue);
    List<AccessibilityIssue> createAllIssues(List<AccessibilityIssue> issues);
    List<AccessibilityIssue> getIssuesByPageId(Long pageId);
}
