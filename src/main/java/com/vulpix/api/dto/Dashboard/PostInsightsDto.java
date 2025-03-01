package com.vulpix.api.dto.Dashboard;

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
    private String name;
    private Long Likes;
    private Long Views;
    private Long Shares;
    private Long Saves;
    private Long Comments;
}
