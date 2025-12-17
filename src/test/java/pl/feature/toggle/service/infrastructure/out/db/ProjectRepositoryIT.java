package pl.feature.toggle.service.infrastructure.out.db;

import pl.feature.toggle.service.AbstractITTest;
import pl.feature.toggle.service.application.port.out.ProjectRepository;
import pl.feature.toggle.service.domain.project.ProjectSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectRepositoryIT extends AbstractITTest {

    @Autowired
    private ProjectRepository sut;

    @Test
    @DisplayName("Should save and then find by id")
    void test01() {
        // given
        var projectSnapshot = ProjectSnapshot.create();
        sut.save(projectSnapshot);

        // when
        var result = sut.findById(projectSnapshot.id());

        // then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(projectSnapshot);
    }

}