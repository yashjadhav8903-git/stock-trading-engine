package com.tradingEngine.stockTrade.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Trade_Audit_Logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String methodName;
    private String status;
    private String ipAddress;
    @Column(columnDefinition = "TEXT")
    private String message;

    private Long executionTime;
    private LocalDateTime timestamp;
}
