package org.example.backend.dto.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BlacklistDTO {
    @NotNull(message = "bookId不能为空")
    private Long bookId;

    /**
     * 推荐类型（为空表示全局）
     */
    private String recommendType;

    /**
     * 推荐位（为空表示全位置）
     */
    private String position;

    private String reason;

    /**
     * 0-禁用，1-启用
     */
    private Integer isEnabled;
}



