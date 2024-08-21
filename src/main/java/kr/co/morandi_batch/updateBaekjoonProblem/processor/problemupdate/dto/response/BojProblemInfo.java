package kr.co.morandi_batch.updateBaekjoonProblem.processor.problemupdate.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class BojProblemInfo {
    private long problemId;
    private String titleKo;
//    private List<TitleDTO> titles;
//    private boolean isSolvable;
//    private boolean isPartial;
    private long acceptedUserCount;
    private int level;
//    private int votedUserCount;
//    private boolean sprout;
//    private boolean givesNoRating;
//    private boolean isLevelLocked;
//    private double averageTries;
//    private boolean official;
    private List<TagDTO> tags;

}
