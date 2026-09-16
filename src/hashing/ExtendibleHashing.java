package hashing;

import model.Usuario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementacion de Hashing Dinamico Extendible.
 *
 * Conceptos clave (vistos en clase):
 * - Profundidad Global (GD): numero de bits del hash que usa el directorio para indexar.
 *   Entradas del directorio = 2^GD.
 * - Profundidad Local (LD): numero de bits que caracterizan a cada bucket.
 *   Siempre LD <= GD.
 * - Directorio: arreglo de punteros a buckets. Tamano = 2^GD.
 * - Split: cuando un bucket se llena, se divide y se redistribuyen TODOS sus registros.
 *   - Caso 1 (LD < GD): split sin duplicar directorio.
 *   - Caso 2 (LD = GD): split CON duplicacion del directorio (GD + 1).
 *
 * Funcion hash con mascara: hash & ((1 << GD) - 1)
 * Esto extrae los ultimos GD bits del hash para ubicar la entrada en el directorio.
 */
public class ExtendibleHashing {

    /** Capacidad maxima de registros por bucket (B) */
    private final int capacidadBucket;

    /** Profundidad global: cuantos bits se usan para indexar el directorio */
    private int profundidadGlobal;

    /** Directorio: arreglo de punteros a buckets */
    private List<Bucket> directorio;

    /** Contador total de registros insertados */
    private int totalRegistros;

    /**
     * Constructor del hashing extendible.
     * @param capacidadBucket capacidad maxima de registros por bucket (B).
     */
    public ExtendibleHashing(int capacidadBucket) {
        this.capacidadBucket = capacidadBucket;
        this.profundidadGlobal = 0;
        this.directorio = new ArrayList<>();
        this.totalRegistros = 0;

        // Estado inicial: un solo bucket apuntado por una sola entrada
        Bucket bucketInicial = new Bucket(capacidadBucket, 0);
        directorio.add(bucketInicial);
    }

    /**
     * Funcion hash: convierte la cedula a un valor entero no negativo.
     * Usa multiplicacion por primo para dispersar mejor los valores
     * y luego toma modulo de un numero primo grande para evitar negativos.
     *
     * Para cedulas positivas (que es el caso normal), esta funcion
     * genera buena dispersion sin necesidad de Math.abs.
     */
    private int hash(long clave) {
        // Primo grande para buena dispersion
        long primo = 2654435761L;
        long h = clave * primo;
        // Asegurar valor positivo usando modulo con primo grande
        return (int) ((h & 0x7FFFFFFF) % Integer.MAX_VALUE);
    }

    /**
     * Calcula la posicion en el directorio usando la mascara de bits.
     * posicion = hash & ((1 << profundidadGlobal) - 1)
     *
     * Esto extrae los ultimos GD bits del hash.
     */
    private int obtenerPosicionDirectorio(long clave) {
        int h = hash(clave);
        int mascara = (1 << profundidadGlobal) - 1;
        return h & mascara;
    }

    /**
     * Calcula el bit adicional (en la posicion LD) para decidir
     * a que bucket va un registro durante el split.
     *
     * @param clave cedula del usuario
     * @param profundidad la profundidad (LD + 1) que se esta usando para el split
     * @return 0 o 1, indicando a que bucket va el registro
     */
    private int obtenerBitAdicional(long clave, int profundidad) {
        int h = hash(clave);
        int mascara = 1 << (profundidad - 1);
        return (h & mascara) >> (profundidad - 1);
    }

