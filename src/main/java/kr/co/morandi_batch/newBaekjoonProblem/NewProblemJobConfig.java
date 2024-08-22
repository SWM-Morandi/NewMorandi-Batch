package kr.co.morandi_batch.newBaekjoonProblem;

import kr.co.morandi_batch.newBaekjoonProblem.processor.NewProblemProcessor;
import kr.co.morandi_batch.newBaekjoonProblem.reader.NewProblemPagingReader;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemDTO;
import kr.co.morandi_batch.domain.problem.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class NewProblemJobConfig {

    private final NewProblemPagingReader newProblemPagingReader;

    private final NewProblemProcessor newProblemProcessor;

    private final JdbcBatchItemWriter<Problem> newProblemWriter;

    @Bean
    Job newBaekjoonProblemJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("newBaekjoonProblemJob", jobRepository)
                .flow(newBaekjoonProblemStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    Step newBaekjoonProblemStep(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
        return new StepBuilder("newBaekjoonProblemStep", jobRepository)
                .<ProblemDTO, Problem>chunk(50, transactionManger)
//                .allowStartIfComplete(true)
                .reader(newProblemPagingReader)
                .processor(newProblemProcessor)
                .writer(newProblemWriter)
                .build();
    }
}
