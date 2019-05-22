package controller;

import Util.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.model.Sheet;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试控制器
 * ---------
 * 单机器调度任务
 *
 * @author nss
 */
@Controller
@RequestMapping("/home")
@Component
public class IndexController {

    @RequestMapping("/index")
    public String index() {
        //TestTask();
        return "index";
    }

    @RequestMapping("/export")
    @ResponseBody
    public void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 组装数据
        List<User> list = new ArrayList<>();
        User user = new User();
        user.setAge("28");
        user.setName("xuja");
        User user1 = new User();
        user1.setAge("24");
        user1.setName("xujiao");
        list.add(user);
        list.add(user1);

        String[] title = {"姓名", "性别"};
        String fileName = System.currentTimeMillis() + ".xls";
        String sheetName = "myexcel";


        String content[][] = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            content[i][0] = ((User) obj).getName();
            content[i][1] = ((User) obj).getAge();
        }

        HSSFWorkbook workbook = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);

        try {
            this.setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            os.flush();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @RequestMapping("/unport")
    @ResponseBody
    public void unport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String filePath = "D:\\1558493925024.xls";

        if (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx")) {
            System.out.println("不是excel");
        }

        FileInputStream stream = null;
        HSSFWorkbook workbook = null;

        try {
            stream = new FileInputStream(filePath);
            workbook = new HSSFWorkbook(stream);
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow row = sheet.getRow(0);

            //判断表头是否正确
            if (row.getPhysicalNumberOfCells() != 2) {
                System.out.println("表头的数量不对!");
            }
            int totalNum = sheet.getLastRowNum();

            String name = StringUtils.EMPTY;
            String age = StringUtils.EMPTY;

            for (int i = 0; i < totalNum; i++) {
                Row row1 = sheet.getRow(i);

                Cell cell = row1.getCell(0);
                name = cell.getStringCellValue();

                cell = row1.getCell(1);
                age = cell.getStringCellValue();

                //Cell cell = row1.getCell(i);


                System.out.println(name +"-------"+age);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            fileName = new String(fileName.getBytes(), "ISO8859-1");
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

   /* @Scheduled(cron = "0/2 * * * * ?")
    public void TestTask() {
        System.out.println("2S执行一次");
    }*/
}
