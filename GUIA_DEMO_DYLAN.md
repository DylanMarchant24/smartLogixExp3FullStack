# Guía de Pruebas, Despliegue Cloud y Evidencias — Evaluación Parcial N°3 (Dylan)

**Asignatura:** DSY1107 - Desarrollo Cloud Native I  
**Proyecto:** SmartLogix — Plataforma de Gestión Logística Cloud Native  
**Responsable:** Dylan (Infraestructura Docker/AWS, Persistencia Cloud RDS/MySQL, RabbitMQ Cluster, Apache Kafka Cluster, API Gateway/BFF y Seguridad IDaaS Microsoft Entra ID)

---

## 1. Levantar la Infraestructura (Local y Cloud AWS EC2)

### 1.1. Entorno Local (Docker Desktop)

```bash
# 1. Copiar variables de entorno base (opcional si se usan los defaults)
cp .env.example .env

# 2. Construir y levantar toda la infraestructura (Clusters RabbitMQ, Kafka, MySQL y Microservicios)
docker compose up -d --build

# 3. Verificar estado de todos los contenedores
docker compose ps
```

### 1.2. Entorno Cloud (AWS EC2 Ubuntu)

En AWS EC2, exporta la IP pública de la instancia en la variable `HOST_IP` para que los **Advertised Listeners** externos de los brokers de Kafka (`kafka-1:9092` y `kafka-2:9093`) y las URLs de CORS/Frontend apunten automáticamente a la IP pública de AWS:

```bash
# 1. Detectar automáticamente la IP pública de la instancia EC2
export HOST_IP=$(curl -s ifconfig.me)
echo "IP Pública EC2: $HOST_IP"

# 2. Levantar el clúster completo en segundo plano
HOST_IP=$HOST_IP docker compose up -d --build

# 3. Verificar que los contenedores estén corriendo (Up / healthy)
docker compose ps
```

> **Nota Terraform (`terraform/`):** El archivo `terraform/user_data.sh.tpl` ya inyecta automáticamente `HOST_IP=$PUBLIC_IP`, `RABBITMQ_ERLANG_COOKIE` y `KAFKA_UI_PORT=8095` en el archivo `.env` al aprovisionar la máquina EC2.

### 1.3. Puertos Requeridos en el Security Group de AWS (`terraform/main.tf`)

| Puerto(s) | Protocolo | Servicio | Propósito en la Demostración |
| :--- | :---: | :--- | :--- |
| `22` | TCP | SSH | Administración remota de la instancia EC2 |
| `3000` | TCP | Frontend (Nginx + React) | SPA con login IDaaS Microsoft Entra ID (MSAL) |
| `8080` | TCP | BFF (`smartlogix-bff`) | Backend For Frontend protegido con OAuth2 Resource Server (JWT) |
| `8085` | TCP | API Gateway (`smartlogix-gateway`) | Punto de entrada centralizado con validación JWT y ruteo Eureka |
| `8761` | TCP | Eureka Server | Dashboard de descubrimiento de microservicios |
| `8081-8092` | TCP | Microservicios Core | Pedidos (`8081`), Envíos (`8082`), Inventario (`8083`), Notificaciones (`8084`), Pagos (`8087`), Sucursales (`8088`), Proveedores (`8089`), Usuarios (`8090`), Cupones (`8091`), Reportes (`8092`) |
| `8093` | TCP | `ms-rabbit-admin` | API REST y Swagger UI para administración dinámica de RabbitMQ |
| `8094` | TCP | `ms-auditoria` | Consumidor Kafka (`@KafkaListener`) y API REST de administración de tópicos Kafka |
| `8095` | TCP | `kafka-ui` | Dashboard web visual para Brokers, Tópicos, Particiones y Mensajes de Kafka |
| `9092-9093` | TCP | Kafka Brokers (`kafka-1`, `kafka-2`) | Listeners externos del clúster Apache Kafka |
| `15672-15673` | TCP | RabbitMQ Management UI | Consola web del clúster RabbitMQ (Nodo 1: `15672`, Nodo 2: `15673`) |

---

