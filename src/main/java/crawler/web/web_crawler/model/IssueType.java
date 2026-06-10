package crawler.web.web_crawler.model;

public enum IssueType {

    // IMAGE
    MISSING_ALT(5, "Add a descriptive alt attribute to your <img> tag. Example: <img src='image.jpg' alt='A dog playing in the park'>"),
    EMPTY_ALT(3, "Fill in the alt attribute with a meaningful description of the image content."),
    ALT_TOO_SHORT(1, "The alt text is too short. Provide a more descriptive text (at least 3 characters)."),
    ALT_TOO_LONG(1, "The alt text is too long. Keep it concise, under 100 characters."),
    ALT_IS_FILENAME(2, "Replace the filename with a meaningful description. Example: instead of 'IMG_001.jpg' use 'Team photo at conference'."),
    NO_LAZY_LOADING(1, "Add loading='lazy' to improve page performance. Example: <img src='image.jpg' loading='lazy' alt='...'>."),

    // VIDEO
    MISSING_CONTROLS(5, "Add the controls attribute to allow users to play, pause and control the video. Example: <video controls>."),
    MISSING_CAPTIONS(5, "Add captions for deaf and hard of hearing users. Example: <track kind='captions' src='captions.vtt' srclang='en' label='English'>."),
    MISSING_ARIA_LABEL(3, "Add an aria-label attribute to describe the video content. Example: <video aria-label='Tutorial on Spring Boot'>."),
    MISSING_TITLE(2, "Add a title attribute to describe the video. Example: <video title='Introduction to Web Accessibility'>."),
    AUTOPLAY(2, "Avoid autoplay as it can be disruptive. If needed, always combine with muted and provide controls."),
    AUTOPLAY_WITHOUT_CAPTIONS(4, "Video with autoplay must have captions. Add <track kind='captions' src='captions.vtt'> inside the video tag."),
    AUTOPLAY_MUTED(1, "Autoplay with muted is acceptable, but ensure controls are available for users to unmute if needed."),
    IFRAME_MISSING_TITLE(2, "Add a title attribute to the iframe to describe its content. Example: <iframe title='YouTube video about accessibility'>."),

    // LANGUAGE
    MISSING_LANGUAGE(4, "Add a lang attribute to the <html> tag. Example: <html lang='en'> for English or <html lang='mk'> for Macedonian."),
    LANGUAGE_IS_EMPTY(3, "The lang attribute is empty. Specify a valid language code. Example: <html lang='en'>."),
    INVALID_LANGUAGE_CODE(3, "The language code is not valid. Use ISO 639-1 codes such as 'en', 'mk', 'de', 'fr'."),

    // TITLE
    TITLE_NOT_EXISTS(4, "Add a <title> tag inside <head>. Example: <title>Home - My Website</title>."),
    TITLE_IS_EMPTY(3, "The title tag is empty. Add a meaningful page title. Example: <title>About Us - My Website</title>."),
    TITLE_IS_NOT_MEANINGFUL(2, "The title is too short. Use a descriptive title that explains the page content."),
    TITLE_IS_GENERIC(1, "The title is too generic. Use a specific title that describes the page. Example: instead of 'Home' use 'Home - Archismart Architecture'."),

    // HEADINGS
    MISSING_HEADINGS(3, "Add heading elements (h1-h6) to structure your content. Start with a single <h1> for the main title."),
    H1_APPEARS_MORE_THAN_ONCE(2, "Use only one <h1> per page. It should represent the main topic of the page."),
    H1_DOES_NOT_APPEAR(3, "Add an <h1> element to define the main heading of the page."),
    HEADING_STRUCTURE_INVALID(2, "Do not skip heading levels. Example: after <h1> use <h2>, not <h4>. Maintain a logical hierarchy."),

    // PAGE
    PAGE_NOT_SCALABLE(3, "Ensure the page supports zoom up to 400% without horizontal scrolling. Avoid fixed widths and use responsive CSS units like %, rem, or em."),

    // KEYBOARD
    KEYBOARD_TRAP(5, "Ensure keyboard focus is never trapped. Users must be able to navigate away from any component using only the keyboard."),
    BAD_TABINDEX(2, "Avoid using tabindex values greater than 0. Use tabindex='0' to include elements in natural tab order or tabindex='-1' to exclude them."),
    MISSING_SKIP_LINK(2, "Add a 'Skip to content' link at the top of the page. Example: <a href='#main-content'>Skip to main content</a>.");

    private final int weight;
    private final String suggestion;

    IssueType(int weight, String suggestion) {
        this.weight = weight;
        this.suggestion = suggestion;
    }

    public int getWeight() {
        return weight;
    }

    public String getSuggestion() {
        return suggestion;
    }
}