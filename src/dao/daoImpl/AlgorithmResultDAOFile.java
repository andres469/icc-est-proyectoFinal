package src.dao.daoImpl;

import src.dao.AlgorithmResultDAO;
import src.models.AlgorithmResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmResultDAOFile implements AlgorithmResultDAO {
    private static final String FILE_NAME = "results.txt";

    @Override
    public void saveResult(AlgorithmResult result) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(result.getAlgorithmName() + "," + result.getPathLength() + "," + result.getExecutionTime());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving result: " + e.getMessage());
        }
    }

    @Override
    public List<AlgorithmResult> loadResults() {
        List<AlgorithmResult> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int path = Integer.parseInt(parts[1]);
                    long time = Long.parseLong(parts[2]);
                    results.add(new AlgorithmResult(name, path, time));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading results: " + e.getMessage());
        }

        return results;
    }
}
