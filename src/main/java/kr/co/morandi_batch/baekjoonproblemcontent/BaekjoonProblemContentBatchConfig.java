package kr.co.morandi_batch.baekjoonproblemcontent;

import kr.co.morandi_batch.baekjoonproblemcontent.dto.ProblemContent;
import kr.co.morandi_batch.baekjoonproblemcontent.processor.BaekjoonContentsProcessor;
import kr.co.morandi_batch.baekjoonproblemcontent.writer.BaekjoonProblemContentWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BaekjoonProblemContentBatchConfig {

    private final JpaPagingItemReader<Long> baekjoonProblemIdReader;

    private final BaekjoonContentsProcessor baekjoonContentsProcessor;

    private final BaekjoonProblemContentWriter baekjoonProblemContentWriter;

    @Bean
    Job getBaekjoonContentJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("baekjoonProblemContentsJob", jobRepository)
                .flow(getBaekjoonContentStep(jobRepository, transactionManager))
                .end()
                .build();
    }

    @Bean
    Step getBaekjoonContentStep(JobRepository jobRepository, PlatformTransactionManager transactionManger) {
        return new StepBuilder("baekjoonProblemContentsStep", jobRepository)
                .<Long, ProblemContent>chunk(50, transactionManger)
                .reader(baekjoonProblemIdReader)
                .processor(baekjoonContentsProcessor)
                .writer(baekjoonProblemContentWriter)
                .build();
    }
}
