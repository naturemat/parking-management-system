# Sistema de Gestion de Estacionamiento - Arquitectura DDD (Domain-Driven Design)

## Objetivo Principal

Sistema de consola que controla el ingreso y salida de vehiculos en un parqueadero, aplicando reglas de negocio estrictas como compatibilidad vehiculo-espacio, rechazo por capacidad llena, y calculo de costo basado en tiempo estacionado. Implementado con patrones tacticos de Domain-Driven Design (Agregados, Value Objects, Eventos de Dominio, Servicios de Dominio y Repositorios).

---

## Funcionalidades

- **Ingreso de vehiculos** con validacion de espacio disponible, compatibilidad de tipo y unicidad de placa activa.
- **Registro de salida** con calculo automatico de costo ($2.00 por hora, minimo 1 hora).
- **Consulta de estado** del estacionamiento (ocupados, disponibles, detalle por espacio).
- **Tickets activos** en tiempo real.
- **Historial por vehiculo** con todos sus tickets (activos y cerrados).
- **Ingresos totales** acumulados.
- **Demo automatizada** que ejecuta 11 pasos probando todas las reglas de negocio como si se hiciera manualmente.

---

## Reglas de Negocio

1. **No asignar espacio ocupado** - Cada espacio solo puede tener un vehiculo a la vez.
2. **Compatibilidad vehiculo-espacio** - Electricos e hibridos solo en espacios ELECTRIC. Gasolina y diesel solo en STANDARD.
3. **Rechazar si estacionamiento lleno** - Si todos los espacios estan ocupados, se rechaza el ingreso.
4. **Un vehiculo no puede ingresar dos veces sin salir** - Si tiene un ticket activo, no puede generar un nuevo ingreso.
5. **Costo depende del tiempo estacionado** - Minimo 1 hora a $2.00, fracciones se redondean a la hora completa.

---

## Arquitectura DDD (Domain-Driven Design)

```
+------------------------------------------------------------------+
|                    Presentation Layer (CLI)                       |
|                    com.parking.presentation.cli                   |
|                    ParkingCli.java (menu por consola)             |
+------------------------------------------------------------------+
                             |  llama
                             v
+------------------------------------------------------------------+
|                   Application Layer (Services)                    |
|                   com.parking.application.service                 |
|                   ParkingApplicationService.java                  |
|                   Orquesta casos de uso, coordina el dominio      |
|                   Sin logica de negocio, solo delegacion          |
+------------------------------------------------------------------+
                             |  utiliza
                             v
+------------------------------------------------------------------+
|                      Domain Layer (Modelo)                        |
|  com.parking.domain.model       |  com.parking.domain.valueObject |
|  - ParkingLot (Agregado)        |  - Plate (Value Object)         |
|  - Space (Entidad)              |  - SpaceCode (Value Object)     |
|  - Ticket (Agregado)            |  - Money (Value Object)         |
|  - Vehicle (Entidad)            |  - HourlyRate (Value Object)    |
|                                 |                                 |
|  com.parking.domain.service     |  com.parking.domain.event       |
|  - ParkingPricingService        |  - DomainEvent (interface)      |
|  - RevenueService               |  - VehicleParkedEvent           |
|                                 |  - VehicleExitedEvent           |
|  com.parking.domain.repository  |                                 |
|  - ParkingLotRepository (intf)  |  com.parking.domain.exception   |
|  - TicketRepository (intf)      |  - ParkingException            |
|  - VehicleRepository (intf)     |  - VehicleAlreadyParkedException|
|                                 |  - SpaceOccupiedException       |
|                                 |  - ParkingFullException         |
|                                 |  - IncompatibleVehicleTypeExcep |
|                                 |  - EntityNotFoundException      |
|                                 |  - NoActiveTicketException      |
+------------------------------------------------------------------+
                             |  implementa
                             v
+------------------------------------------------------------------+
|                   Infrastructure Layer (Persistencia)             |
|  com.parking.infrastructure.persistence.jpa                       |
|  - JPA Entities (SpaceJpaEntity, TicketJpaEntity, VehicleJpaEntity)|
|  - Spring Data Repositories                                       |
|  - DomainEntityMapper (convierte entre dominio y JPA)             |
|  - Implementaciones de repositorios del dominio                   |
|                                                                   |
|  com.parking.infrastructure.config                                |
|  - DataInitializer (crea 10 espacios al iniciar)                  |
|  - AppConfig (beans de servicios de dominio)                      |
+------------------------------------------------------------------+
```

