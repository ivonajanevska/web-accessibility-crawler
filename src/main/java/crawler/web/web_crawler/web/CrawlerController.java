package crawler.web.web_crawler.web;

import crawler.web.web_crawler.model.AccessibilityIssue;
import crawler.web.web_crawler.model.CrawlSession;
import crawler.web.web_crawler.model.PageResult;
import crawler.web.web_crawler.service.AccessibilityIssueService;
import crawler.web.web_crawler.service.CrawlSessionService;
import crawler.web.web_crawler.service.CrawlerService;
import crawler.web.web_crawler.service.PageResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class CrawlerController {

    private final CrawlerService crawlerService;
    private final CrawlSessionService crawlSessionService;
    private final PageResultService pageResultService;
    private final AccessibilityIssueService accessibilityIssueService;



    @GetMapping("/")
    public String homePage(Model model) {
        List<CrawlSession> sessions = crawlSessionService.getAllSessions();
        model.addAttribute("sessions", sessions);
        return "index";
    }

    @PostMapping("/crawl")
    public String startCrawl(@RequestParam String urls,
                             @RequestParam(defaultValue = "1") int maxDepth,
                             Model model) {

        List<String> urlList = parseUrls(urls);

        if (urlList.size() == 1) {
            CrawlSession session = crawlerService.startCrawling(urlList.get(0), maxDepth);
            return "redirect:/sessions/" + session.getId();
        } else {
            List<CrawlSession> sessions = crawlerService.startBatchCrawling(urlList, maxDepth);
            model.addAttribute("sessions", sessions);
            return "redirect:/";
        }
    }

    @GetMapping("/sessions/{id}")
    public String getSessionById(@PathVariable Long id, Model model) {
        CrawlSession crawlSession = crawlSessionService.getSessionById(id);
        List<PageResult> pages = pageResultService.getPagesBySessionId(id);

        model.addAttribute("crawlSession", crawlSession); // ← промени го името
        model.addAttribute("pages", pages);
        return "session-detail";
    }



    @GetMapping("/pages/{id}")
    public String getPageById(@PathVariable Long id, Model model) {
        PageResult page = pageResultService.getPageById(id);
        List<AccessibilityIssue> issues = accessibilityIssueService.getIssuesByPageId(id);
        model.addAttribute("page", page);
        model.addAttribute("issues", issues);
        return "page-detail";
    }

    @PostMapping("/batch-crawl")
    public String startBatchCrawl(@RequestParam String urls,
                                  @RequestParam (defaultValue = "1") int maxDepth,
                                  Model model)
    {
        List<String> urlList = parseUrls(urls);
        List<CrawlSession> sessions = crawlerService.startBatchCrawling(urlList, maxDepth);

        model.addAttribute("sessions", sessions);
        return "batch-results";
    }

    @PostMapping("/batch-crawl-csv")
    public String startBatchCrawlCsv(@RequestParam("file") MultipartFile file,
                                     @RequestParam(defaultValue = "1") int maxDepth,
                                     Model model) throws IOException {

        List<String> urlList = parseCsvFile(file);
        List<CrawlSession> sessions = crawlerService.startBatchCrawling(urlList, maxDepth);

        model.addAttribute("sessions", sessions);
        return "batch-results";
    }



    private List<String> parseUrls(String urls) {
        return Arrays.stream(urls.split("[\\n\\r\\s,]+"))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .collect(Collectors.toList());
    }

    private List<String> parseCsvFile(MultipartFile file) throws IOException {
        String content = new String(file.getBytes());
        return Arrays.stream(content.split("[\\n\\r,]+"))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .collect(Collectors.toList());
    }
}