package crawler.web.web_crawler.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class PageResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pageUrl;
    private int totalImages;
    private int totalVideos;
    private int totalIssues;
    private int score;
    private String grade;

    @ManyToOne
    @JoinColumn(name = "crawl_session_id")
    private CrawlSession crawlSession;

    @OneToMany(mappedBy = "pageResult", cascade = CascadeType.ALL)
    private List<AccessibilityIssue> issues;
}
