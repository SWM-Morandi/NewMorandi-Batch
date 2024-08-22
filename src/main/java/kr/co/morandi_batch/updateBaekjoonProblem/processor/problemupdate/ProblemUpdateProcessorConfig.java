package kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate;

import kr.co.morandi_batch.domain.problem.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ProblemUpdateProcessorConfig {

    private final GetProblemUpdateInfoProcessor getProblemUpdateInfoProcessor;
    private final ProblemUpdateProcessor problemUpdateProcessor;
    @Bean
    CompositeItemProcessor<List<Problem>, List<Problem> > compositeItemProcessor() {
        return new CompositeItemProcessorBuilder<List<Problem>, List<Problem>>()
                .delegates(List.of(getProblemUpdateInfoProcessor, problemUpdateProcessor))
                .build();
    }

}
