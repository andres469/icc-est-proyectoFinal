package src.models;

import java.util.ArrayList;
import java.util.List;

public class SolveResults {
    private List<AlgorithmResult> results;

    public SolveResults() {
        this.results = new ArrayList<>();
    }

    public void addResult(AlgorithmResult result) {
        results.add(result);
    }

    public List<AlgorithmResult> getResults() {
        return results;
    }
}
