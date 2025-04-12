package it.andrearaponi.gender.rules;

import it.andrearaponi.gender.dto.GenderClassificationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.kie.api.runtime.KieRuntimeBuilder;
import org.kie.api.runtime.KieSession;

@Singleton
public class DroolsGenderValidator {

    @Inject
    KieRuntimeBuilder runtimeBuilder;

    public GenderClassificationResponse process(GenderClassificationResponse input) {
        KieSession kieSession = runtimeBuilder.newKieSession("gender");
        kieSession.insert(input);
        kieSession.fireAllRules();
        return kieSession.getSingleInstanceOf(GenderClassificationResponse.class);
    }
}
