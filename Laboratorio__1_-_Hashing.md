# Laboratorio – Hashing Dinámico (Extendible Hashing) vs Búsqueda Secuencial

## 1. Descripción General

El laboratorio consiste en desarrollar un programa informático sencillo que permita registrar información de usuarios y comparar el tiempo de búsqueda utilizando dos métodos distintos:

- Extendible Hashing (Hashing dinámico).
- Búsqueda secuencial.

## 2. Requerimientos Funcionales

El sistema debe permitir las siguientes operaciones:

- Registrar usuarios con la siguiente información:
  - Nombre
  - Cédula (CC)
  - Correo electrónico
- La cédula será la clave principal (índice) de cada registro.
- **Restricción importante:** No se permiten cédulas duplicadas. El sistema debe validar esto.
- Permitir buscar un usuario ingresando su cédula. Al realizar la búsqueda, el sistema debe:
  - Buscar usando Extendible Hashing.
  - Buscar usando búsqueda secuencial.
  - Mostrar en pantalla el tiempo que tomó cada método.

## 3. Consideraciones Técnicas

Para garantizar el aprendizaje práctico, se deben seguir estas reglas:

- Se debe implementar Extendible Hashing desde cero.
- No usar bases de datos externas (MySQL, SQLite, etc.).
- Se pueden usar estructuras en memoria o manejar archivos, según decisión del estudiante.
- Se puede usar cualquier lenguaje de programación.
- No es obligatorio implementar una interfaz gráfica; una aplicación por consola simple es suficiente.

## 4. Salida Esperada

Cuando se busque un usuario exitosamente, el sistema debe mostrar la información de la siguiente manera:

```
Usuario encontrado:
Nombre: Juan
CC: 12345
Correo: juan@email.com
Tiempo búsqueda (Hashing): X ms
Tiempo búsqueda (Secuencial): Y ms
```

## 5. Evaluación

La calificación de este laboratorio se basará en la siguiente rúbrica:

| Criterio | Porcentaje |
|---|---|
| Implementación de Extendible Hashing | 30% |
| Validación de datos (no duplicados) | 10% |
| Implementación de búsqueda secuencial | 10% |
| Medición y comparación de tiempos | 15% |
| Claridad del código | 10% |
| Exposición del laboratorio | 25% |

## 6. Exposición

El laboratorio debe ser explicado en clase. Durante la sustentación se evaluará el funcionamiento, la explicación del algoritmo y la comparación de resultados.

**Importante:** Si el estudiante o grupo no realiza la exposición, la nota de la práctica será 0.

## 7. Recomendaciones

- Usar suficientes datos de prueba (un volumen alto) para notar las diferencias reales en los tiempos de ejecución.
- Validar correctamente las entradas por teclado.
- Manejar adecuadamente las colisiones en la estructura de hashing.
- Imprimir los resultados de forma clara y ordenada en la consola.
