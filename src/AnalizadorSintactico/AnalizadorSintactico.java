package AnalizadorSintactico;
import AnalizadorLexico.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;


public class AnalizadorSintactico {

    public static LinkedList<String> logs = new LinkedList<>();
    public static LinkedList<String> logsWarning = new LinkedList<>();
    public static LinkedList<String> logsErrores = new LinkedList<>();
    public static LinkedList<String> polaca = new LinkedList<>();

    public static void add(String s) {
        logs.add(s);
    }

    public static void addWarning(String s) {
        logsWarning.add(s);
    }

    public static void addErrores(String s) {
        logsErrores.add(s);
    }

    public static void addPolaca(String s){polaca.add(s);}
    public static Parser p = new Parser();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Uso: java Main <ruta/al/archivo.txt>");
            System.err.println("Ej:  java Main ~/Desktop/programa.txt");
            System.exit(1);
        }

        String arg = args[0];
        String contenido;
        try {
            if ("-".equals(arg)) {
                contenido = new String(System.in.readAllBytes());
            } else {
                Path ruta = Paths.get(arg);
                contenido = Files.readString(ruta);
            }
            // Agregar EOF
            contenido = contenido.replace("\r\n", "\n").replace('\r', '\n');
            contenido += "$";

            AnalizadorLexico.archivo = contenido;
            AnalizadorLexico.indiceArchivo = 0;
            AnalizadorLexico.numLinea = 1;

            if (p.yyparse() == 0){
                System.out.println("Parser finalizo");
            }else {
                System.out.println("Parser no finalizo");
            }

            for (Map.Entry<String, TDSObject> e : AnalizadorLexico.tablaDeSimbolos.entrySet()) {
                System.out.println(
                        "\nTDSObject:\n" +
                                "  Lexema: " + e.getKey() + "\n" +
                                "  Tipo: " + e.getValue().getTipoVariable() + "\n" +
                                "  Uso: " + e.getValue().getUso() + "\n" +
                                "  Retornos: " + e.getValue().getTiposRetorno() + "\n" +
                                "  Parámetros: " + e.getValue().getTiposParametros() + "\n" +
                                "  Modos de Parámetro: " + e.getValue().getSemanticaParametros()
                );
            }
            System.out.println("\n=== POLACA INVERSA ===");
            List<String> polaca = Parser.getPolaca();
            for (int i = 0; i < polaca.size(); i++)
                System.out.println((i + 1) + " " + polaca.get(i));

            for (String w : AnalizadorLexico.listaWarnings) {
                System.err.println("WARNING: " + w);
            }
            for (String e : AnalizadorLexico.listaErrores) {
                System.err.println("ERROR: " + e);
            }
            for (String aux : AnalizadorSintactico.logsWarning) {
                System.err.println(aux);
            }
            for (String aux : AnalizadorSintactico.logsErrores) {
                System.err.println(aux);
            }


            if (AnalizadorLexico.listaErrores.isEmpty() && AnalizadorSintactico.logsErrores.isEmpty()) {

                List<String> erroresGeneracion = new ArrayList<>();
                String rutaAsm = "C:\\Users\\Lucas\\Desktop\\salida\\salida.asm";

                GeneradorASM.generarCodigoASM(polaca, rutaAsm, erroresGeneracion);

                if (!erroresGeneracion.isEmpty()) {
                    for (String e : erroresGeneracion) {
                        System.err.println("Error en generación de código: " + e);
                    }
                } else {
                    System.out.println("Se generó el archivo assembler en: " + rutaAsm);
                }
            } else {
                System.err.println("No se generó código assembler debido a errores previos.");
            }

        } catch (IOException ex) {
            System.err.println("No se pudo leer el archivo: " + ex.getMessage());
            System.exit(2);
        } catch (Exception ex) {
            System.err.println("Error de ejecución: " + ex.getMessage());
            ex.printStackTrace();   
            System.exit(3);
        }
    }
}