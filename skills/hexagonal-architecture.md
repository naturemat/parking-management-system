```markdown
# Hexagonal Architecture (Ports & Adapters) Skill - Java

## Purpose
Apply hexagonal architecture in Java when exam explicitly asks for it.

## Execution Rules (MANDATORY)
- Java code with interfaces (ports) and implementations (adapters)
- Package: `com.yourapp.core` (inside), `com.yourapp.adapters` (outside)
- .env and cookies.json at root
- Show all changes with paths
- Update REFACTORING_LOG.md

## Directory Structure (Java)
src/main/java/com/yourapp/
├── core/
│ ├── domain/
│ │ ├── model/
│ │ │ ├── Post.java
│ │ │ └── InstagramUsername.java
│ │ └── exceptions/
│ │ └── DomainException.java
│ └── application/
│ ├── ports/
│ │ ├── driving/
│ │ │ └── ForScraping.java
│ │ └── driven/
│ │ ├── ForFetchingInstagram.java
│ │ └── ForStoringPosts.java
│ └── services/
│ └── ScrapingService.java
└── adapters/
├── driving/
│ └── cli/
│ └── CliAdapter.java
└── driven/
├── instagram/
│ └── InstagramAdapter.java
└── filesystem/
└── FileSystemPostRepository.java

text

## Step-by-Step

### Step 1: Define Driving Port (what the user can do)
```java
package com.yourapp.core.application.ports.driving;

public interface ForScraping {
    ScrapingResult scrapePosts(String username);
}
Step 2: Define Driven Ports (what infrastructure must provide)
java
package com.yourapp.core.application.ports.driven;

public interface ForFetchingInstagram {
    List<RawPostDto> fetchPostsByUsername(String username);
}

public interface ForStoringPosts {
    void save(Post post);
    Optional<Post> findById(String id);
}
Step 3: Implement Use Case (inside hexagon)
java
package com.yourapp.core.application.services;

public class ScrapingService implements ForScraping {
    private final ForFetchingInstagram instagramFetcher;
    private final ForStoringPosts postStorage;
    
    public ScrapingService(ForFetchingInstagram instagramFetcher, ForStoringPosts postStorage) {
        this.instagramFetcher = instagramFetcher;
        this.postStorage = postStorage;
    }
    
    @Override
    public ScrapingResult scrapePosts(String username) {
        List<RawPostDto> rawPosts = instagramFetcher.fetchPostsByUsername(username);
        List<Post> domainPosts = rawPosts.stream()
            .map(raw -> new Post(raw.id(), raw.username(), raw.mediaUrl()))
            .collect(Collectors.toList());
        domainPosts.forEach(postStorage::save);
        return new ScrapingResult(domainPosts.size());
    }
}
Step 4: Create Driven Adapters (outside hexagon)
java
package com.yourapp.adapters.driven.instagram;

public class InstagramAdapter implements ForFetchingInstagram {
    private final String apiKey;
    private final HttpClient httpClient;
    
    public InstagramAdapter(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }
    
    @Override
    public List<RawPostDto> fetchPostsByUsername(String username) {
        // Actual HTTP implementation
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.instagram.com/v1/users/" + username + "/media"))
            .header("Authorization", "Bearer " + apiKey)
            .build();
        // execute and parse...
        return List.of();
    }
}
Step 5: Create Driving Adapter (CLI)
java
package com.yourapp.adapters.driving.cli;

public class CliAdapter {
    private final ForScraping scrapingService;
    
    public CliAdapter(ForScraping scrapingService) {
        this.scrapingService = scrapingService;
    }
    
    public void run(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar app.jar <username>");
            return;
        }
        ScrapingResult result = scrapingService.scrapePosts(args[0]);
        System.out.println("Scraped " + result.getCount() + " posts");
    }
}
Step 6: Composition Root (Main.java)
java
package com.yourapp;

public class Main {
    public static void main(String[] args) {
        // Load config
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("API_KEY");
        
        // Create adapters (outside)
        InstagramAdapter instagramAdapter = new InstagramAdapter(apiKey);
        FileSystemPostRepository postRepository = new FileSystemPostRepository("./posts.json");
        
        // Create use case with injected adapters (inside)
        ScrapingService scrapingService = new ScrapingService(instagramAdapter, postRepository);
        
        // Create driving adapter
        CliAdapter cli = new CliAdapter(scrapingService);
        
        // Run
        cli.run(args);
    }
}
When to Use (Exam Indicators)
Question mentions "ports and adapters"

Question mentions "dependency inversion explicitly"

Question shows hexagon diagram

Validation Checklist
No adapter imports inside core/ package

All dependencies point inward (core knows nothing about adapters)

Ports are Java interfaces, not implementations

Composition root (Main) wires everything

Can replace CLI with REST without touching core

Can replace Instagram API with mock without touching core
```