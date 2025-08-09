package com.bhaskarshashwath.Ziplink.model;



import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@NoArgsConstructor
@Data
public class ClickEventDTO {
    private LocalDate clickDate;
    private Long count;
}
