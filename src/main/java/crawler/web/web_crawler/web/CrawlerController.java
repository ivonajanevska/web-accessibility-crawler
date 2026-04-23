package crawler.web.web_crawler.web;

import crawler.web.web_crawler.model.CrawlSession;
import crawler.web.web_crawler.model.PageResult;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CrawlerController {

    private final CrawlerService crawlerService;
    private final CrawlSessionService crawlSessionService;
    private final PageResultService pageResultService;

    // ═══════════════════════════════════════════════
    // ПОЧЕТНА СТРАНИЦА
    // ═══════════════════════════════════════════════

    // Ја прикажува почетната страница со формата


    // ═══════════════════════════════════════════════
    // CRAWLING
    // ═══════════════════════════════════════════════

    // Прима URL и maxDepth, започнува crawling
    @PostMapping("/crawl")
    public String startCrawl(@RequestParam String url,
                             @RequestParam(defaultValue = "1") int maxDepth,
                             Model model) {

        CrawlSession session = crawlerService.startCrawling(url, maxDepth);
        return "redirect:/sessions/" + session.getId();
    }

    // ═══════════════════════════════════════════════
    // СЕСИИ
    // ═══════════════════════════════════════════════

    // Листа на сите сесии
    @GetMapping("/")
    public String homePage(Model model) {
        List<CrawlSession> sessions = crawlSessionService.getAllSessions();
        model.addAttribute("sessions", sessions);
        return "index";
    }

    // Детали за една сесија
    @GetMapping("/sessions/{id}")
    public String getSessionById(@PathVariable Long id, Model model) {
        CrawlSession crawlSession = crawlSessionService.getSessionById(id);
        List<PageResult> pages = pageResultService.getPagesBySessionId(id);

        model.addAttribute("crawlSession", crawlSession); // ← промени го името
        model.addAttribute("pages", pages);
        return "session-detail";
    }

    // ═══════════════════════════════════════════════
    // СТРАНИЦИ
    // ═══════════════════════════════════════════════

    // Детали за една страница и нејзините проблеми
    @GetMapping("/pages/{id}")
    public String getPageById(@PathVariable Long id, Model model) {
        PageResult page = pageResultService.getPageById(id);

        model.addAttribute("page", page);
        model.addAttribute("issues", page.getIssues());
        return "page-detail"; // → templates/page-detail.html
    }
}