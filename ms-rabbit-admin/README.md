# ms-rabbit-admin

Microservicio Spring Boot para administrar recursos RabbitMQ mediante REST.

## Endpoints

- POST /api/rabbit-admin/queues
- DELETE /api/rabbit-admin/queues/{name}
- POST /api/rabbit-admin/exchanges
- DELETE /api/rabbit-admin/exchanges/{name}
- POST /api/rabbit-admin/bindings
- DELETE /api/rabbit-admin/bindings?exchange=...&queue=...&routingKey=...

Puerto local: 8093.

Credenciales/host RabbitMQ se configuran mediante variables de entorno.
