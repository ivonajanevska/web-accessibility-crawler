package crawler.web.web_crawler.service.Implementation;

import crawler.web.web_crawler.model.PageResult;
import crawler.web.web_crawler.repository.PageResultRepository;
import crawler.web.web_crawler.service.PageResultService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PageResultServiceImpl implements PageResultService {

    private final PageResultRepository pageResultRepository;


    @Override
    public PageResult createPage (PageResult pageResult) {
        return pageResultRepository.save(pageResult);
    }

    @Override
    public PageResult getPageById(Long id) {
        return pageResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("The page with id " + id + " was not found"));
    }

    @Override
    public List<PageResult> getPagesBySessionId(Long sessionId) {
        return pageResultRepository.findByCrawlSessionId(sessionId);
    }
}
