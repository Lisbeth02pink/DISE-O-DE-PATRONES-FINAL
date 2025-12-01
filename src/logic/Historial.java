package logic;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Historial {

    private static final String FILE_PATH = "historial.txt";

    public static void guardarHistorial(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("Fecha: " + fecha + " - Puntuación: " + score);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar historial: " + e.getMessage());
        }
    }

    public static List<String> obtenerHistorial() {
        List<String> historial = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                historial.add(linea);
            }
        } catch (IOException e) {
            historial.add("No se pudo cargar historial.");
        }
        return historial;
    }

    public static void limpiarHistorial() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("historial.txt"))) {
            writer.write("");  
        } catch (IOException e) {
            System.err.println("No se pudo limpiar el historial: " + e.getMessage());
        }
    }

}
