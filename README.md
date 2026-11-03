# Prueba Técnica Financiera

Backend desarrollado con **Java 17 + Spring Boot + PostgreSQL** para administrar clientes, cuentas y transacciones financieras.

## Tecnologías

- Java 17
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- PostgreSQL
- Bean Validation
- Lombok
- JUnit 5
- Mockito
- Git / GitHub
- Postman
- Docker / Docker Compose

## Arquitectura

Se utilizó una arquitectura MVC por capas:

```text
Controller
   ↓
Service
   ↓
Repository
   ↓
PostgreSQL
```

Estructura principal:

```text
controller/
dto/
entity/
enums/
repository/
service/
```

## Entidades

- `Cliente`
- `Cuenta`
- `Transaccion`
- `Movimiento`

Enums utilizados:

- `TipoCuenta`: `AHORROS`, `CORRIENTE`
- `EstadoCuenta`: `ACTIVA`, `INACTIVA`, `CANCELADA`
- `TipoTransaccion`: `CONSIGNACION`, `RETIRO`, `TRANSFERENCIA`
- `TipoMovimiento`: `CREDITO`, `DEBITO`

## Reglas principales implementadas

### Clientes
- Solo se permiten clientes mayores de edad.
- Fecha de creación automática.
- Fecha de modificación automática.
- Validación básica de correo.
- Nombre y apellido mínimo de 2 caracteres.
- No se puede eliminar un cliente con cuentas vinculadas.

### Cuentas
- Toda cuenta pertenece a un cliente.
- Ahorros inicia por `53`.
- Corriente inicia por `33`.
- Número único de 10 dígitos generado automáticamente.
- Estados: activa, inactiva y cancelada.
- Solo se cancela una cuenta con saldo `0`.
- Una cuenta de ahorros no puede quedar con saldo negativo.

### Transacciones
- Consignación.
- Retiro.
- Transferencia.
- Actualización automática de saldo y saldo disponible.
- Transferencia genera:
    - movimiento `DEBITO` en cuenta origen;
    - movimiento `CREDITO` en cuenta destino.
- Las operaciones financieras utilizan `@Transactional`.

## API REST

Base URL:

```text
http://localhost:8080
```

### Clientes

```text
POST   /api/clientes
GET    /api/clientes
GET    /api/clientes/{id}
PUT    /api/clientes/{id}
DELETE /api/clientes/{id}
```

Ejemplo para crear cliente:

```json
{
  "tipoIdentificacion": "CC",
  "numeroIdentificacion": "1075000001",
  "nombres": "Carlos",
  "apellido": "Ramirez",
  "correoElectronico": "carlos@gmail.com",
  "fechaNacimiento": "1998-06-15"
}
```

### Cuentas

```text
POST  /api/cuentas
GET   /api/cuentas
GET   /api/cuentas/{id}
PATCH /api/cuentas/{id}/activar
PATCH /api/cuentas/{id}/inactivar
PATCH /api/cuentas/{id}/cancelar
```

Crear cuenta de ahorros:

```json
{
  "tipoCuenta": "AHORROS",
  "clienteId": 1,
  "exentaGMF": false
}
```

Crear cuenta corriente:

```json
{
  "tipoCuenta": "CORRIENTE",
  "clienteId": 1,
  "exentaGMF": false
}
```

### Transacciones

Consignación:

```text
POST /api/transacciones/consignacion
```

```json
{
  "numeroCuenta": "5312345678",
  "monto": 500000
}
```

Retiro:

```text
POST /api/transacciones/retiro
```

```json
{
  "numeroCuenta": "5312345678",
  "monto": 100000
}
```

Transferencia:

```text
POST /api/transacciones/transferencia
```

```json
{
  "cuentaOrigen": "5312345678",
  "cuentaDestino": "3312345678",
  "monto": 200000
}
```

Consultar movimientos:

```text
GET /api/transacciones/cuenta/{cuentaId}/movimientos
```

## Configuración PostgreSQL

Base utilizada:

```text
prueba_financiera
```

`application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/prueba_financiera
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

La contraseña se configura mediante la variable de entorno:

```text
DB_PASSWORD
```

## Pruebas

Las rutas fueron validadas manualmente con Postman.

También se implementaron pruebas unitarias con **JUnit 5 + Mockito**.

Ubicación:

```text
src/test/java/com/pruebafinanciera/backend
```

Ejecutar test de Service:

```bash
./mvnw -Dtest=ClienteServiceTest test
```

Ejecutar test de Controller:

```bash
./mvnw -Dtest=ClienteControllerTest test
```

Ejecutar todos los tests:

```bash
./mvnw test
```

Resultado esperado:

```text
Tests run: ...
Failures: 0
Errors: 0
BUILD SUCCESS
```

## Ejecución con Docker

Requiere Docker y Docker Compose. Levanta PostgreSQL y el backend:

```bash
docker compose up --build
```

API disponible en `http://localhost:8080`. Credenciales opcionales: copiar `.env.example` a `.env` y editarlas.

Detener (conservando datos):

```bash
docker compose down
```

Detener y borrar la base de datos:

```bash
docker compose down -v
```

## Ejecución local (sin Docker)

Desde Git Bash:

```bash
./mvnw spring-boot:run
```

En Windows CMD/PowerShell:

```bash
mvnw.cmd spring-boot:run
```

API disponible en:

```text
http://localhost:8080
```

## Control de versiones

El desarrollo se registró mediante commits por bloques funcionales, por ejemplo:

```text
feat: implement client and account modules
feat: add transaction and movement enums
feat: add transaction and movement entities
feat: add transaction and movement repositories
feat: add transaction request DTOs
feat: implement financial transaction service
feat: expose transaction endpoints
test: add client service unit tests
test: add client controller unit tests
docs: add project documentation
```

## Estado

Implementado:

- Backend Spring Boot
- PostgreSQL
- Clientes
- Cuentas
- Consignación
- Retiro
- Transferencia
- Movimientos
- Validaciones
- Postman
- JUnit / Mockito
- Git / GitHub

Pendiente si se desea continuar:

- Frontend
- Manejo global de excepciones HTTP
- Ampliar cobertura de pruebas
