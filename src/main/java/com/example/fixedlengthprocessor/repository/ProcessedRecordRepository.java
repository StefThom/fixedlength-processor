package com.example.fixedlengthprocessor.repository;

import com.example.fixedlengthprocessor.domain.ProcessedRecord;
import org.springframework.data.repository.CrudRepository;

public interface ProcessedRecordRepository extends CrudRepository<ProcessedRecord, Long> {
}
