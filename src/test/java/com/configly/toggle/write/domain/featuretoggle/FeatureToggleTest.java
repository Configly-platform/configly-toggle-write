package com.configly.toggle.write.domain.featuretoggle;

import org.junit.jupiter.api.Test;
import com.configly.model.Revision;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.environment.EnvironmentStatus;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.model.project.ProjectId;
import com.configly.value.toggle.FeatureToggleValueBuilder;
import com.configly.value.toggle.FeatureToggleValueSnapshot;
import com.configly.toggle.write.domain.featuretoggle.exception.CannotOperateOnArchivedFeatureToggleException;
import com.configly.toggle.write.domain.reference.EnvironmentRef;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static com.configly.toggle.write.builder.FakeCreateFeatureToggleCommandBuilder.fakeCreateFeatureToggleCommandBuilder;
import static com.configly.toggle.write.builder.FakeFeatureToggleBuilder.fakeFeatureToggleBuilder;

class FeatureToggleTest {

    @Test
    void should_create_active_toggle_with_initial_revision() {
        // given
        var envRef = EnvironmentRef.from(ProjectId.create(), EnvironmentId.create(), EnvironmentStatus.ACTIVE, Revision.from(1));
        var command = fakeCreateFeatureToggleCommandBuilder()
                .withEnvironmentId(envRef.environmentId())
                .build();

        // when
        var toggle = FeatureToggle.create(command, envRef);

        // then
        assertThat(toggle.status()).isEqualTo(FeatureToggleStatus.ACTIVE);
        assertThat(toggle.revision()).isEqualTo(Revision.initialRevision());
        assertThat(toggle.environmentId()).isEqualTo(envRef.environmentId());
        assertThat(toggle.id()).isNotNull();
        assertThat(toggle.createdAt()).isNotNull();
        assertThat(toggle.updatedAt()).isNotNull();
    }

    @Test
    void should_archive_active_toggle() {
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(2))
                .build();

        // when
        var result = toggle.archive();

        // then
        assertThat(result.wasUpdated()).isTrue();
        assertThat(result.featureToggle().status()).isEqualTo(FeatureToggleStatus.ARCHIVED);
        assertThat(result.featureToggle().revision()).isEqualTo(Revision.from(3));
        assertThat(result.changes()).anyMatch(c ->
                c.field() == FeatureToggleField.STATUS &&
                        c.before().equals(FeatureToggleStatus.ACTIVE) &&
                        c.after().equals(FeatureToggleStatus.ARCHIVED)
        );
    }

    @Test
    void should_active_archived_toggle(){
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ARCHIVED)
                .revision(Revision.from(2))
                .build();

        // when
        var result = toggle.restore();

        // then
        assertThat(result.wasUpdated()).isTrue();
        assertThat(result.featureToggle().status()).isEqualTo(FeatureToggleStatus.ACTIVE);
        assertThat(result.featureToggle().revision()).isEqualTo(Revision.from(3));
        assertThat(result.changes()).anyMatch(c ->
                c.field() == FeatureToggleField.STATUS &&
                        c.before().equals(FeatureToggleStatus.ARCHIVED) &&
                        c.after().equals(FeatureToggleStatus.ACTIVE)
        );
    }

    @Test
    void should_change_value_when_active() {
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(10))
                .value(FeatureToggleValueBuilder.bool(false))
                .build();

        // when
        var result = toggle.changeValue(new FeatureToggleValueSnapshot("TRUE"));

        // then
        assertThat(result.wasUpdated()).isTrue();
        assertThat(result.featureToggle().revision()).isEqualTo(Revision.from(11));
        assertThat(result.featureToggle().value().asText()).isEqualTo("TRUE");
        assertThat(result.changes()).anyMatch(c -> c.field() == FeatureToggleField.VALUE);
    }

    @Test
    void should_return_no_changes_when_change_value_to_same_value() {
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ACTIVE)
                .value(FeatureToggleValueBuilder.bool(true))
                .build();

        // when
        var result = toggle.changeValue(new FeatureToggleValueSnapshot("TRUE"));

        // then
        assertThat(result.wasUpdated()).isFalse();
        assertThat(result.featureToggle()).isEqualTo(toggle);
    }

    @Test
    void should_do_nothing_when_archiving_archived_toggle() {
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ARCHIVED)
                .build();

        // when
        var result = toggle.archive();

        // then
        assertThat(result.wasUpdated()).isFalse();
        assertThat(result.featureToggle()).isEqualTo(toggle);
    }

    @Test
    void should_do_nothing_when_activating_active_toggle() {
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ACTIVE)
                .build();

        // when
        var result = toggle.restore();

        // then
        assertThat(result.wasUpdated()).isFalse();
        assertThat(result.featureToggle()).isEqualTo(toggle);
    }

    @Test
    void should_throw_when_updating_archived_toggle() {
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ARCHIVED)
                .build();

        // when
        var ex = catchException(() -> toggle.update(FeatureToggleName.create("X"), FeatureToggleDescription.create("Y")));

        // then
        assertThat(ex).isInstanceOf(CannotOperateOnArchivedFeatureToggleException.class);
    }

    @Test
    void should_return_no_changes_when_update_has_same_name_and_description() {
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ACTIVE)
                .build();

        // when
        var result = toggle.update(toggle.name(), toggle.description());

        // then
        assertThat(result.wasUpdated()).isFalse();
        assertThat(result.featureToggle()).isEqualTo(toggle);
    }

    @Test
    void should_update_name_and_description_and_increment_revision() {
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ACTIVE)
                .revision(Revision.from(10))
                .build();

        var newName = FeatureToggleName.create("NEW_NAME");
        var newDesc = FeatureToggleDescription.create("NEW_DESC");

        // when
        var result = toggle.update(newName, newDesc);

        // then
        assertThat(result.wasUpdated()).isTrue();
        assertThat(result.featureToggle().name()).isEqualTo(newName);
        assertThat(result.featureToggle().description()).isEqualTo(newDesc);
        assertThat(result.featureToggle().revision()).isEqualTo(Revision.from(11));
        assertThat(result.changes()).anyMatch(c -> c.field() == FeatureToggleField.NAME);
        assertThat(result.changes()).anyMatch(c -> c.field() == FeatureToggleField.DESCRIPTION);
    }

    @Test
    void should_throw_when_changing_value_on_archived_toggle() {
        // given
        var toggle = fakeFeatureToggleBuilder()
                .status(FeatureToggleStatus.ARCHIVED)
                .build();

        // when
        var ex = catchException(() -> toggle.changeValue(new FeatureToggleValueSnapshot("TRUE")));

        // then
        assertThat(ex).isInstanceOf(CannotOperateOnArchivedFeatureToggleException.class);
    }

}