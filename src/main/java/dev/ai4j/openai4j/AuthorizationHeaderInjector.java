package dev.ai4j.openai4j;

import java.util.Collections;

public class AuthorizationHeaderInjector extends GenericHeaderInjector {

    AuthorizationHeaderInjector(String apiKey) {
        super(Collections.singletonMap("Authorization", "Bearer " + apiKey));
    }
}
