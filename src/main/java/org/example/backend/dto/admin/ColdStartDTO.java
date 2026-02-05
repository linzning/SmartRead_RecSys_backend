package org.example.backend.dto.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ColdStartDTO {
    @NotNull(message = "bookId不能为空")
    private Long bookId;

    private Integer initialExposure;

    /**
     * 0-1
     */
    private Double manualWeight;

    private String startTime;

    private String endTime;

    private Integer isEnabled;
}



