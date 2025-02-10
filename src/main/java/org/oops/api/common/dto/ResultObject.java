package org.oops.api.common.dto;

import org.oops.api.common.constant.ResultType;
import org.oops.api.common.exception.BaseException;

public class ResultObject {
    public String code;

    public String desc;

    public ResultObject(ResultType resultType) {
        this.code = resultType.getCode();
        this.desc = resultType.getDesc();
    }

    public ResultObject(ResultType resultType, String message) {
        this.code = resultType.getCode();
        this.desc = message;
    }

    public ResultObject(BaseException e) {
        this.code = e.getCode();
        this.desc = e.getDesc();
    }

    public static ResultObject getSuccess() {
        return new ResultObject(ResultType.SUCCESS);
    }
    public static ResultObject getFailure(String message){
        return new ResultObject(ResultType.SYSTEM_ERROR);
    }

}
