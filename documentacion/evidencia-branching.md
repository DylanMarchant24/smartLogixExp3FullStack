# Evidencia de Branching - SmartLogix EP3

## 1. Evidencia base

En GitHub se observa que el repositorio trabaja con las siguientes ramas:

```text
main
develop
feature/benjamin-tests-docs
feature/cristobal-jwt-auth
feature/ricardo-discovery-gateway
```

Esta evidencia reemplaza la version anterior del documento, que contenia nombres de ramas genericos que no coincidian con el repositorio real.

## 2. Relacion entre ramas y colaboradores

| Colaborador | Rama de trabajo | Aporte documentado |
|---|---|---|
| Ricardo Novoa | `feature/ricardo-discovery-gateway` | Service Discovery con Eureka, API Gateway, rutas, configuracion de monitoreo con Actuator y documentacion tecnica asociada. |
| Cristobal Perez | `feature/cristobal-jwt-auth` | Autenticacion JWT en el BFF, login, validacion de token, filtro de seguridad, proteccion de operaciones criticas y ajustes de frontend para autenticacion. |
| Benjamin Meneses | `feature/benjamin-tests-docs` | Pruebas unitarias de backend y frontend, guias de ejecucion, informe de pruebas y documentacion de cobertura. |

## 3. Capturas que deben ir en esta seccion

### Evidencia 1 - Lista de ramas

Aqui va la captura de GitHub donde se ven las ramas:

```text
main
develop
feature/benjamin-tests-docs
feature/cristobal-jwt-auth
feature/ricardo-discovery-gateway
```

Esta captura demuestra que el equipo utilizo ramas separadas por responsabilidad y que no se trabajo todo directamente sobre `main`.

### Evidencia 2 - Commits por rama

Aqui va una captura de los commits de cada rama, idealmente desde:

```text
GitHub > Code > selector de rama > Commits
```

Se debe mostrar el autor, la fecha y el mensaje del commit.

### Evidencia 3 - Pull Requests o merges

Aqui va la captura de los Pull Requests cerrados o merges realizados hacia `develop`.

Si el equipo no uso Pull Requests y realizo merges locales, se debe mostrar una captura del historial de commits o del grafico de ramas.

### Evidencia 4 - Integracion final

Aqui va una captura donde se vea que `develop` o `main` contiene el proyecto integrado con:

- `discovery-server/`
- `api-gateway/`
- `bff/`
- `ms-inventario/`
- `ms-pedidos/`
- `ms-envios/`
- `frontend/`
- `documentacion/`

## 4. Historial esperado segun el flujo real

El historial del repositorio deberia reflejar una estructura parecida a esta:

```text
main
└── develop
    ├── feature/ricardo-discovery-gateway
    ├── feature/cristobal-jwt-auth
    └── feature/benjamin-tests-docs
```

No se incluye una rama `release/*` porque no aparece en la evidencia actual del repositorio. Si posteriormente se crea una rama de release, se puede agregar al plan de branching, pero no corresponde documentarla como evidencia si no existe.

## 5. Buenas practicas aplicadas

- Separacion del trabajo por ramas `feature/*`.
- Uso de `develop` como rama de integracion.
- `main` reservada como rama estable.
- Ramas con nombres descriptivos y asociadas a responsabilidades reales.
- Documentacion de cambios por colaborador.
- Evidencia visual de ramas y commits desde GitHub.

## 6. Conflictos

No se debe inventar un conflicto si no ocurrio realmente. Para cumplir con buenas practicas, se documenta el procedimiento de resolucion:

```bash
git checkout develop
git pull origin develop

git checkout feature/nombre-rama
git merge develop

# Resolver archivos con conflicto
git add archivo-resuelto
git commit -m "fix(branching): resuelve conflicto con develop"
git push origin feature/nombre-rama
```

Si existio un conflicto real, aqui debe agregarse:

- Archivo afectado.
- Ramas involucradas.
- Captura del conflicto o del commit de resolucion.
- Explicacion breve de como se resolvio.

## 7. Conclusion

La estrategia de branching utilizada permite demostrar colaboracion real dentro del proyecto SmartLogix. Cada integrante trabajo en una rama diferenciada, lo que facilita la trazabilidad de aportes, reduce errores por cambios simultaneos y permite integrar el sistema de forma ordenada hacia `develop` y `main`.
