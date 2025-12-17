package pl.feature.toggle.service.infrastructure.out.db;

import com.ftaas.domain.CreatedAt;
import com.ftaas.domain.UpdatedAt;
import com.ftaas.domain.environment.EnvironmentId;
import com.ftaas.domain.featuretoggle.*;
import com.ftaas.domain.project.ProjectId;
import github.saqie.ftaas.jooq.tables.records.EnvironmentSnapshotRecord;
import github.saqie.ftaas.jooq.tables.records.FeatureToggleRecord;
import github.saqie.ftaas.jooq.tables.records.ProjectSnapshotRecord;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;

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
                FeatureToggleValueRecognizer.from(type, record.getCurrentValue()),
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
        featureToggleRecord.setCurrentValue(featureToggle.value().stringValue());
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
