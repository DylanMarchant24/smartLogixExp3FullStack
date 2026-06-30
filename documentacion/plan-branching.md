# Plan de Branching - SmartLogix EP3

## 1. Estrategia utilizada

Para la Entrega Parcial 3 se utilizo una estrategia basada en Git Flow simplificado. El repositorio mantiene una rama estable `main`, una rama de integracion `develop` y ramas `feature/*` separadas por responsabilidad de cada integrante.

La idea principal fue evitar trabajar directamente sobre `main`. Cada colaborador desarrollo su parte en una rama propia, luego los cambios se integraron a `develop` y finalmente se preparo la version estable del proyecto.

## 2. Ramas reales del repositorio

Las ramas visibles en GitHub son:

```text
main
develop
feature/benjamin-tests-docs
feature/cristobal-jwt-auth
feature/ricardo-discovery-gateway
```

> La documentacion anterior mencionaba ramas genericas que no corresponden a la evidencia actual de GitHub. Por eso fueron reemplazadas por las ramas reales del repositorio.

## 3. Flujo de trabajo

```text
main
└── develop
    ├── feature/ricardo-discovery-gateway
    ├── feature/cristobal-jwt-auth
    └── feature/benjamin-tests-docs
```

El flujo acordado fue:

1. Crear cada rama `feature/*` desde `develop`.
2. Realizar commits pequeños y descriptivos.
3. Subir la rama a GitHub.
4. Integrar los cambios hacia `develop` mediante Pull Request o merge controlado.
5. Validar ejecucion del proyecto y pruebas.
6. Llevar los cambios finales desde `develop` hacia `main` cuando la entrega estuviera estable.

## 4. Responsabilidades por rama

| Integrante | Rama | Responsabilidad principal | Archivos/carpetas relacionadas |
|---|---|---|---|
| Ricardo Novoa | `feature/ricardo-discovery-gateway` | Incorporacion de Service Discovery, API Gateway y monitoreo | `discovery-server/`, `api-gateway/`, configuraciones Eureka, Actuator, `documentacion/ep3/service-discovery.md`, `documentacion/ep3/api-gateway.md`, `documentacion/ep3/monitoreo.md` |
| Cristobal Perez | `feature/cristobal-jwt-auth` | Implementacion de autenticacion JWT y proteccion de rutas criticas | `bff/src/main/java/.../controller/AuthController.java`, `bff/src/main/java/.../security/`, `frontend/src/services/auth.js`, `frontend/src/components/ProtectedRoute.js`, `documentacion/ep3/seguridad-jwt.md` |
| Benjamin Meneses | `feature/benjamin-tests-docs` | Pruebas unitarias, cobertura y documentacion tecnica | `bff/src/test/`, `ms-inventario/src/test/`, `ms-pedidos/src/test/`, `ms-envios/src/test/`, `frontend/src/**/*.test.js`, `documentacion/guia-ejecucion-pruebas.md`, `documentacion/informe-pruebas.md`, `documentacion/resultados-pruebas-cobertura.md` |

## 5. Reglas aplicadas

| Regla | Aplicacion en el proyecto |
|---|---|
| No trabajar directo en `main` | `main` se mantiene como rama estable de entrega. |
| Usar `develop` como integracion | Las features se consolidan antes de pasar a `main`. |
| Usar ramas descriptivas | Cada rama indica responsable y modulo trabajado. |
| Commits claros | Se recomienda usar mensajes con prefijos `feat`, `fix`, `test`, `docs`, `refactor` o `chore`. |
| Evidencia de colaboracion | Se documentan ramas, responsables, commits y Pull Requests cuando esten disponibles. |

## 6. Convencion de commits recomendada

Formato utilizado o recomendado:

```text
<tipo>(<modulo>): descripcion breve
```

Ejemplos acordes al proyecto:

```text
feat(gateway): agrega rutas hacia bff y microservicios
feat(discovery): configura servidor Eureka
feat(auth): implementa login con JWT en BFF
test(backend): agrega pruebas unitarias de servicios
docs(branching): actualiza evidencia de ramas reales
```

## 7. Resolucion de conflictos

Si dos integrantes modifican el mismo archivo, se debe actualizar la rama con `develop`, resolver el archivo manualmente y luego continuar el merge o rebase.

Ejemplo de procedimiento:

```bash
git checkout develop
git pull origin develop

git checkout feature/nombre-rama
git merge develop

# Si aparece conflicto:
# 1. Abrir el archivo marcado por Git.
# 2. Eliminar las marcas <<<<<<<, ======= y >>>>>>>.
# 3. Dejar una version final coherente.

git add archivo-resuelto
git commit -m "fix(branching): resuelve conflicto con develop"
git push origin feature/nombre-rama
```

> Importante: en la documentacion final no se debe inventar un conflicto. Si no hubo conflicto real, se deja el procedimiento como buena practica y no como evidencia historica.

## 8. Evidencias que deben acompañar este plan

En la entrega se recomienda adjuntar capturas de:

- Lista de ramas en GitHub, donde se vean `main`, `develop` y las tres ramas `feature/*`.
- Commits de cada rama.
- Pull Requests o merges hacia `develop`, si existen.
- Ejecucion de pruebas backend y frontend.
- Ejecucion de servicios principales: Eureka, API Gateway, BFF, microservicios y frontend.

## 9. Estado final esperado

La estructura de branching queda alineada con el trabajo real del equipo:

- Ricardo: discovery, gateway y monitoreo.
- Cristobal: autenticacion JWT y seguridad.
- Benjamin: pruebas, cobertura y documentacion.

Con esto se cumple la trazabilidad minima requerida: ramas por integrante, separacion de responsabilidades y evidencia de integracion.
