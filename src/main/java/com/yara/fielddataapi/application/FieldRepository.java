package com.yara.fielddataapi.application;

import com.yara.fielddataapi.application.domain.Field;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldRepository extends MongoRepository<Field, String> {
}