## 2. Pruebas de Seguridad Backend y Frontend (OAuth2 / OIDC + JWT en Microsoft Entra ID)

Reemplaza `localhost` por `$HOST_IP` si ejecutas las pruebas contra AWS EC2.

### Caso A: Petición al API Gateway / BFF SIN Token (`401 Unauthorized`)

Demuestra que las rutas de negocio están protegidas y rechazan accesos anónimos:

```bash
# Vía API Gateway (:8085)
curl -i -X GET http://localhost:8085/api/bff/pedidos

# Vía BFF directo (:8080)
curl -i -X GET http://localhost:8080/api/bff/dashboard
```
**Resultado esperado:** `HTTP/1.1 401 Unauthorized` (cabecera `WWW-Authenticate: Bearer`).

---

### Caso B: Petición con un Token JWT Falso o Alterado (`401 Unauthorized`)

Demuestra que Spring Security OAuth2 Resource Server verifica criptográficamente la firma RS256 contra las llaves públicas (`JWKS`) de Microsoft Entra ID y valida `iss` / `aud`:

```bash
# Token con firma inválida / payload manipulado
TOKEN_FALSO="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkF0YWNhbnRlIiwiaWF0IjoxNTE2MjM5MDIyfQ.firma_invalida_manipulada"

# Prueba contra API Gateway (:8085)
curl -i -X GET http://localhost:8085/api/bff/pedidos \
  -H "Authorization: Bearer $TOKEN_FALSO"

# Prueba contra BFF (:8080)
curl -i -X GET http://localhost:8080/api/bff/inventario \
  -H "Authorization: Bearer $TOKEN_FALSO"
```
**Resultado esperado:** `HTTP/1.1 401 Unauthorized` (`invalid_token`).

---

### Caso C: Petición con un Token JWT Válido emitido por Microsoft Entra ID (`200 OK` / `201 Created`)

1. Inicia sesión en el Frontend (`http://localhost:3000` o `https://<tu-dominio-ngrok>`) con tu cuenta corporativa de Microsoft Entra ID.
2. Abre **DevTools (F12) -> Red (Network)**, selecciona cualquier petición hacia `/api/bff/...` y copia el contenido del header `Authorization: Bearer <TU_TOKEN_JWT>` (o ejecuta la llamada directamente desde la interfaz).

```bash
export JWT_VALIDO="<PEGA_AQUI_EL_ACCESS_TOKEN_DE_ENTRA_ID>"

# 1. Consultar Dashboard vía API Gateway (:8085) -> 200 OK
curl -i -X GET http://localhost:8085/api/bff/dashboard \
  -H "Authorization: Bearer $JWT_VALIDO"

# 2. Crear un Pedido vía API Gateway (:8085) -> 200 OK / 201 Created
# (Dispara simultáneamente eventos a RabbitMQ y Apache Kafka)
curl -i -X POST http://localhost:8085/api/bff/pedidos \
  -H "Authorization: Bearer $JWT_VALIDO" \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": "Empresa Minera Norte S.A.",
    "origen": "Centro Distribucion Santiago",
    "destino": "Faena Antofagasta",
    "peso": 125.5,
    "Prioridad": "ALTA"
  }'
```
**Resultado esperado:** `HTTP/1.1 200 OK` con el JSON del pedido persistido en MySQL/RDS y publicado en RabbitMQ + Kafka.

---

## 3. Pruebas de Infraestructura de Mensajería y Streaming (RabbitMQ Cluster + Kafka Cluster)

### 3.1. Demostración de Clúster RabbitMQ (2 Nodos), Colas, Exchanges, DLQ y `ms-rabbit-admin`

#### A) Verificar el Clúster de 2 Nodos (`rabbit@rabbitmq-1` y `rabbit@rabbitmq-2`)
```bash
docker exec smartlogix-rabbitmq rabbitmqctl cluster_status
```
También puedes abrir la consola web en:
- **Nodo 1:** `http://localhost:15672` (usuario: `smartlogix`, password: `smartlogix123`)
- **Nodo 2:** `http://localhost:15673` (usuario: `smartlogix`, password: `smartlogix123`)

