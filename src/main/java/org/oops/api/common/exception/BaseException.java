package org.oops.api.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.oops.api.common.constant.ResultType;

@Getter
@NoArgsConstructor
public class BaseException extends RuntimeException {
    private String code;
    private String desc;
    private String extraMessage;

    public BaseException(ResultType resultType) {
        super(resultType.getCode());
        this.code = resultType.getCode();
        this.desc = resultType.getDesc();
    }

    public BaseException(ResultType resultType, String extraMessage) {
        super(resultType.getDesc() + " - " + extraMessage);
        this.code = resultType.getCode();
        this.desc = resultType.getDesc();
        this.extraMessage = extraMessage;
    }
}
