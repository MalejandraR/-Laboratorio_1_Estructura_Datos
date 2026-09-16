# Guia de Sustentacion — Laboratorio de Hashing Dinamico Extendible

## Indice

1. [Titulo del laboratorio](#1-titulo-del-laboratorio)
2. [Objetivo general](#2-objetivo-general)
3. [Que es hashing extendible y por que se usa](#3-que-es-hashing-extendible-y-por-que-se-usa)
4. [Diferencia entre hashing estatico y dinamico](#4-diferencia-entre-hashing-estatico-y-dinamico)
5. [Componentes: directorio, buckets, global depth, local depth](#5-componentes-directorio-buckets-global-depth-local-depth)
6. [Funcionamiento paso a paso](#6-funcionamiento-paso-a-paso)
7. [Regla de redistribucion de todos los registros](#7-regla-de-redistribucion-de-todos-los-registros)
8. [Por que la busqueda es O(1) promedio](#8-por-que-la-busqueda-es-1-promedio)
9. [Comparacion teorica con busqueda secuencial O(n)](#9-comparacion-teorica-con-busqueda-secuencial-on)
10. [Responsabilidades: Persona 1 vs Persona 2](#10-responsabilidades-persona-1-vs-persona-2)
11. [Explicacion del codigo implementado](#11-explicacion-del-codigo-implementado)
12. [Materiales de referencia del profesor](#12-materiales-de-referencia-del-profesor)
13. [Resultados esperados y que demostrar en la exposicion](#13-resultados-esperados-y-que-demostrar-en-la-exposicion)
14. [Posibles preguntas del profesor y respuestas sugeridas](#14-posibles-preguntas-del-profesor-y-respuestas-sugeridas)
15. [Recomendaciones para una buena sustentacion](#15-recomendaciones-para-una-buena-sustentacion)

---

## 1. Titulo del laboratorio

**Laboratorio 1 — Hashing Dinamico Extendible vs Busqueda Secuencial**

Materia: Estructura de Datos y Laboratorio
Tema: Comparacion de metodos de busqueda con indices dinamicos en memoria.

---

## 2. Objetivo general

Desarrollar desde cero una implementacion de **Hashing Dinamico Extendible** en Java, demostrando su capacidad de crecimiento automatico sin reconstruccion completa, y comparar su rendimiento de busqueda O(1) contra una busqueda secuencial O(n) para evidenciar las ventajas de usar un indice hash dinamico.

**Objetivos especificos:**
- Comprender y aplicar los conceptos de profundidad global, profundidad local, directorio y buckets.
- Implementar el algoritmo de split con y sin duplicacion del directorio.
- Validar la restriccion de unicidad de cedulas (no duplicados).
- Medir y comparar tiempos de busqueda entre ambos metodos.
- Explicar el funcionamiento del algoritmo durante la sustentacion en clase.

---

## 3. Que es hashing extendible y por que se usa

### Definicion

El **Hashing Dinamico Extendible** es una tecnica de indexacion que permite que la estructura de hash **crezca dinamicamente** a medida que se insertan registros, sin necesidad de reconstruir toda la estructura desde cero.

A diferencia del hashing estatico, donde el numero de buckets es fijo desde el inicio, el hashing extendible usa un **directorio de punteros** que puede duplicar su tamano cuando es necesario, manteniendo el acceso directo O(1) a los registros.

### Por que se usa

1. **Evita reconstrucciones costosas**: En hashing estatico, si los datos crecen mucho, hay que rehashgear todo el archivo, lo cual es O(n) y puede bloquear el sistema.
2. **Minimiza overflow**: Al dividir buckets dinamicamente, se evitan cadenas largas de desbordamiento.
3. **Crecimiento controlado**: El directorio crece de 2 en 2 (duplicacion), no de forma descontrolada.
4. **Acceso rapido**: Si el directorio cabe en memoria RAM, cualquier registro se localiza con exactamente 1 acceso a disco (el bucket).

### Ejemplo analogico

Imagina una biblioteca con estantes (buckets) y un indice (directorio). En hashing estatico, si se llenan los estantes, tenes que reconstruir todo el indice desde cero. En hashing extendible, solo agregas estantes nuevos y actualizas algunas entradas del indice — las demas siguen apuntando a los estantes originales.

---

## 4. Diferencia entre hashing estatico y dinamico

| Caracteristica | Hashing Estatico | Hashing Dinamico Extendible |
|---|---|---|
| Numero de buckets | Fijo desde el inicio | Crece dinamicamente |
| Directorio | No existe o es fijo | Duplica su tamano cuando LD = GD |
| Overflow | Cadenas largas o rehash completo | Split de buckets individuales |
| Reconstruccion | Requiere rehash completo O(n) | Nunca requiere rehash completo |
| Complejidad de busqueda | O(1) promedio | O(1) promedio |
| Uso de disco | Posible desperdicio de espacio | Crecimiento bajo demanda |
| Implementacion | Simple | Mas compleja pero mas escalable |

### Ejemplo visual de la diferencia

**Hashing Estatico (B=4):**
```
Directorio fijo: [B0, B1, B2, B3]
Si B0 se llena -> overflow con cadena -> degradacion
Si no caben mas buckets -> REHASH COMPLETO
```

**Hashing Extendible (B=4):**
```
Directorio inicial: [B0]  (GD=0, LD=0)
Directorio duplicado: [B0, B1]  (GD=1)
Directorio duplicado: [B0, B1, B2, B3]  (GD=2)
Cada bucket se divide individualmente cuando se llena
```

---

## 5. Componentes: directorio, buckets, global depth, local depth

### Directorio

El directorio es un **arreglo de punteros** a buckets. Su tamano es **2^GD** (2 elevado a la profundidad global).

**Ejemplo:** Si GD = 3, el directorio tiene 8 entradas:

| Entrada (binario) | Puntero a |
|---|---|
| 000 | Bucket A |
| 001 | Bucket A |
| 010 | Bucket B |
| 011 | Bucket C |
| 100 | Bucket A |
| 101 | Bucket D |
| 110 | Bucket B |
| 111 | Bucket C |

**Nota importante:** Varias entradas pueden apuntar al mismo bucket. Esto ocurre cuando LD < GD.

### Buckets

Cada bucket es un contenedor de registros (usuarios) con:
- **Capacidad maxima (B)**: Numero maximo de registros que puede contener.
- **Profundidad Local (LD)**: Numero de bits del hash que comparten los registros almacenados en ese bucket.
- **Lista interna**: Arreglo o lista de registros.

**Regla fundamental:** Siempre LD <= GD.

### Global Depth (GD)

- Perteneciente al **directorio completo**.
- Indica **cuantos bits** del resultado del hash se usan para indexar las entradas del directorio.
- Determina el **tamano del directorio** (2^GD entradas).
- **Aumenta** cuando un bucket con LD = GD se divide y se necesita duplicar el directorio.

### Local Depth (LD)

- Perteneciente a **cada bucket individual**.
- Indica **cuantos bits** del hash comparten los registros almacenados en ese bucket.
- **Aumenta** cuando el bucket se divide (split).
- **Nunca puede ser mayor** que la Global Depth.

### Relacion entre GD y LD

| GD | LD | Que significa |
|---|---|---|
| GD = 2, LD = 1 | 2 entradas del directorio apuntan al mismo bucket |
| GD = 2, LD = 2 | Solo 1 entrada del directorio apunta al bucket |
| GD = 3, LD = 2 | 2 entradas del directorio apuntan al mismo bucket |
| GD = 3, LD = 3 | Solo 1 entrada del directorio apunta al bucket |

---

## 6. Funcionamiento paso a paso

### 6.1 Insercion de un registro

**Algoritmo completo:**

1. **Calcular hash** de la cedula usando la funcion hash.
2. **Extraer los ultimos GD bits** del hash usando la mascara: `hash & ((1 << GD) - 1)`
3. **Obtener la posicion** en el directorio (valor entre 0 y 2^GD - 1).
4. **Acceder al bucket** apuntado por esa posicion.
5. **Verificar espacio**: Si el bucket tiene capacidad, insertar directamente.
6. **Si esta lleno (overflow)**: Realizar split.

### 6.2 Overflow y Split

Cuando un bucket se llena y se intenta insertar un nuevo registro, ocurre **overflow**. El comportamiento del split depende de la relacion entre LD y GD:

#### Caso 1: Split SIN duplicar directorio (LD < GD)

**Condicion:** La profundidad local del bucket es menor que la global.

**Que significa:** Varias entradas del directorio apuntan al mismo bucket. Podemos dividir el bucket y actualizar solo esas entradas.

**Pasos:**
1. Crear bucket viejo y bucket nuevo, ambos con LD = LD_anterior + 1.
2. Redistribuir TODOS los registros del bucket lleno + el nuevo registro usando el bit adicional (posicion LD).
3. Actualizar las entradas del directorio que apuntaban al bucket original.

**Ejemplo:**
```
ANTES (GD=2, LD=1):
  Directorio: [B0, B1, B1, B0]
  B0 contiene: [1023, 4012]  (LD=1, bit 0)
  B1 contiene: [2045, 3078]  (LD=1, bit 1)

Insertar 5067 (overflow en B0):
  NuevaLD = 2
  B0 viejo: registros con bit(LD=2) = 0 -> [1023, 4012]  (00, 10)
  B0 nuevo: registros con bit(LD=2) = 1 -> [5067]          (01)

DESPUES (GD=2, LD=2):
  Directorio: [B0_viejo, B0_nuevo, B0_viejo, B0_nuevo]
```

#### Caso 2: Split CON duplicacion del directorio (LD = GD)

**Condicion:** La profundidad local del bucket es igual a la global.

**Que significa:** El bucket ya usa todos los bits disponibles. Para diferenciar mejor los registros, se necesita un bit adicional. Por eso, el directorio debe duplicar su tamano.

**Pasos:**
1. **Duplicar directorio**: GD = GD + 1, tamano = 2^(GD+1).
2. Crear bucket viejo y bucket nuevo, ambos con LD = LD_anterior + 1.
3. Redistribuir TODOS los registros del bucket lleno + el nuevo registro usando el bit adicional.
4. Actualizar las entradas del directorio.

**Ejemplo:**
```
ANTES (GD=2, LD=2):
  Directorio: [B0, B1, B2, B3]  (4 entradas)
  B0 contiene: [1023, 2045, 3078, 4012]  (lleno)

Insertar 5067 (overflow en B0):
  GD = 3 (duplicar directorio -> 8 entradas)
  NuevaLD = 3
  B0 viejo: registros con bit(LD=3) = 0 -> [1023, 3078]
  B0 nuevo: registros con bit(LD=3) = 1 -> [2045, 4012, 5067]

DESPUES (GD=3, LD=3):
  Directorio: [B0_v, B0_n, B1, B2, B0_v, B0_n, B3, B2]
```

---

## 7. Regla de redistribucion de todos los registros

**Regla fundamental:** Cuando un bucket se divide, **no solo se acomoda el nuevo registro** que causo el desbordamiento. Se deben **re-evaluar los hashes de TODOS los registros** que estaban en el bucket original.

**Por que?** Porque al incrementar la LD, estamos usando un bit adicional del hash. Registros que antes iban al mismo bucket ahora pueden ir a bucket diferentes.

**Proceso:**
1. Obtener todos los registros del bucket lleno.
2. Agregar el nuevo registro que causa el overflow.
3. Para cada registro, calcular el bit en la posicion LD (LD nueva = LD anterior + 1).
4. Si el bit es 0, el registro va al bucket viejo.
5. Si el bit es 1, el registro va al bucket nuevo.

**Ejemplo concreto:**
```
Bucket A (LD=1): [1023, 2045, 3078, 4012]
Binarios de las cedulas (ultimos bits):
  1023 = ...0010  (bit 1 = 1)
  2045 = ...0001  (bit 1 = 0)
  3078 = ...1110  (bit 1 = 1)
  4012 = ...0100  (bit 1 = 0)
  5067 = ...0011  (bit 1 = 1)  [nuevo]

Split con LD=2:
  Bit LD=2 (posicion 1):
    1023: ...01 -> bit = 1 -> bucket nuevo
    2045: ...00 -> bit = 0 -> bucket viejo
    3078: ...11 -> bit = 1 -> bucket nuevo
    4012: ...01 -> bit = 1 -> bucket nuevo
    5067: ...01 -> bit = 1 -> bucket nuevo
```

---

## 8. Por que la busqueda es O(1) promedio

### Analisis de complejidad

La busqueda en hashing extendible consta de 4 pasos:

1. **Calcular hash**: O(1) — operacion aritmetica simple.
2. **Aplicar mascara**: O(1) — operacion bitwise: `hash & ((1 << GD) - 1)`.
3. **Acceder al directorio**: O(1) — acceso a arreglo por indice.
4. **Recorrer el bucket**: O(B) donde B es la capacidad del bucket (constante small).

**Complejidad total: O(1) + O(1) + O(1) + O(B) = O(B) = O(1)** ya que B es constante (tipicamente 4-8 registros).

### Por que no es O(n)

- No se recorre toda la coleccion de registros.
- No se recorren todos los buckets.
- Solo se accede a **exactamente 1 bucket**.
- Dentro de ese bucket, se buscan maximo B registros (constante).

### Comparacion visual

```
Busqueda secuencial O(n):
  [B0][B1][B2][B3]...[Bn]
  Recorrer todos los registros -> n comparaciones

Busqueda por hashing O(1):
  Directorio -> Posicion -> Bucket -> max B comparaciones
  No importa cuantos registros hay en total
```

---

## 9. Comparacion teorica con busqueda secuencial O(n)

| Aspecto | Hashing Extendible O(1) | Busqueda Secuencial O(n) |
|---|---|---|
| Complejidad promedio | O(1) | O(n) |
| Mejor caso | O(1) | O(1) |
| Peor caso | O(B) constante | O(n) |
| Requiere indice | Si (directorio) | No |
| Uso de memoria | Adicional (directorio) | Minimo |
| Insercion | O(1) amortizado | O(1) |
| Eliminacion | O(1) amortizado | O(n) |
| Escalabilidad | Excelente | Mala |

### Ejemplo numerico

Para **10,000 registros**:

| Metodo | Operaciones promedio | Tiempo estimado |
|---|---|---|
| Busqueda secuencial | ~5,000 comparaciones | ~50 ms |
| Hashing extendible | ~4 comparaciones (B=4) | ~0.004 ms |

**Diferencia: ~12,500 veces mas rapido** el hashing extendible.

### Cuando usar cada uno

- **Hashing extendible**: Cuando se necesita busqueda rapida por clave y los datos crecen dinamicamente.
- **Busqueda secuencial**: Para datasets pequenos, cuando no se necesita indice, o para verificacion exhaustiva.

---

## 10. Responsabilidades: Persona 1 vs Persona 2

### Persona 1: Motor de Hashing Dinamico (Core)

| Modulo | Archivo | Descripcion |
|---|---|---|
| Modelo de datos | `model/Usuario.java` | POJO con nombre, cedula (clave), correo |
| Bucket | `hashing/Bucket.java` | Capacidad maxima, profundidad local, lista interna |
| Hashing extendido | `hashing/ExtendibleHashing.java` | Directorio, GD, funcion hash, split, busqueda |

**Operaciones implementadas:**
- Insercion con split automatico
- Busqueda por cedula O(1)
- Impresion del estado del directorio
- Conteo de buckets unicos y registros totales

### Persona 2: Busqueda Secuencial, Benchmarking e Interfaz

| Modulo | Archivo | Descripcion |
|---|---|---|
| Busqueda secuencial | `search/BusquedaSecuencial.java` | Recorrido lineal O(n) en ArrayList |
| Benchmark | `app/Benchmark.java` | Medicion con System.nanoTime() |
| Generador de datos | `app/DataGenerator.java` | Generacion masiva de usuarios |
| Interfaz de consola | `app/Main.java` | Menu interactivo, integracion general |

**Operaciones a implementar:**
- Busqueda secuencial lineal
- Comparacion de tiempos (hashing vs secuencial)
- Generacion de 10,000+ registros de prueba
- Menu de consola interactivo

---

## 11. Explicacion del codigo implementado

### 11.1 `model/Usuario.java`

Clase POJO simple con tres atributos:
- `nombre` (String): Nombre del usuario.
- `cedula` (long): Clave principal unica.
- `correo` (String): Correo electronico.

Incluye constructor, getters, setters y `toString()` formateado.

### 11.2 `hashing/Bucket.java`

Cada bucket representa un contenedor de registros con:

```java
public class Bucket {
    private final int capacidadMaxima;    // B: limite de registros
    private int profundidadLocal;         // LD: bits que comparten los registros
    private final List<Usuario> usuarios; // Lista interna de registros

    public boolean tieneEspacio() {
        return usuarios.size() < capacidadMaxima;
    }

    public void insertar(Usuario usuario) {
        if (!tieneEspacio()) {
            throw new IllegalStateException("Bucket lleno. Se requiere split.");
        }
        usuarios.add(usuario);
    }
}
```

**Metodos clave:**
- `tieneEspacio()`: Verifica si el bucket no esta lleno.
- `insertar()`: Agrega un usuario (con validacion de capacidad).
- `getUsuarios()`: Retorna la lista para redistribucion durante el split.

### 11.3 `hashing/ExtendibleHashing.java`

Es el nucleo de la implementacion. Componentes principales:

#### Funcion hash

```java
private int hash(long clave) {
    long primo = 2654435761L;  // Primo grande para dispersion
    long h = clave * primo;
    return (int) ((h & 0x7FFFFFFF) % Integer.MAX_VALUE);
}
```

**Por que esta funcion?**
- Multiplicacion por primo grande: dispersa bien los valores de cedula.
- `& 0x7FFFFFFF`: Asegura valor positivo (limpia el bit de signo).
- `% Integer.MAX_VALUE`: Mantiene el valor en rango entero positivo.

#### Obtener posicion en el directorio

```java
private int obtenerPosicionDirectorio(long clave) {
    int h = hash(clave);
    int mascara = (1 << profundidadGlobal) - 1;
    return h & mascara;
}
```

**Explicacion:** La mascara extrae los ultimos GD bits del hash.

Ejemplo: Si GD = 3 y hash = 10110101, la mascara es 111 (binario).
`10110101 & 00000111 = 101` (posicion 5 en el directorio).

#### Insercion con validacion de duplicados

```java
public boolean insertar(Usuario usuario) {
    long cedula = usuario.getCedula();

    // Verificar duplicados en TODA la estructura
    if (existeCedula(cedula)) {
        return false;  // Cedula duplicada
    }

    int posicion = obtenerPosicionDirectorio(cedula);
    Bucket bucket = directorio.get(posicion);

    if (bucket.tieneEspacio()) {
        bucket.insertar(usuario);
        totalRegistros++;
        return true;
    }

    // Overflow: realizar split
    split(posicion, usuario);
    totalRegistros++;
    return true;
}
```

**Nota sobre duplicados:** El metodo `existeCedula()` recorre TODOS los buckets unicos del directorio para validar que la cedula no exista en ninguno. Esto es importante porque varias entradas del directorio pueden apuntar al mismo bucket.

#### Split: el corazon del algoritmo

```java
private void split(int posicionDirectorio, Usuario nuevoUsuario) {
    Bucket bucketLleno = directorio.get(posicionDirectorio);
    int ldAnterior = bucketLleno.getProfundidadLocal();

    // Si LD == GD: duplicar directorio primero
    if (ldAnterior == profundidadGlobal) {
        duplicarDirectorio();
        posicionDirectorio = obtenerPosicionDirectorio(nuevoUsuario.getCedula());
    }

    // Crear bucket viejo y nuevo con LD + 1
    int nuevaLD = ldAnterior + 1;
    Bucket bucketViejo = new Bucket(capacidadBucket, nuevaLD);
    Bucket bucketNuevo = new Bucket(capacidadBucket, nuevaLD);

    // Redistribuir TODOS los registros + el nuevo
    List<Usuario> todosLosRegistros = new ArrayList<>(bucketLleno.getUsuarios());
    todosLosRegistros.add(nuevoUsuario);

    for (Usuario u : todosLosRegistros) {
        int bit = obtenerBitAdicional(u.getCedula(), nuevaLD);
        if (bit == 0) {
            bucketViejo.insertar(u);
        } else {
            bucketNuevo.insertar(u);
        }
    }

    // Actualizar el directorio
    actualizarDirectorio(posicionDirectorio, bucketViejo, bucketNuevo, ldAnterior);
}
```

#### Duplicacion del directorio

```java
private void duplicarDirectorio() {
    int tamanoAnterior = directorio.size();
    int nuevoTamano = tamanoAnterior * 2;

    List<Bucket> nuevoDirectorio = new ArrayList<>(nuevoTamano);
    for (int i = 0; i < tamanoAnterior; i++) {
        Bucket b = directorio.get(i);
        nuevoDirectorio.add(b);  // Entrada 2i
        nuevoDirectorio.add(b);  // Entrada 2i+1
    }

    directorio = nuevoDirectorio;
    profundidadGlobal++;
}
```

**Explicacion:** Cada entrada se duplica porque ahora se usa un bit mas del hash. Las entradas 2i y 2i+1 apuntan al mismo bucket porque el bit adicional aun no ha sido diferenciado.

#### Busqueda O(1)

```java
public Usuario buscar(long cedula) {
    int posicion = obtenerPosicionDirectorio(cedula);
    Bucket bucket = directorio.get(posicion);

    for (Usuario u : bucket.getUsuarios()) {
        if (u.getCedula() == cedula) {
            return u;
        }
    }
    return null;
}
```

**Por que es O(1)?**
- `obtenerPosicionDirectorio()`: O(1) — mascara bitwise.
- Acceso al bucket: O(1) — indice de arreglo.
- Busqueda en el bucket: O(B) constante (maximo 4-8 registros).

### 11.4 `app/Main.java`

Demonstracion estructurada de las operaciones:

1. **Insercion basica**: Llena el bucket inicial.
2. **Primer split**: Insertar el 5to registro causa overflow.
3. **Mas inserciones**: Provoca splits adicionales.
4. **Busqueda por hashing**: Demuestra O(1).
5. **Rechazo de duplicados**: Valida unicidad.
6. **Insercion masiva**: 20 registros para ver crecimiento.
7. **Resumen final**: Estadisticas de la estructura.

---

## 12. Materiales de referencia del profesor

### 12.1 Presentacion: "9Hashing Dinamico Extendible - Modificado.pptx"

**Por que se uso:** Es la referencia principal del algoritmo. Contiene:
- Explicacion visual del directorio y buckets.
- Paso a paso de insercion con ejemplos concretos.
- Los dos casos de split (sin y con duplicacion).
- La regla fundamental de redistribucion.
- Ejemplo concreto con las cedulas: 1023, 2045, 3078, 4012, 5067, 6089.

### 12.2 Documento: "Laboratorio__1_-_Hashing.md"

**Por que se uso:** Define los requisitos funcionales del laboratorio:
- Estructura de datos: nombre, cedula, correo.
- Restriccion: no duplicados.
- Comparacion de tiempos: hashing vs secuencial.
- Rubrica de evaluacion (60% codigo, 25% exposicion, 15% tiempos).

### 12.3 Estructura del proyecto: "Estructura.txt"

**Por que se uso:** Define la division de responsabilidades:
- Persona 1: hashing/Bucket.java + hashing/ExtendibleHashing.java
- Persona 2: search/BusquedaSecuencial.java + app/Benchmark.java + app/DataGenerator.java
- Modelo compartido: model/Usuario.java

### 12.4 Codigo de referencia: "LabHash.java"

**Por que se uso:** Ejemplo de hashing estatico con archivos (RandomAccessFile). Sirve como referencia de:
- Estructura de archivos (users.dat, index.dat).
- Medicion de tiempos con System.nanoTime().
- Comparacion de busqueda con y sin indice.

---

## 13. Resultados esperados y que demostrar en la exposicion

### 13.1 Criterios de evaluacion

| Criterio | Porcentaje | Que demostrar |
|---|---|---|
| Implementacion de Extendible Hashing | 30% | Split funcional, directorio dinamico |
| Validacion de datos (no duplicados) | 10% | Rechazo de cedula existente |
| Implementacion de busqueda secuencial | 10% | Recorrido lineal O(n) |
| Medicion y comparacion de tiempos | 15% | Diferencia numerica medible |
| Claridad del codigo | 10% | Comentarios, nombres descriptivos |
| Exposicion del laboratorio | 25% | Explicacion clara del algoritmo |

### 13.2 Que demostrar en la exposicion

1. **Estado inicial**: Directorio con 1 bucket, GD=0, LD=0.
2. **Llenado del bucket**: Insertar 4 registros (capacidad B=4).
3. **Primer split**: Insertar el 5to registro, mostrar como se divide el bucket.
4. **Impresion del directorio**: Mostrar las entradas y como apuntan a buckets diferentes.
5. **Busqueda O(1)**: Buscar un registro y medir el tiempo.
6. **Rechazo de duplicados**: Intentar insertar una cedula existente.
7. **Crecimiento dinamico**: Insertar muchos registros y mostrar como crece el directorio.
8. **Comparacion de tiempos**: hashing O(1) vs secuencial O(n) con medicion real.

### 13.3 Datos de prueba sugeridos

Para evidenciar la diferencia de rendimiento, usar al menos **10,000 registros**:
- Cedulas unicas desde 1000 hasta 11000.
- Nombres generados automaticamente.
- Medicion de tiempos con System.nanoTime().

---

## 14. Posibles preguntas del profesor y respuestas sugeridas

### Pregunta 1: "Que pasa si la profundidad local es mayor que la global?"

**Respuesta:** Eso no puede ocurrir en un hashing extendible correctamente implementado. La regla fundamental es que siempre LD <= GD. Si un bucket tiene LD = GD y se llena, primero se duplica el directorio (GD = GD + 1), y luego se divide el bucket (LD = LD + 1). De esta forma, despues del split, LD sigue siendo <= GD.

### Pregunta 2: "Por que redistribuir TODOS los registros y no solo el nuevo?"

**Respuesta:** Porque al incrementar la profundidad local, estamos usando un bit adicional del hash. Registros que antes iban al mismo bucket ahora pueden ir a bucket diferentes. Si solo movieramos el nuevo registro, los registros existentes quedarian mal ubicados y la busqueda fallaria.

### Pregunta 3: "Cuando se duplica el directorio y cuando no?"

**Respuesta:** Se duplica el directorio cuando LD = GD (el bucket ya usa todos los bits disponibles). No se duplica cuando LD < GD (el bucket comparte menos bits que el directorio, por lo que varias entradas apuntan al mismo bucket y se pueden reasignar sin duplicar).

### Pregunta 4: "Cual es la complejidad de la insercion?"

**Respuesta:** La insercion es O(1) amortizado. El split puede tomar O(B) tiempo (redistribuir B registros), pero ocurre una vez cada B inserciones en promedio, por lo que la amortizacion da O(1).

### Pregunta 5: "Que ventaja tiene sobre el hashing estatico?"

**Respuesta:** La ventaja principal es que no requiere reconstruccion completa (rehash) cuando los datos crecen. En hashing estatico, si se llenan todos los buckets, hay que crear una nueva tabla con mas buckets y reubicar todos los registros (O(n)). En hashing extendible, solo se divide un bucket y se actualizan algunas entradas del directorio.

### Pregunta 6: "Que es la mascara de bits y como funciona?"

**Respuesta:** La mascara es un patron de bits que se usa para extraer los ultimos GD bits de un hash. Se calcula como `(1 << GD) - 1`. Por ejemplo, si GD = 3, la mascara es 111 (binario) = 7 (decimal). Si el hash es 10110101, la operacion AND con la mascara da 101 = 5, que es la posicion en el directorio.

### Pregunta 7: "Por que multiplicar por un primo en la funcion hash?"

**Respuesta:** Los numeros primos tienen buena dispersion cuando se usan en multiplicacion. Esto significa que cedulas cercanas (como 1023 y 1024) producen hashes muy diferentes, lo que reduce las colisiones. El primo 2654435761 es comunmente usado en hashing por su buena dispersion y porque es primo de Mersenne.

### Pregunta 8: "Cuantos bits se necesitan para representar una cedula colombiana?"

**Respuesta:** Las cedulas colombianas tienen hasta 10 digitos, que en binario son aproximadamente 34 bits. Nuestra funcion hash maneja esto multiplicando por un primo grande y tomando modulo para obtener un valor positivo en rango de entero.

### Pregunta 9: "Que es el overflow y como se maneja?"

**Respuesta:** El overflow ocurre cuando se intenta insertar un registro en un bucket que ya esta lleno (tiene B registros). Se maneja con un split: se divide el bucket lleno en dos buckets nuevos, se redistribuyen todos los registros usando el bit adicional, y se actualiza el directorio.

### Pregunta 10: "Que diferencia hay entre este laboratorio y el codigo del profesor (LabHash.java)?"

**Respuesta:** LabHash.java implementa hashing estatico con archivos (RandomAccessFile), donde el numero de buckets es fijo (100). Nuestra implementacion usa hashing dinamico extendible en memoria, donde el directorio y los buckets crecen automaticamente. La ventaja es que no necesitamos reconstruir la estructura cuando los datos crecen.

---

## 15. Recomendaciones para una buena sustentacion

### 15.1 Preparacion

1. **Entender el algoritmo a fondo**: No memorizar, sino comprender cada paso del split.
2. **Practicar la explicacion**: Explicar el algoritmo en voz alta, como si fuera a ensenar.
3. **Tener ejemplos concretos**: Preparar el ejemplo con las cedulas 1023, 2045, 3078, 4012, 5067.
4. **Conocer los materiales del profesor**: Saber que diapositivas, documentos y codigos se usaron.

### 15.2 Durante la exposicion

1. **Empezar con el estado inicial**: Mostrar GD=0, LD=0, un solo bucket.
2. **Mostrar el paso a paso**: Insertar registros uno por uno, mostrando como cambia el directorio.
3. **Explicar cada split**: Decir si es con o sin duplicacion y por que.
4. **Demostrar busqueda O(1)**: Buscar un registro y mostrar el tiempo.
5. **Demostrar rechazo de duplicados**: Intentar insertar una cedula existente.
6. **Comparar tiempos**: Mostrar la diferencia numerica entre hashing y secuencial.
7. **Mostrar el codigo**: Explicar las partes clave (hash, mascara, split, busqueda).

### 15.3 Errores comunes a evitar

1. **No explicar la mascara de bits**: Es fundamental para entender como se ubica un registro.
2. **Decir que se redistribuye "solo el nuevo registro"**: Siempre se redistribuyen TODOS los registros.
3. **Confundir GD y LD**: Son conceptos diferentes. GD es del directorio, LD es de cada bucket.
4. **No mencionar la restriccion LD <= GD**: Es la regla mas importante del hashing extendible.
5. **No demostrar el codigo**: La sustentacion debe incluir demostracion en vivo.

### 15.4 Estructura sugerida de la exposicion (10-15 minutos)

1. **Introduccion** (1 min): Que es hashing extendible y por que se usa.
2. **Componentes** (2 min): Directorio, buckets, GD, LD.
3. **Algoritmo paso a paso** (4 min): Insercion, overflow, split.
4. **Demostracion en vivo** (3 min): Insertar registros, mostrar splits, buscar.
5. **Comparacion de tiempos** (2 min): Hashing O(1) vs secuencial O(n).
6. **Conclusiones** (1 min): Ventajas y aprendizajes.
7. **Preguntas** (2-3 min): Responder preguntas del profesor.

### 15.5 Consejos finales

- **Mantener la calma**: Si no sabes una pregunta, decir "no estoy seguro, pero creo que..." y razonar.
- **Ser honesto**: Si hay partes que no se implementaron, decirlo claramente.
- **Mostrar dominio tecnico**: Usar terminologia correcta (overflow, split, redistribucion, mascara).
- **Conectar con la teoria**: Relacionar el codigo con lo visto en clase.
- **Cuidar el tiempo**: No extenderte en una parte, cubrir todos los puntos.

---

## Archivos del proyecto

| Archivo | Responsabilidad | Funcion |
|---|---|---|
| `src/model/Usuario.java` | Compartido | POJO con nombre, cedula, correo |
| `src/hashing/Bucket.java` | Persona 1 | Contenedor de registros con LD |
| `src/hashing/ExtendibleHashing.java` | Persona 1 | Directorio, GD, hash, split, busqueda |
| `src/app/Main.java` | Persona 2 (integrado) | Demostracion y menu de consola |

---

## Glosario de terminos

| Termino | Definicion |
|---|---|
| **Hashing** | Tecnica de acceso directo que usa una funcion hash para ubicar registros |
| **Bucket** | Contenedor de registros en una estructura de hashing |
| **Directorio** | Arreglo de punteros a buckets |
| **Global Depth (GD)** | Numero de bits que usa el directorio para indexar |
| **Local Depth (LD)** | Numero de bits que caracterizan a cada bucket |
| **Split** | Division de un bucket cuando se llena |
| **Overflow** | Condicion que ocurre cuando un bucket esta lleno y se intenta insertar |
| **Redistribucion** | Proceso de reubicar registros entre buckets despues de un split |
| **Mascara** | Patron de bits para extraer posiciones del hash |
| **Colision** | Cuando dos claves distintas producen la misma posicion en el directorio |
| **Amortizado** | Promedio de complejidad a largo plazo, no en una sola operacion |

---

*Guia preparada para la sustentacion del Laboratorio 1 de Estructura de Datos y Laboratorio.*
*Fecha: Semana del laboratorio.*
