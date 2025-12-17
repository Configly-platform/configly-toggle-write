package pl.feature.toggle.service.infrastructure.out.db;

import pl.feature.toggle.service.AbstractITTest;
import pl.feature.toggle.service.application.port.out.EnvironmentRepository;
import pl.feature.toggle.service.domain.environment.EnvironmentSnapshot;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentRepositoryIT extends AbstractITTest {

    @Autowired
    private EnvironmentRepository sut;

    private ProjectSnapshot projectSnapshot;

    @BeforeEach
    void setUp() {
        projectSnapshot = createProject();
    }

    @Test
    @DisplayName("Should save and then find by id")
    void test01() {
        // given
        var environmentSnapshot = EnvironmentSnapshot.create(projectSnapshot.id());
        sut.save(environmentSnapshot);

        // when
        var result = sut.findById(environmentSnapshot.id());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(environmentSnapshot);
    }
}