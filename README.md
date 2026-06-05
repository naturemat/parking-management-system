# Parking Management System

Examen de Medio Semestre - Arquitectura de Software
Desarrollado por Mateo Cobo

## Descripcion

Sistema de gestion de estacionamiento que controla el ingreso y salida de vehiculos, validando reglas de negocio como compatibilidad vehiculo-espacio, capacidad maxima, unicidad de ticket activo y calculo de costo por tiempo estacionado.

---

## Ramas del Repositorio

Este proyecto contiene **3 implementaciones** del mismo sistema, cada una en una rama separada, para comparar distintas arquitecturas de software:

| Rama | Arquitectura | Stack |
|------|--------------|-------|
| `spaghetti-parking` | Codigo espagueti | Java puro, un solo archivo `Main.java`, compilacion con `javac` |
| `layered-parking` | Capas (Layered) | Spring Boot, JPA, H2, Maven |
| `ddd-tactical-parking` | DDD Tactico | Spring Boot, JPA, H2, Maven |

---

## Diferencias Clave entre Ramas

### spaghetti-parking
- Todo el codigo en un unico archivo `src/Main.java`
- Sin separacion de responsabilidades
- Sin gestor de dependencias (compila con `javac`)
- Logica de negocio mezclada con entrada/salida por consola
- 20 espacios, tarifa variable por tipo de espacio

### layered-parking
- Separacion en 4 capas: Presentation, Application, Domain, Infrastructure
- Entidades de dominio anemicas (solo datos, sin comportamiento)
- Toda la logica de negocio en un `ParkingService` (Transaction Script)
- Entidades JPA con anotaciones Spring en la capa de dominio
- 10 espacios (A1-A5 STANDARD, B1-B3 ELECTRIC, B4-B5 STANDARD)
- Tarifa fija de $2.00 por hora

### ddd-tactical-parking
- Misma separacion en capas, pero con patrones tacticos de DDD
- **Agregados**: `ParkingLot` (raiz) y `Ticket`
- **Value Objects**: `Plate`, `SpaceCode`, `Money`, `HourlyRate`
- **Eventos de Dominio**: `VehicleParkedEvent`, `VehicleExitedEvent`
- **Servicios de Dominio**: `ParkingPricingService`, `RevenueService`
- **Excepciones explicitas**: 6 excepciones de negocio especificas
- **Dominio puro**: sin anotaciones Spring ni JPA en la capa domain
- **Separacion de persistencia**: entidades JPA independientes del modelo de dominio
- Mismas reglas y menu que la version en capas

---

## Como Explorar

```bash
# Ver la implementacion espagueti
git checkout spaghetti-parking

# Ver la implementacion por capas
git checkout layered-parking

# Ver la implementacion DDD
git checkout ddd-tactical-parking
```

Cada rama contiene su propio `README.md` con instrucciones detalladas para compilar y ejecutar ese proyecto especifico.
