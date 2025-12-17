package pl.feature.toggle.service.infrastructure.out.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.feature.toggle.service.AbstractITTest;
import pl.feature.toggle.service.application.port.out.FeatureToggleRepository;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.domain.featuretoggle.FeatureToggle;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;
import pl.feature.toggle.service.model.featuretoggle.*;

import static org.assertj.core.api.Assertions.assertThat;

class FeatureToggleRepositoryIT extends AbstractITTest {

    @Autowired
    private FeatureToggleRepository sut;
    private ProjectSnapshot projectSnapshot;
    private EnvironmentSnapshot environmentSnapshot;

    @BeforeEach
    void setUp() {
        projectSnapshot = createProject();
        environmentSnapshot = createEnvironment(projectSnapshot.id());
    }

    @Test
    @DisplayName("Should save and then find by id")
    void test01() {
        // given
        var featureToggle = create("TEST");
        sut.save(featureToggle);

        // when
        var result = sut.findById(featureToggle.id());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(featureToggle);
    }

    @Test
    @DisplayName("Should delete toggle by id")
    void test02() {
        // given
        var featureToggle = create("TEST");
        sut.save(featureToggle);

        // when
        sut.delete(featureToggle);

        // then
        assertThat(sut.findById(featureToggle.id())).isNotPresent();
    }

    @Test
    @DisplayName("Should return true when feature toggle exists by id")
    void test03() {
        // given
        var featureToggle = create("TEST");
        sut.save(featureToggle);

        // when
        var result = sut.exists(featureToggle.id());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when feature toggle doesn't exist by id")
    void test04() {
        // given && then
        var result = sut.exists(FeatureToggleId.create());

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when feature toggle doesn't exist by name")
    void test05() {
        // given && then
        var result = sut.exists(FeatureToggleName.create("XXX"));

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true when feature toggle exists by name")
    void test06() {
        // given
        var featureToggle = create("TEST");
        sut.save(featureToggle);

        // when
        var result = sut.exists(featureToggle.name());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should update feature toggle")
    void test07() {
        // given
        var featureToggle = create("TEST");
        sut.save(featureToggle);

        var updated = copy(featureToggle, "NEW_NAME");

        // when
        sut.update(updated);

        // then
        var result = sut.findById(featureToggle.id());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(updated);
    }

    private FeatureToggle create(String name) {
        return FeatureToggle.create(
                environmentSnapshot.id(),
                projectSnapshot.id(),
                FeatureToggleName.create(name),
                FeatureToggleDescription.empty(),
                FeatureToggleType.BOOLEAN,
                BooleanFeatureToggleValue.enabled()
        );
    }

    private FeatureToggle copy(FeatureToggle featureToggle, String newName) {
        return new FeatureToggle(
                featureToggle.id(),
                featureToggle.environmentId(),
                featureToggle.projectId(),
                FeatureToggleName.create(newName),
                featureToggle.description(),
                featureToggle.type(),
                featureToggle.value(),
                featureToggle.createdAt(),
                featureToggle.updatedAt()
        );
    }

}