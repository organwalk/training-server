package com.training.learn.consumer.like;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class LikeState {
    public StateEnum getLikeState(Integer oldState, Integer state){
        if (Objects.nonNull(oldState) && !Objects.equals(oldState, state)) {
            if (oldState == 0 && state == 1) {
                return StateEnum.TO_LIKED;
            } else {
                return StateEnum.NOT_LIKED;
            }
        } else if (Objects.isNull(oldState) && state == 1) {
            return StateEnum.LIKED;
        } else {
            return StateEnum.NONE;
        }
    }
}
