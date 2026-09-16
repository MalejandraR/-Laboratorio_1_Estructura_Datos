package app;

import hashing.ExtendibleHashing;
import model.Usuario;

/**
 * Main de demostracion para el Laboratorio de Hashing Dinamico Extendible.
 *
 * Demuestra las operaciones:
 * - Insercion de registros con crecimiento dinamico.
 * - Splits automaticos (con y sin duplicacion del directorio).
 * - Impresion del directorio en cada etapa critica.
 * - Busqueda por hashing O(1).
 * - Rechazo de duplicados.
 * - Medicion de tiempos de busqueda.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("==========================================================");
        System.out.println("   LABORATORIO: Hashing Dinamico Extendible");
        System.out.println("==========================================================\n");

        // 1. Crear estructura con capacidad de bucket = 4
        ExtendibleHashing hashing = new ExtendibleHashing(4);

        // 2. Insercion: Llenar el primer bucket (4 registros)
        System.out.println("--- PASO 1: Insertar 4 usuarios (llena el bucket inicial) ---\n");

        hashing.insertar(new Usuario("Ana",    1023, "ana@mail.com"));
        hashing.insertar(new Usuario("Pedro",  2045, "pedro@mail.com"));
        hashing.insertar(new Usuario("Luis",   3078, "luis@mail.com"));
        hashing.insertar(new Usuario("Maria",  4012, "maria@mail.com"));

        hashing.imprimirDirectorio();

        // 3. Primer split: Insertar el 5to registro causa overflow
        System.out.println("\n--- PASO 2: Insertar 5to usuario (causa PRIMER SPLIT) ---\n");

        hashing.insertar(new Usuario("Sara", 5067, "sara@mail.com"));

        hashing.imprimirDirectorio();

        // 4. Mas inserciones: Provocar splits adicionales
        System.out.println("\n--- PASO 3: Insertar mas usuarios para provocar mas splits ---\n");

        hashing.insertar(new Usuario("Juan",   6089, "juan@mail.com"));
        hashing.insertar(new Usuario("Carlos", 7123, "carlos@mail.com"));
        hashing.insertar(new Usuario("Laura",  8156, "laura@mail.com"));

        hashing.imprimirDirectorio();

        // 5. Busqueda por hashing: O(1) promedio
        System.out.println("--- PASO 4: Busqueda por Hashing (O(1) promedio) ---\n");

        long cedulaBuscada = 6089;
        System.out.println("Buscando cedula: " + cedulaBuscada);

        long startBusqueda = System.nanoTime();
        Usuario encontrado = hashing.buscar(cedulaBuscada);
        long endBusqueda = System.nanoTime();

        if (encontrado != null) {
            System.out.println("Usuario encontrado:");
            System.out.println(encontrado);
        } else {
            System.out.println("Usuario no encontrado");
        }
        System.out.println("Tiempo busqueda (Hashing): " + (endBusqueda - startBusqueda) + " ns\n");

        // 6. Rechazo de duplicados
        System.out.println("--- PASO 5: Rechazo de duplicados ---\n");

        boolean duplicado1 = hashing.insertar(new Usuario("Duplicado", 6089, "dup@mail.com"));
        System.out.println("Intentar insertar CC 6089 (ya existe): "
                + (duplicado1 ? "PERMITIDA (error)" : "RECHAZADA (correcto)"));

        boolean duplicado2 = hashing.insertar(new Usuario("Nuevo", 9999, "nuevo@mail.com"));
        System.out.println("Insertar CC 9999 (nuevo): "
                + (duplicado2 ? "PERMITIDA (correcto)" : "RECHAZADA (error)"));

        // 7. Insercion masiva: Provocar muchos splits para ver crecimiento
        System.out.println("\n--- PASO 6: Insercion masiva (20 usuarios) ---\n");

        for (int i = 0; i < 20; i++) {
            hashing.insertar(new Usuario("Usuario" + i, 10000 + i, "user" + i + "@mail.com"));
        }

        hashing.imprimirDirectorio();

        // 8. Busqueda de registro masivo
        System.out.println("--- PASO 7: Buscar un registro de la insercion masiva ---\n");

        cedulaBuscada = 10015;
        System.out.println("Buscando cedula: " + cedulaBuscada);

        startBusqueda = System.nanoTime();
        encontrado = hashing.buscar(cedulaBuscada);
        endBusqueda = System.nanoTime();

        if (encontrado != null) {
            System.out.println("Usuario encontrado:");
            System.out.println(encontrado);
        } else {
            System.out.println("Usuario no encontrado");
        }
        System.out.println("Tiempo busqueda (Hashing): " + (endBusqueda - startBusqueda) + " ns\n");

        // Resumen final
        System.out.println("==========================================================");
        System.out.println("   RESUMEN DE LA ESTRUCTURA");
        System.out.println("==========================================================");
        System.out.println("   Total registros insertados: " + hashing.getTotalRegistros());
        System.out.println("   Profundidad Global (GD): " + hashing.getProfundidadGlobal());
        System.out.println("   Tamano del directorio: " + hashing.getTamanoDirectorio() + " entradas");
        System.out.println("   Buckets unicos activos: " + hashing.contarBucketsUnicos());
        System.out.println("==========================================================\n");
    }
}
