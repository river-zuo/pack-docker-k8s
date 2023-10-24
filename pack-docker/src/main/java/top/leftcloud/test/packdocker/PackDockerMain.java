package top.leftcloud.test.packdocker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@SpringBootApplication
public class PackDockerMain {

    @GetMapping("/hello")
    public Map<String, Object> map(Map<String, String> params) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(params);
        map.put("res", "hello");
        return map;
    }

    public static void main(String[] args) {

        SpringApplication.run(PackDockerMain.class, args);
    }

}
