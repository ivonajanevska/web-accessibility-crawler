package crawler.web.web_crawler.service;

import crawler.web.web_crawler.model.CrawlSession;

import java.util.List;

public interface CrawlerService {

    CrawlSession startCrawling(String baseUrl, int maxDepth);

    List<CrawlSession> startBatchCrawling(List<String> urls, int maxDepth);
}
