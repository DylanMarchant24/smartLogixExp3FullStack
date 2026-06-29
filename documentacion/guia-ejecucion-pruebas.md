# Guía de Ejecución de Pruebas – SmartLogix EP3

**Autor:** Benjamín  

---

## Requisitos previos

| Herramienta | Versión mínima | Verificar con |
|---|---|---|
| Java JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Node.js | 18+ | `node -version` |
| npm | 9+ | `npm -version` |

---

## 1. Pruebas de backend (JUnit 5 + JaCoCo)

Cada microservicio tiene su propio conjunto de pruebas. Ejecutar desde la raíz del proyecto:

### BFF
```bash
cd bff
mvn clean test jacoco:report
# Reporte HTML: bff/target/site/jacoco/index.html
```

### ms-inventario
```bash
cd ms-inventario
mvn clean test jacoco:report
# Reporte HTML: ms-inventario/target/site/jacoco/index.html
```

### ms-pedidos
```bash
cd ms-pedidos
mvn clean test jacoco:report
# Reporte HTML: ms-pedidos/target/site/jacoco/index.html
```

### ms-envios
```bash
cd ms-envios
mvn clean test jacoco:report
# Reporte HTML: ms-envios/target/site/jacoco/index.html
```

> **Nota:** Las pruebas de backend usan H2 en memoria (configurado en `application-test.properties`), por lo que **no requieren MySQL activo**.

---

## 2. Pruebas de frontend (React Testing Library + Jest)

```bash
cd frontend
npm install
npm run test:coverage
# Reporte HTML: frontend/coverage/lcov-report/index.html
```

Para ejecutar en modo watch (desarrollo):
```bash
npm test
```

---

## 3. Script unificado

Para ejecutar todo de una vez desde la raíz del proyecto:

```bash
bash scripts/run-backend-tests.bat
bash scripts/run-frontend-tests.bat
```

O en Windows (doble clic o desde cmd):
```
scripts\run-backend-tests.bat
scripts\run-frontend-tests.bat
```

---

## 4. Interpretar los reportes JaCoCo

Al abrir `target/site/jacoco/index.html` en el navegador verás:

- **Instructions (%)**: líneas de bytecode cubiertas → indicador principal.
- **Branches (%)**: ramas de if/switch cubiertas.
- **Methods (%)**: métodos ejecutados al menos una vez.
- **Classes (%)**: clases con al menos una prueba.

El objetivo es **≥ 60% en Instructions** en todos los módulos.

---

## 5. Interpretar el reporte de frontend

Al abrir `frontend/coverage/lcov-report/index.html`:

- **Statements**: sentencias ejecutadas.
- **Branches**: ramas de código (ternarios, &&, ||).
- **Functions**: funciones llamadas.
- **Lines**: líneas ejecutadas.

Los archivos excluidos de cobertura (configurados en `package.json`) son `index.js` y `reportWebVitals.js`.

---

## 6. Dónde encontrar los archivos de prueba

| Componente | Archivo |
|---|---|
| BFF | `bff/src/test/java/cl/duocuc/smartlogix/bff/BffServiceTest.java` |
| ms-inventario | `ms-inventario/src/test/java/cl/duocuc/smartlogix/inventario/InventarioServiceTest.java` |
| ms-pedidos | `ms-pedidos/src/test/java/cl/duocuc/smartlogix/pedidos/PedidoServiceTest.java` |
| ms-envios | `ms-envios/src/test/java/cl/duocuc/smartlogix/envios/EnvioServiceTest.java` |
| Frontend – StatCard | `frontend/src/components/StatCard.test.js` |
| Frontend – useDashboard | `frontend/src/hooks/useDashboard.test.js` |

