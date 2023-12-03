package top.leftcloud.test.packdocker.crypt;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

@Slf4j
@RequestMapping("/api2")
@RestController
public class CryptController {

    Function<String, String> function = AesEncryption::processCrypt;

    @PostMapping("/decrypt")
    public Map<String, Object> decrypt(@RequestBody Map<String, String> body) {
        String crypt = body.get("crypt");
        String apply = function.apply(crypt);
        TreeMap<String, Object> treeMap = Maps.<String, Object>newTreeMap();
        treeMap.put("result", apply);
        log.info("param:{}, result:{}", body, treeMap);
        return treeMap;
    }

}
