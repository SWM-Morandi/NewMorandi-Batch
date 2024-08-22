package kr.co.morandi_batch.baekjoonproblemcontent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString

public class ProblemContent {

    private Long baekjoonProblemId;
    private String title;
    private String memoryLimit;
    private String timeLimit;
    private String description;
    private String input;
    private String output;
    private List<SampleData> samples;
    private String hint;
    private List<Subtask> subtasks;
    private String problemLimit;
    private String additionalTimeLimit;
    private String additionalJudgeInfo;


    @Builder
    private ProblemContent(Long baekjoonProblemId, String title, String memoryLimit, String timeLimit, String description, String input, String output, List<SampleData> samples, String hint, List<Subtask> subtasks, String problemLimit, String additionalTimeLimit, String additionalJudgeInfo) {
        this.baekjoonProblemId = baekjoonProblemId;
        this.title = title;
        this.memoryLimit = memoryLimit;
        this.timeLimit = timeLimit;
        this.description = description;
        this.input = input;
        this.output = output;
        this.samples = samples;
        this.hint = hint;
        this.subtasks = subtasks;
        this.problemLimit = problemLimit;
        this.additionalTimeLimit = additionalTimeLimit;
        this.additionalJudgeInfo = additionalJudgeInfo;
    }
}
