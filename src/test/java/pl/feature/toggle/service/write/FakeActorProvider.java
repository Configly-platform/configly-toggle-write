package pl.feature.toggle.service.write;

import pl.feature.toggle.service.web.actor.Actor;
import pl.feature.toggle.service.web.actor.ActorProvider;

public class FakeActorProvider implements ActorProvider {
    @Override
    public Actor current() {
        return Actor.system();
    }
}
