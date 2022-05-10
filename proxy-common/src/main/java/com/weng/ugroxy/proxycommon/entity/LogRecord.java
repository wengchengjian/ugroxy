package com.weng.ugroxy.proxycommon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO 待完善日志记录实体类
 * @Author 翁丞健
 * @Date 2022/4/26 23:18
 * @Version 1.0.0
 */
@Table(name = "log_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "ip")
    private String ip;

    @Column(name = "log")
    private String log;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "create_date")
    @CreatedDate
    private LocalDateTime createDate;

    @Column(name = "last_modified_time")
    @LastModifiedDate
    private LocalDateTime lastModifiedTime;
}
