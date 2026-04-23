package crawler.web.web_crawler.service;

import crawler.web.web_crawler.model.CrawlSession;

public interface CrawlerService {

    CrawlSession startCrawling(String baseUrl, int maxDepth);
}
