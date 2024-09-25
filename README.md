# Books Info REST API

## Prerequisites to run
- JDK 21 or higher
- Maven
- Docker + Docker Compose

## Features
- **CRUD Operations**
- **Rating System**: Users can rate books, and the average rating is calculated
- **Filters**: Books can be filtered by various attributes: Title, Author, Published Year etc. Also attributes can be filtered using minimum, maximum and between values.

## How to compile and run

- Build the project using IDE or Maven
- If building with Maven, run the following command:
-      mvn clean package
- Run the following command
-     docker compose down && docker compose build --no-cache && docker compose up -d --force-recreate
- You can access the REST API at http://localhost:8080/swagger-ui/index.html

## API Usage

- **book-controller**:
  - GET /books: Retrieve all books or with a filter applied, for example
  -      "minprice": 1000
  - PUT /books: Update an existing book
  - POST /books: Add a new book to the collection
- **rating-controller**:
  - GET /ratings/{bookId}: Get all ratings that this book has
  - POST /ratings/{bookId}: Rate a book by ID between 1 and 5
  - GET /ratings/average/{bookId}: Get an average rating of the book
 
## Code Coverage

- This project includes unit and integration tests to ensure the functionality of the API. You can run the tests with the following command:
-       mvn test
- Test coverage reports can be generated using:
-      mvn clean test jacoco:report
