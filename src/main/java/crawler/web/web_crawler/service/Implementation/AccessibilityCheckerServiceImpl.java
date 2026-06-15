package crawler.web.web_crawler.service.Implementation;

import crawler.web.web_crawler.model.AccessibilityIssue;
import crawler.web.web_crawler.model.ElementType;
import crawler.web.web_crawler.model.IssueType;
import crawler.web.web_crawler.service.AccessibilityCheckerService;
import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.idealized.Javascript;
import org.springframework.stereotype.Service;


import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class AccessibilityCheckerServiceImpl implements AccessibilityCheckerService {


    @Override
    public List<AccessibilityIssue> checkPage(Document document) {

        List<AccessibilityIssue> issues = new ArrayList<>();
        issues.addAll(checkImages(document));
        issues.addAll(checkVideos(document));
        issues.addAll(checkIframes(document));
        issues.addAll(checkLanguage(document));
        issues.addAll(checkTitle(document));
        issues.addAll(checkHeadings(document));
        issues.addAll(checkFormLabels(document));
        //issues.addAll(checkZoomAccessibility(document, ));
        return issues;
    }


    private List<AccessibilityIssue> checkImages(Document document) {
        List<AccessibilityIssue> issues = new ArrayList<>();

        Elements images = document.select("img");

        for (Element img : images) {

            if (!img.hasAttr("alt")) {
                issues.add(createIssue(ElementType.IMAGE, IssueType.MISSING_ALT, "There is no ALT attribute", img.outerHtml()));
            } else {
                String altText = img.attr("alt").trim();
                if (altText.length() == 0) {
                    issues.add(createIssue(ElementType.IMAGE, IssueType.EMPTY_ALT, "The ALT is empty", img.outerHtml()));
                }else {
                    if (altText.length() < 3) {
                        issues.add(createIssue(ElementType.IMAGE, IssueType.ALT_TOO_SHORT, "The ALT is too short", img.outerHtml()));
                    }
                    if (altText.length() > 100) {
                        issues.add(createIssue(ElementType.IMAGE, IssueType.ALT_TOO_LONG, "The ALT is too long", img.outerHtml()));
                    }
                    if (isFilename(altText)) {
                        issues.add(createIssue(ElementType.IMAGE, IssueType.ALT_IS_FILENAME, "The ALT is a filename", img.outerHtml()));
                    }
                }
            }
            if(!img.hasAttr("loading"))
            {
                issues.add(createIssue(ElementType.IMAGE, IssueType.NO_LAZY_LOADING, "No loading predifined", img.outerHtml()));
            }


        }

        return issues;
    }


    private List<AccessibilityIssue> checkVideos(Document document) {
        List<AccessibilityIssue> issues = new ArrayList<>();

        Elements videos = document.select("video");

        for (Element video : videos) {
            if (!video.hasAttr("controls")) {
                issues.add(createIssue(ElementType.VIDEO, IssueType.MISSING_CONTROLS, "This video is missing controls", video.outerHtml()));
            }
            Elements tracks = video.select("track[kind=captions]");
            if (tracks.isEmpty()) {
                issues.add(createIssue(ElementType.VIDEO, IssueType.MISSING_CAPTIONS, "This video is missing captions", video.outerHtml()));
            }
            if (!video.hasAttr("aria-label")) {
                issues.add(createIssue(ElementType.VIDEO, IssueType.MISSING_ARIA_LABEL, "This video is missing aria-label", video.outerHtml()));
            }
            if(video.hasAttr("autoplay"))
            {
                issues.add(createIssue(ElementType.VIDEO, IssueType.AUTOPLAY, "The video has autoplay", video.outerHtml()));
                if (tracks.isEmpty())
                {
                    issues.add(createIssue(ElementType.VIDEO, IssueType.AUTOPLAY_WITHOUT_CAPTIONS, "The video has autoplay, but it's missing captions", video.outerHtml()));
                }
                if(video.hasAttr("muted"))
                {
                    issues.add(createIssue(ElementType.VIDEO, IssueType.AUTOPLAY_MUTED, "The video has autoplay, but it's muted", video.outerHtml()));
                }
            }
            if(!video.hasAttr("title"))
            {
                issues.add(createIssue(ElementType.VIDEO, IssueType.MISSING_TITLE, "The video does not have a title", video.outerHtml()));
            }
        }
        return issues;
    }

    private List<AccessibilityIssue> checkIframes(Document document)
    {
        List<AccessibilityIssue> issues = new ArrayList<>();

        Elements iframes = document.select("iframe");

        for(Element iframe : iframes)
        {
            String src = iframe.attr("src").toLowerCase();

            boolean isVideoIframe = src.contains("youtube.com") ||
                                    src.contains("youtu.be") ||
                                    src.contains("vimeo.com");

            if(isVideoIframe) {
                if (!iframe.hasAttr("title") || iframe.attr("title").trim().isEmpty())
                {
                    issues.add(createIssue(ElementType.IFRAME, IssueType.IFRAME_MISSING_TITLE, "The video is missing a title", iframe.outerHtml()));
                }
            }
        }

        return issues;
    }

    private List<AccessibilityIssue> checkLanguage(Document document)
    {
        List<AccessibilityIssue> issues = new ArrayList<>();

        Element html = document.selectFirst("html");

        if (html == null)
        {
            issues.add(createIssue(ElementType.LANGUAGE, IssueType.MISSING_LANGUAGE, "Missing <html> element", document.outerHtml()));
            return issues;
        }
        if(!html.hasAttr("lang"))
        {
            issues.add(createIssue(ElementType.LANGUAGE, IssueType.MISSING_LANGUAGE, "This page is missing language tag", html.outerHtml()));
        }else{
            String htmlLang = html.attr("lang").trim().toLowerCase();
            if (htmlLang.length() == 0)
            {
                issues.add(createIssue(ElementType.LANGUAGE, IssueType.LANGUAGE_IS_EMPTY, "The language attribute is empty", html.outerHtml()));
            }
            else if(!htmlLang.matches("^[a-zA-Z]{2,3}(-[a-zA-Z]{2})?$"))
            {
                issues.add(createIssue(ElementType.LANGUAGE, IssueType.INVALID_LANGUAGE_CODE, "This language code is not valid", html.outerHtml()));
            }
        }
        return issues;
    }



    private List<AccessibilityIssue> checkTitle (Document document)
    {
        List<AccessibilityIssue> issues = new ArrayList<>();
        Element title = document.selectFirst("title");

        List<String> badTitles = List.of("home", "homepage", "about", "contact", "services", "контакт", "дома", "услуги");
        if(title == null)
        {
            issues.add(createIssue(ElementType.TITLE, IssueType.MISSING_TITLE, "Title tag is missing", document.outerHtml()));

        }else {
            String textTitle = title.text().trim();

            if (textTitle.isEmpty()) {
                issues.add(createIssue(ElementType.TITLE, IssueType.TITLE_IS_EMPTY, "This title is empty", title.outerHtml()));
            }else if (textTitle.length() < 4) {
                issues.add(createIssue(ElementType.TITLE, IssueType.TITLE_IS_NOT_MEANINGFUL, "This title is very short", title.outerHtml()));
            }else if(badTitles.contains(textTitle.toLowerCase()))
            {
                issues.add(createIssue(ElementType.TITLE, IssueType.TITLE_IS_GENERIC, "This title is generic", title.outerHtml()));
            }
        }

        return issues;

    }



    private List<AccessibilityIssue> checkHeadings (Document document) {
        List<AccessibilityIssue> issues = new ArrayList<>();

        Elements headings = document.select("h1, h2, h3, h4, h5, h6");
        Elements h1s = document.select("h1");
        if(headings.isEmpty())
        {
            issues.add(createIssue(ElementType.HEADING, IssueType.MISSING_HEADINGS, "There is no <h..> element", document.outerHtml()));
            return issues;
        }else if(h1s.size() > 1)
        {
            issues.add(createIssue(ElementType.HEADING, IssueType.H1_APPEARS_MORE_THAN_ONCE, "The H1 tag appears more than once", document.outerHtml()));
        }
        List<Integer> levels = new ArrayList<>();


        for (Element h : headings)
        {
            String tag = h.tagName();
            int tagLevel = Integer.parseInt(tag.substring(1));
            levels.add(tagLevel);

        }
        for (int i=1; i<levels.size(); i++)
        {
            int prev = levels.get(i-1);
            int curr = levels.get(i);

            if (curr - prev > 1)
            {
                issues.add(createIssue(ElementType.HEADING, IssueType.HEADING_STRUCTURE_INVALID, "This structure is not valid", headings.get(i).outerHtml()));
            }
        }

        return issues;
    }


    private List<AccessibilityIssue> checkFormLabels(Document document) {
        List<AccessibilityIssue> issues = new ArrayList<>();

        Elements labels = document.select("label");
        Elements inputs = document.select("input[type=text], input[type=email], input[type=password], input[type=tel], input[type=number], textarea, select");

        // Собери ги сите id-ови на input елементи
        Set<String> inputIds = new HashSet<>();
        for (Element input : inputs) {
            if (input.hasAttr("id")) {
                inputIds.add(input.attr("id"));
            }
        }

        // Собери ги сите 'for' вредности од label елементи
        Set<String> labelForValues = new HashSet<>();

        for (Element label : labels) {
            if (!label.hasAttr("for")) {
                issues.add(createIssue(ElementType.FORM, IssueType.FORM_LABEL_MISSING_FOR,
                        "Label is missing the 'for' attribute", label.outerHtml()));
            } else {
                String forValue = label.attr("for");
                labelForValues.add(forValue);

                if (!inputIds.contains(forValue)) {
                    issues.add(createIssue(ElementType.FORM, IssueType.FORM_LABEL_FOR_MISMATCH,
                            "Label 'for' attribute does not match any input id: " + forValue,
                            label.outerHtml()));
                }
            }
        }

        // Провери дали секој input има соодветен label
        for (Element input : inputs) {
            String inputId = input.attr("id");

            if (inputId.isEmpty() || !labelForValues.contains(inputId)) {
                issues.add(createIssue(ElementType.FORM, IssueType.FORM_INPUT_MISSING_LABEL,
                        "Input field is missing an associated label", input.outerHtml()));
            }
        }

        return issues;
    }

    private List<AccessibilityIssue> checkZoomAccessibility(Document document, WebDriver driver) {



        List<AccessibilityIssue> issues = new ArrayList<>();


        return issues;
    }

    private boolean isFilename(String altText) {
        String lower = altText.toLowerCase();
        return lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg") ||
                lower.endsWith(".png") ||
                lower.endsWith(".gif") ||
                lower.endsWith(".webp") ||
                lower.endsWith(".svg") ||
                lower.endsWith(".bmp");
    }


    private AccessibilityIssue createIssue(ElementType elementType, IssueType issueType, String description, String elementHTML)
    {
        AccessibilityIssue issue = new AccessibilityIssue();
        issue.setElementType(elementType);
        issue.setIssueType(issueType);
        issue.setDescription(description);
        issue.setElementHTML(elementHTML);
        issue.setSuggestion(issueType.getSuggestion());
        return issue;
    }
}
