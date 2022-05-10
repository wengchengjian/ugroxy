package com.weng.ugroxy.proxycommon.repository;

import com.weng.ugroxy.proxycommon.entity.LogRecord;
import org.springframework.data.repository.CrudRepository;


/**
 * @Author 翁丞健
 * @Date 2022/4/26 23:33
 * @Version 1.0.0
 */
public interface LogRecordRepository extends CrudRepository<LogRecord, Long> {

}
