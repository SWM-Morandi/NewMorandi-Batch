package kr.co.morandi_batch.baekjoonproblemcontent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class SampleData {

    private String input;
    private String output;
    private String explanation;

    @Builder
    private SampleData(String input, String output, String explanation) {
        this.input = input;
        this.output = output;
        this.explanation = explanation;
    }
}

