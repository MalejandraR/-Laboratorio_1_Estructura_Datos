<!-- Slide number: 1 -->

![image.png](Picture4.jpg)
Hashing Dinámico Extendible
Directorios, buckets y crecimiento dinámico en disco

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 2 -->

Objetivos de aprendizaje

![image.png](Picture3.jpg)
Al finalizar la clase el estudiante podrá:
Explicar qué es Hashing Dinámico Extendible.

![image.png](Picture14.jpg)
Diferenciar Hashing Estático y Hashing Dinámico.

![image.png](Picture15.jpg)
Comprender el papel del directorio.

![image.png](Picture16.jpg)
Interpretar globalDepth y localDepth.

![image.png](Picture17.jpg)
Explicar cuándo se divide un bucket.

![image.png](Picture20.jpg)
Explicar cuándo se duplica el directorio.

![image.png](Picture21.jpg)
Realizar inserciones paso a paso.

![image.png](Picture22.jpg)
Ejecutar una búsqueda usando los bits del hash.

![image.png](Picture23.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 3 -->

¿Por qué no basta con Hashing Estático?
En Hashing Estático, el número de buckets es fijo. Si los datos crecen demasiado, algunos buckets pueden llenarse y generar cadenas largas o desbordamientos.
Esto afecta el rendimiento porque aumenta el número de accesos a disco.

![image.png](Picture1.jpg)
¿Cómo permitir que el índice crezca sin reconstruir toda la estructura desde cero?

![image.png](Picture2.jpg)

![image.png](Picture3.jpg)
Hashing Estático
Hashing Dinámico Extendible

![image.png](Picture10.jpg)

![image.png](Picture15.jpg)
Buckets fijos
Directorio dinámico

![image.png](Picture12.jpg)

![image.png](Picture17.jpg)
Posible desbordamiento
Buckets que se dividen cuando es necesario

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 4 -->

Concepto general de Extendible Hashing
Utiliza un directorio de punteros que actúa como capa intermedia entre el resultado del hash y los buckets físicos en disco.
No se utiliza todo el valor numérico del hash. Se utilizan los primeros o últimos bits de la representación binaria del hash para ubicar una entrada en el directorio.

![image.png](Picture1.jpg)

![image.png](Picture10.jpg)

![image.png](Picture2.jpg)

![image.png](Picture11.jpg)

![image.png](Picture3.jpg)

![image.png](Picture14.jpg)

![image.png](Picture12.jpg)

![image.png](Picture4.jpg)

![image.png](Picture13.jpg)

![image.png](Picture5.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 5 -->

El uso del Directorio
El directorio contiene entradas binarias.

![image.png](Picture14.jpg)
Cada entrada del directorio apunta a un bucket físico.

![image.png](Picture15.jpg)
El directorio puede crecer duplicando su tamaño.

![image.png](Picture16.jpg)
Normalmente es pequeño y puede cargarse en memoria RAM para evitar accesos constantes a disco.

![image.png](Picture17.jpg)
Función Hash(Key) → Bits Binarios
Ejemplo: 10110…

![image.png](Picture10.jpg)
Directorio usa N bits iniciales / finales

![image.png](Picture12.jpg)
PUNTERO A BLOQUE / BUCKET

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 6 -->

Componentes físicos en disco
La estructura se sostiene en tres archivos fundamentales:

![image.png](Picture1.jpg)

![image.png](Picture2.jpg)

![image.png](Picture3.jpg)

![image.png](Picture16.jpg)

![image.png](Picture18.jpg)

![image.png](Picture20.jpg)
directory.dat
buckets.dat
users.dat

![image.png](Picture4.jpg)

![image.png](Picture5.jpg)

![image.png](Picture6.jpg)
Almacena la matriz de punteros. Es pequeño y normalmente puede cargarse en memoria RAM o caché para ubicar rápidamente el bucket.
Contiene los buckets físicos en disco. Cada bucket almacena registros o referencias a registros. Es la estructura que crece dinámicamente.
Contiene los registros reales. Si no es clusterizada, los buckets guardan el recordOffset, mientras los datos viven en users.dat.

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 7 -->

Profundidad Global (Global Depth)
La Global Depth (GD) es un número entero asociado al directorio completo.

![image.png](Picture2.jpg)
Ejemplo: GD = 2
22 = 4 entradas
Indica cuántos bits del resultado de la función hash se están utilizando actualmente para indexar las entradas del directorio.

| BITS | PUNTERO |
| --- | --- |
| 00 | → Ptr a B1 |
| 01 | → Ptr a B2 |
| 10 | → Ptr a B3 |
| 11 | → Ptr a B4 |

A medida que la base de datos crece y requiere más punteros, la profundidad global aumenta.

![image.png](Picture1.jpg)

Entradas del directorio = 2GlobalDepth

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 8 -->

Profundidad Local (Local Depth)
La Local Depth (LD) es un número entero asociado a cada bucket individual.

![image.png](Picture2.jpg)
Ejemplo Visual
Indica cuántos bits del hash comparten los registros almacenados en ese bucket específico.

![image.png](Picture3.jpg)

Bucket A LD = 1

![image.png](Picture4.jpg)
Registros con hash que terminan en:

![image.png](Picture1.jpg)
Regla principal: Local Depth ≤ Global Depth
...0
Si LD = GD, el bucket es apuntado por exactamente una entrada del directorio.

![image.png](Picture21.jpg)
Si GD = 2 y LD = 1, entonces las entradas 00 y 10 apuntarán a este mismo bucket, porque ambas comparten el último bit 0.
Si LD < GD, el bucket puede ser apuntado por múltiples entradas del directorio.

![image.png](Picture22.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 9 -->

Relación entre Global Depth y Local Depth

![image.png](Picture1.jpg)

![image.png](Picture2.jpg)
Global Depth
Local Depth
Pertenece al directorio completo.
Pertenece a cada bucket.

![image.png](Picture17.jpg)

![image.png](Picture21.jpg)
Define cuántos bits se usan para indexar.
Define cuántos bits caracterizan a ese bucket.

![image.png](Picture18.jpg)

![image.png](Picture22.jpg)
Determina el tamaño del directorio.
Aumenta cuando el bucket se divide.

![image.png](Picture19.jpg)

![image.png](Picture23.jpg)
Si aumenta, el directorio se duplica.
Nunca puede ser mayor que Global Depth.

![image.png](Picture20.jpg)

![image.png](Picture24.jpg)

![image.png](Picture5.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 10 -->

Inserción de registros: Paso a paso

![image.png](Picture1.jpg)

![image.png](Picture2.jpg)

![image.png](Picture3.jpg)

![image.png](Picture4.jpg)

![image.png](Picture5.jpg)

![image.png](Picture6.jpg)

![image.png](Picture7.jpg)

![image.png](Picture8.jpg)
Calcular Hash
Ubicar Bucket
Verificar espacio
Manejar Overflow

![image.png](Picture19.jpg)

![image.png](Picture20.jpg)

![image.png](Picture21.jpg)
Se aplica la función hash a la clave del registro a insertar.
Se toman los últimos GD bits del hash para encontrar la entrada en el directorio.
Si hay espacio, se inserta y finaliza. Si está lleno, ocurre Overflow.
Se divide el bucket lleno (Split). El comportamiento depende de comparar la LD con la GD.

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 11 -->

Casos de Split
Existen dos casos principales cuando un bucket se llena y causa overflow:
Caso 1: Split sin duplicar
Caso 2: Split con duplicación

![image.png](Picture1.jpg)

![image.png](Picture2.jpg)
Condición: Local Depth < Global Depth
Condición: Local Depth = Global Depth
El bucket comparte menos bits que el directorio. Por eso, puede dividirse el bucket y actualizar algunos punteros del directorio sin duplicarlo.
El bucket lleno ya usa todos los bits disponibles. Para diferenciar los registros, se necesita un bit adicional. Por eso, el directorio debe duplicar su tamaño (GD+1).

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 12 -->

Regla fundamental de redistribución
Cuando un bucket se divide, no solo se acomoda el nuevo registro que causó el desbordamiento.
Se deben re-evaluar los hashes de TODOS los registros que estaban en el bucket original.
Luego se usa el bit adicional (LD + 1) para redistribuirlos entre el bucket viejo y el nuevo bucket.

![image.png](Picture1.jpg)
"El split redistribuye todos los registros del bucket, no solo el registro nuevo."

![image.png](Picture2.jpg)
Ejemplo: Si antes mirábamos 2 bits, por ejemplo , y dividimos el bucket 01, ahora miramos 3 bits. Algunos registros pueden quedarse en 001 y otros moverse a 101, según los últimos bits del hash.

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 13 -->

Caso 1: Split sin Duplicar Directorio

![image.png](Picture1.jpg)
Condición de overflow: Local Depth < Global Depth
Interpretación: El bucket comparte menos bits que el directorio, por lo tanto varias entradas del directorio apuntan a ese mismo bucket. Esto permite dividir el bucket y redistribuir registros SIN duplicar el directorio completo.
Antes del split, varias entradas del directorio apuntan al mismo bucket.

![image.png](Picture13.jpg)

![image.png](Picture2.jpg)
Después del split, algunas de esas entradas siguen apuntando al bucket viejo y otras apuntan al bucket nuevo.

![image.png](Picture14.jpg)
“El bucket original se divide en dos. La LD de ambos se incrementa en +1. Los punteros del directorio se actualizan para apuntar a los respectivos buckets.”
Ambos buckets incrementan su Local Depth en +1.

![image.png](Picture15.jpg)
La Global Depth no cambia.

![image.png](Picture16.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 14 -->

Caso 2: Split con Duplicación

![image.png](Picture1.jpg)
Condición de overflow: Local Depth = Global Depth
Interpretación: El bucket lleno ya usa todos los bits que actualmente maneja el directorio. Para distinguir mejor los registros se requiere un bit adicional.

![image.png](Picture2.jpg)

![image.png](Picture3.jpg)
Acciones:
El Directorio duplica su tamaño.

![image.png](Picture17.jpg)
GD = 2 (4 entradas)
GD incrementa en +1.

![image.png](Picture18.jpg)

![image.png](Picture10.jpg)
El bucket lleno se divide.

![image.png](Picture19.jpg)
GD = 3 (8 entradas)
LD incrementa en +1 para esos dos buckets.

![image.png](Picture20.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 15 -->

Ventajas de Extendible Hashing

![image.png](Picture1.jpg)

![image.png](Picture2.jpg)

![image.png](Picture3.jpg)

![image.png](Picture6.jpg)

![image.png](Picture9.jpg)

![image.png](Picture12.jpg)

![image.png](Picture15.jpg)

![image.png](Picture16.jpg)

![image.png](Picture17.jpg)
Crecimiento Dinámico Real
Acceso Rápido
Minimiza Overflow
El archivo crece a medida que se necesita, bucket por bucket, evitando el desperdicio masivo de disco preasignado.
Si el directorio está en memoria RAM, cualquier registro puede localizarse con exactamente 1 acceso a disco para leer el bucket correspondiente.
Al dividir buckets dinámicamente, se evita casi por completo la necesidad de largas cadenas de desbordamiento que degradan el rendimiento.

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 16 -->

Estado inicial del sistema

![](object2.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 17 -->

Inserción: 1023 -> Ana

![](object2.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 18 -->

Inserción: 1023 -> Pedro

![](object2.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 19 -->

Inserción: 1023 -> Luis

![](object2.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 20 -->

Inserción: 1023 -> María

![](object2.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 21 -->

Inserción: 1023 -> Sara

![](object2.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 22 -->

Inserción: 1023 -> Juan

![](object2.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 23 -->

Estado final y proceso de búsqueda

![](object2.jpg)

Ingeniería de Sistemas | Estructura de datos y laboratorio

<!-- Slide number: 24 -->

Conclusiones
Extendible Hashing permite crecimiento dinámico del índice.

![image.png](Picture15.jpg)
El directorio usa los últimos GD bits del hash.

![image.png](Picture16.jpg)
Cada bucket posee su propia Local Depth.

![image.png](Picture17.jpg)
Si LD = GD y hay overflow, se duplica el directorio.

![image.png](Picture18.jpg)
Si LD < GD y hay overflow, solo se divide el bucket.

![image.png](Picture19.jpg)
Después de un split, todos los registros del bucket se redistribuyen.

![image.png](Picture20.jpg)
El uso de directory.dat, buckets.dat y users.dat permite separar índice y datos.

![image.png](Picture21.jpg)
Esta técnica mejora el rendimiento y evita reconstrucciones completas frecuentes.

![image.png](Picture24.jpg)
“Hashing Dinámico Extendible combina acceso rápido con crecimiento controlado en disco.”

Ingeniería de Sistemas | Estructura de datos y laboratorio
