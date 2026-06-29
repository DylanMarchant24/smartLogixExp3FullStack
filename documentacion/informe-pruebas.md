# Informe de Pruebas Unitarias – SmartLogix EP3

**Asignatura:** DSY1106 – Desarrollo FullStack III  
**Integrante:** Benjamín  
**Módulo:** Pruebas, cobertura y documentación de entrega  

---

## 1. Resumen ejecutivo

Se implementaron **42 pruebas unitarias** distribuidas en cinco componentes del sistema SmartLogix. Todas las pruebas siguen el patrón **AAA (Arrange – Act – Assert)** y utilizan mocks para aislar la unidad bajo prueba de sus dependencias externas (bases de datos, servicios remotos).

| Componente | Tecnología | Pruebas | Resultado esperado |
|---|---|---:|---|
| BFF | JUnit 5 + Mockito | 8 | ✅ PASSED |
| ms-inventario | JUnit 5 + Mockito | 9 | ✅ PASSED |
| ms-pedidos | JUnit 5 + Mockito | 8 | ✅ PASSED |
| ms-envios | JUnit 5 + Mockito | 7 | ✅ PASSED |
| Frontend | React Testing Library | 10 | ✅ PASSED |
| **Total** | | **42** | |

Cobertura objetivo: **≥ 60%** en todos los componentes (requisito mínimo de la evaluación).

---

## 2. Pruebas por componente

### 2.1 BFF – `BffServiceTest.java`

El BFF es el Backend For Frontend que agrega datos de los tres microservicios. Las pruebas cubren:

| # | Nombre del test | Escenario | Resultado |
|---|---|---|---|
| 1 | `obtenerDashboard_combinaTresServicios` | Agrega datos de inventario, pedidos y envíos correctamente | ✅ |
| 2 | `obtenerDashboard_servicioFalla_retornaVacio` | Degradación controlada: si un servicio no responde, retorna lista vacía | ✅ |
| 3 | `crearPedido_delegaCorrectamente` | Proxy POST hacia ms-pedidos | ✅ |
| 4 | `crearProducto_delegaCorrectamente` | Proxy POST hacia ms-inventario | ✅ |
| 5 | `actualizarProducto_delegaCorrectamente` | Proxy PUT hacia ms-inventario | ✅ |
| 6 | `cambiarEstadoPedido_delegaCorrectamente` | Proxy PATCH hacia ms-pedidos | ✅ |
| 7 | `actualizarEstadoEnvio_delegaCorrectamente` | Proxy PATCH hacia ms-envios | ✅ |
| 8 | `eliminarProducto_delegaCorrectamente` | Proxy DELETE hacia ms-inventario | ✅ |

**Patrón aplicado:** Backend For Frontend (BFF) – la capa BFF agrega y adapta datos, evitando que el frontend llame directamente a los microservicios.

**Herramienta de cobertura:** JaCoCo → `bff/target/site/jacoco/index.html`

---

### 2.2 ms-inventario – `InventarioServiceTest.java`

Microservicio de gestión de productos con persistencia en MySQL usando JPA.

| # | Nombre del test | Escenario | Resultado |
|---|---|---|---|
| 1 | `listarTodos_retornaLista` | Devuelve todos los productos del repositorio | ✅ |
| 2 | `obtenerPorSku_existente_retornaDTO` | Retorna DTO cuando el SKU existe | ✅ |
| 3 | `obtenerPorSku_noExistente_lanzaExcepcion` | Lanza RuntimeException si el SKU no existe | ✅ |
| 4 | `crear_skuNuevo_persisteProducto` | Persiste producto y llama a `save()` | ✅ |
| 5 | `crear_skuDuplicado_lanzaExcepcion` | Rechaza SKU duplicado sin llamar a `save()` | ✅ |
| 6 | `reducirStock_stockSuficiente_actualiza` | Reduce stock correctamente con query JPQL | ✅ |
| 7 | `reducirStock_stockInsuficiente_lanzaExcepcion` | Lanza excepción con mensaje de stock insuficiente | ✅ |
| 8 | `eliminar_productoExistente_eliminaCorrectamente` | Invoca `deleteById()` cuando el producto existe | ✅ |
| 9 | `eliminar_productoInexistente_lanzaExcepcion` | Lanza excepción si el producto no existe | ✅ |

**Patrón aplicado:** Repository Pattern – la lógica de negocio está separada del acceso a datos. El `ProductoRepository` es mockeado en las pruebas.

**Herramienta de cobertura:** JaCoCo → `ms-inventario/target/site/jacoco/index.html`

---

### 2.3 ms-pedidos – `PedidoServiceTest.java`

Microservicio de órdenes de compra. Valida SKU contra ms-inventario, aplica Factory Method para construir pedidos.

| # | Nombre del test | Escenario | Resultado |
|---|---|---|---|
| 1 | `obtenerPorId_existente_retornaDTO` | Retorna DTO del pedido existente | ✅ |
| 2 | `obtenerPorId_noExistente_lanzaExcepcion` | Lanza excepción cuando no existe | ✅ |
| 3 | `listarTodos_retornaListaCompleta` | Lista todos los pedidos del repositorio | ✅ |
| 4 | `cambiarEstado_actualizaCorrectamente` | Actualiza estado y fecha de modificación | ✅ |
| 5 | `factoryMethod_crearNuevo_estadoCREADO` | Factory crea pedido con estado CREADO | ✅ |
| 6 | `factoryMethod_crearValidado_stockRegistrado` | Factory crea pedido VALIDADO con stock | ✅ |
| 7 | `factoryMethod_crearAprobado_estadoAPROBADO` | Factory crea pedido APROBADO con stock | ✅ |
| 8 | `listarPorEstado_retornaFiltrados` | Filtra pedidos por estado correctamente | ✅ |

