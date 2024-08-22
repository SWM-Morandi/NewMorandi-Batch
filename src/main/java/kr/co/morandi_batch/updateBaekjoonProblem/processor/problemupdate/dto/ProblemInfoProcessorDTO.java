package kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate.dto;

import kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate.dto.response.BojProblemInfoList;
import kr.co.morandi_batch.domain.problem.Problem;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemInfoProcessorDTO {
    List<Problem> problems;
    BojProblemInfoList bojProblemInfoList;
    @Builder
    private ProblemInfoProcessorDTO(List<Problem> problems, BojProblemInfoList bojProblemInfoList) {
        this.problems = problems;
        this.bojProblemInfoList = bojProblemInfoList;
    }
}