### Patrones DDD Implementados

| Patron | Clase | Proposito |
|--------|-------|-----------|
| **Agregado (Aggregate Root)** | `ParkingLot` | Gestiona la coleccion de espacios y aplica reglas de negocio de asignacion. |
| **Agregado (Aggregate Root)** | `Ticket` | Administra el ciclo de vida de una sesion de estacionamiento. |
| **Entidad (Entity)** | `Space` | Espacio de estacionamiento con identidad (codigo). |
| **Entidad (Entity)** | `Vehicle` | Vehiculo con identidad (placa). |
| **Value Object** | `Plate` | Placa del vehiculo como objeto inmutable. |
| **Value Object** | `SpaceCode` | Codigo de espacio como objeto inmutable. |
| **Value Object** | `Money` | Valor monetario con operaciones (sumar, multiplicar). |
| **Value Object** | `HourlyRate` | Tarifa por hora con capacidad de calcular costo. |
| **Evento de Dominio** | `VehicleParkedEvent` | Notifica que un vehiculo se estaciono exitosamente. |
| **Evento de Dominio** | `VehicleExitedEvent` | Notifica que un vehiculo abandono el estacionamiento. |
| **Servicio de Dominio** | `ParkingPricingService` | Calcula el costo de estacionamiento segun el tiempo usando `HourlyRate`. |
| **Servicio de Dominio** | `RevenueService` | Calcula ingresos totales, conteo de tickets cerrados e ingresos por vehiculo. |
| **Excepcion de Dominio** | `ParkingException` | Base para todas las excepciones del dominio. |
| **Excepcion de Dominio** | `VehicleAlreadyParkedException` | Lanzada cuando un vehiculo intenta ingresar teniendo un ticket activo. |
| **Excepcion de Dominio** | `SpaceOccupiedException` | Lanzada cuando se intenta asignar un espacio ya ocupado. |
| **Excepcion de Dominio** | `ParkingFullException` | Lanzada cuando el estacionamiento esta completamente lleno. |
| **Excepcion de Dominio** | `IncompatibleVehicleTypeException` | Lanzada cuando el tipo de vehiculo no coincide con el tipo de espacio. |
| **Excepcion de Dominio** | `EntityNotFoundException` | Lanzada cuando no se encuentra una entidad (vehiculo, espacio, etc.). |
| **Excepcion de Dominio** | `NoActiveTicketException` | Lanzada al intentar sacar un vehiculo sin ticket activo. |
| **Repositorio (Interfaz)** | `ParkingLotRepository` | Contrato de persistencia para el estacionamiento en el dominio. |
| **Repositorio (Interfaz)** | `TicketRepository` | Contrato de persistencia para tickets en el dominio. |
| **Repositorio (Interfaz)** | `VehicleRepository` | Contrato de persistencia para vehiculos en el dominio. |
| **Separacion de Persistencia** | JPA Entities vs Domain Entities | Las entidades JPA son independientes de las entidades del dominio. |

---

## Estructura del Proyecto