**Patrón aplicado:** Factory Method (`PedidoFactory`) – centraliza la creación de pedidos según su estado inicial, garantizando objetos bien formados.

**Herramienta de cobertura:** JaCoCo → `ms-pedidos/target/site/jacoco/index.html`

---

### 2.4 ms-envios – `EnvioServiceTest.java`

Microservicio de despacho. Gestiona el ciclo de vida de los envíos, genera códigos de seguimiento y registra la fecha de entrega al completarse.

| # | Nombre del test | Escenario | Resultado |
|---|---|---|---|
| 1 | `listar_retornaLista` | Devuelve todos los envíos | ✅ |
| 2 | `obtenerPorId_existente_retornaDTO` | Retorna DTO con datos completos del envío | ✅ |
| 3 | `obtenerPorId_noExistente_lanzaExcepcion` | Lanza excepción cuando el envío no existe | ✅ |
| 4 | `crearEnvio_persisteCorrectamente` | Crea envío con estado PENDIENTE y código seguimiento | ✅ |
| 5 | `actualizarEstado_enCamino_actualizaEstado` | Cambia estado a EN_CAMINO correctamente | ✅ |
| 6 | `actualizarEstado_entregado_registraFecha` | Al marcar ENTREGADO, registra `fechaEntrega` | ✅ |
| 7 | `listarPorPedido_retornaFiltrados` | Filtra envíos por pedidoId | ✅ |

**Herramienta de cobertura:** JaCoCo → `ms-envios/target/site/jacoco/index.html`

---

### 2.5 Frontend – React Testing Library

Pruebas del componente `StatCard` y el hook personalizado `useDashboard`.

| # | Archivo | Test | Escenario | Resultado |
|---|---|---|---|---|
| 1 | `StatCard.test.js` | `renderiza el título correctamente` | Verifica que el título se muestra en el DOM | ✅ |
| 2 | `StatCard.test.js` | `renderiza el valor correctamente` | Verifica que el valor numérico aparece | ✅ |
| 3 | `StatCard.test.js` | `muestra "–" cuando value es undefined` | Fallback cuando no hay datos | ✅ |
| 4 | `StatCard.test.js` | `renderiza el subtexto cuando se provee la prop "sub"` | Prop opcional de texto secundario | ✅ |
| 5 | `StatCard.test.js` | `NO renderiza subtexto cuando la prop "sub" no se provee` | Limpieza de DOM innecesario | ✅ |
| 6 | `StatCard.test.js` | `aplica el role region y aria-label para accesibilidad` | Buena práctica de accesibilidad (a11y) | ✅ |
| 7 | `StatCard.test.js` | `renderiza el ícono correctamente` | Ícono emoji en el DOM | ✅ |
| 8 | `useDashboard.test.js` | `retorna loading=true al inicio` | Estado de carga inicial | ✅ |
| 9 | `useDashboard.test.js` | `carga los datos correctamente` | Datos del BFF disponibles tras la carga | ✅ |
| 10 | `useDashboard.test.js` | `captura el error si la API falla` | Manejo de errores en el hook | ✅ |

**Tecnologías usadas:** `@testing-library/react`, `@testing-library/jest-dom`, `@testing-library/user-event`, `jest.mock()` para mockear el módulo `api.js`.

**Herramienta de cobertura:** React Scripts (Jest) → `frontend/coverage/lcov-report/index.html`

---

## 3. Métricas de cobertura esperadas

> Ejecutar los comandos de la guía de ejecución para obtener los valores reales.

| Componente | Líneas | Ramas | Métodos | Clases |
|---|---:|---:|---:|---:|
| BFF | ≥ 75% | ≥ 65% | ≥ 90% | 100% |
| ms-inventario | ≥ 80% | ≥ 70% | ≥ 95% | 100% |
| ms-pedidos | ≥ 75% | ≥ 60% | ≥ 90% | 100% |
| ms-envios | ≥ 70% | ≥ 60% | ≥ 85% | 100% |
| Frontend (Stmts) | ≥ 65% | ≥ 60% | ≥ 70% | – |

---

## 4. Patrones de diseño aplicados y su impacto en calidad

| Patrón | Dónde se aplica | Beneficio en mantenibilidad |
|---|---|---|
| **Backend For Frontend (BFF)** | `BffService.java` | Un único punto de integración; el frontend no necesita conocer las URLs de los microservicios |
| **Repository Pattern** | Todos los microservicios | La lógica de negocio puede testearse sin base de datos real (mock del repository) |
| **Factory Method** | `PedidoFactory.java` | Creación de pedidos centralizada; nuevos estados se agregan sin modificar la lógica del servicio |
| **Service Layer (Frontend)** | `api.js` | Toda la comunicación HTTP está centralizada; cambiar la URL del BFF requiere modificar un solo archivo |
| **Custom Hook** | `useDashboard.js` | Lógica de estado reutilizable y testeable de forma aislada del componente React |
| **DTO Pattern** | Todos los servicios | Los contratos de API están desacoplados del modelo de datos interno; protege la capa de persistencia |

---

## 5. Buenas prácticas implementadas

- **Nomenclatura descriptiva** en los tests usando `@DisplayName` y el formato `método_escenario_resultado`.
- **Aislamiento total**: ningún test accede a una base de datos real ni a servicios externos.
- **Verificación negativa**: cada funcionalidad importante tiene un test de camino feliz y uno de error.
- **Accesibilidad en frontend**: los componentes React incluyen `role` y `aria-label` validados por pruebas.
- **Degradación controlada en BFF**: si un microservicio falla, el dashboard retorna lista vacía en vez de propagar el error.
- **Configuración separada de testing**: los microservicios usan `application-test.properties` con H2 en memoria para evitar dependencia de MySQL en CI.

