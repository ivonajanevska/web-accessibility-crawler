package crawler.web.web_crawler.service.Implementation;

import crawler.web.web_crawler.model.*;
import crawler.web.web_crawler.service.*;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {

    private final CrawlSessionService crawlSessionService;
    private final PageResultService pageResultService;
    private final AccessibilityIssueService accessibilityIssueService;
    private final AccessibilityCheckerService accessibilityCheckerService;
    private final ScoreCalculationService scoreCalculationService;

    // ═══════════════════════════════════════════════
    // ЈАВНА МЕТОДА
    // ═══════════════════════════════════════════════

    @Override
    public CrawlSession startCrawling(String baseUrl, int maxDepth) {

        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "https://" + baseUrl;
        }

        CrawlSession session = crawlSessionService.createSession(baseUrl);
        WebDriver driver = createDriver();

        try {
            Set<String> visitedUrls = new HashSet<>();
            crawlPage(driver, baseUrl, baseUrl, maxDepth, 0, visitedUrls, session);
            finalizeSession(session);

        } catch (Exception e) {
            failSession(session);

        } finally {
            driver.quit();
        }

        return session;
    }

    @Override
    public List<CrawlSession> startBatchCrawling(List<String> urls, int maxDepth) {
        List<CrawlSession> sessions = new ArrayList<>();

        for (String url : urls) {
            String trimmedUrl = url.trim();
            if (!trimmedUrl.isEmpty()){
                CrawlSession session = startCrawling(trimmedUrl, maxDepth);
                sessions.add(session);
            }
        }
        return sessions;
    }

    // ═══════════════════════════════════════════════
    // CRAWLING
    // ═══════════════════════════════════════════════

    private void crawlPage(WebDriver driver,
                           String url,
                           String baseUrl,
                           int maxDepth,
                           int currentDepth,
                           Set<String> visitedUrls,
                           CrawlSession session) {

        if (shouldSkip(url, baseUrl, currentDepth, maxDepth, visitedUrls)) return;

        visitedUrls.add(url);

        try {
            driver.get(url);
            Document document = Jsoup.parse(driver.getPageSource());

            List<AccessibilityIssue> issues = collectIssues(document, driver);

            PageResult savedPage = savePageResult(url, document, issues, session);

            linkIssuesToPage(issues, savedPage);

            followLinks(driver, document, baseUrl, maxDepth, currentDepth, visitedUrls, session);

        } catch (Exception e) {
            System.err.println("Грешка при crawling на: " + url + " — " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════
    // ПОМОШНИ МЕТОДИ ЗА CRAWLING
    // ═══════════════════════════════════════════════

    private boolean shouldSkip(String url,
                               String baseUrl,
                               int currentDepth,
                               int maxDepth,
                               Set<String> visitedUrls) {
        return currentDepth > maxDepth ||
                visitedUrls.contains(url) ||
                !url.startsWith(baseUrl);
    }

    private List<AccessibilityIssue> collectIssues(Document document, WebDriver driver) {
        List<AccessibilityIssue> issues = new ArrayList<>();
        issues.addAll(accessibilityCheckerService.checkPage(document));
        issues.addAll(checkKeyboardNavigation(driver));
        return issues;
    }

    private PageResult savePageResult(String url,
                                      Document document,
                                      List<AccessibilityIssue> issues,
                                      CrawlSession session) {
        PageResult pageResult = buildPageResult(url, document, issues, session);
        return pageResultService.createPage(pageResult);
    }

    private PageResult buildPageResult(String url,
                                       Document document,
                                       List<AccessibilityIssue> issues,
                                       CrawlSession session) {
        int pageScore = scoreCalculationService.calculatePageScore(issues);
        String pageGrade = scoreCalculationService.calculateGrade(pageScore);

        PageResult pageResult = new PageResult();
        pageResult.setPageUrl(url);
        pageResult.setTotalImages(document.select("img").size());
        pageResult.setTotalVideos(document.select("video").size());
        pageResult.setTotalIssues(issues.size());
        pageResult.setScore(pageScore);
        pageResult.setGrade(pageGrade);
        pageResult.setCrawlSession(session);
        return pageResult;
    }

    private void linkIssuesToPage(List<AccessibilityIssue> issues, PageResult savedPage) {
        issues.forEach(issue -> issue.setPageResult(savedPage));
        accessibilityIssueService.createAllIssues(issues);
    }

    private void followLinks(WebDriver driver,
                             Document document,
                             String baseUrl,
                             int maxDepth,
                             int currentDepth,
                             Set<String> visitedUrls,
                             CrawlSession session) {
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String nextUrl = link.absUrl("href");
            crawlPage(driver, nextUrl, baseUrl, maxDepth, currentDepth + 1, visitedUrls, session);
        }
    }

    // ═══════════════════════════════════════════════
    // SESSION МЕТОДИ
    // ═══════════════════════════════════════════════

    private void finalizeSession(CrawlSession session) {
        List<PageResult> pages = pageResultService.getPagesBySessionId(session.getId());
        double sessionScore = scoreCalculationService.calculateSessionScore(pages);
        String sessionGrade = scoreCalculationService.calculateSessionGrade(sessionScore);

        session.setAverageScore(sessionScore);
        session.setOverallGrade(sessionGrade);
        session.setStatus("DONE");
        session.setFinishedAt(LocalDateTime.now());
        crawlSessionService.updateSession(session);
    }

    private void failSession(CrawlSession session) {
        session.setStatus("FAILED");
        session.setFinishedAt(LocalDateTime.now());
        crawlSessionService.updateSession(session);
    }

    // ═══════════════════════════════════════════════
    // DRIVER
    // ═══════════════════════════════════════════════

    private WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-notifications");
        return new ChromeDriver(options);
    }

    // ═══════════════════════════════════════════════
    // KEYBOARD NAVIGATION
    // ═══════════════════════════════════════════════

    private List<AccessibilityIssue> checkKeyboardNavigation(WebDriver driver) {
        List<AccessibilityIssue> issues = new ArrayList<>();

        try {
            issues.addAll(checkTabTrap(driver));
            issues.addAll(checkTabIndex(driver));
            issues.addAll(checkSkipLink(driver));
        } catch (Exception e) {
            System.err.println("Tab order check failed: " + e.getMessage());
        }

        return issues;
    }

    private List<AccessibilityIssue> checkTabTrap(WebDriver driver) throws InterruptedException {
        List<AccessibilityIssue> issues = new ArrayList<>();
        WebElement body = driver.findElement(By.tagName("body"));
        Set<String> visited = new HashSet<>();

        for (int i = 0; i < 15; i++) {
            body.sendKeys(Keys.TAB);
            Thread.sleep(300);

            WebElement active = driver.switchTo().activeElement();
            String identifier = active.getTagName() + "#" + active.getAttribute("id");

            if (visited.contains(identifier)) {
                AccessibilityIssue issue = new AccessibilityIssue();
                issue.setElementType(ElementType.PAGE);
                issue.setIssueType(IssueType.KEYBOARD_TRAP);
                issue.setDescription("Keyboard trap detected - focus is looping.");
                issue.setElementHTML(identifier);
                issues.add(issue);
                break;
            }
            visited.add(identifier);
        }
        return issues;
    }

    private List<AccessibilityIssue> checkTabIndex(WebDriver driver) {
        List<AccessibilityIssue> issues = new ArrayList<>();
        List<WebElement> customTabIndex = driver.findElements(By.xpath("//*[@tabindex]"));

        for (WebElement el : customTabIndex) {
            String val = el.getAttribute("tabindex");
            if (val != null && !val.equals("0") && !val.equals("-1")) {
                AccessibilityIssue issue = new AccessibilityIssue();
                issue.setElementType(ElementType.PAGE);
                issue.setIssueType(IssueType.BAD_TABINDEX);
                issue.setDescription("Custom tabindex may break natural tab order.");
                issue.setElementHTML(el.getTagName() + " tabindex=" + val);
                issues.add(issue);
            }
        }
        return issues;
    }

    private List<AccessibilityIssue> checkSkipLink(WebDriver driver) {
        List<AccessibilityIssue> issues = new ArrayList<>();
        List<WebElement> skipLinks = driver.findElements(By.xpath("//a[contains(@href, '#')]"));

        boolean hasSkip = skipLinks.stream()
                .anyMatch(el -> el.getText().toLowerCase().contains("skip"));

        if (!hasSkip) {
            AccessibilityIssue issue = new AccessibilityIssue();
            issue.setElementType(ElementType.PAGE);
            issue.setIssueType(IssueType.MISSING_SKIP_LINK);
            issue.setDescription("No 'Skip to content' link found.");
            issue.setElementHTML("N/A");
            issues.add(issue);
        }
        return issues;
    }
}