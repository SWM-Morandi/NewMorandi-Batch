package kr.co.morandi_batch.baekjoonproblemcontent.reader;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BaekjoonProblemIdReaderConfig {

    private final EntityManagerFactory emf;
    @Bean
    JpaPagingItemReader<Long> baekjoonProblemIdReader() {
        return new JpaPagingItemReader<>() {{
            setEntityManagerFactory(emf);
            setQueryString("SELECT p.baekjoonProblemId FROM Problem p");
            setPageSize(50);
        }};
    }

}
