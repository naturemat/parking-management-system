# Sistema de Gestion de Estacionamiento

**Parking Management System** - Examen Hemi, Arquitectura de Software

---

## Que logra este proyecto

Un sistema funcional de gestion de estacionamiento que controla el ingreso y salida de vehiculos aplicando reglas de negocio reales. Todo el codigo esta en un unico archivo (`src/Main.java`) y opera via consola.

### Reglas de negocio implementadas

1. **Solo vehiculos registrados pueden ingresar** - El sistema valida que la placa exista en el registro antes de permitir el acceso.
2. **No hay ingreso doble sin salida** - Un vehiculo dentro del estacionamiento no puede volver a ingresar hasta que haya salido.
3. **Compatibilidad de espacios para vehiculos electricos** - Los vehiculos electricos solo pueden ocupar espacios STANDARD o ELECTRIC_CHARGING. Los vehiculos de combustion no pueden ocupar espacios de carga electrica.
4. **Rechazo por capacidad llena** - Si no hay espacios disponibles (o ninguno compatible), el ingreso es rechazado.
5. **Costo basado en tiempo estacionado** - Se cobra por hora o fraccion (minimo 1 hora) multiplicado por la tarifa del espacio.
6. **Reingreso permitido post-salida** - Una vez que el vehiculo sale y paga, puede volver a ingresar.

### Entidades del dominio

| Entidad | Descripcion |
|---|---|
| `Vehicle` | Placa, tipo (GASOLINE/DIESEL/ELECTRIC/HYBRID), marca, modelo, propietario |
| `Space` | Identificador, tipo (STANDARD/ELECTRIC_CHARGING/DISABLED), piso, tarifa por hora |
| `Ticket` | ID unico, placa, espacio, timestamp entrada/salida, costo total |

---

## Arquitectura del codigo

No hay arquitectura. Es **codigo spaguetti** intencional: todo el codigo (enumeraciones, entidades, logica de negocio e interfaz de usuario) reside en un unico archivo `src/Main.java`. No hay separacion en capas, no hay patrones de diseno, no hay inyeccion de dependencias. Simplemente funciona.

---

## Como probarlo

### Requisitos

- Java JDK 11 o superior instalado
- Variable `JAVA_HOME` configurada, o `javac` y `java` accesibles desde la terminal

### Compilar

Desde la raiz del proyecto:

```sh
javac -d . src\Main.java
```

Esto genera los archivos `*.class` necesarios para ejecutar.

### Ejecutar

```sh
java Main
```

### Menu interactivo

Al ejecutar, el sistema pide la capacidad maxima del estacionamiento (Enter para 20). Luego presenta un menu con 10 opciones:

1. Registrar un nuevo vehiculo
2. Ingreso de vehiculo
3. Salida de vehiculo
4. Ver estado completo del estacionamiento
5. Consultar tickets
6. Agregar nuevo espacio
7. Listar vehiculos registrados
8. Listar espacios
9. Escenario de demostracion automatico
0. Salir

### Demo automatico

Seleccionar la opcion **9** ejecuta 14 pruebas que recorren todas las reglas de negocio, mostrando mensajes de aceptacion o rechazo segun corresponda. Es la forma mas rapida de verificar que el sistema funciona correctamente.

### Ejemplo de uso manual

```
1. Opcion 7 -> Lista vehiculos registrados (vienen 7 de ejemplo)
2. Opcion 2 -> Ingresar placa "ABC-123" (vehiculo a gasolina)
3. Opcion 4 -> Ver estado, espacio A1 ocupado
4. Opcion 3 -> Salir con "ABC-123", muestra factura con costo
5. Opcion 2 -> Ingresar "ABC-123" de nuevo (funciona, ya salio)
6. Opcion 0 -> Salir del sistema
```

### Nota sobre archivos .class

Al compilar con `javac` se generan archivos `*.class` (bytecode de Java). Son el resultado de la compilacion y los necesita la JVM para ejecutar el programa. Se pueden eliminar con `Remove-Item *.class` y recompilar sin problemas.
