package kr.co.morandi_batch.updateBaekjoonProblem;

import kr.co.morandi_batch.batch.pagingCollectionsItemReader.PagingCollectionsItemReader;
import kr.co.morandi_batch.domain.problem.Problem;
import kr.co.morandi_batch.updateBaekjoonProblem.writer.ProblemUpdateWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class UpdateBaekjoonProblemBatchConfig {

    private final PagingCollectionsItemReader<Problem, List<Problem>> problemPagingCollectionsItemReader;

    private final CompositeItemProcessor<List<Problem>, List<Problem>> compositeItemProcessor;
    private final ProblemUpdateWriter problemUpdateWriter;

    @Bean
    Job updateBaekjoonProblemJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("updateBaekjoonProblem", jobRepository)
                .flow(updateProblemStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    Step updateProblemStep(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
        return new StepBuilder("updateProblemStep", jobRepository)
                .<List<Problem>, List<Problem>>chunk(10, transactionManger)
//                .allowStartIfComplete(true)
                .reader(problemPagingCollectionsItemReader)
                .processor(compositeItemProcessor)
                .writer(problemUpdateWriter)
                .build();
    }

}
