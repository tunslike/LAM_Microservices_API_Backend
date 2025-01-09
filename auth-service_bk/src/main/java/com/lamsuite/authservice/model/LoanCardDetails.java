package com.lamsuite.authservice.model;


import com.lamsuite.authservice.model.Loan.LoanHistory;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoanCardDetails {
    private LoanDetails loanDetails;
    private List<LoanHistory> loanHistory;
}
