package org.oops.api.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.oops.api.common.exception.BaseException;

@Getter
@NoArgsConstructor
public class ResponseDTO<T> {
    private ResultObject result;

    private T data;

    @Builder(builderMethodName = "resultBuilder")
    public ResponseDTO(ResultObject result) { this.result = result; }

    @Builder(builderMethodName = "resultDataBuilder")
    public ResponseDTO(ResultObject result, T data) {
        this.result = result;
        this.data = data;
    }

    public static <T> ResponseDTO<T> ok() {
        return new ResponseDTO<>(ResultObject.getSuccess());
    }

    public static <T> ResponseDTO<T> ok(T data) {
        return new ResponseDTO<>(ResultObject.getSuccess(), data);
    }

    public ResponseDTO(BaseException ex) {
        this.result = new ResultObject(ex);
    }

    // 에러 응답 처리 (ResultObject를 통해 에러 메시지 관리)
    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>(ResultObject.getFailure(message));
    }
}
