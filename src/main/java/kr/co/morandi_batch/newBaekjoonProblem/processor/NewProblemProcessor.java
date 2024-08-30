package kr.co.morandi_batch.newBaekjoonProblem.processor;

import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemDTO;
import kr.co.morandi_batch.domain.problem.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NewProblemProcessor implements ItemProcessor<ProblemDTO, Problem> {

    @Override
    public Problem process(ProblemDTO problemDTO) {
        log.debug("Processing problem with ID: {}", problemDTO.getProblemId());
        return problemDTO.toEntity();
    }
}
