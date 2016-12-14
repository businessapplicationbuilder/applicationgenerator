package hu.applicationgenerator.helper;

import hu.applicationgenerator.DataModel;
import hu.applicationgenerator.DataModel.Tables.Table.Fields.Field;

public class SchemaHelper {

    //Visszaad egy táblát a neve alapján
    public static DataModel.Tables.Table getTableByName(DataModel dataModel, String tableName) {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getName().toLowerCase().equals(tableName.toLowerCase())) {
                return table;
            }
        }
        return null;
    }

    //Visszaad egy választólistát a neve alapján
    public static DataModel.ChoiceLists.ChoiceList getChoiceListByName(DataModel dataModel, String choiceListName) {
        for (DataModel.ChoiceLists.ChoiceList choiceList: dataModel.getChoiceLists().getChoiceList()) {
            if (choiceList.getName().toLowerCase().equals(choiceListName.toLowerCase())) {
                return choiceList;
            }
        }
        return null;
    }
    
    public static DataModel.Types.Type getTypeByName(DataModel dataModel, String typeName) {
        for (DataModel.Types.Type type: dataModel.getTypes().getType()) {
            if (type.getName().toLowerCase().equals(typeName.toLowerCase())) {
                return type;
            }
        }
        return null;
    }
    
    
    public static String getDBType(DataModel dataModel, String typeName) {
        int numTypes = dataModel.getTypes().getType().size();
        String result = "";
        for (int t=0;t<=numTypes-1;t++) {
            if (dataModel.getTypes().getType().get(t).getName().toLowerCase().equals(typeName.toLowerCase())) {
                result = dataModel.getTypes().getType().get(t).getOracletype();
                if (dataModel.getTypes().getType().get(t).getLength() != null) {
                    result = result + "(" + dataModel.getTypes().getType().get(t).getLength() + ")";
                }
            }
        }
        return result;
    }

    public static boolean hasSearchableField(DataModel dataModel, String tableName, String fieldName) {
        int numTables = dataModel.getTables().getTable().size();
        for (int t=0;t<=numTables-1;t++) {
            if (dataModel.getTables().getTable().get(t).getName().equals(tableName)) {
                int numFields = dataModel.getTables().getTable().get(t).getFields().getField().size();
                for (int f=0;f<=numFields-1;f++) {
                    if ((dataModel.getTables().getTable().get(t).getFields().getField().get(f).getName().equals(fieldName)) && (dataModel.getTables().getTable().get(t).getFields().getField().get(f).getSearchable()!= null) && (dataModel.getTables().getTable().get(t).getFields().getField().get(f).getSearchable().toLowerCase().equals("yes")) &&
                        (getDBType(dataModel, dataModel.getTables().getTable().get(t).getFields().getField().get(f).getType().toLowerCase()).indexOf("varchar") > -1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasSearchableField(DataModel dataModel, String tableName) {
        int numTables = dataModel.getTables().getTable().size();
        for (int t=0;t<=numTables-1;t++) {
            if (dataModel.getTables().getTable().get(t).getName().equals(tableName)) {
                int numFields = dataModel.getTables().getTable().get(t).getFields().getField().size();
                for (int f=0;f<=numFields-1;f++) {
                    if ((dataModel.getTables().getTable().get(t).getFields().getField().get(f).getSearchable()!= null) && (dataModel.getTables().getTable().get(t).getFields().getField().get(f).getSearchable().toLowerCase().equals("yes")) &&
                        (getDBType(dataModel, dataModel.getTables().getTable().get(t).getFields().getField().get(f).getType().toLowerCase()).indexOf("varchar") > -1)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static String upperFirstCase(String text) {
        return text.substring(0,1).toUpperCase() + text.substring(1, text.length());
    }

    public static String lowerFirstCase(String text) {
        return text.substring(0,1).toLowerCase() + text.substring(1, text.length());
    }
    
    public static boolean hasDateField(DataModel dataModel, String tableName) {
        for (DataModel.Tables.Table.Fields.Field field: SchemaHelper.getTableByName(dataModel, tableName).getFields().getField()) {
            if (getDBType(dataModel, field.getType().toLowerCase()).contains("date")) {
             return true;   
            }
        }
        return false;
    }
    
    public static String createSQLPrimaryKey(DataModel.Tables.Table table) {
        return table.getName().toLowerCase() + "id";
    }

    public static String createJavaPrimaryKey(DataModel.Tables.Table table) {
        return lowerFirstCase(table.getName()) + "id";
    }
    
    public static String getJavaType(DataModel dataModel, DataModel.Tables.Table.Fields.Field field) {
        return getTypeByName(dataModel, field.getType()).getJavatype();
    }

    public static String getSQLType(DataModel dataModel, DataModel.Tables.Table.Fields.Field field) {
        if ((getTypeByName(dataModel, field.getType()).getOracletype().equals("varchar2")) && (getTypeByName(dataModel, field.getType()).getLength() != null) && (getTypeByName(dataModel, field.getType()).getLength() > 0)) {
            return "varchar2(" + getTypeByName(dataModel, field.getType()).getLength() + ")";
        }
        if ((getTypeByName(dataModel, field.getType()).getOracletype().equals("varchar2")) && ((getTypeByName(dataModel, field.getType()).getLength() == null) || (getTypeByName(dataModel, field.getType()).getLength() == 0))) {
            return "varchar2(2000)";
        }
        return getTypeByName(dataModel, field.getType()).getOracletype();
    }
    
    public static boolean hasSearchField(DataModel dataModel, DataModel.Tables.Table.Fields.Field field) {
        if ((field.getSearchable()!= null) && (field.getSearchable().toLowerCase().equals("yes")) && (getSQLType(dataModel, field).contains("varchar"))) {
            return true;
        }
        return false;
    }
    
    public static String getTableNameFromReference(DataModel.Tables.Table.Fields.Field field) {
        if (field.getReference() != null) {
            if (field.getReference().contains(".")) {
                return field.getReference().substring(0, field.getReference().indexOf("."));
            } else {
                return field.getReference();
            }
        }
        return "";
    }

    public static String getFieldNameFromReference(DataModel.Tables.Table.Fields.Field field) {
        if (field.getReference() != null) {
            if (field.getReference().contains(".")) {
                return field.getReference().substring(field.getReference().indexOf(".")+1, field.getReference().length());
            } else {
                return "";
            }
        }
        return "";
    }

    public static String getTableNameFromMemberOf(DataModel.Tables.Table table) {
        if (table.getMemberof() != null) {
            if (table.getMemberof().contains(".")) {
                return table.getMemberof().substring(0, table.getMemberof().indexOf("."));
            } else {
                return table.getMemberof();
            }
        }
        return "";
    }
    
    public static String getFieldNameFromMemberOf(DataModel.Tables.Table table) {
        if (table.getMemberof() != null) {
            if (table.getMemberof().contains(".")) {
                return table.getMemberof().substring(table.getMemberof().indexOf(".")+1, table.getMemberof().length());
            } else {
                return "";
            }
        }
        return "";
    }
    
    public static String getFirstTableNameFromMtm(DataModel.Tables.Table table) {
        if (table.getMtm() != null) {
            return table.getMtm().substring(0, table.getMtm().indexOf("::"));
        }
        return "";
    }

    public static String getSecondTableNameFromMtm(DataModel.Tables.Table table) {
        if (table.getMtm() != null) {
            return table.getMtm().substring(table.getMtm().indexOf("::")+2, table.getMtm().length());
        }
        return "";
    }
    
    public static boolean hasSchemaFlexibleAttribute(DataModel dataModel) {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if ((table.getFlexibleattribute() != null) && (table.getFlexibleattribute().equals("yes"))) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasFieldReference(DataModel dataModel, String tableName, String fieldName) {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getName().toLowerCase().equals(tableName.toLowerCase())) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if ((field.getName().toLowerCase().equals(fieldName.toLowerCase())) || (field.getName().toLowerCase().equals(fieldName.toLowerCase() + "id"))) {
                        if (field.getReference() != null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasChoiceListField(DataModel dataModel, String tableName) {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getName().toLowerCase().equals(tableName.toLowerCase())) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if (field.getType().toLowerCase().equals("choicelist")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
}
