package dataObjects;

public class FieldInfoDO {
    private String fieldName;//字段名
    private String fieldType;//字段类型
    private String comment;//字段注释
    private String sourceTableName;//字段来源表
    private String sourceFieldName;//字段来源表对应字段

    public FieldInfoDO(String fieldName, String fieldType, String sourceTableName, String sourceFieldName,String comment) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.sourceTableName = sourceTableName;
        this.sourceFieldName = sourceFieldName;
        this.comment = comment;
    }

    public FieldInfoDO() {}

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getComment() {
        return comment;
    }

    public String getSourceTableName() {
        return sourceTableName;
    }

    public String getSourceFieldName() {
        return sourceFieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName=fieldName;
    }

    public void setFieldType(String fieldType) {
        this.fieldType=fieldType;
    }

    public void setComment(String comment) {
        this.comment=comment;
    }

    public void setSourceTableName(String sourceTableName) {
        this.sourceTableName=sourceTableName;
    }

    public void setSourceFieldName(String sourceFieldName) {
        this.sourceFieldName=sourceFieldName;
    }
}
