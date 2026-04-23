package crawler.web.web_crawler.service;

import crawler.web.web_crawler.model.PageResult;
import org.hibernate.query.Page;

import java.util.List;

public interface PageResultService {

    PageResult createPage(PageResult pageResult);
    PageResult getPageById(Long id);
    List<PageResult> getPagesBySessionId(Long sessionId);
}
