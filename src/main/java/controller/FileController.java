package controller;

import Common.FileUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.io.InputStream;


@RestController
@RequestMapping(value = "/api")
public class FileController {

    @RequestMapping(value = "/uploadFile")
    public String uploadFile(@RequestParam("myfile") CommonsMultipartFile uploadFile) throws IOException {
        String fileName = uploadFile.getOriginalFilename();

        try {
            // 获取文件流
            InputStream stream = uploadFile.getInputStream();

            // 保存文件
            FileUtil.saveFileFromStream(stream, "D:\\testfile"+"\\"+fileName);
        } catch (IOException ex) {
            ex.fillInStackTrace();
        }

        return "index.jsp";

    }
}
