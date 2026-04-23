package crawler.web.web_crawler.service;

import crawler.web.web_crawler.model.AccessibilityIssue;
import org.jsoup.nodes.Document;

import java.util.List;

public interface AccessibilityCheckerService {

    List<AccessibilityIssue> checkPage(Document document);


}
