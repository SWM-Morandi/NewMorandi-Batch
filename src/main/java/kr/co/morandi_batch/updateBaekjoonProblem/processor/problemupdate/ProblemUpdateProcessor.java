package kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate;

import jakarta.annotation.PostConstruct;
import kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate.dto.ProblemInfoProcessorDTO;
import kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate.dto.response.BojProblemInfo;
import kr.co.morandi_batch.domain.algorithm.Algorithm;
import kr.co.morandi_batch.domain.algorithm.AlgorithmRepository;
import kr.co.morandi_batch.domain.problem.Problem;
import kr.co.morandi_batch.domain.problem.ProblemTier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProblemUpdateProcessor implements ItemProcessor<ProblemInfoProcessorDTO, List<Problem>> {
    private Map<Integer, Algorithm> algorithmMap;

    private final AlgorithmRepository algorithmRepository;

    @PostConstruct
    public void init() throws IOException {
        List<Algorithm> initialAlgorithms = algorithmRepository.findAll();

        this.algorithmMap = initialAlgorithms.stream()
                .collect(Collectors.toMap(Algorithm::getBojTagId, algorithm -> algorithm));
    }
    @Override
    public List<Problem> process(ProblemInfoProcessorDTO item) throws Exception {
        //Solved AC에서 가져온 정보를 가지고 BaekjoonProblem ID를 Key로 하고, BojProblemInfo를 Value로 하는 Map을 만들어서 반환
        final Map<Long, BojProblemInfo> updatedProblemInfo = item.getBojProblemInfoList().getItems().stream()
                .collect(Collectors.toMap(BojProblemInfo::getProblemId, problem -> problem));

        final List<Problem> problems = item.getProblems();

        return problems.stream().peek(problem -> {
            final Long problemId = problem.getBaekjoonProblemId();

            BojProblemInfo bojProblemInfo = updatedProblemInfo.get(problemId);

            problem.updateProblemTier(ProblemTier.of(bojProblemInfo.getLevel()));
            problem.updateSolvedCount(bojProblemInfo.getAcceptedUserCount());
            problem.updateProblemAlgorithm(bojProblemInfo.getTags().stream()
                                                .map(tag -> algorithmMap.get(tag.getBojTagId()))
                                                .toList());

        }).toList();
    }


}
