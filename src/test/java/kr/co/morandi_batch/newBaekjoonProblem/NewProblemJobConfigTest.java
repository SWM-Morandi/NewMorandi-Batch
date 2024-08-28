package kr.co.morandi_batch.newBaekjoonProblem;

import kr.co.morandi_batch.domain.problem.Problem;
import kr.co.morandi_batch.domain.problem.ProblemRepository;
import kr.co.morandi_batch.domain.problem.ProblemTier;
import kr.co.morandi_batch.newBaekjoonProblem.processor.NewProblemProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(properties = { "spring.batch.job.enabled=false" })
class NewProblemJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job newBaekjoonProblemJob;

    @Autowired
    private ProblemRepository problemRepository;

    @SpyBean
    private NewProblemProcessor mockProcessor;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(newBaekjoonProblemJob);
    }

    @DisplayName("DB에 저장된 백준 문제 이후의 데이터가 저장되어야 한다.")
    @Test
    public void testProblemJobConfig() throws Exception {
        // given
        Problem problem = Problem.create(32000L, ProblemTier.B1, 2000L);
        problemRepository.save(problem);

        // when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        assertEquals(BatchStatus.COMPLETED, execution.getStatus());
        assertThat(problemRepository.findAll()).hasSizeGreaterThan(1);
    }

    @DisplayName("배치 중 에러가 발생하면 롤백되어야 한다.")
    @Test
    public void testRollbackOnError() throws Exception {
        // given
        Problem problem = Problem.create(32000L, ProblemTier.B1, 2000L);
        problemRepository.save(problem);

        // when
        doThrow(new RuntimeException("Test Exception")).when(mockProcessor).process(any());
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // then
        assertEquals(BatchStatus.FAILED, execution.getStatus());
        assertThat(problemRepository.findAll()).hasSize(1);  // 롤백되어 DB 상태가 초기 상태와 동일
    }
}