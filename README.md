# Dundie Awards

The project has been adjusted according to the key points discussed in our last meeting.

## Disclaimer
All changes were made with high-scale scenarios in mind. The trophies delivered by Michael Scott have become famous, and companies with millions of employees are now handing them out :).

## Transactions
The SAGA pattern was adopted to manage the flow of Dundie deliveries. Although we only have one service for the purpose of this test, this pattern is important to maintain tracking of the deliveries and to ensure atomicity in a flow that may involve a large set of data and steps.

The SAGA is controlled by the Dundie Delivery status tables. These statuses allow us to see which processing steps have been executed and identify any issues.

One point that was heavily discussed in our conversation was what would happen with transactions that were not committed after messages were published to the queues. This issue was solved using TransactionalEventListener, which only sends messages to the queue after the transaction is committed. This way, we only publish a message when we are sure the data has been persisted in the database.

## Compensation
This transaction strategy leaves us with a potential gap where, if the message broker is unavailable, the processing would be left in limbo. That’s where the DundieDeliverySentinel comes in. Using a scheduler, this component runs a cron job every minute to check for intermediate statuses and send the items to the appropriate queues.

## Resilience
The application is designed to run without the message broker or the cache. Any unavailability of these components does not compromise the application’s core functionality. In the case of the broker, Dundie delivery is delayed, but users can still make new requests. Once the broker is back online, everything is reprocessed automatically.

## Software Architecture
A clear separation between Application and Infrastructure layers was implemented, based on Hexagonal Architecture. We don’t yet have a Domain layer, and therefore no dependency inversion. This layer can be added as the complexity of the Dundie delivery rules increases.

## Running the Application
The application is configured in its default profile to run with local infrastructure (Database, Message Broker, and Cache). So before starting, run:

```
docker-compose up -d
```

After that, for the first run only, the database scripts need to be executed. To do this, run the application with the ```db-migration``` profile, or simply enable Flyway temporarily in the default profile:

```
spring:
  application:
    name: dundie-awards
  flyway:
    enabled: true
```

After that, run the application normally. In the ```postman``` folder, you’ll find a collection for the Dundie Awards API.