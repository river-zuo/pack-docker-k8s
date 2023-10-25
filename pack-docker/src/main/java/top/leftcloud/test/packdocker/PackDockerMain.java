package top.leftcloud.test.packdocker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
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
        log.info("main start");
        SpringApplication.run(PackDockerMain.class, args);
        log.info("main finish");
    }

}
