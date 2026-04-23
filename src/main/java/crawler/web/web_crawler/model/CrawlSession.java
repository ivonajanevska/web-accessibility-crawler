package crawler.web.web_crawler.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.query.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class CrawlSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String baseUrl;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String status;

    @OneToMany(mappedBy = "crawlSession", cascade = CascadeType.ALL)
    private List<PageResult> pageResults;
}