```
src/main/java/com/parking/
├── ParkingApplication.java                           # @SpringBootApplication + CommandLineRunner
├── domain/
│   ├── valueObject/
│   │   ├── Plate.java                                # VO: placa del vehiculo
│   │   ├── SpaceCode.java                            # VO: codigo de espacio
│   │   ├── Money.java                                # VO: dinero con operaciones
│   │   └── HourlyRate.java                           # VO: tarifa por hora
│   ├── model/
│   │   ├── ParkingLot.java                           # AGREGADO RAÍZ: gestiona espacios y reglas de ingreso
│   │   ├── Space.java                                # Entidad: espacio de estacionamiento
│   │   ├── SpaceType.java                            # Enum: STANDARD, ELECTRIC
│   │   ├── Ticket.java                               # AGREGADO: ticket de estacionamiento
│   │   ├── Vehicle.java                              # Entidad: vehiculo
│   │   └── VehicleType.java                          # Enum: ELECTRIC, GASOLINE, DIESEL, HYBRID
│   ├── event/
│   │   ├── DomainEvent.java                          # Interface para eventos de dominio
│   │   ├── VehicleParkedEvent.java                   # Evento: vehiculo estacionado
│   │   └── VehicleExitedEvent.java                   # Evento: vehiculo salio
│   ├── service/
│   │   ├── ParkingPricingService.java                # Servicio: calculo de tarifas con HourlyRate
│   │   └── RevenueService.java                       # Servicio: calculo de ingresos y metricas
│   ├── repository/
│   │   ├── ParkingLotRepository.java                 # Interfaz de repositorio de ParkingLot
│   │   ├── TicketRepository.java                     # Interfaz de repositorio de Ticket
│   │   └── VehicleRepository.java                    # Interfaz de repositorio de Vehicle
│   └── exception/
│       ├── ParkingException.java                     # Excepcion base del dominio
│       ├── VehicleAlreadyParkedException.java        # Vehiculo ya estacionado
│       ├── SpaceOccupiedException.java               # Espacio ocupado
│       ├── ParkingFullException.java                 # Estacionamiento lleno
│       ├── IncompatibleVehicleTypeException.java     # Tipo incompatible
│       ├── EntityNotFoundException.java              # Entidad no encontrada
│       └── NoActiveTicketException.java              # Sin ticket activo
├── application/
│   ├── dto/
│   │   └── ParkingStatusDTO.java                    # DTO para mostrar estado del estacionamiento
│   └── service/
│       └── ParkingApplicationService.java           # Servicio de aplicacion (orquesta casos de uso)
├── infrastructure/
│   ├── persistence/
│   │   └── jpa/
│   │       ├── entity/
│   │       │   ├── SpaceJpaEntity.java              # Entidad JPA para espacios
│   │       │   ├── TicketJpaEntity.java             # Entidad JPA para tickets
│   │       │   └── VehicleJpaEntity.java            # Entidad JPA para vehiculos
│   │       ├── repository/
│   │       │   ├── SpringDataSpaceJpaRepository.java   # Spring Data para espacios
│   │       │   ├── SpringDataTicketJpaRepository.java  # Spring Data para tickets
│   │       │   └── SpringDataVehicleJpaRepository.java # Spring Data para vehiculos
│   │       ├── DomainEntityMapper.java              # Mapeo entre dominio y JPA
│   │       ├── ParkingLotRepositoryImpl.java        # Implementacion de ParkingLotRepository
│   │       ├── TicketRepositoryImpl.java            # Implementacion de TicketRepository
│   │       └── VehicleRepositoryImpl.java           # Implementacion de VehicleRepository
│   └── config/
│       ├── DataInitializer.java                     # Inicializa 10 espacios al arrancar
│       └── AppConfig.java                           # Configuracion de beans (servicios de dominio)
└── presentation/
    └── cli/
        └── ParkingCli.java                          # Menu interactivo por consola
```

---

## Aspectos Destacados de la Arquitectura DDD

### El Dominio es Puro Java

La capa de dominio (`com.parking.domain`) no tiene ninguna dependencia de frameworks. No usa anotaciones de Spring, JPA ni ninguna otra tecnologia. Es Java puro con patrones de diseno.

