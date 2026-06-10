package crawler.web.web_crawler.service.Implementation;

import crawler.web.web_crawler.model.AccessibilityIssue;
import crawler.web.web_crawler.model.PageResult;
import crawler.web.web_crawler.service.ScoreCalculationService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ScoreCalculationServiceImpl implements ScoreCalculationService {
    @Override
    public int calculatePageScore(List<AccessibilityIssue> issues) {
        int penalty = issues.stream()
                .mapToInt(issue -> issue.getIssueType().getWeight())
                .sum();
        return Math.max(0, 100 - penalty);
    }

    @Override
    public String calculateGrade(int score) {
        if (score >= 90) return "A";
        if (score >= 75) return "B";
        if (score >= 60) return "C";
        if (score >= 40) return "D";
        return "F";
    }

    @Override
    public double calculateSessionScore(List<PageResult> pages) {
        if (pages.isEmpty())
            return 0;
        double total = 0;
        for (PageResult page : pages) {
            total += page.getScore();
        }

        return Math.round((total / pages.size()) * 10.0)/10.0;
    }

    @Override
    public String calculateSessionGrade(double averageScore) {
        if (averageScore >= 90) return "A";
        if (averageScore >= 75) return "B";
        if (averageScore >= 60) return "C";
        if (averageScore >= 40) return "D";
        return "F";
    }
}
