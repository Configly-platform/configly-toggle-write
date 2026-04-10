package com.configly.toggle.write.infrastructure.out.db;

import com.configly.jooq.tables.records.EnvironmentRefRecord;
import com.configly.jooq.tables.records.FeatureToggleRecord;
import com.configly.jooq.tables.records.ProjectRefRecord;
import com.configly.model.CreatedAt;
import com.configly.model.Revision;
import com.configly.model.UpdatedAt;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.model.project.ProjectId;
import com.configly.model.project.ProjectStatus;
import com.configly.value.toggle.FeatureToggleValueBuilder;
import com.configly.value.toggle.FeatureToggleValueType;
import com.configly.value.toggle.FeatureToggleValueSnapshot;
import com.configly.toggle.write.domain.featuretoggle.FeatureToggle;
import com.configly.toggle.write.domain.reference.EnvironmentRef;
import com.configly.toggle.write.domain.reference.ProjectRef;

class Mapper {

    static FeatureToggle toDomain(FeatureToggleRecord record) {
        return new FeatureToggle(
                FeatureToggleId.create(record.getId()),
                EnvironmentId.create(record.getEnvironmentId()),
                FeatureToggleName.create(record.getName()),
                FeatureToggleDescription.create(record.getDescription()),
                FeatureToggleValueBuilder.from(FeatureToggleValueSnapshot.of(record.getCurrentValue()), FeatureToggleValueType.fromString(record.getType())),
                FeatureToggleStatus.valueOf(record.getStatus()),
                CreatedAt.of(record.getCreatedAt()),
                UpdatedAt.of(record.getUpdatedAt()),
                Revision.from(record.getRevision())
        );
    }

    static FeatureToggleRecord toRecord(FeatureToggle featureToggle) {
        var featureToggleRecord = new FeatureToggleRecord();
        featureToggleRecord.setEnvironmentId(featureToggle.environmentId().uuid());
        featureToggleRecord.setId(featureToggle.id().uuid());
        featureToggleRecord.setName(featureToggle.name().value());
        featureToggleRecord.setDescription(featureToggle.description().value());
        featureToggleRecord.setType(featureToggle.value().typeName());
        featureToggleRecord.setCurrentValue(featureToggle.value().asText());
        featureToggleRecord.setStatus(featureToggle.status().name());
        featureToggleRecord.setCreatedAt(featureToggle.createdAt().toLocalDateTime());
        featureToggleRecord.setUpdatedAt(featureToggle.updatedAt().toLocalDateTime());
        featureToggleRecord.setRevision(featureToggle.revision().value());
        return featureToggleRecord;
    }

    static EnvironmentRef toReference(EnvironmentRefRecord record) {
        return new EnvironmentRef(
                EnvironmentId.create(record.getId()),
                ProjectId.create(record.getProjectId()),
                EnvironmentStatus.valueOf(record.getStatus()),
                Revision.from(record.getLastRevision()),
                record.getConsistent());
    }

    static ProjectRef toReference(ProjectRefRecord record) {
        return new ProjectRef(
                ProjectId.create(record.getId()),
                ProjectStatus.valueOf(record.getStatus()),
                Revision.from(record.getLastRevision()),
                record.getConsistent());
    }
}
