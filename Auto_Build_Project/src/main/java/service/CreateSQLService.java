package service;

import org.apache.poi.ss.usermodel.*;
import utils.StringUtils;

import java.util.*;

//建表语句（create sql）生成服务类
public class CreateSQLService {
    //建表语句生成
    //workbook 当前要传入的excel
    //sheetNum excel哪一sheet转化为建表sql
    //tableNameRowIP sheet中代表表名的那一列 用数字-1表示
    //返回结果  生成的sql语句,存放在list中
    public List<String> createSQLBuild(Workbook workbook,Integer sheetNum,Integer tableNameRowIP,
                                 Integer fieldNameRowIP,Integer commentRowIP,Integer dataTypeRowIP
                                 ){

        //第一层的 HashMap 的 key 是表名。
        //第一层的 HashMap 的 value 是一个 map，这个map代表该表下所有字段
        //第二层map key代表的是字段名，value的map一般会挂两个，一个是comment一个是datatype
        Map<String, Map<String, Map<String, String>>> tableFieldsMap = new HashMap<>();
        Integer tableNameRow=0;
        Integer fieldNameRow=1;
        Integer commentRow=2;
        Integer dataTypeRow=3;
        if(tableNameRowIP != null){
            tableNameRow=tableNameRowIP;
        }
        if(fieldNameRowIP != null){
            fieldNameRow=fieldNameRowIP;
        }
        if(commentRowIP != null){
            commentRow=commentRowIP;
        }
        if(dataTypeRowIP != null){
            dataTypeRow=dataTypeRowIP;
        }


        if(workbook==null){
            System.out.println("workbook对象为空");
            return null;
        }

        try{
            Sheet sheet = workbook.getSheetAt(sheetNum);
            if(sheet==null){
                System.out.println("当前sheet对象为空");
                return null;
            }

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Get the cell values from columns
                Cell tableCell = row.getCell(tableNameRow); // Table name
                Cell fieldCell = row.getCell(fieldNameRow); // Field name
                Cell commentCell = row.getCell(commentRow); // Comment
                Cell typeCell = row.getCell(dataTypeRow); // Datatype

                if (tableCell != null && tableCell.getCellType() == CellType.STRING &&
                        !tableCell.getStringCellValue().trim().isEmpty()&&
                        !fieldCell.getStringCellValue().trim().isEmpty()&&
                        fieldCell != null && fieldCell.getCellType() == CellType.STRING
                        && !StringUtils.containsChineseCharacters(tableCell.getStringCellValue())
                ) {

                    String tableName = tableCell.getStringCellValue();
                    String fieldName = fieldCell.getStringCellValue();
                    String comment = (commentCell != null && commentCell.getCellType() == CellType.STRING)
                            ? commentCell.getStringCellValue().replace("\'","").replace("\"","") : "";
                    String type = (typeCell != null && typeCell.getCellType() == CellType.STRING)
                            ? typeCell.getStringCellValue() : "";

                    // If the table is not already in the map, add it with a new field map
                    tableFieldsMap.putIfAbsent(tableName, new HashMap<>());

                    // Create a new field map containing comment and type
                    Map<String, String> fieldInfoMap = new HashMap<>();
                    fieldInfoMap.put("comment", comment);
                    fieldInfoMap.put("type", type);

                    // Update the table's map with the field map
                    tableFieldsMap.get(tableName).put(fieldName, fieldInfoMap);
                }
            }

            //根据hashmap编写建表语句
            List result = generateCreateTableStatements(tableFieldsMap);
            System.out.println(result.toString());
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //根据Map<String, Map<String, Map<String, String>>>结构创建建表语句
    public  List<String> generateCreateTableStatements(Map<String, Map<String, Map<String, String>>> tableFieldsMap) {
        List<String> createTableStatements = new ArrayList<>();

        for (Map.Entry<String, Map<String, Map<String, String>>> tableEntry : tableFieldsMap.entrySet()) {
            String tableName = tableEntry.getKey();
            Map<String, Map<String, String>> fields = tableEntry.getValue();

            StringBuilder createTableSQL = new StringBuilder();
            createTableSQL.append("CREATE TABLE ").append(tableName).append(" (\n");

            for (Map.Entry<String, Map<String, String>> fieldEntry : fields.entrySet()) {
                String fieldName = fieldEntry.getKey();
                Map<String, String> fieldAttributes = fieldEntry.getValue();
                String fieldType = fieldAttributes.get("type");
                String fieldComment = fieldAttributes.get("comment");

                createTableSQL.append("    ").append(fieldName).append(" ").append(fieldType);
                if (fieldComment != null && !fieldComment.isEmpty()) {
                    createTableSQL.append(" COMMENT '").append(fieldComment).append("'");
                }
                createTableSQL.append(",\n");
            }

            // 去掉最后一个逗号和换行符
            if (createTableSQL.length() > 0) {
                createTableSQL.setLength(createTableSQL.length() - 2);
            }

            createTableSQL.append(");");

            createTableStatements.add(createTableSQL.toString());
        }

        return createTableStatements;
    }


}
