package kr.co.morandi_batch.baekjoonproblemcontent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Subtask {

    private String title;
    private List<String> conditions; // 리스트나 단락 형태의 조건 정보
    private String tableConditionsHtml; // 테이블 형태의 조건 정보를 저장하는 HTML 문자열

    @Builder
    private Subtask(String title, List<String> conditions, String tableConditionsHtml) {
        this.title = title;
        this.conditions = conditions;
        this.tableConditionsHtml = tableConditionsHtml;
    }
}
