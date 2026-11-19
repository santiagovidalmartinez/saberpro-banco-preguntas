# Banco de Preguntas Saber PRO — Entrega Final (Taller 13)

Universidad del Cauca · Ingeniería de Software II · Corte III

Ecosistema de 3 microservicios + RabbitMQ + PostgreSQL, con **DDD puro**, **arquitectura hexagonal**
y despliegue con **Docker Compose** sobre la red `saberpro-net`.

## Arquitectura

| Servicio | Puerto | Responsabilidad |
|---|---|---|
| `validation-service` | 8082 | Valida enunciado, justificación y opciones (REST síncrono) |
| `question-service` | 8083 | Agregado `Question` (dominio puro), puertos, adaptadores JPA/memoria, publica en RabbitMQ |
| `lifecycle-service` | 8084 | Consume `pregunta.revisar.queue` (`@RabbitListener`) |
| `rabbitmq` | 5672 / 15672 | Broker AMQP (panel: http://localhost:15672, guest/guest) |
| `postgres` | interno | Base de datos de `question-service` (volumen `pgdata`) |

Flujo: `POST /api/preguntas` → Question llama a Validation (REST) → 201 / 400.
`POST /api/preguntas/{id}/enviar-revision` → cambia el estado a `PENDIENTE_REVISION`, publica el evento
en `pregunta.exchange` (routing key `pregunta.revisar`) y responde **202 Accepted** → Lifecycle lo consume.

### Hexágono de `question-service`

```
api/ (REST, adaptador primario)
  └─ usa → domain/ports/inbound  (CrearPreguntaUseCase, EvaluarPreguntaUseCase, ConsultarPreguntaUseCase)
              └─ implementado por → application/QuestionApplicationService
                    └─ usa → domain/ports/outbound (PreguntaRepositoryPort, NotificacionPort, ValidacionPort)
                              ├─ infrastructure/jpa       JpaQuestionAdapter     (Spring Data JPA)
                              ├─ infrastructure/memory    InMemoryQuestionAdapter (ConcurrentHashMap)
                              ├─ infrastructure/messaging RabbitMQPublisher       (AMQP)
                              └─ infrastructure/rest      ValidationRestClient    (REST)
domain/model: Question, AnswerOption, Enunciado, Justificacion, QuestionId, EstadoPregunta  (Java puro)
```

`domain` y `application` no importan Spring, JPA ni Jackson (lo comprueba `ArquitecturaHexagonalTest`).

## Requisitos

Docker Desktop (con Docker Compose v2). No hace falta tener Java ni Maven: el `Dockerfile` compila el JAR
en una primera etapa (`maven:3.9-eclipse-temurin-17`) y ejecuta en `openjdk:17-jdk-slim`.

## Ejecutar todo con Docker

```bash
docker compose up -d --build     # construye las 3 imágenes y levanta todo
docker ps                        # deben aparecer 5 contenedores
docker logs -f saberpro-question-service
docker logs -f saberpro-lifecycle-service
```

La primera construcción tarda varios minutos (descarga imágenes y dependencias Maven).

Apagar: `docker compose down` (con `-v` también borra los datos de PostgreSQL).

> Si los puertos 8082–8084, 5672 o 15672 están ocupados (por ejemplo por contenedores de talleres
> anteriores): `docker ps`, y `docker stop <nombre>` de los que estorben.

## Probar (Postman o Thunder Client)

Importar `postman/Taller13_Docker.postman_collection.json` y ejecutar las solicitudes 0 a 10 en orden
(o el Runner). Manualmente:

| Paso | Petición | Esperado |
|---|---|---|
| 1 | `GET  http://localhost:8083/api/preguntas` | 200 y lista |
| 2 | `POST http://localhost:8083/api/preguntas` (JSON abajo) | **201** con `id` |
| 3 | mismo POST con justificación `"corta"` | **400** |
| 4 | `POST http://localhost:8083/api/preguntas/{id}/enviar-revision` | **202** `{operationId,status}` |
| 5 | `GET http://localhost:8084/lifecycle/eventos` y `docker logs saberpro-lifecycle-service` | evento recibido |
| 6 | http://localhost:15672 → Queues → `pregunta.revisar.queue` | 1 consumidor, mensaje procesado |

```json
{
  "enunciado": "¿Cuál es la capital de Colombia?",
  "justificacion": "Bogotá es la capital y sede del gobierno nacional.",
  "opciones": [
    {"texto": "Medellín", "correcta": false},
    {"texto": "Bogotá", "correcta": true},
    {"texto": "Cali", "correcta": false},
    {"texto": "Popayán", "correcta": false}
  ]
}
```

## Demostración de intercambiabilidad (InMemory vs JPA)

`HexagonalConfiguration` elige el adaptador con `saberpro.persistence`. Sin tocar `domain` ni `application`:

```bash
cd question-service
mvn spring-boot:run                                                  # JpaQuestionAdapter (H2)
mvn spring-boot:run "-Dspring-boot.run.profiles=memory" "-Dspring-boot.run.arguments=--saberpro.messaging.enabled=false --saberpro.validation.enabled=false"
```

Las mismas pruebas (`QuestionRepositoryPortContractTest`, `QuestionApiContractTest`) se ejecutan contra ambos.

## Pruebas unitarias

```bash
cd question-service   && mvn clean test
cd validation-service && mvn clean test
cd lifecycle-service  && mvn clean test
```

## Trazabilidad semestral

| Corte | Enfoque | Hitos |
|---|---|---|
| I (25%) | Monolito limpio & SOLID | SOLID, MVC, Pipes & Filters (HU03), Microkernel |
| II (35%) | Microservicios & GoF | Bounded contexts, REST, AMQP, Builder/State/Adapter, JPA |
| III (40%) | DDD, Hexagonal & Docker | Dominio puro, puertos/adaptadores, InMemory vs JPA, Dockerfile, Compose |
