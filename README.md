# Parking Management System

Examén de Medio Semestre, para la materia de Arquitetcura de Software
Developed by Mateo Cobo

## Descripción
Sistema de gestión de estacionamiento que controla ingreso y salida de vehículos.

## Reglas de negocio
- No se puede asignar un espacio ocupado
- Vehículos eléctricos solo pueden ocupar espacios compatibles (espacios 16-20)
- Si el estacionamiento está lleno (20 espacios) se rechaza el ingreso
- Un vehículo no puede ingresar dos veces sin salir
- El costo depende del tiempo estacionado ($5 por hora o fracción, vehículos eléctricos tienen 20% descuento)

## Estructura del Proyecto
Este repositorio contiene **3 implementaciones diferentes** en ramas separadas para comparar arquitecturas:

| Rama | Arquitectura | Descripción |
|------|--------------|-------------|
| `spaghetti-parking` | Código espagueti | Todo en un solo archivo `Main.java` sin estructura |
| `layered-parking` | Capas tradicionales | Separación en presentation/application/domain/infrastructure |
| `ddd-tactical-parking` | DDD Táctico | Aggregates, Value Objects, Domain Services, Repositories |

## Cómo revisar cada implementación

```bash
# Ver implementación espagueti
git checkout spaghetti-parking

# Ver implementación por capas
git checkout layered-parking

# Ver implementación DDD
git checkout ddd-tactical-parking
