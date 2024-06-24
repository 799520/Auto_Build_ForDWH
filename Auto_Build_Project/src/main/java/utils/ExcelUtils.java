package utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;

public class ExcelUtils {
    //根据位置载入excel对象
    //excelFilePath excel文件当前系统下目录
    public static Workbook getExcel(String excelFilePath){
        try{
            FileInputStream file = new FileInputStream(new File(excelFilePath));
            Workbook workbook = WorkbookFactory.create(file);
            return workbook;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
