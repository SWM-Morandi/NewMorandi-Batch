package kr.co.morandi_batch.batch.reader.dto;

import kr.co.morandi_batch.domain.problem.Problem;
import kr.co.morandi_batch.updateBaekjoonProblem.reader.dto.ProblemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.co.morandi_batch.domain.problem.ProblemTier.B5;
import static org.assertj.core.api.Assertions.assertThat;

class ProblemDTOTest {
    @DisplayName("ProblemDTO에서 Problem Entity로 변환할 수 있다.")
    @Test
    void toEntity() {
        // given
        ProblemDTO problemDTO = ProblemDTO.builder()
                .problemId(1L)
                .titleKo("titleKo")
                .level(1)
                .acceptedUserCount(100)
                .build();

        // when
        Problem entity = problemDTO.toEntity();

        // then
        assertThat(entity).isNotNull()
                .extracting("baekjoonProblemId", "problemTier", "solvedCount")
                .containsExactly(1L, B5, 100L);

    }

}