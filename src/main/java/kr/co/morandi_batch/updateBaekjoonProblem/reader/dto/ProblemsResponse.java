package kr.co.morandi_batch.updateBaekjoonProblem.reader.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProblemsResponse {
    private int count;
    private List<ProblemDTO> items;

    @Builder
    private ProblemsResponse(int count, List<ProblemDTO> items) {
        this.count = count;
        this.items = items;
    }

    public static ProblemsResponse create(int count, List<ProblemDTO> items) {
        return new ProblemsResponse(count, items);
    }
}