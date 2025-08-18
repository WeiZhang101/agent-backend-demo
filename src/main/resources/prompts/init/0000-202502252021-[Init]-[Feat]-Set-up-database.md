## Objective

- Set up flyway configuration
- Set up database configuration
- Set up JPA configuration

## Tasks

- add profiles configuration in application.yaml file

```yml
spring:
  profiles:
    active: ${profile:local}
```

- add flyway configuration in application.yaml file

```yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    table: flyway_schema_history
    out-of-order: false
    baseline-on-migrate: true
    repair-on-migrate: true
    validate-on-migrate: true
```

- add datasource configuration in application.yaml file

```yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000

      validation-timeout: 5000
      keepalive-time: 30000

      leak-detection-threshold: 30000
      pool-name: "AgentHikariPool"

      data-source-properties:
        cachePrepStmts: true
        rewriteBatchedStatements: true
        useServerPrepStmts: true
```

- add postgres configuration in application-local.yaml

```yml
spring:
  datasource:
    postgres:
      url: jdbc:postgresql://localhost:5432/agent-backend
      username: postgres
      password: postgres
      driver-class-name: org.postgresql.Driver
```

- add JPA configuration in application.yaml

```yml
spring:
  jpa:
    show-sql: true
    
    properties:
      hibernate:
        jdbc:
          batch_size: 25
        order_inserts: true
        order_updates: true
        batch_versioned_data: true

        generate_statistics: true
        format_sql: true
        use_sql_comments: true

        enable_lazy_load_no_trans: false
```

- add JPA configuration for application-local.yaml

```yml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```