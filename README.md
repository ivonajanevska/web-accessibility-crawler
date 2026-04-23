# Web Accessibility Crawler

Web Accessibility Crawler is a web application designed for automatic analysis 
of web accessibility on websites, with a focus on images, videos, page structure, 
language and title attributes.

The system is built using Spring Boot and follows a Layered Architecture pattern 
to ensure clean separation of concerns and maintainability.

### Features
- Automatic crawling of websites starting from a given URL
- Configurable crawl depth (1, 2 or 3 levels)
- Accessibility analysis of images (alt attributes)
- Accessibility analysis of videos (captions, controls, aria-label)
- Detection of YouTube/Vimeo iframe accessibility issues
- Page title and language attribute validation
- Heading structure validation (h1-h6 hierarchy)
- Session-based result storage and history
- Detailed per-page issue reporting

#

### Architecture

The project follows the Layered Architecture:
- **Model** → Core entities and domain models
- **Repository** → Data access layer (Spring Data JPA)
- **Service** → Business logic layer
- **Controller** → Presentation layer (Thymeleaf Views)

#

### Technologies Used

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot | Web framework |
| Selenium WebDriver | Browser automation for dynamic pages |
| Jsoup | HTML parsing and analysis |
| Spring Data JPA | ORM / data access |
| H2 Database | In-memory database |
| Thymeleaf | Frontend template engine |
| Lombok | Boilerplate code reduction |

#

### Domain Models

- CrawlSession
- PageResult
- AccessibilityIssue

### Enum Types

**ElementType:**
- IMAGE, VIDEO, IFRAME, HEADING, LANGUAGE, TITLE

**IssueType:**
- MISSING_ALT, EMPTY_ALT, ALT_TOO_SHORT, ALT_TOO_LONG, ALT_IS_FILENAME
- MISSING_CONTROLS, MISSING_CAPTIONS, MISSING_ARIA_LABEL, MISSING_TITLE
- AUTOPLAY_WITHOUT_CAPTIONS, IFRAME_MISSING_TITLE
- MISSING_HEADINGS, H1_APPEARS_MORE_THAN_ONCE, HEADING_STRUCTURE_INVALID
- MISSING_LANGUAGE, LANGUAGE_IS_EMPTY, INVALID_LANGUAGE_CODE
- TITLE_IS_EMPTY, TITLE_IS_NOT_MEANINGFUL, TITLE_IS_GENERIC

#

### Accessibility Checks

#### Images (`<img>`)
| Check | Description |
|---|---|
| MISSING_ALT | Image has no alt attribute |
| EMPTY_ALT | Image has empty alt attribute |
| ALT_TOO_SHORT | Alt text is shorter than 3 characters |
| ALT_TOO_LONG | Alt text is longer than 100 characters |
| ALT_IS_FILENAME | Alt text appears to be a filename |

#### Videos (`<video>`)
| Check | Description |
|---|---|
| MISSING_CONTROLS | Video has no controls attribute |
| MISSING_CAPTIONS | Video has no captions track |
| MISSING_ARIA_LABEL | Video has no aria-label attribute |
| MISSING_TITLE | Video has no title attribute |
| AUTOPLAY_WITHOUT_CAPTIONS | Video has autoplay but no captions |

#### Iframes (`<iframe>`)
| Check | Description |
|---|---|
| IFRAME_MISSING_TITLE | YouTube/Vimeo iframe has no title attribute |

#### Headings
| Check | Description |
|---|---|
| MISSING_HEADINGS | Page has no heading elements |
| H1_APPEARS_MORE_THAN_ONCE | Page has more than one h1 element |
| HEADING_STRUCTURE_INVALID | Heading levels are skipped |

#### Language & Title
| Check | Description |
|---|---|
| MISSING_LANGUAGE | Page has no lang attribute |
| LANGUAGE_IS_EMPTY | Lang attribute is empty |
| INVALID_LANGUAGE_CODE | Lang attribute has invalid value |
| MISSING_TITLE | Page has no title tag |
| TITLE_IS_EMPTY | Title tag is empty |
| TITLE_IS_NOT_MEANINGFUL | Title is too short |
| TITLE_IS_GENERIC | Title is a generic word |

#

## Routes & Endpoints

### Crawler

| Method | Route | Description |
|---|---|---|
| `GET` | `/` | Home page with crawl form and session history |
| `POST` | `/crawl` | Start a new crawl session |

### Sessions

| Method | Route | Description |
|---|---|---|
| `GET` | `/sessions/{id}` | View crawl session details and visited pages |

### Pages

| Method | Route | Description |
|---|---|---|
| `GET` | `/pages/{id}` | View page details and detected issues |

#

### Setup Instructions

#### Prerequisites

- Java 21
- Maven 3.8+
- Google Chrome (latest version)
- ChromeDriver (matching Chrome version)

### 1. Clone the repository

```bash
git clone https://github.com/ivonajanevska/web-accessibility-crawler.git
cd web-accessibility-crawler
```

### 2. Build the project

```bash
mvn clean install
```

### 3. Run the application

```bash
mvn spring-boot:run
```

### 4. Open in browser
