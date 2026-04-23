package crawler.web.web_crawler.service.Implementation;

import crawler.web.web_crawler.model.AccessibilityIssue;
import crawler.web.web_crawler.model.CrawlSession;
import crawler.web.web_crawler.model.PageResult;
import crawler.web.web_crawler.service.*;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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


    @Override
    public CrawlSession startCrawling(String baseUrl, int maxDepth) {

        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "https://" + baseUrl;
        }
        CrawlSession session = crawlSessionService.createSession(baseUrl);

        // 2. Постави Chrome во headless режим (без да се отвора прозорец)
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {
            // 3. Започни со crawling
            Set<String> visitedUrls = new HashSet<>();
            crawlPage(driver, baseUrl, baseUrl, maxDepth, 0, visitedUrls, session);

            // 4. Означи сесијата како завршена
            session.setStatus("DONE");
            session.setFinishedAt(LocalDateTime.now());
            crawlSessionService.updateSession(session);

        } catch (Exception e) {
            session.setStatus("FAILED");
            session.setFinishedAt(LocalDateTime.now());
            crawlSessionService.updateSession(session);

        } finally {
            driver.quit();
        }

        return session;
    }

    private void crawlPage(WebDriver driver,
                           String url,
                           String baseUrl,
                           int maxDepth,
                           int currentDepth,
                           Set<String> visitedUrls,
                           CrawlSession session) {
        if (currentDepth > maxDepth) return;
        if (visitedUrls.contains(url)) return;
        if (!url.startsWith(baseUrl)) return;

        visitedUrls.add(url);

        try {
            driver.get(url);

            Document document = Jsoup.parse(driver.getPageSource());

            List<AccessibilityIssue> issues = accessibilityCheckerService.checkPage(document);

            PageResult pageResult = new PageResult();
            pageResult.setPageUrl(url);
            pageResult.setTotalImages(document.select("img").size());
            pageResult.setTotalVideos(document.select("video").size());
            pageResult.setTotalIssues(issues.size());
            pageResult.setCrawlSession(session);
            PageResult savedPage = pageResultService.createPage(pageResult);

            issues.forEach(issue -> issue.setPageResult(savedPage));
            accessibilityIssueService.createAllIssues(issues);

            Elements links = document.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                crawlPage(driver, nextUrl, baseUrl, maxDepth, currentDepth + 1, visitedUrls, session);
            }

        } catch (Exception e) {
            System.err.println("Грешка при crawling на: " + url + " — " + e.getMessage());
        }
    }
}
