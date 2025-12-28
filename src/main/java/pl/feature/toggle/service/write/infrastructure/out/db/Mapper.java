package pl.feature.toggle.service.write.infrastructure.out.db;

import github.saqie.ftaas.jooq.tables.records.EnvironmentSnapshotRecord;
import github.saqie.ftaas.jooq.tables.records.FeatureToggleRecord;
import github.saqie.ftaas.jooq.tables.records.ProjectSnapshotRecord;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleType;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueRecognizer;
import pl.feature.toggle.service.model.featuretoggle.value.FeatureToggleValueSpec;
import pl.feature.toggle.service.write.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.*;
import pl.feature.toggle.service.model.project.ProjectId;

class Mapper {

    static FeatureToggle toDomain(FeatureToggleRecord record) {
        var type = FeatureToggleType.valueOf(record.getType());
        return new FeatureToggle(
                FeatureToggleId.create(record.getId()),
                EnvironmentId.create(record.getEnvironmentId()),
                ProjectId.create(record.getProjectId()),
                FeatureToggleName.create(record.getName()),
                FeatureToggleDescription.create(record.getDescription()),
                type,
                FeatureToggleValueRecognizer.from(FeatureToggleValueSpec.create(record.getCurrentValue(), type)),
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
        featureToggleRecord.setType(featureToggle.type().name());
        featureToggleRecord.setCurrentValue(featureToggle.value().asText());
        featureToggleRecord.setCreatedAt(featureToggle.createdAt().toLocalDateTime());
        featureToggleRecord.setUpdatedAt(featureToggle.updatedAt().toLocalDateTime());
        return featureToggleRecord;
    }

    static EnvironmentSnapshot toDomain(EnvironmentSnapshotRecord record) {
        return new EnvironmentSnapshot(EnvironmentId.create(record.getId()), ProjectId.create(record.getProjectId()));
    }

    static EnvironmentSnapshotRecord toRecord(EnvironmentSnapshot environmentSnapshot) {
        return new EnvironmentSnapshotRecord(environmentSnapshot.id().uuid(), environmentSnapshot.projectId().uuid());
    }

    static ProjectSnapshot toDomain(ProjectSnapshotRecord record) {
        return new ProjectSnapshot(ProjectId.create(record.getId()));
    }

    static ProjectSnapshotRecord toRecord(ProjectSnapshot projectSnapshot) {
        return new ProjectSnapshotRecord(projectSnapshot.id().uuid());
    }
}
