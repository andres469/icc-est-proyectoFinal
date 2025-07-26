package src.dao;

import java.util.List;
import src.models.AlgorithmResult;

public interface AlgorithmResultDAO {
    void saveResult(AlgorithmResult result);
    List<AlgorithmResult> loadResults();
}
