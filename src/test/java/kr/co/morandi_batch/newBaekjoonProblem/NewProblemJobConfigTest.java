package kr.co.morandi_batch.newBaekjoonProblem;

import kr.co.morandi_batch.batch.config.BatchTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBatchTest
@SpringBootTest(properties = { "spring.batch.job.enabled=false" })
class NewProblemJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private Job newBaekjoonProblemJob;

    @BeforeEach
    void setUp() {
        jobLauncherTestUtils.setJob(newBaekjoonProblemJob);
    }

    @Test
    public void testProblemJobConfig() throws Exception {
        JobExecution execution = jobLauncherTestUtils.launchJob();
        Assertions.assertEquals(BatchStatus.COMPLETED, execution.getStatus());
    }
}