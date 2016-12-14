package hu.applicationgenerator.writer;

import hu.applicationgenerator.helper.PropertyHelper;
import hu.applicationgenerator.helper.SchemaHelper;
import hu.applicationgenerator.validator.SchemaTableChecker;
import hu.applicationgenerator.DataModel;
import hu.applicationgenerator.DataModel.Tables.Table;
import hu.applicationgenerator.DataModel.Tables.Table.Fields.Field;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleWriter {
    
    //Adott táblát létrehozó utasítások előtti bevezető rész kiírása
    private void writeHeaderText(PrintWriter printWriter, String tableName) {
        printWriter.println("-------------------------------------------------------------------------------");
        printWriter.println("-- " + tableName.toUpperCase());
        printWriter.println("-------------------------------------------------------------------------------");
    }
    
    //Táblákat droppoló parancsok kiírása
    private void writeDropCommands(PrintWriter printWriter, DataModel dataModel) {
        String commandLine = "";
        if (PropertyHelper.NEEDDROPCOMMANDS) {
            printWriter.println("BEGIN");
            printWriter.println("   BEGIN EXECUTE IMMEDIATE 'DROP VIEW authorities'; EXCEPTION WHEN OTHERS THEN NULL; END;");
            printWriter.println("   BEGIN EXECUTE IMMEDIATE 'DROP VIEW users'; EXCEPTION WHEN OTHERS THEN NULL; END;");
            printWriter.println("   BEGIN EXECUTE IMMEDIATE 'DROP TABLE authority'; EXCEPTION WHEN OTHERS THEN NULL; END;");
            printWriter.println("   BEGIN EXECUTE IMMEDIATE 'DROP TABLE applicationuser'; EXCEPTION WHEN OTHERS THEN NULL; END;");
            boolean hasReference;
            List<Table> tables = new ArrayList(dataModel.getTables().getTable());
            while (tables.size() > 0) {
                for (Table table: tables) {
                    hasReference = false;
                    for (Table table2: tables) {
                        if (table2.getFields() != null) {
                            for (Field field: table2.getFields().getField()) {
                                if ((field.getReference() != null) && (SchemaHelper.getTableNameFromReference(field).toLowerCase().equals(table.getName().toLowerCase())) && (!table.getName().toLowerCase().equals(table2.getName().toLowerCase()))) {
                                    hasReference = true;
                                }
                            }
                        }
                    }
                    if (!hasReference) {
                        printWriter.println("   BEGIN EXECUTE IMMEDIATE 'DROP TABLE " + table.getName().toLowerCase() + "'; EXCEPTION WHEN OTHERS THEN NULL; END;");
                        printWriter.println("   BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE " + PropertyHelper.SEQUENCEPREFIX + "_" + table.getName().toLowerCase() +"'; EXCEPTION WHEN OTHERS THEN NULL; END;");
                        if ((table.getHistory() != null) && (table.getHistory().toLowerCase().equals("yes"))) {
                            printWriter.println("   BEGIN EXECUTE IMMEDIATE 'DROP TABLE " + table.getName().toLowerCase() + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "'; EXCEPTION WHEN OTHERS THEN NULL; END;");
                            printWriter.println("   BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE " + PropertyHelper.SEQUENCEPREFIX + "_" + table.getName().toLowerCase() + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "'; EXCEPTION WHEN OTHERS THEN NULL; END;");
                        }
                        tables.remove(table);
                        break;
                    }
                }
            }
            printWriter.println("END;");
            printWriter.println("/");
        }
    }

    //Alapértelmezetten minden rendszerben létező, Spring keretrendszerhez szükséges táblák kiírása
    private void writeDefaultTables(PrintWriter printWriter) {
            writeHeaderText(printWriter, "APPLICATIONUSER");
            printWriter.println("CREATE TABLE applicationuser(username varchar2(50) not null primary key, password varchar2(50) not null, enabled char check (enabled in (0,1)) not null);");
            printWriter.println("COMMENT ON TABLE applicationuser is 'Java Spring keretrendszer authentikáció funkciójához szükséges tábla: kliens felhasználók.';");
            printWriter.println("COMMENT ON COLUMN applicationuser.username is 'Felhasználó loginneve, aki az alkalmazás kliensét használja.';");
            printWriter.println("COMMENT ON COLUMN applicationuser.password is 'Felhasználó jelszava, aki az alkalmazás kliensét használja.';");
            printWriter.println("COMMENT ON COLUMN applicationuser.enabled is 'Felhasználó engedélyezett, azaz beléphet-e az alkalmazásba. Lehetséeg értékei: 1=igen, 0=nem.';");
            printWriter.println("CREATE VIEW users AS SELECT * FROM APPLICATIONUSER;");
            writeHeaderText(printWriter, "AUTHORITY");
            printWriter.println("CREATE TABLE authority(username varchar2(50) not null, authority varchar2(50) not null, constraint fk_authorities_0 foreign key(username) references applicationuser(username));");
            printWriter.println("COMMENT ON TABLE authority is 'Java Spring keretrendszer authentikáció funkciójához szükséges tábla: felhasználói jogkörök.';");
            printWriter.println("COMMENT ON COLUMN authority.username is 'Felhasználó loginneve." + PropertyHelper.FOREIGNKEYDESCRIPTIONPOSTFIX + " users.username';");
            printWriter.println("COMMENT ON COLUMN authority.authority is 'Felhasználó jogköre.';");
            printWriter.println("CREATE UNIQUE INDEX ix_authorities_0 ON authority (username,authority);");
            printWriter.println("INSERT INTO applicationuser(username, password, enabled) VALUES('admin','admin',1);");
            printWriter.println("INSERT INTO authority(username, authority) VALUES('admin', 'ROLE_USER');");
            printWriter.println("COMMIT;");
            printWriter.println("CREATE VIEW authorities AS SELECT * FROM AUTHORITY;");
    }

    public void writeChoiceListValues(PrintWriter printWriter, DataModel dataModel) {
        boolean hasValue = false;
        for (DataModel.ChoiceLists.ChoiceList choiceList: dataModel.getChoiceLists().getChoiceList()) {
            printWriter.println("insert into userlist(userlistid, creationdate, createdby, name, s_name, description, type) values (sq_userlist.nextval, sysdate, '" + PropertyHelper.APPLICATIONNAME + "', '" + choiceList.getName() + "', '" + choiceList.getName().toUpperCase() + "', '" + choiceList.getDescription() + "', 'S');");
            hasValue = true;
            if (choiceList.getElements() != null) {
                for (DataModel.ChoiceLists.ChoiceList.Elements.Element element: choiceList.getElements().getElement()) {
                    printWriter.println("insert into userlistvalue(userlistvalueid, creationdate, createdby, userlistid, name, description, position) values(sq_userlistvalue.nextval, sysdate, '"  + PropertyHelper.APPLICATIONNAME + "', sq_userlist.currval, '" + element.getName() + "', '" + element.getDescription() + "', " + element.getPosition() + ");" );
                }
            }
        }
        if (hasValue) {
            printWriter.println("commit;");
        }
    }
    
    public void writeSQLSchema(DataModel dataModel) {
        try {
            String commandLine = "";
            PrintWriter printWriter = new PrintWriter(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\sql\\" + PropertyHelper.APPLICATIONNAME + ".sql", "UTF-8");
            writeDropCommands(printWriter, dataModel);
            System.out.println("writeDropCommands ready.");
            writeDefaultTables(printWriter);
            System.out.println("writeDefaultTables ready.");
            for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
                int indexcounter = 0;
                int foreignkeycounter = 0;
                boolean needHistoryTable = (table.getHistory() != null) && (table.getHistory().toLowerCase().equals("yes"));
                String tableName = table.getName().toLowerCase();
                writeHeaderText(printWriter, tableName);
                commandLine = "CREATE TABLE " + tableName + "(";
                
                //Mezők kiírása
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    String fieldName = field.getName().toLowerCase();
                    String fieldType = SchemaHelper.getSQLType(dataModel, field);
                    boolean isNullable = (field.getNullable() != null) && (field.getNullable().toLowerCase().equals("no"));
                    boolean isSearchable = (field.getSearchable()!= null) && (field.getSearchable().toLowerCase().equals("yes"));
                    boolean isUnique = (field.getUnique()!= null) && (field.getUnique().toLowerCase().equals("yes"));
                    boolean isPrimaryKey = (field.getPrimarykey()!= null) && (field.getPrimarykey().toLowerCase().equals("yes"));
                    commandLine = commandLine + fieldName + " ";
                    commandLine = commandLine + fieldType;
                    if (isPrimaryKey) {
                        commandLine = commandLine + " not null primary key";
                    }
                    if (isNullable) {
                        commandLine = commandLine + " not null";
                    }
                    if (isUnique) {
                        commandLine = commandLine + " unique";
                    }
                    commandLine = commandLine + ", ";
                    if (SchemaHelper.hasSearchField(dataModel, field)) {
                        commandLine = commandLine + "s_" + field.getName().toLowerCase() + " ";
                        commandLine = commandLine + fieldType;
                        if (isNullable) {
                           commandLine = commandLine + " not null";
                        }
                        commandLine = commandLine + ", ";
                    }
                }
                //printWriter.println(commandLine);
                //Külső kulcs referenciák kiírása
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    String fieldName = field.getName().toLowerCase();
                    if (field.getReference() != null) {
                        String reference = SchemaHelper.getTableNameFromReference(field);
                        commandLine = commandLine + "constraint " + PropertyHelper.FOREIGNKEYPREFIX + "_" + tableName + "_" + foreignkeycounter++;
                        commandLine = commandLine + " foreign key (" + fieldName + ") references " + reference.toLowerCase() + "(" + reference.toLowerCase() + "id), ";
                    }
                }
                commandLine = commandLine.substring(0, commandLine.length()-2);
                commandLine = commandLine + ");";
                printWriter.println(commandLine);

                //Indexek kiírása
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    String fieldName = field.getName().toLowerCase();
                    String fieldType = SchemaHelper.getSQLType(dataModel, field);
                    boolean isSearchable = (field.getSearchable()!= null) && (field.getSearchable().toLowerCase().equals("yes"));
                    boolean isSearchableStandalone = (field.getSearchablestandalone()!= null) && (field.getSearchablestandalone().toLowerCase().equals("yes"));
                    commandLine = "";
                    if (isSearchable) {
                        if ((field.getSearchablewith() == null) || (isSearchableStandalone)) {
                            if (SchemaHelper.hasSearchField(dataModel, field)) {
                                printWriter.println("CREATE INDEX " + PropertyHelper.INDEXPREFIX + "_" + tableName + "_" + indexcounter++ + " ON " + tableName + "(s_" + fieldName + ");");
                            } else {
                                printWriter.println("CREATE INDEX " + PropertyHelper.INDEXPREFIX + "_" + tableName + "_" + indexcounter++ + " ON " + tableName + "(" + fieldName + ");");
                            }
                        } else {
                            String searchPair;
                            if (SchemaHelper.hasFieldReference(dataModel, tableName, field.getSearchablewith())) {
                                searchPair = field.getSearchablewith() + "id";
                            } else {
                                searchPair = field.getSearchablewith();
                            }
                            String fieldsToBeIndexed = fieldName + "," + searchPair;
                            List<String> items = Arrays.asList(fieldsToBeIndexed.split("\\s*,\\s*"));
                            String indexFields = "";
                            for (String fieldToBeIndexed : items) {
                                if (SchemaHelper.hasSearchableField(dataModel, tableName, fieldToBeIndexed)) {
                                    indexFields = indexFields + "s_" + fieldToBeIndexed + ", ";
                                } else {
                                    indexFields = indexFields + fieldToBeIndexed + ", ";
                                }
                            }
                            indexFields = indexFields.substring(0, indexFields.length()-2);
                            printWriter.println("CREATE INDEX " + PropertyHelper.INDEXPREFIX + "_" + tableName + "_" + indexcounter++ + " ON " + tableName + "(" + indexFields + ");");
                        }    
                    }
                }

                //Kommentek kiírása
                //TODO: s_-s mezők kommentjeinek elkészítése
                printWriter.println("COMMENT ON TABLE " + tableName + " IS '" + table.getDescription() + "';");
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    String fieldName = field.getName().toLowerCase();
                    String fieldReference = SchemaHelper.getTableNameFromReference(field);
                    if (!fieldReference.equals("")) {
                        printWriter.println("COMMENT ON COLUMN " + tableName + "." + fieldName + " IS '" + field.getDescription() + " " + PropertyHelper.FOREIGNKEYDESCRIPTIONPOSTFIX + " " + fieldReference.toLowerCase() + "." + fieldReference.toLowerCase() + "id.';");    
                    } else {
                        printWriter.println("COMMENT ON COLUMN " + tableName + "." + fieldName + " IS '" + field.getDescription() + "';");
                    }
                }

                //Szekvenciák kiírása
                printWriter.println("CREATE SEQUENCE " + PropertyHelper.SEQUENCEPREFIX + "_" + tableName + ";");
                if (needHistoryTable) {
                    printWriter.println("CREATE SEQUENCE " + PropertyHelper.SEQUENCEPREFIX + "_" + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + ";");
                }

                //History tábla kiírása
                if (needHistoryTable) {
                    commandLine = "CREATE TABLE " + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "(";
                    commandLine = commandLine + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "id number not null primary key, ";
                    commandLine = commandLine + "action varchar2(250) not null, ";
                    //commandLine = commandLine + SchemaHelper.createSQLPrimaryKey(table) + " number, ";
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String fieldName = field.getName().toLowerCase();
                        String fieldType = SchemaHelper.getSQLType(dataModel, field);
                        commandLine = commandLine + fieldName + " ";
                        commandLine = commandLine + fieldType;
                        commandLine = commandLine + ", ";
                    }
                    commandLine = commandLine.substring(0, commandLine.length()-2);
                    commandLine = commandLine + ");";
                    printWriter.println(commandLine);
                    printWriter.println("CREATE INDEX " + PropertyHelper.INDEXPREFIX + "_" + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "_0 ON " + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "(" + tableName +  "id" + ");");
                    printWriter.println("CREATE INDEX " + PropertyHelper.INDEXPREFIX + "_" + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "_1 ON " + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "(creationdate);");
                }

                //Triggerek kiírása
                if (SchemaHelper.hasSearchableField(dataModel, tableName)) {
                    //BEFORE INSERT
                    printWriter.println("CREATE OR REPLACE TRIGGER " + PropertyHelper.TRIGGERPREFIX + "_" + tableName + "_bi");
                    printWriter.println("BEFORE INSERT ON " + tableName + " FOR EACH ROW");
                    printWriter.println("BEGIN");
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String fieldName = field.getName().toLowerCase();
                        if (SchemaHelper.hasSearchField(dataModel, field)) {
                            printWriter.println("  if upper(:new." + fieldName + ") <> :new.s_" + fieldName + " or :new.s_" +fieldName + " is null then");
                            printWriter.println("    :new.s_" + fieldName + " := upper(:new." + fieldName + ");");
                            printWriter.println("  end if;");
                        }
                    }
                    printWriter.println("END;");
                    printWriter.println("/");
                    //BEFORE UPDATE
                    printWriter.println("CREATE OR REPLACE TRIGGER " + PropertyHelper.TRIGGERPREFIX + "_" + tableName + "_bu");
                    printWriter.println("BEFORE UPDATE ON " + tableName + " FOR EACH ROW");
                    printWriter.println("BEGIN");
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String fieldName = field.getName().toLowerCase();
                        if (SchemaHelper.hasSearchField(dataModel, field)) {
                            printWriter.println("  if upper(:new." + fieldName + ") <> :new.s_" + fieldName + " or :new.s_" +fieldName + " is null then");
                            printWriter.println("    :new.s_" + fieldName + " := upper(:new." + fieldName + ");");
                            printWriter.println("  end if;");
                        }
                    }
                    printWriter.println("END;");
                    printWriter.println("/");
                }    
                if (needHistoryTable) {
                    printWriter.println("CREATE OR REPLACE TRIGGER " + PropertyHelper.TRIGGERPREFIX + "_" + tableName + "_ai");
                    printWriter.println("AFTER INSERT ON " + tableName + " FOR EACH ROW");
                    printWriter.println("BEGIN");
                    commandLine = "   INSERT INTO " + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "(" +
                                  tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "id, " +
                                  "action, ";
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String fieldName = field.getName().toLowerCase();
                        commandLine = commandLine + fieldName + ", ";
                    }
                    commandLine = commandLine.substring(0, commandLine.length()-2);
                    commandLine = commandLine + ") values(" + PropertyHelper.SEQUENCEPREFIX + "_" + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + ".nextval, " + "'" + PropertyHelper.INSERTHISTORYACTION + "', ";
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String fieldName = field.getName().toLowerCase();
                        if ((!fieldName.toLowerCase().equals("creationdate")) && (!fieldName.toLowerCase().equals("createdby")) && (!fieldName.toLowerCase().equals("modificationdate")) && (!fieldName.toLowerCase().equals("modifiedby")) && (!fieldName.toLowerCase().equals(tableName + "id"))) {
                            commandLine = commandLine + ":new." + fieldName + ", ";
                        } else {
                            if (fieldName.toLowerCase().equals("creationdate")) {
                                commandLine = commandLine + "sysdate, ";
                            }
                            if (fieldName.toLowerCase().equals("createdby")) {
                                commandLine = commandLine + "nvl(:new.modifiedby, :new.createdby), ";
                            }
                            if (fieldName.toLowerCase().equals("modificationdate")) {
                                commandLine = commandLine + "null, ";
                            }
                            if (fieldName.toLowerCase().equals("modifiedby")) {
                                commandLine = commandLine + "null, ";
                            }
                            if (fieldName.toLowerCase().equals(tableName + "id")) {
                                commandLine = commandLine + ":new." + tableName + "id, ";
                            }
                        }
                    }
                    commandLine = commandLine.substring(0, commandLine.length()-2);
                    commandLine = commandLine + ");";
                    printWriter.println(commandLine);
                    printWriter.println("END;");
                    printWriter.println("/");
                }
                printWriter.println("CREATE OR REPLACE TRIGGER " + PropertyHelper.TRIGGERPREFIX + "_" + tableName + "_au");
                printWriter.println("AFTER UPDATE ON " + tableName + " FOR EACH ROW");
                printWriter.println("BEGIN");
                printWriter.println("   if :new.modifiedby is null or trim(:new.modifiedby) = '' then");
                printWriter.println("     Rollback;");
                printWriter.println("     Raise_Application_Error(-21000, '" + tableName + ".modifiedby " + PropertyHelper.MODIFICATIONDATENOTNULLERRORMESSAGEPOSTFIX + "');");
                printWriter.println("   end if;");
                printWriter.println("   if :new.modificationdate is null or trim(:new.modificationdate) = '' then");
                printWriter.println("     Rollback;");
                printWriter.println("     Raise_Application_Error(-21000, '" + tableName + ".modificationdate " + PropertyHelper.MODIFICATIONDATENOTNULLERRORMESSAGEPOSTFIX + "');");
                printWriter.println("   end if;");
                if (needHistoryTable) {
                    commandLine = "   INSERT INTO " + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "(" +
                                  tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "id, " +
                                  "action, ";
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String fieldName = field.getName().toLowerCase();
                        commandLine = commandLine + fieldName + ", ";
                    }
                    commandLine = commandLine.substring(0, commandLine.length()-2);
                    commandLine = commandLine + ") values(" + PropertyHelper.SEQUENCEPREFIX + "_" + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + ".nextval, " + "'" + PropertyHelper.MODIFYHISTORYACTION + "', ";
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String fieldName = field.getName().toLowerCase();
                        if ((!fieldName.toLowerCase().equals("creationdate")) && (!fieldName.toLowerCase().equals("createdby")) && (!fieldName.toLowerCase().equals("modificationdate")) && (!fieldName.toLowerCase().equals("modifiedby")) && (!fieldName.toLowerCase().equals(tableName + "id"))) {
                            commandLine = commandLine + ":new." + fieldName + ", ";
                        } else {
                            if (fieldName.toLowerCase().equals("creationdate")) {
                                commandLine = commandLine + "sysdate, ";
                            }
                            if (fieldName.toLowerCase().equals("createdby")) {
                                commandLine = commandLine + "nvl(:new.modifiedby, :new.createdby), ";
                            }
                            if (fieldName.toLowerCase().equals("modificationdate")) {
                                commandLine = commandLine + "null, ";
                            }
                            if (fieldName.toLowerCase().equals("modifiedby")) {
                                commandLine = commandLine + "null, ";
                            }
                            if (fieldName.toLowerCase().equals(tableName + "id")) {
                                commandLine = commandLine + ":new." + tableName + "id, ";
                            }
                        }
                    }
                    commandLine = commandLine.substring(0, commandLine.length()-2);
                    commandLine = commandLine + ");";
                    printWriter.println(commandLine);
                }
                printWriter.println("END;");
                printWriter.println("/");
                if (needHistoryTable) {
                    printWriter.println("CREATE OR REPLACE TRIGGER " + PropertyHelper.TRIGGERPREFIX + "_" + tableName + "_ad");
                    printWriter.println("AFTER DELETE ON " + tableName + " FOR EACH ROW");
                    printWriter.println("BEGIN");
                    commandLine = "   INSERT INTO " + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "(" +
                                  tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "id, " +
                                  "action, ";
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String fieldName = field.getName().toLowerCase();
                        commandLine = commandLine + fieldName + ", ";
                    }
                    commandLine = commandLine.substring(0, commandLine.length()-2);
                    commandLine = commandLine + ") values(" + PropertyHelper.SEQUENCEPREFIX + "_" + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + ".nextval, " + "'" + PropertyHelper.DELETEHISTORYACTION + "', ";
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String fieldName = field.getName().toLowerCase();
                        if ((!fieldName.toLowerCase().equals("creationdate")) && (!fieldName.toLowerCase().equals("createdby")) && (!fieldName.toLowerCase().equals("modificationdate")) && (!fieldName.toLowerCase().equals("modifiedby")) && (!fieldName.toLowerCase().equals(tableName + "id"))) {
                            commandLine = commandLine + ":old." + fieldName + ", ";
                        } else {
                            if (fieldName.toLowerCase().equals("creationdate")) {
                                commandLine = commandLine + "sysdate, ";
                            }
                            if (fieldName.toLowerCase().equals("createdby")) {
                                commandLine = commandLine + "nvl(:old.modifiedby, :old.createdby), ";
                            }
                            if (fieldName.toLowerCase().equals("modificationdate")) {
                                commandLine = commandLine + "null, ";
                            }
                            if (fieldName.toLowerCase().equals("modifiedby")) {
                                commandLine = commandLine + "null, ";
                            }
                            if (fieldName.toLowerCase().equals(tableName + "id")) {
                                commandLine = commandLine + ":old." + tableName + "id, ";
                            }
                        }
                    }
                    commandLine = commandLine.substring(0, commandLine.length()-2);
                    commandLine = commandLine + ");";
                    printWriter.println(commandLine);
                    printWriter.println("END;");
                    printWriter.println("/");
                }
                printWriter.println("CREATE OR REPLACE TRIGGER " + PropertyHelper.TRIGGERPREFIX + "_" + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + "_au");
                printWriter.println("AFTER UPDATE ON " + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + " FOR EACH ROW");
                printWriter.println("BEGIN");
                printWriter.println("   if :new.modifiedby is null or trim(:new.modifiedby) = '' then");
                printWriter.println("     Rollback;");
                printWriter.println("     Raise_Application_Error(-21000, '" + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + ".modifiedby " + PropertyHelper.MODIFICATIONDATENOTNULLERRORMESSAGEPOSTFIX + "');");
                printWriter.println("   end if;");
                printWriter.println("   if :new.modificationdate is null or trim(:new.modificationdate) = '' then");
                printWriter.println("     Rollback;");
                printWriter.println("     Raise_Application_Error(-21000, '" + tableName + "_" + PropertyHelper.HISTORYTABLENAMEPOSTFIX + ".modificationdate " + PropertyHelper.MODIFICATIONDATENOTNULLERRORMESSAGEPOSTFIX + "');");
                printWriter.println("   end if;");
                printWriter.println("END;");
                printWriter.println("/");
                System.out.println(table.getName() + " done.");
            }
            writeChoiceListValues(printWriter, dataModel);
            printWriter.close();
        } catch (IOException e) {
            System.out.println("ERROR: File write error. " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        
    }
    
    public void writeSchema(DataModel dataModel) {
        
        try {
            writeSQLSchema(dataModel);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
            
    }
    
}
