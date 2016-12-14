package hu.applicationgenerator.validator;

import hu.applicationgenerator.helper.SchemaHelper;
import hu.applicationgenerator.DataModel;

//A séma <type> elemének, és alatta lévő elemeninek és azokra mutató referenciáinak ellenőrzése
public class SchemaTypeChecker {

    //Típusdefiníciók ellenőrzése
    private static void checkValidFieldTypes(DataModel dataModel) throws Exception {
        if (dataModel.getTypes() == null) {
            throw new Exception("Schema has not any type definitions.");
        }
        for (DataModel.Types.Type type: dataModel.getTypes().getType()) {
            if (type.getName() == null) {
                throw new Exception("Schema has a type which has no any name attribute.");
            }
            if (type.getOracletype() == null) {
                throw new Exception(type.getName() + " type has no any oracletype attribute.");
            }
            if (type.getJavatype() == null) {
                throw new Exception(type.getName() + " type has no any javatype attribute.");
            }
            if ((type.getOracletype().toLowerCase().contains("varchar")) && (type.getLength() > 2000)) {
                throw new Exception(type.getName() + " type definition oracletype attribute must be clob because length is more than 2000 characters.");
            }
        }
    }

    //A táblákban szereplő mezők típusainak ellenőrzése, hogy mindegyik előre definiált típusú-e
    private static void checkValidTypeDefinitions(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if (field.getType() != null) {
                        if (SchemaHelper.getTypeByName(dataModel, field.getType()) == null) {
                            throw new Exception(table.getName() + "." + field.getName() + " has invalid type reference.");
                        }
                    }
                }
            }
        }
    }

    public static void DoCheck(DataModel dataModel) throws Exception {
        checkValidFieldTypes(dataModel);
        checkValidTypeDefinitions(dataModel);
    }
    
}
