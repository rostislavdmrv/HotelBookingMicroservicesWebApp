package com.tinqinacademy.myhotel.api.operations.retrivesreports;

import com.tinqinacademy.myhotel.api.models.output.VisitorReportOutput;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportOutput {
    private List<VisitorReportOutput> roomRenters;
}