    /**
     * Verifica si una cedula ya existe en TODA la estructura de hashing.
     * Recorre cada bucket unico del directorio para validar duplicados
     * antes de permitir una insercion.
     *
     * @param cedula la cedula a buscar
     * @return true si la cedula ya existe en algun bucket
     */
    private boolean existeCedula(long cedula) {
        Set<Bucket> bucketsVisitados = new HashSet<>();
        for (Bucket bucket : directorio) {
            // Solo revisar cada bucket una vez (varios punteros pueden apuntar al mismo)
            if (bucketsVisitados.add(bucket)) {
                for (Usuario u : bucket.getUsuarios()) {
                    if (u.getCedula() == cedula) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Inserta un usuario en la estructura de Hashing Extendible.
     *
     * Algoritmo (visto en clase):
     * 1. Calcular hash de la cedula.
     * 2. Usar los ultimos GD bits para ubicar la entrada en el directorio.
     * 3. Obtener el bucket apuntado por esa entrada.
     * 4. Verificar que la cedula no exista en TODA la estructura (duplicados).
     * 5. Si tiene espacio: insertar directamente.
     * 6. Si esta lleno (overflow): realizar split.
     *    a. Si LD < GD: split sin duplicar directorio.
     *    b. Si LD = GD: duplicar directorio (GD + 1), luego split.
     * 7. Redistribuir TODOS los registros del bucket lleno usando el bit adicional.
     *
     * @param usuario el usuario a insertar
     * @return true si se inserto, false si la cedula ya existia (duplicada)
     */
    public boolean insertar(Usuario usuario) {
        long cedula = usuario.getCedula();

        // PASO 4: Verificar duplicados en TODA la estructura
        if (existeCedula(cedula)) {
            return false; // Cedula duplicada
        }

        int posicion = obtenerPosicionDirectorio(cedula);
        Bucket bucket = directorio.get(posicion);

        // Caso base: si el bucket tiene espacio, insertar directamente
        if (bucket.tieneEspacio()) {
            bucket.insertar(usuario);
            totalRegistros++;
            return true;
        }

        // Overflow: el bucket esta lleno, hay que hacer split
        split(posicion, usuario);
        totalRegistros++;
        return true;
    }

    /**
     * Realiza el split de un bucket que se ha llenado.
     *
     * Algoritmo fiel a la teoria vista en clase:
     * 1. Si LD == GD: duplicar el directorio primero (GD = GD + 1).
     * 2. Crear bucket viejo y bucket nuevo, ambos con LD = LD_anterior + 1.
     * 3. Redistribuir TODOS los registros del bucket lleno + el nuevo registro
     *    usando el bit en la posicion LD (el bit adicional).
     * 4. Actualizar el directorio: solo las entradas que realmente apuntaban
     *    al bucket original se redistribuyen entre viejo y nuevo.
     *
     * @param posicionDirectorio posicion en el directorio del bucket a dividir
     * @param nuevoUsuario el usuario que causa el overflow (se inserta despues del split)
     */
    private void split(int posicionDirectorio, Usuario nuevoUsuario) {
        Bucket bucketLleno = directorio.get(posicionDirectorio);
        int ldAnterior = bucketLleno.getProfundidadLocal();

        // PASO 1: Si LD == GD, duplicar directorio antes de split
        if (ldAnterior == profundidadGlobal) {
            duplicarDirectorio();
            // Recalcular posicion despues de duplicar el directorio
            posicionDirectorio = obtenerPosicionDirectorio(nuevoUsuario.getCedula());
        }

        // PASO 2: Crear bucket viejo y bucket nuevo con LD + 1
        int nuevaLD = ldAnterior + 1;
        Bucket bucketViejo = new Bucket(capacidadBucket, nuevaLD);
        Bucket bucketNuevo = new Bucket(capacidadBucket, nuevaLD);

        // PASO 3: Redistribuir TODOS los registros del bucket lleno + el nuevo registro
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

        // PASO 4: Actualizar el directorio
        // Solo las entradas que apuntaban al bucket original se redistribuyen
        actualizarDirectorio(posicionDirectorio, bucketViejo, bucketNuevo, ldAnterior);
    }

    /**
     * Actualiza los punteros del despues de un split.
     *
     * Antes del split, varias entradas del directorio podian apuntar al mismo bucket
     * (porque LD < GD). Despues del split, esas entradas se redistribuyen:
     * - Las que tienen el bit 0 en la posicion LD apuntan al bucket viejo.
     * - Las que tienen el bit 1 en la posicion LD apuntan al bucket nuevo.
     *
     * Entradas que NO apuntaban al bucket original no se modifican.
     *
     * @param posicionOriginal posicion del bucket que se dividio
     * @param bucketViejo bucket que recibe los registros con bit 0
     * @param bucketNuevo bucket que recibe los registros con bit 1
     * @param ldAnterior profundidad local del bucket antes del split
     */
    private void actualizarDirectorio(int posicionOriginal, Bucket bucketViejo,
                                       Bucket bucketNuevo, int ldAnterior) {
        // Las entradas del directorio que apuntaban al bucket original son aquellas
        // cuyos ultimos LD bits coinciden con los ultimos LD bits de posicionOriginal.
        //
        // Ejemplo: si LD=1 y posicionOriginal=0 (binario 00), entonces las entradas
        // 00, 10, 000, 100, etc. apuntaban al mismo bucket (todas las que terminan en 0).
        //
        // Despues del split, esas entradas se redistribuyen segun el bit en posicion LD:
        // - Bit LD = 0 -> bucket viejo
        // - Bit LD = 1 -> bucket nuevo

        int entradasDirectorio = directorio.size();
        int mascaraLD = (1 << ldAnterior) - 1;  // Mascara para extraer los ultimos LD bits

        for (int i = 0; i < entradasDirectorio; i++) {
            // Verificar si esta entrada apuntaba al bucket que se esta dividiendo
            if ((i & mascaraLD) == (posicionOriginal & mascaraLD)) {
                // Esta entrada apuntaba al bucket original
                // Determinar a que bucket nuevo apunta usando el bit en la posicion LD
                int bit = (i >> ldAnterior) & 1;
                if (bit == 0) {
                    directorio.set(i, bucketViejo);
                } else {
                    directorio.set(i, bucketNuevo);
                }
            }
        }
    }

    /**
     * Duplica el directorio: duplica su tamano y aumenta la profundidad global en +1.
     *
     * Cada entrada existente se duplica para que cada par de entradas
     * apunte al mismo bucket (porque ahora se usa un bit mas del hash).
     *
     * Ejemplo: si GD = 2 (4 entradas) y se duplica, GD = 3 (8 entradas).
     * La entrada 00 se convierte en 000 y 001, ambas apuntando al mismo bucket.
     */
    private void duplicarDirectorio() {
        int tamanoAnterior = directorio.size();
        int nuevoTamano = tamanoAnterior * 2;

        // Duplicar: cada entrada existente se copia
        // Entrada i se convierte en entradas 2i y 2i+1
        List<Bucket> nuevoDirectorio = new ArrayList<>(nuevoTamano);
        for (int i = 0; i < tamanoAnterior; i++) {
            Bucket b = directorio.get(i);
            nuevoDirectorio.add(b);       // Par 2i: mismo bucket
            nuevoDirectorio.add(b);       // Par 2i+1: mismo bucket
        }

        directorio = nuevoDirectorio;
        profundidadGlobal++;

        System.out.println("[SPLIT] Directorio duplicado: GD = " + profundidadGlobal
                + " | Entradas: " + directorio.size());
    }

    /**
     * Busca un usuario por su cedula usando el Hashing Extendible.
     *
     * Busqueda O(1) promedio:
     * 1. Calcular hash de la cedula.
     * 2. Usar mascara para obtener posicion en el directorio.
     * 3. Acceder al bucket apuntado.
     * 4. Recorrer el bucket (maximo B registros).
     *
     * @param cedula la cedula a buscar
     * @return el usuario encontrado, o null si no existe
     */
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

    /**
     * Retorna el numero total de buckets activos en el directorio.
     * Utile para diagnosticar el estado de la estructura.
     */
    public int contarBucketsUnicos() {
        java.util.Set<Bucket> bucketsUnicos = new java.util.HashSet<>(directorio);
        return bucketsUnicos.size();
    }

    /**
     * Retorna la profundidad global actual.
     */
    public int getProfundidadGlobal() {
        return profundidadGlobal;
    }

    /**
     * Retorna el tamano actual del directorio (numero de entradas).
     */
    public int getTamanoDirectorio() {
        return directorio.size();
    }

    /**
     * Retorna el total de registros insertados.
     */
    public int getTotalRegistros() {
        return totalRegistros;
    }

    /**
     * Retorna el bucket en una posicion especifica del directorio.
     * Para inspeccion y depuracion.
     */
    public Bucket getBucketEnPosicion(int posicion) {
        if (posicion < 0 || posicion >= directorio.size()) {
            return null;
        }
        return directorio.get(posicion);
    }

    /**
     * Imprime el estado actual del directorio: cada entrada y su bucket.
     * Util para visualizar como se redistribuyen los punteros.
     */
    public void imprimirDirectorio() {
        System.out.println("=== DIRECTORIO (GD=" + profundidadGlobal
                + ", entradas=" + directorio.size() + ") ===");

        java.util.Set<Bucket> bucketsVistos = new java.util.HashSet<>();
        int numBucket = 0;

        for (int i = 0; i < directorio.size(); i++) {
            Bucket b = directorio.get(i);
            String marca = bucketsVistos.add(b) ? "[Bucket " + numBucket++ + "]" : "         ";

            // Formatear la posicion como binario con ceros a la izquierda
            String bits;
            if (profundidadGlobal == 0) {
                bits = "0";
            } else {
                bits = String.format("%" + profundidadGlobal + "s",
                        Integer.toBinaryString(i)).replace(' ', '0');
            }
            System.out.printf("  %s %s -> %s%n", bits, marca, b);
        }

        System.out.println("  Total buckets unicos: " + bucketsVistos.size());
        System.out.println("  Total registros: " + totalRegistros);
        System.out.println("=========================================");
    }
}
