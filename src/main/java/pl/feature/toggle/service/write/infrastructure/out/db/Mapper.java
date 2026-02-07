package pl.feature.toggle.service.write.infrastructure.out.db;

import github.saqie.ftaas.jooq.tables.records.EnvironmentRefRecord;
import github.saqie.ftaas.jooq.tables.records.FeatureToggleRecord;
import github.saqie.ftaas.jooq.tables.records.ProjectRefRecord;
import pl.feature.toggle.service.model.CreatedAt;
import pl.feature.toggle.service.model.Revision;
import pl.feature.toggle.service.model.UpdatedAt;
import pl.feature.toggle.service.model.environment.EnvironmentId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleDescription;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.featuretoggle.FeatureToggleName;
import pl.feature.toggle.service.model.project.ProjectId;
import pl.feature.toggle.service.value.FeatureToggleValueBuilder;
import pl.feature.toggle.service.value.FeatureToggleValueType;
import pl.feature.toggle.service.value.raw.FeatureToggleRawValue;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.write.domain.featuretoggle.FeatureToggleStatus;
import pl.feature.toggle.service.write.domain.reference.EnvironmentRef;
import pl.feature.toggle.service.write.domain.reference.EnvironmentStatus;
import pl.feature.toggle.service.write.domain.reference.ProjectRef;
import pl.feature.toggle.service.write.domain.reference.ProjectStatus;

class Mapper {

    static FeatureToggle toDomain(FeatureToggleRecord record) {
        return new FeatureToggle(
                FeatureToggleId.create(record.getId()),
                EnvironmentId.create(record.getEnvironmentId()),
                FeatureToggleName.create(record.getName()),
                FeatureToggleDescription.create(record.getDescription()),
                FeatureToggleValueBuilder.from(FeatureToggleRawValue.of(record.getCurrentValue()), FeatureToggleValueType.fromString(record.getType())),
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
