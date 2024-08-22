package kr.co.morandi_batch.newBaekjoonProblem.writer;

import kr.co.morandi_batch.domain.problem.Problem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;

@Configuration
@Slf4j
public class NewProblemWriter {

    @Bean
    public JdbcBatchItemWriter<Problem> problemWriter(DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Problem>()
                .itemSqlParameterSourceProvider(item -> {
                    MapSqlParameterSource paramSource = new MapSqlParameterSource();
                    paramSource.addValue("baekjoonProblemId", item.getBaekjoonProblemId());
                    paramSource.addValue("problemTier", item.getProblemTier().name());
                    paramSource.addValue("problemStatus", item.getProblemStatus().name());
                    paramSource.addValue("solvedCount", item.getSolvedCount());
                    return paramSource;
                })
                .sql("INSERT INTO problem (baekjoon_problem_id, problem_tier, problem_status, solved_count) VALUES (:baekjoonProblemId, :problemTier, :problemStatus, :solvedCount)")
                .dataSource(dataSource)
                .build();
    }



}


