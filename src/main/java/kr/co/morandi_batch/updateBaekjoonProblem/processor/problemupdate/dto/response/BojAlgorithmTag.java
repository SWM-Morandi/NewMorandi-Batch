package kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString

public class BojAlgorithmTag {
    String key;
    String id;
    int bojTagId;

}
