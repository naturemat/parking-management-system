## 5. `refactor-instructions.md` (Java version template)

```markdown
# Refactor Instructions Template - Java (FILL BEFORE EXAM)

## Fill this section with YOUR decisions:

### 1. Source Code Location
Current code file: _________________________________
(example: InstagramScraper.java or Main.java)

text

### 2. Target Architecture (check one)
- [ ] Layered (com.yourapp.presentation/application/domain/infrastructure)
- [ ] Hexagonal (com.yourapp.core + com.yourapp.adapters)
- [ ] DDD Tactical (aggregates, VOs, events)
- [ ] Bounded Contexts (Maven multi-module)

### 3. Base Package Name
com._________________________________
(example: com.instagram.scraper)

text

### 4. Folder Mapping (explicit)

**Move these responsibilities to `domain/` package**:
text

**Move these to `application/` package**:
text

**Move these to `infrastructure/` package**:
text

**Move these to `interfaces/` or `presentation/` package**:
text

### 5. Ubiquitous Language Dictionary (Java class names)

| Concept | Java Class Name | Package | Notes |
|---------|----------------|---------|-------|
| Instagram username | `InstagramUsername` | domain.valueobjects | Immutable, validate format |
| Post from Instagram | `InstagramPost` | domain.entities | Entity with ID |
| Scraping operation | `ScrapingJob` | domain.aggregates | Aggregate root |
| API rate limit | `RateLimit` | domain.valueobjects | With remaining/duration |
| _________________ | _________________ | _________________ | |
| _________________ | _________________ | _________________ | |

### 6. Value Objects to Create (with Java types)
InstagramUsername: String value (validation: regex, non-blank)

PostId: String id + LocalDateTime postedAt

MediaUrl: URL value (validation: starts with http)

RateLimit: int remaining, Duration resetTime

text

### 7. Entities to Create
InstagramPost: id(PostId), author(InstagramUsername), mediaUrl(MediaUrl), postedAt(LocalDateTime), likeCount(int)

UserSession: sessionId(String), apiKey(String), createdAt(LocalDateTime)

text

### 8. Aggregates (root entities)
ScrapingSession: root = ScrapingSession with List<InstagramPost> posts

UserProfile: root = UserProfile with List<ScrapingSession> sessions

text

### 9. Repository Interfaces (one per aggregate)
ScrapingSessionRepository: save, findById, findByUser, delete

InstagramPostRepository: save, findById, findByAuthor, findAll

text

### 10. Domain Services (stateless operations)
InstagramScrapingService: scrapePosts(username, rateLimit) -> List<InstagramPost>

RateLimitValidationService: isExceeded(rateLimit) -> boolean

text

### 11. Configuration in .env (Java properties)
API_KEY=placeholder
RATE_LIMIT_PER_MINUTE=60
MAX_POSTS_PER_SESSION=100
REQUEST_DELAY_MS=2000
USER_AGENT=Mozilla/5.0

text

### 12. Cookies File Format (cookies.json)
```json
{
  "sessionId": "placeholder",
  "csrfToken": "placeholder",
  "userAgent": "Mozilla/5.0"
}
13. Maven Dependencies Needed
xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
    </dependency>
    <dependency>
        <groupId>io.github.cdimascio</groupId>
        <artifactId>dotenv-java</artifactId>
        <version>3.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
</dependencies>