# Spring AI Chat Application with RAG

This is a demo application contains the Chat application's UI (thymeleaf) and backend (Spring AI, Spring Web and Spring Security).

You can use either the default ```SimpleVectorStore``` that stores the embeddings in memory. 

Or, you can use Postgres vector store by passing a runtime argument:

```bash
-DvectorStoreType=pgvector
```
Or define this property as environment variable.

## Using Postgres Vector Store Database

Make sure the Postgres Vector Store database is up and running. To run the database as docker container, run the following commands.

Pull the image:

```
docker pull pgvector/pgvector:pg16
```

Run the server:

```
docker run -it --rm --name postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres pgvector/pgvector:pg16
```

## Demo

Once the application is started, you can access the application in URL http://localhost:8081/.

By default a login page is shown. Enter the following username/password.

```
Username: howtodoinjava
Password: password
```

After successful login, you will be redirected to chat application page. Feel free to upload any file, and ask AI any question related to the document content.
