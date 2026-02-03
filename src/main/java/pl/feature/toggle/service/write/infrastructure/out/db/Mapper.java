package pl.feature.toggle.service.write.infrastructure.out.db;

import github.saqie.ftaas.jooq.tables.records.EnvironmentRefRecord;
import github.saqie.ftaas.jooq.tables.records.FeatureToggleRecord;
import github.saqie.ftaas.jooq.tables.records.ProjectRefRecord;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.EnvironmentStatus;

class Mapper {

    static FeatureToggle toDomain(FeatureToggleRecord record) {
        return new FeatureToggle(
                FeatureToggleId.create(record.getId()),
                EnvironmentId.create(record.getEnvironmentId()),
                ProjectId.create(record.getProjectId()),
                FeatureToggleName.create(record.getName()),
                FeatureToggleDescription.create(record.getDescription()),
                FeatureToggleValueBuilder.from(record.getCurrentValue(), record.getType()),
                FeatureToggleStatus.valueOf(record.getStatus()),
                CreatedAt.of(record.getCreatedAt()),
                UpdatedAt.of(record.getUpdatedAt())
        );
    }

    static FeatureToggleRecord toRecord(FeatureToggle featureToggle) {
        var featureToggleRecord = new FeatureToggleRecord();
        featureToggleRecord.setProjectId(featureToggle.projectId().uuid());
        featureToggleRecord.setEnvironmentId(featureToggle.environmentId().uuid());
        featureToggleRecord.setId(featureToggle.id().uuid());
        featureToggleRecord.setName(featureToggle.name().value());
        featureToggleRecord.setDescription(featureToggle.description().value());
        featureToggleRecord.setType(featureToggle.value().typeName());
        featureToggleRecord.setCurrentValue(featureToggle.value().asText());
        featureToggleRecord.setStatus(featureToggle.status().name());
        featureToggleRecord.setCreatedAt(featureToggle.createdAt().toLocalDateTime());
        featureToggleRecord.setUpdatedAt(featureToggle.updatedAt().toLocalDateTime());
        return featureToggleRecord;
    }

    static EnvironmentRef toDomain(EnvironmentRefRecord record) {
        return new EnvironmentRef(
                EnvironmentId.create(record.getId()),
                ProjectId.create(record.getProjectId()),
                EnvironmentStatus.valueOf(record.getStatus()));
    }

    static EnvironmentRefRecord toRecord(EnvironmentRef environmentRef) {
        return new EnvironmentRefRecord(
                environmentRef.environmentId().uuid(),
                environmentRef.status().name(),
                environmentRef.projectId().uuid());
    }

    static ProjectRef toDomain(ProjectRefRecord record) {
        return new ProjectRef(
                ProjectId.create(record.getId()),
                ProjectStatus.valueOf(record.getStatus()));
    }

    static ProjectRefRecord toRecord(ProjectRef projectRef) {
        return new ProjectRefRecord(
                projectRef.projectId().uuid(),
                projectRef.status().name());
    }
}
