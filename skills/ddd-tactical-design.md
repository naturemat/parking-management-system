# DDD Tactical Design Skill - Java

## Purpose
Apply Domain-Driven Design tactical patterns to a new or existing Java codebase. Use when exam asks for: aggregates, value objects, entities, domain services, repositories, domain events.

## Execution Rules (MANDATORY)
- ALL code in Java (Java 17+)
- NO emojis, NO decorative comments
- Use Lombok for boilerplate (or generate manually if Lombok not allowed)
- Ubiquitous Language: same term for same concept across ALL packages
- Configurable parameters in `.env` at project root (read with dotenv-java or Properties)
- Cookies in `cookies.json` at project root
- Clean Code: descriptive names, small methods (<20 lines), single responsibility
- Package naming: `com.yourapp.{context}.{layer}`
- Show EVERY file change with path and rationale
- Update `REFACTORING_LOG.md` after each change

## Output Structure (Maven/Gradle)
When applied, create/modify:
src/main/java/com/yourapp/
├── domain/
│ ├── model/
│ │ ├── aggregates/
│ │ │ └── {AggregateRoot}.java
│ │ ├── entities/
│ │ │ └── {Entity}.java
│ │ └── valueobjects/
│ │ └── {ValueObject}.java
│ ├── services/
│ │ └── {DomainService}.java
│ ├── repositories/
│ │ └── {RepositoryInterface}.java
│ └── events/
│ └── {DomainEvent}.java
├── application/
│ ├── commands/
│ │ └── {Command}.java
│ ├── queries/
│ │ └── {Query}.java
│ ├── handlers/
│ │ └── {CommandHandler}.java
│ └── dtos/
│ └── {DTO}.java
├── infrastructure/
│ ├── repositories/
│ │ └── {RepositoryImpl}.java
│ ├── http/
│ │ └── {HttpClient}.java
│ ├── config/
│ │ └── AppConfig.java
│ └── storage/
│ └── FileStorage.java
└── interfaces/
├── cli/
│ └── CliController.java
└── api/
└── RestController.java

src/test/java/com/yourapp/
├── domain/
├── application/
└── infrastructure/

pom.xml (or build.gradle)
.env
cookies.json
REFACTORING_LOG.md


## Step-by-Step Process

### Step 1: Identify Aggregates
- Ask: "What are the transactional boundaries?"
- Output: List of aggregates with root entity

### Step 2: Extract Value Objects
- Find attributes without identity (email, username, postId, url)
- Create immutable Value Objects with validation in constructor
- Implement `equals()` and `hashCode()`

```java
public final class InstagramUsername {
    private final String value;
    
    public InstagramUsername(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.value = value;
    }
    
    public String getValue() { return value; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstagramUsername)) return false;
        InstagramUsername that = (InstagramUsername) o;
        return Objects.equals(value, that.value);
    }
    
    @Override
    public int hashCode() { return Objects.hash(value); }
}
Step 3: Define Entities and Aggregates
public class Post extends AggregateRoot {
    private final PostId id;
    private final InstagramUsername author;
    private final MediaUrl mediaUrl;
    private final LocalDateTime postedAt;
    private int likeCount;
    
    public Post(PostId id, InstagramUsername author, MediaUrl mediaUrl, LocalDateTime postedAt) {
        this.id = id;
        this.author = author;
        this.mediaUrl = mediaUrl;
        this.postedAt = postedAt;
        this.likeCount = 0;
    }
    
    public void updateLikes(int newLikeCount) {
        if (newLikeCount < 0) throw new IllegalArgumentException("Like count cannot be negative");
        this.likeCount = newLikeCount;
        registerEvent(new PostLikesUpdated(this.id, newLikeCount));
    }
    
    // getters...
}
Step 4: Define Domain Services
public interface ScrapingDomainService {
    ScrapingResult scrapePosts(InstagramUsername username, RateLimit rateLimit);
}

public class InstagramScrapingService implements ScrapingDomainService {
    @Override
    public ScrapingResult scrapePosts(InstagramUsername username, RateLimit rateLimit) {
        // business logic here, no infrastructure calls directly
        if (rateLimit.isExceeded()) {
            throw new RateLimitExceededException("Rate limit exceeded for " + username.getValue());
        }
        // validation rules, domain logic
        return new ScrapingResult(List.of());
    }
}
Step 5: Create Repository Interfaces
public interface PostRepository {
    Post save(Post post);
    Optional<Post> findById(PostId id);
    List<Post> findByAuthor(InstagramUsername username);
    void delete(PostId id);
    List<Post> findAll();
}
Step 6: Implement Infrastructure Repository
@Repository
public class PostRepositoryImpl implements PostRepository {
    private final Map<PostId, Post> database = new ConcurrentHashMap<>();
    
    @Override
    public Post save(Post post) {
        database.put(post.getId(), post);
        return post;
    }
    
    @Override
    public Optional<Post> findById(PostId id) {
        return Optional.ofNullable(database.get(id));
    }
    
    // other methods...
}
Step 7: Domain Events
public abstract class DomainEvent {
    private final LocalDateTime occurredAt = LocalDateTime.now();
    public LocalDateTime getOccurredAt() { return occurredAt; }
}

public class PostScraped extends DomainEvent {
    private final PostId postId;
    private final InstagramUsername username;
    
    public PostScraped(PostId postId, InstagramUsername username) {
        this.postId = postId;
        this.username = username;
    }
    
    // getters...
}

Example Prompt to Activate
"Use ddd-tactical-design.md to refactor this Instagram scraper in Java. Identify aggregates: Post, ScrapingSession. Create Value Objects: InstagramUsername, PostId, MediaUrl. Generate repositories and domain events."

Environment Variables Template (.env)
text
API_KEY=your_instagram_api_key
REQUEST_DELAY_SECONDS=2
MAX_RETRIES=3
SESSION_TIMEOUT_MINUTES=30
LOG_LEVEL=INFO
Maven Dependencies (pom.xml essentials)
xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.15.2</version>
    </dependency>
    <dependency>
        <groupId>io.github.cdimascio</groupId>
        <artifactId>dotenv-java</artifactId>
        <version>3.0.0</version>
    </dependency>
</dependencies>
Validation Checklist for Exam
Every domain concept has single representation

Value objects are immutable (final class, final fields)

Value objects validate in constructor

Aggregates protect invariants with methods (not public setters)

Repository interfaces in domain, implementations in infrastructure

No infrastructure imports in domain layer

All classes follow JavaBeans or record pattern (Java 17+)
```