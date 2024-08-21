package kr.co.morandi_batch.updateBaekjoonProblem.reader.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemsResponse {
    private int count;
    private List<ProblemDTO> items;
}