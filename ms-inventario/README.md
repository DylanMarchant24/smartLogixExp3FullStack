# ms-inventario – SmartLogix

Microservicio de gestión de inventario para la plataforma SmartLogix.  
Responsable del registro, consulta y control de stock de productos en tiempo real.

## Tecnologías

- Java 17 + Spring Boot 3.2.4
- Spring Data JPA + MySQL 8
- Lombok + Validation
- JaCoCo (cobertura de tests)
- H2 en memoria para pruebas

## Patrones aplicados

| Patrón | Dónde | Propósito |
|---|---|---|
| **Repository Pattern** | `ProductoRepository` | Desacopla lógica de negocio del acceso a datos |
| **DTO** | `ProductoDTO` | Protege la entidad de exposición directa en la API |
| **Layered Architecture** | model / repository / service / controller | Separación de responsabilidades |

## Estructura

```
ms-inventario/
├── pom.xml
└── src/
    ├── main/java/cl/duocuc/smartlogix/inventario/
    │   ├── MsInventarioApplication.java
    │   ├── model/Producto.java
    │   ├── dto/ProductoDTO.java
    │   ├── repository/ProductoRepository.java
    │   ├── service/InventarioService.java
    │   └── controller/InventarioController.java
    └── test/java/cl/duocuc/smartlogix/inventario/
        └── InventarioServiceTest.java
```

## Requisitos previos

- Java 17+
- Maven 3.8+
- MySQL 8 corriendo en `localhost:3306`

## Instalación y ejecución

```bash
# 1. Crear la base de datos (se crea automáticamente con ddl-auto=update)
#    Asegúrate de que MySQL esté activo con usuario root/root

# 2. Compilar y ejecutar
cd ms-inventario
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8081`

## Ejecutar tests

```bash
mvn test
# Reporte de cobertura JaCoCo en: target/site/jacoco/index.html
```

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/inventario` | Lista todos los productos |
| GET | `/api/inventario/{id}` | Obtiene producto por ID |
| GET | `/api/inventario/sku/{sku}` | Obtiene producto por SKU |
| POST | `/api/inventario` | Crea producto nuevo |
| PUT | `/api/inventario/{id}` | Actualiza producto |
| PATCH | `/api/inventario/sku/{sku}/reducir` | Reduce stock (usado por ms-pedidos) |
| DELETE | `/api/inventario/{id}` | Elimina producto |

## Ejemplo de request

```json
POST /api/inventario
{
  "nombre": "Laptop Pro 15",
  "sku": "SKU-001",
  "stock": 50,
  "precio": 999990,
  "descripcion": "Laptop de alto rendimiento"
}
```
