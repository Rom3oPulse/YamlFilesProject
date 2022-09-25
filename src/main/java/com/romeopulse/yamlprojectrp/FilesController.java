package com.romeopulse.yamlprojectrp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.flipkart.zjsonpatch.JsonDiff;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController("")
public class FilesController {


    @PostMapping(value = "/files",consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // РАБОЧИЙ ВАРИАНТ ЗАПРОСА С КОММЕНТОМ И ФАЙЛОМ
    public String TestCompareFiles(@RequestParam("comment") String comment,
                                   @RequestParam("file")MultipartFile file){

        try (InputStream is = file.getInputStream()){
            return "Коммент:" + comment + " , Формат:" + file.getName()
                    + " Название файла: " + file.getOriginalFilename();
        } catch (IOException ex){
            throw new RuntimeException(ex);
        }
    }


    @PostMapping(value = "/yaml-files",consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // РАБОЧИЙ ВАРИАНТ С ДВУМЯ ФАЙЛАМИ YAML ЗАГРУЖЕННЫМИ В IDE
    public String CompareYamlFiles(@RequestParam("file01") MultipartFile file1,
                                   @RequestParam("file02")MultipartFile file2) {

        try (InputStream is = file1.getInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            JsonNode file01 = objectMapper.readTree(new File("src/main/resources/files/ingress-dc.yaml"));
            JsonNode file02 = objectMapper.readTree(new File("src/main/resources/files/ingress-dc_1.yaml"));
            JsonNode patch = JsonDiff.asJson(file01, file02);
            String diffs = patch.toString();
            return diffs;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    @PostMapping(value = "/yaml-filesP",consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // РАБОЧИЙ ВАРИАНТ С ДВУМЯ ФАЙЛАМИ YAML ДОБАВЛЕННЫМИ ЧЕРЕЗ ПОСТМАН
    public String CompareYamlFilesParam(@RequestParam("file01") MultipartFile file1,
                                        @RequestParam("file02") MultipartFile file2) throws IOException {

            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            InputStream path = file1.getInputStream();
            InputStream path2 = file2.getInputStream();
            JsonNode file01 = objectMapper.readTree(path);
            JsonNode file02 = objectMapper.readTree(path2);
            JsonNode patch = JsonDiff.asJson(file01, file02);
            String diffs = patch.toString();
            return diffs;

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handle(IllegalArgumentException e){
        log.error(e.getMessage());
        return "Oi, polomka!";
    }

}