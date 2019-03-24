package kr.co.gaia012.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${project.name}")
    private String projectName;

    @GetMapping("/")
    public String getProjectName() {
        return projectName;
    }
}
