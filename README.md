# TSET Assignment - Arjol

## Overview

This service is the implementation of the requested Release Manager for the assignment. It tracks when different
microservices are deployed, and it generates a "System Version" to keep a clear history of exactly what was running 
in the environment at any given time.

## How it works
I decided to use a snapshot-based approach. Every time a service version changes, the app increments the global version
and "clones" the current state of all other services into that new version.

I chose this for three main reasons:

1. Traceability: It's very easy to look back at any specific System Version and see the full environment state immediately
2. Performance: Retrieving services for a specific System Version is a single query on the deployed_service table. This keeps the *GET* request simple and efficient for typical deployment histories.
3. Simplicity: The approach avoids complex merge or diff logic when deploying services. Each System Version is a complete snapshot, making the implementation straightforward and easy to maintain.

## Concurrency
Since the requirements mentioned this is a distributed environment, I added Pessimistic Locking on the database level
when the app reads the latest version. This prevents a race condition where two services deploying at the exact same
time might try to generate the same System Version number.

## Tech Stack
Followed the general requirements from the email (assignment description)
* Kotlin
* JPA / Hibernate
* H2 (In-Memory DB, so no persistence between sessions)

## How to run
Java 17 or higher needed. Navigate to project directory and run :

    ./gradlew bootRun

## Running Tests
I wrote an integration test that follows the example provided in the email with the assignment instructions.
You could run the test with the following command :

    ./gradlew test

## Database Inspection
If for any reason you would like to look at the tables while the app is running, I enabled the H2 console.

* Open browser and navigate to : *http://localhost:8080/h2-console*
* Set JDBC URL to : *jdbc:h2:mem:releasedb* (need to change this from default)
* Set user to : sa (default)
* Leave password empty (as default)