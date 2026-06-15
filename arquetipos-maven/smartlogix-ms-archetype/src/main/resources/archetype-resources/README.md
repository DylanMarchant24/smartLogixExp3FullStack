# ${artifactId}

Microservicio Spring Boot generado desde `smartlogix-ms-archetype`.

## Ejecución

```bash
mvn spring-boot:run
```

## Pruebas y cobertura

```bash
mvn test jacoco:report
```

## Estructura generada

- `controller`: expone endpoints REST.
- `service`: contiene reglas de negocio.
- `repository`: abstracción de persistencia JPA.
- `model`: entidad de dominio.
- `dto`: contrato de entrada/salida.