### Value Objects con Comportamiento

- `Money` permite operaciones como `add()` y `multiply()` para calculos financieros.
- `HourlyRate` encapsula la tarifa y sabe calcular costo segun las horas.
- `Plate` y `SpaceCode` garantizan que los valores no sean nulos o vacios.

### Excepciones de Dominio Explicitas

Cada regla de negocio tiene su propia excepcion:
- `VehicleAlreadyParkedException` (regla 4)
- `SpaceOccupiedException` (regla 1)
- `IncompatibleVehicleTypeException` (regla 2)
- `ParkingFullException` (regla 3)
- `EntityNotFoundException` (vehiculo/espacio no encontrado)
- `NoActiveTicketException` (salir sin ticket activo)

### Servicios de Dominio

- `ParkingPricingService`: calcula costo usando `HourlyRate` (2.0) y tiempo estacionado.
- `RevenueService`: calcula ingresos totales, conteo de tickets cerrados e ingresos por vehiculo.

### Agregados con Comportamiento

El `ParkingLot` no es un simple contenedor de datos: contiene toda la logica de negocio para asignar espacios, validar reglas y emitir eventos de dominio. El `Ticket` encapsula su propio ciclo de vida (apertura, cierre con costo).

### Eventos de Dominio

Cada operacion de negocio genera eventos (`VehicleParkedEvent`, `VehicleExitedEvent`) que son publicados por la aplicacion, permitiendo futuras integraciones (notificaciones, auditoria, facturacion).

### Separacion de Persistencia

Las entidades JPA son clases separadas de las entidades del dominio. El `DomainEntityMapper` convierte entre ambos mundos, manteniendo el dominio completamente desacoplado de la tecnologia de persistencia.

---

## Como Ejecutar

### Prerrequisitos
- Java 17 o superior
- Maven 3.6+

### Pasos
```bash
# Compilar y ejecutar
mvn clean package
java -jar target/parking-ddd-1.0.0.jar

# O ejecutar directamente con spring-boot
mvn spring-boot:run
```

### Menu de Opciones

```
=== SISTEMA DE GESTION DE ESTACIONAMIENTO ===
1. Ingresar vehiculo
2. Registrar salida de vehiculo
3. Mostrar estado del estacionamiento
4. Mostrar tickets activos
5. Historial de vehiculo
6. Ingresos totales
7. Demo automatizada
8. Salir
```

### Demo Automatizada

La opcion 7 ejecuta una demostracion completa sin intervencion del usuario, simulando el flujo manual completo:

1. Ingreso exitoso (GASOLINE en STANDARD)
2. Ingreso exitoso (ELECTRIC en ELECTRIC)
3. Rechazo por vehiculo ya estacionado (regla 4) - `VehicleAlreadyParkedException`
4. Rechazo por espacio ocupado (regla 1) - `SpaceOccupiedException`
5. Rechazo por incompatibilidad (regla 2, espacio ocupado) - `SpaceOccupiedException`
6. Ingreso exitoso (HYBRID en ELECTRIC)
7. Rechazo por incompatibilidad de tipo (ELECTRIC en STANDARD) - `IncompatibleVehicleTypeException`
8. Estado del estacionamiento en vivo
9. Salida con calculo de costo (regla 5)
10. Historial del vehiculo que salio
11. Ingresos acumulados
12. Estado final del estacionamiento

### Espacios Disponibles

| Espacio | Tipo |
|---------|------|
| A1-A5 | STANDARD |
| B1-B3 | ELECTRIC |
| B4-B5 | STANDARD |

### Compatibilidad Vehiculo-Espacio

| Tipo Vehiculo | Espacios Compatibles |
|---------------|---------------------|
| ELECTRIC | ELECTRIC |
| HYBRID | ELECTRIC |
| GASOLINE | STANDARD |
| DIESEL | STANDARD |
