
package com.example;

import com.example.model.Greeting;
import com.example.model.User;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class HelloHandler {

    @Autowired
    private Hello helloFunction;
    @Autowired
    private StringRedisTemplate template;

    @FunctionName("hello")
    public HttpResponseMessage execute(
            @HttpTrigger(
                    name = "request",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS
            )
            HttpRequestMessage<Optional<User>> request,
            ExecutionContext context) {

        User user = request.getBody()
                .filter(u -> u.getName() != null)
                .orElseGet(() ->
                        new User(request.getQueryParameters()
                                .getOrDefault("name", "world")
                        )
                );
        ValueOperations<String, String> ops = this.template.opsForValue();
        String key = "user";
        if(!this.template.hasKey(key)){
            ops.set(key, user.getName());
        }
        context.getLogger().info("User name before cache: " + user.getName());
        user.setName(ops.get(key));
        context.getLogger().info("User name after cache: " + user.getName());


        Greeting result = helloFunction.apply(user);

        return request
                .createResponseBuilder(HttpStatus.OK)
                .body(result)
                .header("Content-Type", "application/json")
                .build();
    }
}

