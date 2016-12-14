package hu.applicationgenerator.validator;

import hu.applicationgenerator.helper.PropertyHelper;
import hu.applicationgenerator.helper.SchemaHelper;
import hu.applicationgenerator.helper.StringHelper;
import hu.applicationgenerator.DataModel;

public class SchemaTableChecker {

    //A táblanevek ellenőrzése:
    //- megengedett karakterek
    //- maximális hossz
    //- duplikációk. Az Oracle miatt case insensitive az összehasonlítás, azaz kis-nagy betűk azonosan számítanak.
    //  Két Java entitás/osztály sem lehet ugyanolyan nevű, ami kis-nagy betűkben térnek csak el.
    private static void checkTableNames(DataModel dataModel) throws Exception {
        int i=0;
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getName().length() > PropertyHelper.ORACLETABLENAMEMAXLENGTH) {
                throw new Exception("Size of table name of " + table.getName() + " has " + table.getName().length() + " instead of maximum " + PropertyHelper.ORACLETABLENAMEMAXLENGTH + " characters.");
            }
            if (!StringHelper.isValidIdentifier(table.getName())) {
                throw new Exception("Invalid character in table name: " + table.getName());
            }
            int j=0;
            for (DataModel.Tables.Table table2: dataModel.getTables().getTable()) {
                if ((table2.getName().toLowerCase().equals(table.getName().toLowerCase())) && (j>i)) {
                    throw new Exception("There is duplicated tables with same name: " + table.getName());
                }
                j++;
            }
            i++;
        }
    }

    //A mezőnevek ellenőrzése:
    //- megengedett karakterek
    //- maximális hossz
    //- duplikációk. Az Oracle miatt case insensitive az összehasonlítás, azaz kis-nagy betűk azonosan számítanak.
    //  Két Java mezőnév sem lehet ugyanolyan nevű, ami kis-nagy betűkben térnek csak el.
    private static void checkFieldNames(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() != null) {
                int i=0;
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if (field.getName().length() > PropertyHelper.ORACLEFIELDNAMEMAXLENGTH) {
                        throw new Exception("Size of filed name of " + table.getName() + "." + field.getName() + " has " + field.getName().length() + " instead of maximum " + PropertyHelper.ORACLEFIELDNAMEMAXLENGTH + " characters.");
                    }
                    if (!StringHelper.isValidIdentifier(field.getName())) {
                        throw new Exception("Invalid character in field name: " + table.getName() + "." + field.getName());
                    }
                    int j=0;
                    for (DataModel.Tables.Table.Fields.Field field2: table.getFields().getField()) {
                        if ((field2.getName().toLowerCase().equals(field.getName().toLowerCase())) && (j>i)) {
                            throw new Exception("There is duplicated fields with same name: " + table.getName() + "." + field.getName());
                        }
                        j++;
                    }
                    i++;
                }
            }
        }
    }

    //A külső kulcsok hivatkozásainak ellenőrzése, hogy valós táblára hivatkoznak-e
    private static void checkValidReferences(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if (field.getReference() != null) {
                        //A referencia karakterisztikája nem megfelelő. Ez lehet vagy csak <tábla> vagy <tábla>.<mező>
                        if (field.getReference().contains(".")) {
                            if (StringHelper.numberOfContained(field.getReference(), ".") > 1) {
                                throw new Exception(table.getName() + "." + field.getName() + " field has invalid reference definition: " + field.getReference());    
                            }
                        }
                        String referencedTable = SchemaHelper.getTableNameFromReference(field);
                        String referencedField = SchemaHelper.getFieldNameFromReference(field);
                        //Nem létező táblára hivatkozás
                        if (SchemaHelper.getTableByName(dataModel, referencedTable) == null) {
                            throw new Exception(table.getName() + "." + field.getName() + " field has invalid reference to " + referencedTable + " table.");
                        }
                        if (!referencedField.equals("")) {
                            //Nem létező mezőre hivatkozás, mert a hivatkozott táblának nincsenek mezői
                            //TODO: Felülvizsgálni ezt a feltételt, mert séma ellenőrzéskor még a generált mezők nincsenek meg. Igaz,
                            //mivel generálódnak, ezekre hivatkozni sem illik a modelben.
                            if (SchemaHelper.getTableByName(dataModel, referencedTable).getFields() == null) {
                                throw new Exception(table.getName() + "." + field.getName() + " field has invalid reference to " + referencedTable + "." + referencedField);
                            }
                            //Nem létező mezőre hivatkozás, mert a hivatkozott táblának nincs ilyen mezője
                            boolean found = false;
                            for (DataModel.Tables.Table.Fields.Field field2: SchemaHelper.getTableByName(dataModel, referencedTable).getFields().getField()) {
                                if (field2.getName().toLowerCase().equals(referencedField.toLowerCase())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                throw new Exception(table.getName() + "." + field.getName() + " field has invalid reference to " + referencedTable + "." + referencedField);
                            }
                        }
                    }
                }
            }
        }
    }
    
    //A külső kulcsok hivatkozásainak ellenőrzése, hogy nincs-e körbehivatkozás a táblák között
    //TODO: Nagyobb körök kiszűrése, mivel most csak közvetlen hurkokat szűr
    private static void checkReferenceCircle(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if (field.getReference() != null) {
                        String referencedTable = SchemaHelper.getTableNameFromReference(field);
                        for (DataModel.Tables.Table.Fields.Field field2: SchemaHelper.getTableByName(dataModel, referencedTable).getFields().getField()) {
                            if (field2.getReference() != null) {
                                String referencedTable2 = SchemaHelper.getTableNameFromReference(field2);
                                if (field2.getReference().toLowerCase().equals(table.getName().toLowerCase())) {
                                    throw new Exception(table.getName() + "." + field.getName() + " field has reference circle with " + field.getReference() + "." + field2.getName() + " field.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //A sémadefiníció általános struktúrájának ellenőrzése
    public static void checkSchemaStructure(DataModel dataModel) throws Exception {
        if ((dataModel.getTables() != null) && ((dataModel.getTables().getTable() == null) || (dataModel.getTables().getTable().isEmpty()))) {
            throw new Exception("Schema must contains tables because it contains <Tables> tag.");
        }
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if ((table.getFields() != null) && ((table.getFields().getField() == null) || (table.getFields().getField().isEmpty()))) {
               throw new Exception(table.getName() + " must contains fields because it conatins <Fields> tag.");
            }
        }
    }
    
    //A tartalmazás típusú entitás kapcsolatok (memberof) ellenőrzése, hogy valós táblára mutatnak-e
    public static void checkValidMemberOfs(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() != null) {
                if (table.getMemberof() != null) {
                    if (table.getMemberof().contains(".")) {
                        if (StringHelper.numberOfContained(table.getMemberof(), ".") > 1) {
                            throw new Exception(table.getName() + " has invalid memberof attribute: " + table.getMemberof());    
                        }
                    }
                    String referencedTable = SchemaHelper.getTableNameFromMemberOf(table);
                    String referencedField = SchemaHelper.getFieldNameFromMemberOf(table);
                    if (SchemaHelper.getTableByName(dataModel, referencedTable) == null) {
                        throw new Exception(table.getName() + " table has invalid memberof reference to " + referencedTable + " table.");
                    }
                    if (!referencedField.equals("")) {
                        if (SchemaHelper.getTableByName(dataModel, referencedTable).getFields() == null) {
                            throw new Exception(table.getName() + " table has invalid memberof reference to " + referencedTable + "." + referencedField);
                        }
                        boolean found = false;
                        for (DataModel.Tables.Table.Fields.Field field2: SchemaHelper.getTableByName(dataModel, referencedTable).getFields().getField()) {
                            if (field2.getName().toLowerCase().equals(referencedField.toLowerCase())) {
                                found = true;
                            }
                        }
                        if (!found) {
                            throw new Exception(table.getName() + " table has invalid reference to " + referencedTable + "." + referencedField);
                        }
                    }
                }
            }
        }
    }

    //Paraméter attribútumok megengedett értékének ellenőrzése
    public static void checkEnabledAttributeValues(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if ((table.getHistory() != null) && (!table.getHistory().equals("yes")) && (!table.getHistory().equals("no"))) {
                throw new Exception(table.getName() + " table has invalid value in history attribute");
            }
            if ((table.getVisible() != null) && (!table.getVisible().equals("yes")) && (!table.getVisible().equals("no"))) {
                throw new Exception(table.getName() + " table has invalid value in visible attribute");
            }
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if ((field.getEditable() != null) && (!field.getEditable().equals("yes")) && (!field.getEditable().equals("no"))) {
                        throw new Exception(table.getName() + "." + field.getName() + " field has invalid value in editable attribute");
                    }
                    if ((field.getNullable() != null) && (!field.getNullable().equals("yes")) && (!field.getNullable().equals("no"))) {
                        throw new Exception(table.getName() + "." + field.getName() + " field has invalid value in nullable attribute");
                    }
                    if ((field.getSearchable() != null) && (!field.getSearchable().equals("yes")) && (!field.getSearchable().equals("no"))) {
                        throw new Exception(table.getName() + "." + field.getName() + " field has invalid value in searchable attribute");
                    }
                    if ((field.getSearchablestandalone() != null) && (!field.getSearchablestandalone().equals("yes")) && (!field.getSearchablestandalone().equals("no"))) {
                        throw new Exception(table.getName() + "." + field.getName() + " field has invalid value in searchablestandalone attribute");
                    }
                    if ((field.getUnique() != null) && (!field.getUnique().equals("yes")) && (!field.getUnique().equals("no"))) {
                        throw new Exception(table.getName() + "." + field.getName() + " field has invalid value in unique attribute");
                    }
                    if ((field.getVisible() != null) && (!field.getVisible().equals("yes")) && (!field.getVisible().equals("no"))) {
                        throw new Exception(table.getName() + "." + field.getName() + " field has invalid value in visible attribute");
                    }
                    if (field.getName() == null) {
                        throw new Exception(table.getName() + "." + field.getName() + " field has not name attribute");
                    }
                    if (field.getType() == null) {
                        throw new Exception(table.getName() + "." + field.getName() + " field has not type attribute");
                    }
                }
            }
        }
    }
    
    //A tartalmazás kapcsolat (memberof) hivatkozásainak ellenőrzése, hogy nincs-e körbehivatkozás a táblák között
    //TODO: Nagyobb köröket is ki kell szűrni, nem csak a közvetlen hurkokat
    private static void checkMemberOfCircle(DataModel dataModel) throws Exception {
                
    }

    //Alapértelmezetten generálódó mezők meglétének ellenőrzése - ne legyenek
    private static void checkOOBFieldExistence(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if ((field.getName().toLowerCase().equals("creationdate")) || 
                        (field.getName().toLowerCase().equals("createdby")) ||     
                        (field.getName().toLowerCase().equals("modificationdate")) ||     
                        (field.getName().toLowerCase().equals("modifiedby")) ||     
                        (field.getName().toLowerCase().equals(table.getName().toLowerCase()+"id"))) {
                        throw new Exception("You mustn't define " + table.getName() + "." + field.getName() + " field because it's generated automatically.");
                    }
                }
            }
        }
                
    }    

    //Alapértelmezetten generálódó táblák meglétének ellenőrzése - ne legyenek
    private static void checkOOBTableExistence(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if ((table.getName().toLowerCase().equals("felxibleattribute")) || 
                (table.getName().toLowerCase().equals("flexibleattributevalue")) ||     
                (table.getName().toLowerCase().equals("userlist")) ||     
                (table.getName().toLowerCase().equals("userlistvalue")) ||     
                (table.getName().toLowerCase().equals("applicationuser")) ||     
                (table.getName().toLowerCase().equals("users")) ||     
                (table.getName().toLowerCase().equals("authority")) ||     
                (table.getName().toLowerCase().equals("authorities"))) {
            throw new Exception("You mustn't define " + table.getName() + " table because it's generated automatically.");
            }
        }
    }    

    //A many-to-many attribútumokban hivatkozott referenciák meglétének ellenőrzése
    private static void checkMTMReferences(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getMtm() != null) {
                if (!table.getMtm().contains("::")) {
                    throw new Exception(table.getName() + " has invalid mtm attribute value (it doesn't contain :: delimitier).");
                }
                if (StringHelper.numberOfContained(table.getMtm(), ":") > 2) {
                    throw new Exception(table.getName() + " has invalid mtm attribute value (it contains more than one :: delimitier).");
                }
                String firstTable = SchemaHelper.getFirstTableNameFromMtm(table).toLowerCase();
                String secondTable = SchemaHelper.getSecondTableNameFromMtm(table).toLowerCase();
                if (SchemaHelper.getTableByName(dataModel, firstTable) == null) {
                    throw new Exception(table.getName() + " has invalid mtm refernece to " + firstTable + " table.");
                }
                if (SchemaHelper.getTableByName(dataModel, secondTable) == null) {
                    throw new Exception(table.getName() + " has invalid mtm refernece to " + secondTable + " table.");
                }
            }
        }
    }

    
    
    public static void DoCheck(DataModel dataModel) throws Exception {
        checkSchemaStructure(dataModel);
        checkTableNames(dataModel);
        checkFieldNames(dataModel);
        checkValidReferences(dataModel);
        checkReferenceCircle(dataModel);    
        checkValidMemberOfs(dataModel);    
        checkEnabledAttributeValues(dataModel);
        checkMemberOfCircle(dataModel);
        checkOOBTableExistence(dataModel);
        checkOOBFieldExistence(dataModel);
        checkMTMReferences(dataModel);
    }
    
}
