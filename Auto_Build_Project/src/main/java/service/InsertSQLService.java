package service;

import dataObjects.FieldInfoDO;
import org.apache.poi.ss.usermodel.*;
import utils.StringUtils;

import java.util.*;

public class InsertSQLService {
    //简单insert语句生成-涉及ods，ads，以及dwm，dws的一部分（初步创建使用）
    //workbook 当前要传入的excel
    //sheetNum excel哪一sheet转化为insert sql
    //tableNameRowIP sheet中代表表名的那一列 用数字-1表示
    //返回结果  生成的sql语句,存放在list中
    public List<String> createSQLBuild(Workbook workbook, Integer sheetNum, Integer tableNameRowIP,
                                       Integer fieldNameRowIP, Integer commentRowIP, Integer dataTypeRowIP,
                                       Integer sourceTableNameIP,Integer sourceFieldNameIP
    ){
        //data carrier
        Map<String, List<FieldInfoDO>> tableFieldsMap = new HashMap<>();

        //设置默认值
        Integer tableNameRow=0;
        Integer fieldNameRow=1;
        Integer commentRow=2;
        Integer dataTypeRow=3;
        Integer sourceTableNameRow=4;
        Integer sourceFieldNameRow=5;
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
        if(sourceTableNameIP != null){
            sourceTableNameRow=sourceTableNameIP;
        }
        if(sourceFieldNameIP != null){
            sourceFieldNameRow=sourceFieldNameIP;
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
                Cell sourceTableNameCell = row.getCell(sourceTableNameRow); // sourceTableName
                Cell sourceFieldNameCell = row.getCell(sourceFieldNameRow); // sourceFieldName

                if (tableCell != null && tableCell.getCellType() == CellType.STRING &&
                        !tableCell.getStringCellValue().trim().isEmpty()&&
                        !fieldCell.getStringCellValue().trim().isEmpty()&&
                        fieldCell != null && fieldCell.getCellType() == CellType.STRING
                        && !StringUtils.containsChineseCharacters(tableCell.getStringCellValue())
                ) {
                    //get tablename
                    //get field infomation
                    String tableName = tableCell.getStringCellValue();
                    String fieldName = fieldCell.getStringCellValue();
                    String comment = (commentCell != null && commentCell.getCellType() == CellType.STRING)
                            ? commentCell.getStringCellValue().replace("\'","").replace("\"","") : "";
                    String type = (typeCell != null && typeCell.getCellType() == CellType.STRING)
                            ? typeCell.getStringCellValue() : "";
                    String sourceTableName=(sourceTableNameCell != null && sourceTableNameCell.getCellType() == CellType.STRING)
                            ? sourceTableNameCell.getStringCellValue() : "";
                    String sourceFieldName=(sourceFieldNameCell != null && sourceFieldNameCell.getCellType() == CellType.STRING)
                            ? sourceFieldNameCell.getStringCellValue() : "";

                    //put data into carrier
                    // If the table is not already in the map, add it with a new field map
                    tableFieldsMap.putIfAbsent(tableName, new ArrayList<FieldInfoDO>());

                    //create a new FieldInfoDO,then put it into tableFieldsMap
                    FieldInfoDO fieldInfoDO=new FieldInfoDO(fieldName,type,sourceTableName,sourceFieldName,comment);

                    // Update the FieldInfoDO with the tableName
                    tableFieldsMap.get(tableName).add(fieldInfoDO);
                }
            }

            //使用tableFieldsMap生成insert语句
            List<String> insertTableStatements=this.generateInsertTableStatements(tableFieldsMap);
            System.out.println(insertTableStatements.toString());

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //TODO 目前这版insert生成只针对 ods，ads，且源头表为单表
    //TODO 若源头表为多表，首先要获取所有源头表，并对其进行编号，并给对应字段添加编号前缀
    public  List<String> generateInsertTableStatements(Map<String, List<FieldInfoDO>> tableFieldsMap) {
        List<String> insertTableStatements = new ArrayList<>();

        //对table做循环
        for(Map.Entry<String, List<FieldInfoDO>> tableEntry : tableFieldsMap.entrySet()){
            String tableName = tableEntry.getKey();
            Set<String> sourceTableNames= new HashSet<>();
            StringBuilder insertTableSQL = new StringBuilder();
            insertTableSQL.append("INSERT INTO TABLE ").append(tableName).append(" (");

            //对table下field循环,拼接insert into table tablename后的字段名部分
            for(FieldInfoDO fieldInfoDO:tableEntry.getValue()){
                insertTableSQL.append(fieldInfoDO.getFieldName()).append(",");
            }
            //去掉最后一个","，同时增加")"
            if (insertTableSQL.length() > 0) {
                insertTableSQL.setLength(insertTableSQL.length() - 1);
            }
            insertTableSQL.append(")").append("\n").append("SELECT\n");

            //对table下field循环,拼接 sourcefield as field,
            for(FieldInfoDO fieldInfoDO:tableEntry.getValue()){
                sourceTableNames.add(fieldInfoDO.getSourceTableName());
                insertTableSQL.append(fieldInfoDO.getSourceFieldName()).append(" AS ").append(fieldInfoDO.getFieldName()).append("\n,");
            }
            //去掉最后一个","
            if (insertTableSQL.length() > 0) {
                insertTableSQL.setLength(insertTableSQL.length() - 1);
            }
            insertTableSQL.append("FROM ");

            for(String a:sourceTableNames){
                insertTableSQL.append(a).append(",");
            }
            //去掉最后的逗号
            if (insertTableSQL.length() > 0) {
                insertTableSQL.setLength(insertTableSQL.length() - 1);
            }
            insertTableSQL.append(";");
            insertTableStatements.add(insertTableSQL.toString());
        }
        return insertTableStatements;
    }

}
