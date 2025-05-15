                                         # Problems
### Configuration File
- Passwords
- Incorrect or duplicated Spring configuration
- Commented configuration. We might have another profile

### Documentation not working

Missing dependency:

```
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0'
```

### Code Architecture
- Using entities as parameters in controllers
- Controllers contain excessive logic
- The employee creation payload requires knowledge of the organization structure
- The current in-memory cache does not handle race conditions

### API

Without breaking contract:
- There is no operation to grant a Dundie
- The @Controller annotation can be replaced with @RestController, removing the need for @ResponseBody
- The @RequestMapping annotation could map the /employee route to simplify the other methods

With breaking contract:
- @ExceptionHandler is missing — no exception handling
- Input fields lack validation
- The response body on delete is irrelevant
- The "get all employees" endpoint should be paginated
- Missing authentication and authorization

### Lack of tests

# Minor Issues
- Favicon icon does not exist causing errors in the logs
- Typo in occured_at. Be careful when refactoring, as it's used in the HTML
- The application.properties file can be deleted

# Rules to Analyze
- Should employees be created with any number of Dundies, or should it always start at zero?

# Points of Attention
- Transactions, in case of dual-write situations

# Possible Improvements – Future
- Dundie might become an entity
- Deletion might be soft to preserve employee history
- Update Java to version 21
- Create a Dockerfile
- Use a custom log configuration with Logback


Validate if organization exists
