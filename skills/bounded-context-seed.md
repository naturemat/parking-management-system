```markdown
# Bounded Context Seed Skill - Java

## Purpose
Define bounded contexts and generate Maven multi-module project seed.

## Execution Rules (MANDATORY)
- Java with Maven multi-module structure
- English, no emojis
- .env and cookies.json at root of each context or global
- Show all changes
- Update REFACTORING_LOG.md

## Multi-Module Maven Structure
project-root/
├── pom.xml (parent)
├── .env
├── cookies.json
├── REFACTORING_LOG.md
├── authentication-context/
│ ├── pom.xml
│ └── src/main/java/com/scraper/authentication/
│ ├── domain/
│ ├── application/
│ ├── infrastructure/
│ └── interfaces/
├── scraping-context/
│ ├── pom.xml
│ └── src/main/java/com/scraper/scraping/
│ ├── domain/
│ ├── application/
│ ├── infrastructure/
│ └── interfaces/
├── storage-context/
│ ├── pom.xml
│ └── src/main/java/com/scraper/storage/
└── shared-kernel/
├── pom.xml
└── src/main/java/com/scraper/shared/
├── events/
└── utils/

text

## Parent POM (pom.xml)
```xml
<project>
    <groupId>com.instagram.scraper</groupId>
    <artifactId>scraper-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    <modules>
        <module>authentication-context</module>
        <module>scraping-context</module>
        <module>storage-context</module>
        <module>shared-kernel</module>
    </modules>
</project>
Step 1: Identify Bounded Contexts
Example for Instagram Scraper System:

Authentication Context (com.scraper.authentication)

Ubiquitous language: User, Session, ApiKey, RateLimit

Responsibilities: API key validation, session management, token refresh

Scraping Context (com.scraper.scraping)

Ubiquitous language: Post, Media, ScrapingJob, RateLimit (different meaning)

Responsibilities: Fetch posts, handle pagination, parse JSON

Storage Context (com.scraper.storage)

Ubiquitous language: Post (persisted version), Archive, Snapshot

Responsibilities: Save to JSON/DB, retrieve, delete old data

Analytics Context (com.scraper.analytics)

Ubiquitous language: EngagementMetrics, Trend, Report

Responsibilities: Calculate likes/comments averages, detect trends

Step 2: Define Context Map
java
// Communication via Domain Events (shared kernel)
package com.scraper.shared.events;

public class PostScrapedEvent {
    private final String postId;
    private final String username;
    private final LocalDateTime scrapedAt;
    
    // constructor, getters
}
Scraping context emits event → Storage context listens

java
// In scraping-context
domainEventPublisher.publish(new PostScrapedEvent(postId, username, now()));

// In storage-context
@EventListener
public void handle(PostScrapedEvent event) {
    storage.save(event.getPostId(), event.getUsername());
}
Step 3: Generate Each Context with DDD Layers
Create script or manually create for each context:

authentication-context domain:

java
package com.scraper.authentication.domain.model;

public class ApiKey {
    private final String value;
    private final LocalDateTime expiresAt;
    
    public ApiKey(String value, LocalDateTime expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }
    
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiresAt);
    }
}
Ubiquitous Language Documentation per Context
Create CONTEXT.md in each context folder:

markdown
# Authentication Context

## Terms
- **User**: Person using the scraper (email, password hash, session token)
- **ApiKey**: Third-party API credential (value, expiration, rate limit tier)
- **Session**: Active authenticated session (sessionId, createdAt, lastUsedAt)

## Business Rules
- ApiKey must be valid for any operation
- Session expires after 30 minutes of inactivity
Example Prompt
"Use bounded-context-seed.md to create a Java Maven multi-module project for Instagram scraping system with contexts: Authentication, Scraping, Storage, Analytics. Generate all package structures and parent POM."

Validation Checklist
Each context has own Maven module

No circular dependencies between modules

Shared kernel is minimal and stable

Context map documented

Same term may have different meaning across contexts (documented in each CONTEXT.md)

No direct database sharing
```