En la pestaña **Overview -> Nodes** se visualizan ambos nodos activos en clúster.

#### B) Administrar Objetos RabbitMQ vía API REST (`ms-rabbit-admin` en puerto `8093`)
```bash
# 1. Crear un Exchange personalizado
curl -i -X POST http://localhost:8093/api/rabbit-admin/exchanges \
  -H "Content-Type: application/json" \
  -d '{
    "name": "smartlogix.logistica.exchange",
    "type": "topic",
    "durable": true,
    "autoDelete": false
  }'

# 2. Crear una Cola con soporte automático de Dead-Letter Exchange (DLX)
curl -i -X POST http://localhost:8093/api/rabbit-admin/queues \
  -H "Content-Type: application/json" \
  -d '{
    "name": "smartlogix.logistica.queue",
    "durable": true,
    "exclusive": false,
    "autoDelete": false
  }'

# 3. Crear el Binding entre el Exchange y la Cola
curl -i -X POST http://localhost:8093/api/rabbit-admin/bindings \
  -H "Content-Type: application/json" \
  -d '{
    "queueName": "smartlogix.logistica.queue",
    "exchangeName": "smartlogix.logistica.exchange",
    "routingKey": "logistica.ruta.#"
  }'

# 4. Probar validación Bean Validation (@Valid / @Pattern) enviando un nombre inválido -> 400 Bad Request
curl -i -X POST http://localhost:8093/api/rabbit-admin/queues \
  -H "Content-Type: application/json" \
  -d '{"name": "nombre invalido con espacios!", "durable": true}'
```

#### C) Verificar Consumo tipado (`PedidoEvent`), ACK Manual y Dead-Letter Queue (`pedidos.dlq`)
```bash
# Ver logs de ms-envios confirmando recepción de PedidoEvent y basicAck manual
docker logs --tail 30 smartlogix-ms-envios
```

---

### 3.2. Demostración de Clúster Apache Kafka (2 Brokers), Tópicos, Particiones y Consumidores

#### A) Listar y Describir Tópicos y Particiones mediante Script CLI (`scripts/kafka-admin.sh`)
```bash
# Listar todos los tópicos del clúster Kafka
bash scripts/kafka-admin.sh list

# Describir particiones (3), factor de replicación (2), líderes e ISR de los tópicos oficiales
bash scripts/kafka-admin.sh describe smartlogix.pedidos.eventos
bash scripts/kafka-admin.sh describe smartlogix.envios.tracking

# Consultar estado y offsets del grupo de consumidores de auditoría
bash scripts/kafka-admin.sh groups
```

#### B) Administrar Tópicos Kafka vía API REST (`ms-auditoria` en puerto `8094`)
```bash
# 1. Listar tópicos existentes en el clúster Kafka
curl -s http://localhost:8094/api/auditoria/kafka/topics | jq .

# 2. Crear un nuevo tópico Kafka dinámicamente con particiones, réplicas y retención
curl -i -X POST http://localhost:8094/api/auditoria/kafka/topics \
  -H "Content-Type: application/json" \
  -d '{
    "name": "smartlogix.flota.telemetria",
    "partitions": 3,
    "replicationFactor": 2,
    "retentionMs": 604800000
  }'

# 3. Eliminar un tópico de prueba
curl -i -X DELETE http://localhost:8094/api/auditoria/kafka/topics/smartlogix.flota.telemetria
```

#### C) Producir y Consumir Eventos en Tiempo Real (Flujo End-to-End Pedidos -> Envíos -> Auditoría)
```bash
# 1. Crear un pedido directamente en ms-pedidos (o vía BFF/Gateway)
# Esto publica en RabbitMQ (pedidos.exchange) Y en Kafka (smartlogix.pedidos.eventos).
# Además, ms-envios recibe el pedido por RabbitMQ, crea el envío y publica en Kafka (smartlogix.envios.tracking).
curl -i -X POST http://localhost:8081/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "cliente": "Constructora Pacífico Ltda.",
    "origen": "Bodega Central Valparaíso",
    "destino": "Obra Concepción",
    "peso": 84.0
  }'

# 2. Verificar que ms-auditoria consumió AMBOS eventos desde Kafka y los persistió en MySQL/RDS
curl -s http://localhost:8094/api/auditoria | jq .

# 3. Ver logs de ms-auditoria mostrando Tópico, Partición, Offset y Payload recibido
docker logs --tail 30 smartlogix-ms-auditoria
```

