package crawler.web.web_crawler.service;

import crawler.web.web_crawler.model.CrawlSession;

import java.util.List;

public interface CrawlSessionService {

    CrawlSession createSession(String baseUrl);
    CrawlSession getSessionById(Long id);
    List<CrawlSession> getAllSessions();
    CrawlSession updateSession(CrawlSession session);
}
