# Arquetipos Maven - SmartLogix

Esta carpeta contiene los arquetipos Maven usados como base para construir los componentes backend solicitados en la evaluación: microservicios y Backend For Frontend (BFF).

## Arquetipos incluidos

| Arquetipo | Propósito | Componentes generados |
|---|---|---|
| `smartlogix-ms-archetype` | Crear microservicios Spring Boot con estructura estándar | controller, service, repository, model, dto, test, pom.xml |
| `smartlogix-bff-archetype` | Crear un BFF/API Gateway para frontend React | controller, service, dto, RestTemplate, test, pom.xml |

## Instalación local de los arquetipos

Ejecutar desde esta carpeta:

```bash
cd arquetipos-maven/smartlogix-ms-archetype
mvn clean install

cd ../smartlogix-bff-archetype
mvn clean install
```

## Generar un nuevo microservicio

```bash
mvn archetype:generate \
  -DarchetypeGroupId=cl.duocuc.smartlogix \
  -DarchetypeArtifactId=smartlogix-ms-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=cl.duocuc.smartlogix \
  -DartifactId=ms-nuevo \
  -Dpackage=cl.duocuc.smartlogix.nuevo \
  -DinteractiveMode=false
```

## Generar un nuevo BFF

```bash
mvn archetype:generate \
  -DarchetypeGroupId=cl.duocuc.smartlogix \
  -DarchetypeArtifactId=smartlogix-bff-archetype \
  -DarchetypeVersion=1.0.0 \
  -DgroupId=cl.duocuc.smartlogix \
  -DartifactId=bff-nuevo \
  -Dpackage=cl.duocuc.smartlogix.bffnuevo \
  -DinteractiveMode=false
```

## Justificación

Los arquetipos permiten repetir la misma estructura técnica en todos los componentes backend. Esto aporta coherencia, mantenibilidad y escalabilidad, ya que cada microservicio nuevo nace con separación por capas, configuración de pruebas, dependencias Maven y convenciones del equipo.
