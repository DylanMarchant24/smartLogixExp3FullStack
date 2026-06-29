# SmartLogix - Plataforma Inteligente de Gestión Logística

**Asignatura:** Desarrollo Fullstack III - DUOC UC  
**Evaluación:** Parcial 3 - Integración de arquitectura de microservicios  
**Integrantes:** Ricardo Novoa - Cristobal Pérez - Benjamín Meneses  
**Profesor:** Israel Alejandro Villagra Riquelme

---

## 1. Descripción del proyecto

SmartLogix es una plataforma de gestión logística para PYMEs de eCommerce. La solución separa las responsabilidades de inventario, pedidos y envíos en microservicios independientes, expone un **Backend For Frontend (BFF)** para adaptar las respuestas al frontend React, y utiliza un **API Gateway** como punto de entrada principal para enrutar las solicitudes hacia los servicios correspondientes.

El objetivo técnico es resolver problemas de sincronización de stock, procesamiento de pedidos, seguimiento de envíos, persistencia de datos y mantenibilidad del sistema mediante una arquitectura de microservicios, patrones de diseño, API REST, Service Discovery, monitoreo y una estrategia de branching basada en Git Flow.

---

## 2. Arquitectura del sistema

```text
┌────────────────────────────────────────────────────────┐
│                  Frontend React (3000)                 │
└────────────────────┬───────────────────────────────────┘
                     │ HTTP/REST
┌────────────────────▼───────────────────────────────────┐
│              API Gateway Spring Cloud (8085)           │
│        Enrutamiento, punto de entrada y filtros        │
└────────────────────┬───────────────────────────────────┘
                     │ lb://bff mediante Eureka
┌────────────────────▼───────────────────────────────────┐
│                  BFF Spring Boot (8080)                │
│     Agrega datos y adapta respuestas para React        │
└──────┬──────────────────┬─────────────────┬────────────┘
       │                  │                 │
       │ REST             │ REST            │ REST
       ▼                  ▼                 ▼
┌──────────────┐   ┌──────────────┐  ┌──────────────┐
│ms-inventario │   │ ms-pedidos   │  │  ms-envios   │
│    8081      │   │    8082      │  │    8083      │
└──────┬───────┘   └──────┬───────┘  └──────┬───────┘
       │                  │                 │
       ▼                  ▼                 ▼
┌──────────────┐   ┌──────────────┐  ┌──────────────┐
│ db_inventario│   │ db_pedidos   │  │  db_envios   │
│    MySQL     │   │    MySQL     │  │    MySQL     │
└──────────────┘   └──────────────┘  └──────────────┘

┌────────────────────────────────────────────────────────┐
│          Discovery Server Eureka (8761)                │
│ Registra API Gateway, BFF y microservicios disponibles │
└────────────────────────────────────────────────────────┘
```

---

## 3. Patrones y componentes arquitectónicos implementados

| Patrón / Componente | Componente | Problema que resuelve |
|---|---|---|
| **Backend For Frontend (BFF)** | `bff` | Evita que React consuma directamente 3 microservicios y entrega respuestas optimizadas para la interfaz. |
| **API Gateway** | `api-gateway` | Centraliza el punto de entrada, enruta solicitudes hacia el BFF y microservicios, y oculta la topología interna. |
| **Service Discovery** | `discovery-server` | Permite registrar y localizar dinámicamente el BFF, API Gateway y microservicios mediante Eureka. |
| **Monitoreo con Actuator** | `api-gateway`, `bff`, microservicios | Expone endpoints de salud y métricas para verificar el estado de los componentes. |
| **Repository Pattern** | `ms-inventario`, `ms-pedidos`, `ms-envios` | Separa lógica de negocio y acceso a datos con Spring Data JPA. |
| **DTO** | Todos los servicios backend | Evita exponer entidades JPA directamente y define contratos claros. |
| **Factory Method** | `ms-pedidos/factory/PedidoFactory.java` | Crea pedidos según estado del ciclo de vida: CREADO, VALIDADO, APROBADO. |
| **Circuit Breaker manual** | `ms-pedidos/service/PedidoService.java` | Evita cascada de fallos cuando `ms-inventario` no responde. |
| **Service Layer Frontend** | `frontend/src/services/api.js` | Centraliza llamadas HTTP al BFF. |
| **Custom Hook** | `frontend/src/hooks/useDashboard.js` | Encapsula carga de datos, errores y refresco del dashboard. |

---

## 4. Stack tecnológico

- **Backend:** Java 17, Spring Boot 3.2.4, Maven
- **Persistencia:** MySQL 8, Spring Data JPA, H2 para pruebas
- **Frontend:** React 18, React Router v6, Axios, NPM
- **Service Discovery:** Netflix Eureka
- **API Gateway:** Spring Cloud Gateway
- **Monitoreo:** Spring Boot Actuator
- **Calidad:** JUnit 5, Mockito, Testing Library, JaCoCo, coverage de React
- **Versionamiento:** Git + Git Flow

---

## 5. Estructura del entregable

