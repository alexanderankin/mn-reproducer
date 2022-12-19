package gallery.sharer.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/api")
public class ApiController {
    @Get
    String hello() {
        return "world";
    }
}
