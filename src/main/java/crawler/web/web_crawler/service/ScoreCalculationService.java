package crawler.web.web_crawler.service;

import crawler.web.web_crawler.model.AccessibilityIssue;
import crawler.web.web_crawler.model.PageResult;

import java.util.List;

public interface ScoreCalculationService {

    int calculatePageScore (List<AccessibilityIssue> issues);

    String calculateGrade(int score);
    double calculateSessionScore(List<PageResult> pages);


    String calculateSessionGrade(double averageScore);

}