```text
Smartlogix-Fullstack-3-develop/
├── frontend/                       # Aplicación React NPM
├── api-gateway/                    # API Gateway Spring Cloud
├── discovery-server/               # Servidor Eureka para Service Discovery
├── bff/                            # Backend For Frontend Spring Boot
├── ms-inventario/                  # Microservicio de inventario
├── ms-pedidos/                     # Microservicio de pedidos
├── ms-envios/                      # Microservicio de envíos
├── arquetipos-maven/               # Arquetipos Maven para backend
├── documentacion/                  # PDFs y evidencias para la pauta
│   └── ep3/                        # Documentación específica Parcial 3
├── postman/                        # Colección Postman de API REST
├── scripts/                        # Scripts para pruebas y reportes
├── plan-branching.md               # Plan Git Flow editable
├── repositorios.txt                # Enlaces a repositorios GitHub
├── .gitignore
└── README.md
```

---

## 6. Inicio rápido

### Prerrequisitos

- Java 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8 corriendo en `localhost:3306` con usuario `root` y password `root`

### 1. Levantar Service Discovery, microservicios, BFF y API Gateway

Abrir una terminal por componente:

```bash
cd discovery-server && mvn spring-boot:run   # Puerto 8761
cd ms-inventario    && mvn spring-boot:run   # Puerto 8081
cd ms-pedidos       && mvn spring-boot:run   # Puerto 8082
cd ms-envios        && mvn spring-boot:run   # Puerto 8083
cd bff              && mvn spring-boot:run   # Puerto 8080
cd api-gateway      && mvn spring-boot:run   # Puerto 8085
```

### 2. URLs de verificación

```text
Eureka:
http://localhost:8761

API Gateway:
http://localhost:8085/api/bff/dashboard

BFF directo:
http://localhost:8080/api/bff/dashboard

Monitoreo:
http://localhost:8085/actuator/health
http://localhost:8080/actuator/health
http://localhost:8081/actuator/health
http://localhost:8082/actuator/health
http://localhost:8083/actuator/health
```

### 3. Levantar frontend

```bash
cd frontend
npm install
npm start
```

Frontend disponible en:

```text
http://localhost:3000
```

---

## 7. Pruebas y cobertura

### Backend

Ejecutar en cada componente backend:

```bash
mvn clean test jacoco:report
```

Reportes esperados:

```text
bff/target/site/jacoco/index.html
ms-inventario/target/site/jacoco/index.html
ms-pedidos/target/site/jacoco/index.html
ms-envios/target/site/jacoco/index.html
```

### Frontend

```bash
cd frontend
npm run test:coverage
```

Reporte esperado:

```text
frontend/coverage/lcov-report/index.html
```

También se incluye el script:

```bash
bash scripts/generar-reportes-pruebas.sh
```

---

## 8. Endpoints principales

### API Gateway

| Método | Endpoint | Servicio destino | Descripción |
|---|---|---|---|
| GET | `/api/bff/dashboard` | `bff` | Dashboard agregado |
| GET | `/api/bff/inventario` | `bff` | Lista productos desde BFF |
| GET | `/api/bff/pedidos` | `bff` | Lista pedidos desde BFF |
| GET | `/api/bff/envios` | `bff` | Lista envíos desde BFF |
| GET | `/api/inventario` | `ms-inventario` | Lista productos directamente desde microservicio |
| GET | `/api/pedidos` | `ms-pedidos` | Lista pedidos directamente desde microservicio |
| GET | `/api/envios` | `ms-envios` | Lista envíos directamente desde microservicio |

### BFF

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/bff/dashboard` | Dashboard agregado |
| GET | `/api/bff/inventario` | Lista productos |
| POST | `/api/bff/inventario` | Crea producto |
| PUT | `/api/bff/inventario/{id}` | Actualiza producto |
| DELETE | `/api/bff/inventario/{id}` | Elimina producto |
| GET | `/api/bff/pedidos` | Lista pedidos |
| POST | `/api/bff/pedidos` | Crea pedido |
| PATCH | `/api/bff/pedidos/{id}/estado` | Cambia estado del pedido |
| GET | `/api/bff/envios` | Lista envíos |
| POST | `/api/bff/envios` | Crea envío |
| PATCH | `/api/bff/envios/{id}/estado` | Cambia estado del envío |

---

## 9. Documentación incluida

### Documentación Parcial 2

- `documentacion/analisis-patrones-arquetipos.pdf`
- `documentacion/plan-branching.pdf`
- `documentacion/evidencia-branching.pdf`
- `documentacion/resultados-pruebas-cobertura.pdf`
- `documentacion/checklist-entrega.md`
- `arquetipos-maven/README.md`

### Documentación Parcial 3

- `documentacion/ep3/service-discovery.md`
- `documentacion/ep3/api-gateway.md`
- `documentacion/ep3/monitoreo.md`
- `documentacion/ep3/arquitectura-microservicios.md`
- `documentacion/ep3/persistencia-datos.pdf`
- `documentacion/ep3/informe-pruebas.pdf`
- `postman/SmartLogix-EP3.postman_collection.json`
- `repositorios.txt`

---

## 10. Evidencias esperadas para la defensa

Para la defensa de la Parcial 3 se consideran las siguientes evidencias:

- Eureka mostrando servicios registrados.
- API Gateway respondiendo en `http://localhost:8085`.
- BFF respondiendo en `http://localhost:8080`.
- Microservicios respondiendo en `8081`, `8082` y `8083`.
- Frontend funcionando en `http://localhost:3000`.
- Persistencia de datos en MySQL.
- Pruebas unitarias, integración y end to end.
- Reportes de cobertura superiores al 60%.
- Colección Postman o Swagger con ejemplos de API REST.
- Commits y ramas en GitHub para evidenciar el trabajo colaborativo.
