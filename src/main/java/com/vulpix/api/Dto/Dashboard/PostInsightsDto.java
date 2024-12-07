package com.vulpix.api.Dto.Dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostInsightsDto {
    private LocalDate day;
    private Integer weekDay;
    private Long totalLikes;
    private Long totalViews;
    private Long totalShares;
    private Long totalSaves;
    private Long totalComments;
}
