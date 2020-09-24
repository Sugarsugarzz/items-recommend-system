package casia.isiteam.recommendsystem.algorithms;

import java.util.List;

public interface RecommendAlgorithm {

    void recommend(List<Long> userIDs, int infoType);
}
