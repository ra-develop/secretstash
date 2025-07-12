Secret Stash - Secure Note-Taking API Solution
==============================================

I'll provide a comprehensive solution for the Secret Stash API using Kotlin with Spring Boot. This solution covers all the requirements including JWT authentication, CRUD operations for notes, security features, and testing.

Solution Overview
-----------------

### Implementation Details

#### Features
- User registration and login with JWT
- CRUD operations for notes
- Optional note expiration
- Rate limiting
- Secure password storage

#### Requirements
- Java 17+
- PostgreSQL

#### Setup
1. Create a PostgreSQL database. You can find more details on setting up PostgresSQL [here](POSTGRESQL.md).
2. Update `application.properties` with your database credentials
3. Run the application: `./gradlew bootRun`

#### API Documentation
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/notes` - Get paginated notes
- `GET /api/notes/latest` - Get latest 1000 notes
- `POST /api/notes` - Create a new note
- `PUT /api/notes/{id}` - Update a note
- `DELETE /api/notes/{id}` - Delete a note

#### Architectural Decisions

1.  **Security**:

    *   JWT for stateless authentication

    *   BCrypt for password hashing

    *   Rate limiting to prevent brute force attacks

    *   CSRF protection disabled as we're using JWT

2.  **Performance**:

    *   Pagination for note retrieval

    *   Optimized queries with JPA

    *   Caffeine cache for rate limiting

3.  **Data Handling**:

    *   Automatic filtering of expired notes

    *   Timestamps managed in UTC

    *   Proper DTOs for request/response separation

4.  **Error Handling**:

    *   Global exception handler for consistent error responses

    *   Proper HTTP status codes

    *   Validation for all inputs


#### How to Use This Code

1.  Create a new Kotlin/Spring Boot project in your IDE

2.  Replace the generated files with the code provided above

3.  Make sure you have PostgreSQL running and update the `application.properties`

4.  Run the application with `./gradlew bootRun`


The API will be available at `http://localhost:8080` with these endpoints:

*   `POST /api/auth/register`- Register new user

*   `POST /api/auth/login` - Login and get JWT token

*   `GET /api/notes` - Get paginated notes

*   `GET /api/notes/latest` - Get latest 1000 notes

*   `POST /api/notes` - Create new note

*   `PUT /api/notes/{id}` - Update note

*   `DELETE /api/notes/{id}` - Delete note

All endpoints except auth require JWT token in the `Authorization: Bearer <token>` header.

You can find more detailed information on the API test case [here](API-TEST-CASES.md).


Conclusion
----------

This solution provides a complete implementation of the Secret Stash API with all required features:

*   Secure JWT-based authentication

*   Full CRUD operations for notes with optional expiry

*   Rate limiting for security

*   Proper error handling and validation

*   Comprehensive testing

*   Clear documentation


The architecture is clean, maintainable, and follows RESTful principles while prioritizing security and performance.


### Project Folder Structure

```
└── 📁secretstash
    └── 📁gradle
        └── 📁wrapper
            ├── gradle-wrapper.jar
            ├── gradle-wrapper.properties
    └── 📁project.resources
        ├── Secret Stash.postman_collection.json
    └── 📁src
        └── 📁main
            └── 📁kotlin
                └── 📁com
                    └── 📁example
                        └── 📁secretstash
                            └── 📁config
                                ├── SecurityConfig.kt
                                ├── WebConfig.kt
                            └── 📁controller
                                ├── AuthController.kt
                                ├── NoteController.kt
                            └── 📁dto
                                ├── AuthRequest.kt
                                ├── AuthResponse.kt
                                ├── NoteDto.kt
                            └── 📁exception
                                ├── CustomExceptions.kt
                                ├── GlobalExceptionHandler.kt
                            └── 📁model
                                ├── Note.kt
                                ├── User.kt
                            └── 📁repository
                                ├── NoteRepository.kt
                                ├── UserRepository.kt
                            └── 📁security
                                ├── JwtTokenFilter.kt
                                ├── JwtTokenProvider.kt
                                ├── UserPrincipal.kt
                            └── 📁service
                                ├── AuthService.kt
                                ├── CustomUserDetailsService.kt
                                ├── NoteService.kt
                                ├── RateLimitingService.kt
                                ├── UserService.kt
                            ├── SecretStashApplication.kt
            └── 📁resources
                └── 📁static
                └── 📁templates
                ├── application.properties
        └── 📁test
            └── 📁kotlin
                └── 📁com
                    └── 📁example
                        └── 📁secretstash
                            └── 📁controller
                                ├── AuthControllerTest.kt
                                ├── NoteControllerTest.kt
                            └── 📁service
                                ├── AuthServiceTest.kt
                                ├── NoteServiceTest.kt
                            ├── SecretStashApplicationTests.kt
    ├── .gitattributes
    ├── .gitignore
    ├── API-TEST-CASES.md
    ├── build.gradle.kts
    ├── gradlew
    ├── gradlew.bat
    ├── POSTGRESQL.md
    ├── README.md
    └── settings.gradle.kts
```

