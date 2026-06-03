```markdown
# Layered Architecture Skill - Java

## Purpose
Refactor spaghetti code into clean 4-layer architecture (Presentation → Application → Domain → Infrastructure) in Java.

## Execution Rules (MANDATORY)
- ALL code in Java
- NO emojis
- Package naming: `com.yourapp.{layer}`
- Config in `.env` read with dotenv-java
- Cookies in `cookies.json` at project root (read with Jackson)
- Show all file changes with paths
- Update `REFACTORING_LOG.md`

## Layer Responsibilities

### 1. Presentation Layer (com.yourapp.presentation)
```java
public class CliController {
    private final ScrapingApplicationService scrapingService;
    
    public CliController(ScrapingApplicationService scrapingService) {
        this.scrapingService = scrapingService;
    }
    
    public void run(String[] args) {
        // Parse CLI args
        String username = args[0];
        ScrapingResult result = scrapingService.scrapeUserPosts(username);
        System.out.println("Scraped " + result.getCount() + " posts");
    }
}
2. Application Layer (com.yourapp.application)
public class ScrapingApplicationService {
    private final PostRepository postRepository;
    private final InstagramHttpClient instagramClient;
    
    public ScrapingApplicationService(PostRepository postRepository, InstagramHttpClient instagramClient) {
        this.postRepository = postRepository;
        this.instagramClient = instagramClient;
    }
    
    public ScrapingResult scrapeUserPosts(String username) {
        // Orchestrate, no business rules
        List<PostDto> rawPosts = instagramClient.fetchPosts(username);
        List<Post> domainPosts = rawPosts.stream()
            .map(dto -> new Post(dto.getId(), dto.getUsername(), dto.getUrl()))
            .collect(Collectors.toList());
        domainPosts.forEach(postRepository::save);
        return new ScrapingResult(domainPosts.size());
    }
}
3. Domain Layer (com.yourapp.domain)
java
public class Post {
    private final String id;
    private final String username;
    private final String mediaUrl;
    private int likeCount;
    
    public Post(String id, String username, String mediaUrl) {
        this.id = id;
        this.username = username;
        this.mediaUrl = mediaUrl;
        this.likeCount = 0;
    }
    
    public void incrementLikes() {
        this.likeCount++;
        // Business rule: max likes validation
        if (this.likeCount > 10000) {
            throw new DomainException("Like count exceeds maximum allowed");
        }
    }
}
4. Infrastructure Layer (com.yourapp.infrastructure)
java
public class InstagramHttpClient {
    private final String apiKey;
    private final HttpClient httpClient;
    
    public InstagramHttpClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }
    
    public List<PostDto> fetchPosts(String username) {
        // HTTP calls to Instagram
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.instagram.com/v1/users/" + username + "/media"))
            .header("Authorization", "Bearer " + apiKey)
            .build();
        // execute and parse JSON...
        return List.of();
    }
}
Directory Structure (Maven)
text
src/main/java/com/yourapp/
├── presentation/
│   └── cli/
│       └── MainController.java
├── application/
│   └── services/
│       └── ScrapingApplicationService.java
├── domain/
│   ├── model/
│   │   └── Post.java
│   ├── exceptions/
│   │   └── DomainException.java
│   └── repositories/
│       └── PostRepository.java
└── infrastructure/
    ├── http/
    │   └── InstagramHttpClient.java
    ├── repositories/
    │   └── PostRepositoryImpl.java
    └── config/
        └── ConfigLoader.java
Refactoring Process (from Java spaghetti)
Step 1: Original Spaghetti Code
java
// Original: all in one Main.java
public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("API_KEY");
        String username = args[0];
        // HTTP call
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.instagram.com/v1/users/" + username + "/media"))
            .header("Authorization", "Bearer " + apiKey)
            .build();
        // parse response, save to file, print results...
    }
}
Step 2: Extract Infrastructure First
Move HTTP logic to InstagramHttpClient

Step 3: Extract Domain
Move business rules (validation, like counting) to Post class

Step 4: Extract Application
Move orchestration to ScrapingApplicationService

Step 5: Extract Presentation
Leave only CLI parsing in MainController

Example Prompt
"Use layered-architecture.md to refactor InstagramScraper.java into presentation/application/domain/infrastructure packages in Java. Follow Maven structure."

Dependency Rule Check
CORRECT:

java
// Presentation depends on Application
CliController → ScrapingApplicationService

// Application depends on Domain and Infrastructure interfaces
ScrapingApplicationService → PostRepository (interface)
ScrapingApplicationService → InstagramHttpClient (concrete, but should be interface)

// Domain depends on nothing
Post → no external imports
INCORRECT:

java
// NO: Domain depends on Infrastructure
import com.yourapp.infrastructure.HttpClient; // FORBIDDEN in domain package
Validation Checklist
No System.out.println in domain or application

No HTTP calls in domain

No file I/O in domain (use infrastructure)

No @Repository in domain (only in infrastructure)

Each class has single responsibility

Constructor injection used (no hidden dependencies)
```