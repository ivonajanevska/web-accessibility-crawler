package crawler.web.web_crawler.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class AccessibilityIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ElementType elementType;
    @Enumerated(EnumType.STRING)
    private IssueType issueType;
    private String description;
    @Column(columnDefinition = "TEXT")
    private String elementHTML;

    @ManyToOne
    @JoinColumn(name = "page_result_id")
    private PageResult pageResult;
}
