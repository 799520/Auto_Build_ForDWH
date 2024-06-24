import org.apache.poi.ss.usermodel.Workbook;
import service.CreateSQLService;
import service.InsertSQLService;
import utils.ExcelUtils;

public class MainMethod {
    public static void main(String[] args) {
        Workbook workbook = ExcelUtils.getExcel("F:\\中交_工作\\路建_投后看板\\程序测试\\附件6：投后_数据模型设计V1.0.xlsx");
        CreateSQLService createSQLService=new CreateSQLService();
        InsertSQLService insertSQLService = new InsertSQLService();

        //todo 需要确认的是 表名、字段名、字段类型在哪列，目前为写死得状态
        //todo 是否完全符合hive语法待测试 入参增加hive或oracle选项，若为hive是否增加 指定分隔符 与 内外部表、if not exists
        //createSQLService.createSQLBuild(workbook,3,2,5,3,6);

        insertSQLService.createSQLBuild(workbook,3,2,5,3,6,10,11);

    }
}
