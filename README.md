# SmartLogix - Plataforma Inteligente de Gestión Logística

**Asignatura:** Desarrollo Fullstack III - DUOC UC  
**Evaluación:** Parcial 2 - Implementación de componentes Frontend y Backend  
**Integrantes:** Ricardo Novoa - Cristobal Pérez - Benjamín Meneses  
**Profesor:** Israel Alejandro Villagra Riquelme

---

## 1. Descripción del proyecto

SmartLogix es una plataforma de gestión logística para PYMEs de eCommerce. La solución separa las responsabilidades de inventario, pedidos y envíos en microservicios independientes, y expone un **Backend For Frontend (BFF)** como punto único de integración para el frontend React.

El objetivo técnico es resolver problemas de sincronización de stock, procesamiento de pedidos, seguimiento de envíos y mantenibilidad del sistema mediante patrones de diseño, arquetipos Maven y una estrategia de branching basada en Git Flow.

---

## 2. Arquitectura del sistema

```text
┌────────────────────────────────────────────────────────┐
│                  Frontend React (3000)                 │
└────────────────────┬───────────────────────────────────┘
                     │ HTTP/REST
┌────────────────────▼───────────────────────────────────┐
│              BFF / API Gateway (8080)                  │
│         Backend For Frontend - Spring Boot             │
└──────┬──────────────────┬─────────────────┬────────────┘
       │                  │                 │
┌──────▼──────┐   ┌───────▼──────┐  ┌──────▼──────────┐
│ms-inventario│   │  ms-pedidos  │  │   ms-envios     │
│   (8081)    │   │    (8082)    │  │    (8083)       │
└──────┬──────┘   └──────┬───────┘  └──────┬──────────┘
       │                 │                 │
  ┌────▼────┐      ┌─────▼────┐     ┌──────▼────┐
  │db_invent│      │db_pedidos│     │ db_envios │
  │ (MySQL) │      │ (MySQL)  │     │  (MySQL)  │
  └─────────┘      └──────────┘     └───────────┘
```

---

## 3. Patrones implementados

| Patrón | Componente | Problema que resuelve |
|---|---|---|
| **Backend For Frontend (BFF)** | `bff` | Evita que React consuma directamente 3 microservicios y entrega respuestas optimizadas para la interfaz. |
| **API Gateway** | `bff` | Centraliza el punto de entrada del frontend y oculta la topología interna. |
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
- **Calidad:** JUnit 5, Mockito, Testing Library, JaCoCo, coverage de React
- **Versionamiento:** Git + Git Flow

---

## 5. Estructura del entregable

```text
Smartlogix-Fullstack-3-develop/
├── frontend/                       # Aplicación React NPM
├── bff/                            # Backend For Frontend Spring Boot
├── ms-inventario/                  # Microservicio de inventario
├── ms-pedidos/                     # Microservicio de pedidos
├── ms-envios/                      # Microservicio de envíos
├── arquetipos-maven/               # Arquetipos Maven para backend
├── documentacion/                  # PDFs y evidencias para la pauta
├── scripts/                        # Script para pruebas y reportes
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

### 1. Levantar microservicios y BFF

Abrir una terminal por componente:

```bash
cd ms-inventario && mvn spring-boot:run   # Puerto 8081
cd ms-pedidos    && mvn spring-boot:run   # Puerto 8082
cd ms-envios     && mvn spring-boot:run   # Puerto 8083
cd bff           && mvn spring-boot:run   # Puerto 8080
```

### 2. Levantar frontend

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

## 8. Endpoints principales del BFF

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

- `documentacion/analisis-patrones-arquetipos.pdf`
- `documentacion/plan-branching.pdf`
- `documentacion/evidencia-branching.pdf`
- `documentacion/resultados-pruebas-cobertura.pdf`
- `documentacion/checklist-entrega.md`
- `arquetipos-maven/README.md`
- `repositorios.txt`