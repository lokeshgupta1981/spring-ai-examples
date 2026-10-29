Source code for the article Connect Spring AI to Existing REST APIs and Services (review draft).

OrderApiTools adapts an existing customer-scoped order API. OrderApiAssistant shows its construction and per-request use with an authenticated Principal. The example assumes the host application supplies authentication and the backend enforces ownership. The sample URL is not a hosted order service.

Run `mvn -Dtest=OrderApiToolsTest test` from the orderdesk directory. Three tests check the request URL, response mapping, generated tool schema and missing context. They do not verify live model tool selection or backend authorization. Configure the actual order API URL and service credentials before using the adapter in an application.