#### D) Inspección Visual en Kafka-UI (`:8095`)
Abre en el navegador `http://localhost:8095` (o `http://<EC2_PUBLIC_IP>:8095`):
- **Brokers:** Verifica que los 2 brokers (`brokerId=1` y `brokerId=2`) estén activos.
- **Topics:** Verifica `smartlogix.pedidos.eventos` y `smartlogix.envios.tracking` con `3` particiones y `Replication Factor = 2`.
- **Consumers:** Verifica el Consumer Group `smartlogix-auditoria-group` activo y sin lag.

---

## 4. Checklist de Capturas de Pantalla para el Informe de Evidencias (EP3)

- [ ] **Captura 1 — Clúster Docker en AWS EC2 / Local:** Salida de `docker compose ps` mostrando activos `rabbitmq-1`, `rabbitmq-2`, `zookeeper`, `kafka-1`, `kafka-2`, `kafka-ui`, `api-gateway`, `bff`, `ms-pedidos`, `ms-envios`, `ms-auditoria`, `ms-rabbit-admin` y `frontend`.
- [ ] **Captura 2 — Clúster RabbitMQ y DLQ:** Consola web de RabbitMQ (`:15672` y `:15673`) mostrando los 2 nodos en clúster (`rabbit@rabbitmq-1`, `rabbit@rabbitmq-2`), las colas `pedidos.creados.queue` (con argumentos `x-dead-letter-exchange` y `x-dead-letter-routing-key`) y `pedidos.dlq`.
- [ ] **Captura 3 — Administración Dinámica de RabbitMQ (`ms-rabbit-admin`):** Ejecución en Swagger UI (`:8093/swagger-ui/index.html`) o `curl` creando/eliminando un exchange/queue y probando un `400 Bad Request` por validación `@Pattern`.
- [ ] **Captura 4 — Clúster Apache Kafka y Tópicos:** Vista de **Kafka-UI (`:8095`)** o salida de `bash scripts/kafka-admin.sh describe smartlogix.pedidos.eventos` mostrando los 2 brokers, los tópicos `smartlogix.pedidos.eventos` y `smartlogix.envios.tracking` con 3 particiones y factor de replicación 2.
- [ ] **Captura 5 — Productor y Consumidor Kafka (`ms-pedidos` / `ms-envios` -> `ms-auditoria`):** Respuesta de `GET http://localhost:8094/api/auditoria` y logs de `smartlogix-ms-auditoria` evidenciando el consumo de ambos tópicos con su partición y offset, persistidos en base de datos.
- [ ] **Captura 6 — Conexión a Base de Datos Cloud con HikariCP:** Archivo `application.yml` con variables parametrizadas `${SPRING_DATASOURCE_URL}` y configuración `spring.datasource.hikari.*`, más los logs de arranque de Spring Boot mostrando `HikariPool-1 - Start completed`.
- [ ] **Captura 7 — Login IDaaS Microsoft Entra ID en Frontend:** Pantalla de inicio de sesión de Microsoft Entra ID redirigiendo a la aplicación React y mostrando la sesión autenticada con el nombre/correo del usuario.
- [ ] **Captura 8 — Interceptor JWT en DevTools:** Pestaña Network del navegador mostrando una petición desde el Frontend hacia el Backend con la cabecera `Authorization: Bearer eyJ...` adjuntada automáticamente por el interceptor HTTP.
- [ ] **Captura 9 — Pruebas de Seguridad en el Backend (`401` vs `200`):** Terminal ejecutando los 3 comandos `curl` de la Sección 2 (sin token $\rightarrow$ `401`, con token adulterado $\rightarrow$ `401`, y con token válido de Entra ID $\rightarrow$ `200 OK`).

