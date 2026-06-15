# ms-envios – SmartLogix

Microservicio de coordinación de envíos para SmartLogix.  
Gestiona el despacho físico de pedidos aprobados, genera códigos de seguimiento y actualiza el estado del envío.

## Tecnologías

- Java 17 + Spring Boot 3.2.4
- Spring Data JPA + MySQL 8
- Lombok + Bean Validation
- JaCoCo (cobertura de pruebas)

## Patrones aplicados

| Patrón | Clase | Descripción |
|---|---|---|
| **Repository Pattern** | `EnvioRepository` | Abstrae acceso a datos |
| **DTO** | `EnvioDTO` | Expone solo los datos necesarios en la API |
| **Layered Architecture** | model / repository / service / controller | Separación de responsabilidades |

## Estructura

```
ms-envios/
├── pom.xml
└── src/
    ├── main/java/cl/duocuc/smartlogix/envios/
    │   ├── MsEnviosApplication.java
    │   ├── model/   (Envio, EstadoEnvio)
    │   ├── dto/     (EnvioDTO)
    │   ├── repository/ (EnvioRepository)
    │   ├── service/ (EnvioService)
    │   └── controller/ (EnvioController)
    └── test/
        └── EnvioServiceTest.java
```

## Requisitos previos

- Java 17+ | Maven 3.8+ | MySQL 8 en `localhost:3306`

## Ejecución

```bash
cd ms-envios
mvn spring-boot:run
# Puerto: 8083
```

## Tests

```bash
mvn test
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/envios` | Lista todos los envíos |
| GET | `/api/envios/{id}` | Obtiene envío por ID |
| GET | `/api/envios/pedido/{pedidoId}` | Envíos de un pedido |
| GET | `/api/envios/seguimiento/{codigo}` | Busca por código de seguimiento |
| POST | `/api/envios` | Crea nuevo envío |
| PATCH | `/api/envios/{id}/estado` | Actualiza estado del envío |

## Body de ejemplo

```json
POST /api/envios
{
  "pedidoId": 1,
  "transportista": "Chilexpress",
  "direccionDestino": "Av. Providencia 1234, Santiago"
}
```
