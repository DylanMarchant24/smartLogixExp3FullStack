# Resumen de cambios realizados

## Código

- Se completaron los endpoints faltantes del BFF para que el frontend no llame directamente a microservicios:
  - `POST /api/bff/inventario`
  - `PUT /api/bff/inventario/{id}`
  - `DELETE /api/bff/inventario/{id}`
  - `PATCH /api/bff/pedidos/{id}/estado`
  - `PATCH /api/bff/envios/{id}/estado`
- Se actualizó `BffService` con proxy genérico para `POST`, `PUT` y `PATCH`.
- Se corrigió y amplió `BffServiceTest` para cubrir dashboard, graceful degradation, CRUD de inventario y cambios de estado.
- Se agregó `frontend/README.md` con instrucciones de ejecución, pruebas y endpoints usados.
- Se agregó `.gitignore` para evitar subir `target`, `node_modules`, `coverage` y archivos temporales.

## Documentación

- Se creó `documentacion/analisis-patrones-arquetipos.pdf`.
- Se creó `documentacion/plan-branching.pdf`.
- Se creó `documentacion/evidencia-branching.pdf`.
- Se creó `documentacion/resultados-pruebas-cobertura.pdf`.
- Se creó `documentacion/checklist-entrega.md`.
- Se creó `repositorios.txt` con estructura solicitada. Debe reemplazarse con URLs reales.

## Arquetipos Maven

- Se agregó `arquetipos-maven/smartlogix-ms-archetype`.
- Se agregó `arquetipos-maven/smartlogix-bff-archetype`.
- Se agregó `arquetipos-maven/README.md` con comandos para instalar y generar proyectos.

## Pendientes antes de subir a Blackboard

- Reemplazar los placeholders de `repositorios.txt` por enlaces reales.
- Subir el proyecto a GitHub con ramas/PRs reales.
- Ejecutar `bash scripts/generar-reportes-pruebas.sh` en un equipo con Maven y Node instalados.
- Adjuntar o mostrar en la presentación los porcentajes reales de cobertura.
