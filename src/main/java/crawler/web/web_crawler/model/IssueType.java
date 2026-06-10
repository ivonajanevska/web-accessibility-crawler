package crawler.web.web_crawler.model;

public enum IssueType {

    // IMAGE
    MISSING_ALT(5),
    EMPTY_ALT(3),
    ALT_TOO_SHORT(1),
    ALT_TOO_LONG(1),
    ALT_IS_FILENAME(2),
    NO_LAZY_LOADING(1),

    // VIDEO
    MISSING_CONTROLS(5),
    MISSING_CAPTIONS(5),
    MISSING_ARIA_LABEL(3),
    MISSING_TITLE(2),
    AUTOPLAY(2),
    AUTOPLAY_WITHOUT_CAPTIONS(4),
    AUTOPLAY_MUTED(1),
    IFRAME_MISSING_TITLE(2),

    // LANGUAGE
    MISSING_LANGUAGE(4),
    LANGUAGE_IS_EMPTY(3),
    INVALID_LANGUAGE_CODE(3),

    // TITLE
    TITLE_NOT_EXISTS(4),
    TITLE_IS_EMPTY(3),
    TITLE_IS_NOT_MEANINGFUL(2),
    TITLE_IS_GENERIC(1),

    // HEADINGS
    MISSING_HEADINGS(3),
    H1_APPEARS_MORE_THAN_ONCE(2),
    H1_DOES_NOT_APPEAR(3),
    HEADING_STRUCTURE_INVALID(2),

    // PAGE
    PAGE_NOT_SCALABLE(3),

    // KEYBOARD
    KEYBOARD_TRAP(5),
    BAD_TABINDEX(2),
    MISSING_SKIP_LINK(2);

    private final int weight;

    IssueType(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
