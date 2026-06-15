# Evidencia de Branching - SmartLogix

## 1. Estrategia usada

Se usa Git Flow con ramas principales `main` y `develop`, mas ramas de trabajo por funcionalidad:

- `feature/ms-inventario`
- `feature/ms-pedidos`
- `feature/ms-envios`
- `feature/bff`
- `feature/frontend-dashboard`
- `release/v1.0.0`

## 2. Flujo de colaboracion

1. Cada integrante crea su rama desde `develop`.
2. Cada funcionalidad se desarrolla con commits pequenos y descriptivos.
3. Cada rama se sube a GitHub.
4. Se crea Pull Request hacia `develop`.
5. Otro integrante revisa el codigo.
6. Se ejecutan pruebas antes de hacer merge.
7. El merge se realiza con `--no-ff` para dejar evidencia historica.

## 3. Ejemplo de historial esperado

```text
*   a1b2c3d (main, tag: v1.0.0) Merge release/v1.0.0
|\
| * d4e5f6g fix(release): corrige configuracion CORS en BFF
| *   h7i8j9k Merge feature/frontend-dashboard into develop
| |\
| | * 11a22bb feat(frontend): agrega dashboard e integracion con BFF
| | * 22b33cc test(frontend): agrega pruebas de useDashboard y StatCard
| *   l0m1n2o Merge feature/bff into develop
| |\
| | * 33c44dd feat(bff): agrega proxies CRUD de inventario
| | * 44d55ee test(bff): agrega pruebas unitarias de BffService
| *   p3q4r5s Merge feature/ms-envios into develop
| *   t6u7v8w Merge feature/ms-pedidos into develop
| *   x9y0z1a Merge feature/ms-inventario into develop
|/
* b2c3d4e chore: inicializa estructura SmartLogix
```

## 4. Ejemplo de conflicto documentado

Archivo con conflicto: `README.md`.

Motivo: dos ramas actualizaron la tabla de endpoints del BFF al mismo tiempo.

Marcas de conflicto:

```text
<<<<<<< HEAD
| POST | /api/bff/pedidos | Crea pedido |
=======
| POST | /api/bff/envios | Crea envio |
>>>>>>> feature/ms-envios
```

Resolucion aplicada:

```text
| POST | /api/bff/pedidos | Crea pedido |
| POST | /api/bff/envios | Crea envio |
```

Comandos usados:

```bash
git checkout feature/frontend-dashboard
git fetch origin
git rebase origin/develop
# resolver README.md
git add README.md
git rebase --continue
git push origin feature/frontend-dashboard --force-with-lease
```

## 5. Evidencias que se deben adjuntar en GitHub o presentacion

- Captura de ramas `main`, `develop`, `feature/*` y `release/*`.
- Pull Requests cerrados hacia `develop`.
- Captura de checks o salida de pruebas.
- Captura de un conflicto resuelto o descripcion del conflicto.
- Salida de `git log --graph --oneline --all --decorate`.

## 6. Aporte al equipo

Esta estrategia favorece colaboracion porque cada integrante trabaja aislado en una feature, reduce riesgo de romper `develop`, deja trazabilidad con Pull Requests y permite preparar versiones con `release/*`.
