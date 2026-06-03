# Sistema de Gestion de Estacionamiento - Arquitectura en Capas (Layered)

## Problema Real

Controlar ingreso de vehiculos a un parqueadero.

### Entidades
- **Vehiculo**: identificado por placa, con marca, modelo y tipo (ELECTRIC, GASOLINE, DIESEL, HYBRID)
- **Espacio**: identificado por codigo (A1, B2, etc.), con un tipo (STANDARD o ELECTRIC) y estado (ocupado/libre)
- **Ticket**: registra el ingreso, salida, costo calculado y estado (activo/cerrado)

### Reglas de Negocio

1. **No asignar espacio ocupado** — Cada espacio solo puede tener un vehiculo a la vez. Si ya tiene una placa asignada, se rechaza la operacion.

2. **Compatibilidad vehiculo-espacio** — Los vehiculos electricos e hibridos solo pueden estacionar en espacios tipo ELECTRIC. Los vehiculos a gasolina y diesel solo en espacios STANDARD.

3. **Rechazar si estacionamiento lleno** — Si la totalidad de los espacios estan ocupados, no se permite el ingreso de ningun vehiculo nuevo.

4. **Un vehiculo no puede ingresar dos veces sin salir** — Si un vehiculo tiene un ticket activo (aun no ha salido), no puede generar un nuevo ingreso. Debe salir primero.

5. **Costo depende del tiempo estacionado** — Al registrar la salida, se calculan las horas transcurridas entre ingreso y salida (minimo 1 hora) y se multiplica por una tarifa fija por hora.

---

## Arquitectura del Software

```
+---------------------------------------------+
|         Presentation Layer (CLI)            |
|   com.parking.presentation.cli              |
|   ParkingCli.java (menu por consola)        |
+---------------------------------------------+
                    |  llama a
                    v
+---------------------------------------------+
|       Application Layer (Services)          |
|   com.parking.application.services          |
|   ParkingService.java (Transaction Script)  |
|   Toda la logica de negocio                 |
+---------------------------------------------+
                    |  usa
                    v
+---------------------------------------------+
|         Domain Layer (Model)                |
|   com.parking.domain.model                  |
|   Entidades anemicas con @Entity            |
|   Solo datos, sin logica                    |
+---------------------------------------------+
                    |  persiste en
                    v
+---------------------------------------------+
|      Infrastructure Layer (Repos/Tech)      |
|   com.parking.infrastructure.repositories   |
|   com.parking.infrastructure.config         |
|   Spring Data JPA + H2                      |
+---------------------------------------------+
```

### Responsabilidad de cada capa

| Capa | Rol |
|------|-----|
| **Presentation** | Interactuar con el usuario via consola (menu de 8 opciones). No contiene logica de negocio. |
| **Application** | Contiene el `ParkingService`, un unico servicio que orquesta y ejecuta todas las reglas de negocio, validaciones, calculos y excepciones (Transaction Script). |
| **Domain** | Define las entidades con sus atributos, getters y setters (anemicas). Sin logica, sin dependencias externas. |
| **Infrastructure** | Implementa la persistencia con Spring Data JPA sobre H2 en memoria. Inicializa los 10 espacios al arrancar. |

---

## Estructura del Proyecto

```
src/main/java/com/parking/
├── ParkingApplication.java              # @SpringBootApplication + CommandLineRunner
├── presentation/
│   └── cli/
│       └── ParkingCli.java             # Menu de consola (8 opciones + demo)
├── application/
│   └── services/
│       └── ParkingService.java         # Transaction Script con toda la logica
├── domain/
│   └── model/
│       ├── Vehicle.java                # @Entity: plate, brand, model, type
│       ├── VehicleType.java            # enum: ELECTRIC, GASOLINE, DIESEL, HYBRID
│       ├── Space.java                  # @Entity: id, type, occupied, assignedVehiclePlate
│       ├── SpaceType.java              # enum: STANDARD, ELECTRIC
│       ├── Ticket.java                 # @Entity: id, vehiclePlate, spaceId, entryTime, exitTime, cost, active
│       └── ParkingStatus.java          # DTO para mostrar estado
└── infrastructure/
    ├── repositories/
    │   ├── VehicleRepository.java      # JpaRepository<Vehicle, String>
    │   ├── SpaceRepository.java        # JpaRepository<Space, String>
    │   └── TicketRepository.java       # JpaRepository<Ticket, Long>
    └── config/
        └── DataInitializer.java        # Crea 10 espacios al iniciar
```

---

## Uso Basico

Al ejecutar la aplicacion se presenta un menu interactivo con 8 opciones:

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

### Opcion destacada: Demo automatizada

La opcion 7 ejecuta una demostracion completa sin intervencion del usuario, mostrando:

- Ingresos exitosos (gasolina, electrico, hibrido)
- Rechazo por vehiculo ya estacionado (regla 4)
- Rechazo por espacio ocupado (regla 1)
- Rechazo por incompatibilidad (regla 2)
- Estado del estacionamiento en vivo
- Salida con calculo de costo (regla 5)
- Historial de un vehiculo
- Ingresos acumulados

---

## Como Ejecutar

### Prerequisitos
- Java 17 o superior
- Maven 3.6+

### Pasos
```bash
# Compilar y ejecutar
mvn clean package
java -jar target/parking-layered-1.0.0.jar

# O solo ejecutar con spring-boot
mvn spring-boot:run
```

### Espacios Disponibles
| Espacio | Tipo      |
|---------|-----------|
| A1-A5   | STANDARD  |
| B1-B3   | ELECTRIC  |
| B4-B5   | STANDARD  |

### Compatibilidad Vehiculo-Espacio
| Tipo Vehiculo | Espacios Compatibles |
|---------------|---------------------|
| ELECTRIC      | ELECTRIC            |
| HYBRID        | ELECTRIC            |
| GASOLINE      | STANDARD            |
| DIESEL        | STANDARD            |
