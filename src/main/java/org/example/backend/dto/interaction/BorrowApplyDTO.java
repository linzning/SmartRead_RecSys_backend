package org.example.backend.dto.interaction;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 借阅申请DTO（用户提交借阅申请时填写预计归还时间）
 */
@Data
public class BorrowApplyDTO {

    /**
     * 预计归还时间（前端传 yyyy-MM-dd HH:mm:ss 或 ISO8601）
     */
    @NotBlank(message = "预计归还时间不能为空")
    private String expectedReturnTime;
}



