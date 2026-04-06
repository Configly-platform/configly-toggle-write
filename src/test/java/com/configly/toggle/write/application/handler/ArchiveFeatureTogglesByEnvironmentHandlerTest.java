package com.configly.toggle.write.application.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.configly.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.web.model.actor.Actor;
import com.configly.web.model.correlation.CorrelationId;
import com.configly.toggle.write.AbstractUnitTest;
import com.configly.toggle.write.application.port.in.ArchiveFeatureTogglesByEnvironmentUseCase;
import com.configly.toggle.write.application.port.in.command.ArchiveFeatureTogglesByEnvironmentCommand;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.configly.contracts.topic.KafkaTopic.FEATURE_TOGGLE;
import static com.configly.toggle.write.builder.FakeFeatureToggleBuilder.fakeFeatureToggleBuilder;

class ArchiveFeatureTogglesByEnvironmentHandlerTest extends AbstractUnitTest {

    private ArchiveFeatureTogglesByEnvironmentUseCase sut;

    @BeforeEach
    void setUp() {
        sut = FeatureToggleHandlerFacade.archiveFeatureTogglesByEnvironmentUseCase(
                featureToggleCommandRepositorySpy,
                featureToggleQueryRepositoryStub,
                outboxWriter
        );
    }

    @Test
    void should_archive_all_active_feature_toggles_for_environment() {
        // given
        var command = new ArchiveFeatureTogglesByEnvironmentCommand(
                ARCHIVED_ENVIRONMENT_WITH_ARCHIVED_PROJECT.environmentId(),
                Actor.system(),
                CorrelationId.generate()
        );

        var firstToggle = fakeFeatureToggleBuilder()
                .environmentId(ARCHIVED_ENVIRONMENT_WITH_ARCHIVED_PROJECT.environmentId())
                .status(FeatureToggleStatus.ACTIVE)
                .build();

        var secondToggle = fakeFeatureToggleBuilder()
                .environmentId(ARCHIVED_ENVIRONMENT_WITH_ARCHIVED_PROJECT.environmentId())
                .status(FeatureToggleStatus.ACTIVE)
                .build();

        featureToggleQueryRepositoryStub.findByEnvironmentIdReturns(List.of(firstToggle, secondToggle));

        // when
        sut.handle(command);

        // then
        assertThat(featureToggleCommandRepositorySpy.allUpdated()).hasSize(2);
        assertThat(featureToggleCommandRepositorySpy.allUpdated())
                .allSatisfy(updateResult -> {
                    assertThat(updateResult.featureToggle().isArchived()).isTrue();
                    assertThat(updateResult.featureToggle().isActive()).isFalse();
                });

        assertHasEventCountOfType(FEATURE_TOGGLE.topicName(), FeatureToggleStatusChanged.class, 2);
    }

    @Test
    void should_skip_feature_toggle_when_already_archived() {
        // given
        featureToggleCommandRepositorySpy.expectNoCalls();
        var command = new ArchiveFeatureTogglesByEnvironmentCommand(
                ARCHIVED_ENVIRONMENT_WITH_ARCHIVED_PROJECT.environmentId(),
                Actor.system(),
                CorrelationId.generate()
        );

        var archivedToggle = fakeFeatureToggleBuilder()
                .environmentId(ARCHIVED_ENVIRONMENT_WITH_ARCHIVED_PROJECT.environmentId())
                .status(FeatureToggleStatus.ARCHIVED)
                .build();

        featureToggleQueryRepositoryStub.findByEnvironmentIdReturns(List.of(archivedToggle));

        // when
        sut.handle(command);

        // then
        assertDoesNotContainEventOfType(FEATURE_TOGGLE.topicName(), FeatureToggleStatusChanged.class);
    }

    @Test
    void should_archive_only_feature_toggles_that_require_update() {
        // given
        var command = new ArchiveFeatureTogglesByEnvironmentCommand(
                ARCHIVED_ENVIRONMENT_WITH_ARCHIVED_PROJECT.environmentId(),
                Actor.system(),
                CorrelationId.generate()
        );

        var activeToggle = fakeFeatureToggleBuilder()
                .environmentId(ARCHIVED_ENVIRONMENT_WITH_ARCHIVED_PROJECT.environmentId())
                .status(FeatureToggleStatus.ACTIVE)
                .build();

        var archivedToggle = fakeFeatureToggleBuilder()
                .environmentId(ARCHIVED_ENVIRONMENT_WITH_ARCHIVED_PROJECT.environmentId())
                .status(FeatureToggleStatus.ARCHIVED)
                .build();

        featureToggleQueryRepositoryStub.findByEnvironmentIdReturns(List.of(activeToggle, archivedToggle));

        // when
        sut.handle(command);

        // then
        assertThat(featureToggleCommandRepositorySpy.allUpdated()).hasSize(1);

        var updated = featureToggleCommandRepositorySpy.lastUpdated();
        assertThat(updated.featureToggle().isArchived()).isTrue();
        assertThat(updated.featureToggle().isActive()).isFalse();

        assertHasEventCountOfType(FEATURE_TOGGLE.topicName(), FeatureToggleStatusChanged.class, 1);
    }

}