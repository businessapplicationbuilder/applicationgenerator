package hu.applicationgenerator.writer;

import hu.applicationgenerator.helper.PropertyHelper;
import hu.applicationgenerator.helper.SchemaHelper;
import hu.applicationgenerator.DataModel;
import java.io.IOException;
import java.io.PrintWriter;

public class JavaSpringMVCWriter {

    public static void writeModelFiles(DataModel dataModel) {
        try {
            for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
                if (((table.getVisible() != null) && (table.getVisible().equals("yes"))) ||
                   (table.getMemberof() != null)) {
                    PrintWriter printWriter = new PrintWriter(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu\\"+PropertyHelper.APPLICATIONNAME+"\\model\\" + SchemaHelper.upperFirstCase(table.getName()) + ".java", "UTF-8");
                    printWriter.println("package hu." + PropertyHelper.APPLICATIONNAME.toLowerCase() + ".model;");
                    printWriter.println("");
                    printWriter.println("import java.util.Date;");
                    printWriter.println("");
                    printWriter.println("public class " + SchemaHelper.upperFirstCase(table.getName()) + " {");
                    printWriter.println("");
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        printWriter.println("    " + SchemaHelper.getJavaType(dataModel, field) + " " + SchemaHelper.lowerFirstCase(field.getJavaName()) + ";");
                    }
                    //Az mtm kapcsolatokat ábrázoló entitások beemelése
                    for (DataModel.Tables.Table table2: dataModel.getTables().getTable()) {
                        if (table2.getMtm() != null) {
                            String firstTable = SchemaHelper.getFirstTableNameFromMtm(table2).toLowerCase();
                            String secondTable = SchemaHelper.getSecondTableNameFromMtm(table2).toLowerCase();
                            String mtmTablePair = "";
                            if (firstTable.equals(table.getName())) {
                                mtmTablePair = secondTable;
                            }
                            if (secondTable.equals(table.getName())) {
                                mtmTablePair = firstTable;
                            }
                            if (!mtmTablePair.isEmpty()) {
                                printWriter.println("    List<Integer> mtm" + table2.getJavaClassName() + "_" + SchemaHelper.upperFirstCase(mtmTablePair) + "id;");
                                for (DataModel.Tables.Table.Fields.Field field: table2.getFields().getField()) {
                                    if ((!field.getSQLName().equals("creationdate")) &&
                                        (!field.getSQLName().equals("modificationdate")) &&     
                                        (!field.getSQLName().equals("createdby")) &&     
                                        (!field.getSQLName().equals("modifiedby")) &&     
                                        (!field.getSQLName().equals(firstTable.toLowerCase() + "id")) &&     
                                        (!field.getSQLName().equals(secondTable.toLowerCase() + "id")) &&     
                                        (!field.getSQLName().equals(table2.getSQLName() + "id"))) {
                                        printWriter.println("    List<" + SchemaHelper.getJavaType(dataModel, field) + "> mtm" + table2.getJavaClassName() + "_" + SchemaHelper.lowerFirstCase(field.getJavaName()) + ";");
                                    }
                                }
                            }
                        }
                    }
                    printWriter.println("");
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        printWriter.println("    public void set" + SchemaHelper.upperFirstCase(field.getJavaName()) + "(" + SchemaHelper.getJavaType(dataModel, field) + " " + SchemaHelper.lowerFirstCase(field.getJavaName()) +") {");
                        printWriter.println("        this." + SchemaHelper.lowerFirstCase(field.getJavaName()) + " = " + SchemaHelper.lowerFirstCase(field.getJavaName()) + ";");
                        printWriter.println("    }");
                        printWriter.println("");
                        printWriter.println("    public " + SchemaHelper.getJavaType(dataModel, field) + " get" + SchemaHelper.upperFirstCase(field.getJavaName()) + "() {");
                        printWriter.println("        return " + SchemaHelper.lowerFirstCase(field.getJavaName()) + ";");
                        printWriter.println("    }");
                        printWriter.println("");
                    }
                    printWriter.println("    @Override");
                    printWriter.println("    public String toString() {");
                    String commandLine = "        return \"" + SchemaHelper.upperFirstCase(table.getName()) + "{\" + \"";
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        commandLine = commandLine + SchemaHelper.lowerFirstCase(field.getJavaName()) + "=" + "\" + " + SchemaHelper.lowerFirstCase(field.getJavaName()) + " + \", ";
                    }
                    commandLine = commandLine.substring(0, commandLine.length()-3);
                    commandLine = commandLine + "'}';";
                    printWriter.println(commandLine);
                    printWriter.println("    }");
                    printWriter.println("");
                    printWriter.println("}");


                    printWriter.close();
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: file write error.");
        }
    }

    public static void writeDaoFiles(DataModel dataModel) {
        try {
            for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
                if (((table.getVisible() != null) && (table.getVisible().equals("yes"))) ||
                   (table.getMemberof() != null)) {
                    String daoClassName = table.getJavaClassName() + "Dao";
                    String modelClassName = table.getJavaClassName();
                    PrintWriter printWriter = new PrintWriter(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu\\"+PropertyHelper.APPLICATIONNAME+"\\dao\\" + SchemaHelper.upperFirstCase(table.getName()) + "Dao.java", "UTF-8");
                    printWriter.println("package hu." + PropertyHelper.APPLICATIONNAME.toLowerCase() + ".dao;");
                    printWriter.println("");
                    printWriter.println("import org.apache.log4j.Logger;");
                    printWriter.println("import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;");
                    printWriter.println("import java.util.ArrayList;");
                    printWriter.println("import java.util.List;");
                    if (SchemaHelper.hasChoiceListField(dataModel, table.getName())) {
                        printWriter.println("import hu." + PropertyHelper.APPLICATIONNAME.toLowerCase() + ".model.UserListValue;");
                    }
                    printWriter.println("");
                    printWriter.println("@Repository");
                    printWriter.println("public class " + daoClassName + " {");
                    printWriter.println("");
                    printWriter.println("    final static Logger log = Logger.getLogger("+daoClassName+".class);");
                    printWriter.println("    NamedParameterJdbcTemplate namedParameterJdbcTemplate;");
                    printWriter.println("");
                    printWriter.println("    @Autowired");
                    printWriter.println("    public void setDataSource(DataSource dataSource) {");
                    printWriter.println("        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);");
                    printWriter.println("    }");
                    printWriter.println("");
                    if (SchemaHelper.hasChoiceListField(dataModel, table.getName())) {
                        printWriter.println("    static UserListValueDao userListValueDao;");
                        printWriter.println("");
                        printWriter.println("    @Autowired");
                        printWriter.println("    public void setUserListValueDao(UserListValueDao userListValueDao) {");
                        printWriter.println("        this.userListValueDao = userListValueDao;");
                        printWriter.println("    }");
                        printWriter.println("");
                    }
                    //findById
                    printWriter.println("    public " + modelClassName + " findById(Integer id) {");
                    printWriter.println("        log.debug(\"findById CALLED.\");");
                    printWriter.println("");
                    printWriter.println("        Map<String, Object> params = new HashMap<String, Object>();");
                    printWriter.println("        params.put(\"id\", id);");
                    printWriter.println("        log.debug(\"Search parameters:\" + params);");
                    printWriter.println("        String sql = \"SELECT * FROM " + table.getSQLName() + " WHERE " + table.getSQLName() + "id=:id\";");
                    printWriter.println("        " + modelClassName + " result = null;");
                    printWriter.println("");
                    printWriter.println("        try {");
                    printWriter.println("            result = namedParameterJdbcTemplate.queryForObject(sql, params, new " + modelClassName + "Mapper());");
                    printWriter.println("        } catch (EmptyResultDataAccessException e) {");
                    printWriter.println("");
                    printWriter.println("        }");
                    printWriter.println("");
                    printWriter.println("        log.debug(\""+ modelClassName +" found: \" + result);");
                    printWriter.println("        return result;");
                    printWriter.println("    }");
                    printWriter.println("");

                    //findAll
                    printWriter.println("    public List<" + modelClassName + "> findByAll() {");
                    printWriter.println("        log.debug(\"findAll CALLED.\");");
                    printWriter.println("");
                    printWriter.println("        String sql = \"SELECT * FROM " + table.getName().toLowerCase() + "\";");
                    printWriter.println("        List<" + modelClassName + "> result = new ArrayList<" + modelClassName + ">()");
                    printWriter.println("");
                    printWriter.println("        try {");
                    printWriter.println("            result = namedParameterJdbcTemplate.query(sql, new " + modelClassName + "Mapper());");
                    printWriter.println("        } catch (EmptyResultDataAccessException e) {");
                    printWriter.println("");
                    printWriter.println("        }");
                    printWriter.println("");
                    printWriter.println("        return result;");
                    printWriter.println("    }");
                    printWriter.println("");

                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        if ((field.getSearchable() != null) && (field.getSearchable().equals("yes"))) {
                            if (field.getSearchablewith() == null) {
                                if ((field.getReference() == null) || (field.getMemberOfReference().equals("yes"))){
                                    printWriter.println("    public List<" + modelClassName + "> findBy" + SchemaHelper.upperFirstCase(field.getJavaName())+ "(" + SchemaHelper.getJavaType(dataModel, field) + " " + SchemaHelper.lowerFirstCase(field.getJavaName())  +") {");
                                    printWriter.println("        log.debug(\"findBy" + SchemaHelper.upperFirstCase(field.getJavaName()) + " CALLED.\");");
                                    printWriter.println("");
                                    printWriter.println("        Map<String, Object> params = new HashMap<String, Object>();");
                                    printWriter.println("        params.put(\"" + field.getJavaName() + "\", " + field.getJavaName() + ");");
                                    printWriter.println("        log.debug(\"Search parameters:\" + params);");
                                    printWriter.println("        String sql = \"SELECT * FROM " + table.getSQLName() + " WHERE " + field.getSQLName() + "=:"+ field.getJavaName() + "\";");
                                    printWriter.println("        List<" + modelClassName + "> result = new ArrayList<" + modelClassName + ">()");
                                    printWriter.println("");
                                    printWriter.println("        try {");
                                    printWriter.println("            result = namedParameterJdbcTemplate.query(sql, params, new " + modelClassName + "Mapper());");
                                    printWriter.println("        } catch (EmptyResultDataAccessException e) {");
                                    printWriter.println("");
                                    printWriter.println("        }");
                                    printWriter.println("");
                                    printWriter.println("        log.debug(\""+ modelClassName +"(s) found: \" + result);");
                                    printWriter.println("        return result;");
                                    printWriter.println("    }");
                                    printWriter.println("");
                                }
                                if (field.getListname() != null) {
                                    printWriter.println("    public List<" + modelClassName + "> findBy" + SchemaHelper.upperFirstCase(field.getJavaName())+ "(" + SchemaHelper.getJavaType(dataModel, field) + " " + SchemaHelper.lowerFirstCase(field.getJavaName())  +") {");
                                    printWriter.println("        log.debug(\"findBy" + SchemaHelper.upperFirstCase(field.getJavaName()) + " CALLED.\");");
                                    printWriter.println("");
                                    printWriter.println("        Map<String, Object> params = new HashMap<String, Object>();");
                                    printWriter.println("        params.put(\"" + field.getJavaName() + "\", " + field.getJavaName() + ");");
                                    printWriter.println("        log.debug(\"Search parameters:\" + params);");
                                    printWriter.println("        String sql = \"SELECT * FROM " + table.getSQLName() + ", userlistvalue WHERE " + table.getSQLName() + "." + field.getSQLName() + "id = userlistvalue.userlistvalueid AND userlistvalue.type='S' AND userlistvalue.s_name=upper(:"+ field.getJavaName() + ")\";");
                                    printWriter.println("        List<" + modelClassName + "> result = new ArrayList<" + modelClassName + ">()");
                                    printWriter.println("");
                                    printWriter.println("        try {");
                                    printWriter.println("            result = namedParameterJdbcTemplate.query(sql, params, new " + modelClassName + "Mapper());");
                                    printWriter.println("        } catch (EmptyResultDataAccessException e) {");
                                    printWriter.println("");
                                    printWriter.println("        }");
                                    printWriter.println("");
                                    printWriter.println("        log.debug(\""+ modelClassName +"(s) found: \" + result);");
                                    printWriter.println("        return result;");
                                    printWriter.println("    }");
                                    printWriter.println("");
                                }
                            }
                        }
                    }
                    
                    //Mapper
                    printWriter.println("    private static final class " + modelClassName + "Mapper implements RowMapper<" + modelClassName + "> {");
                    printWriter.println("");
                    printWriter.println("        public " + modelClassName + " mapRow(ResultSet rs, int rowNum) throws SQLException {");
                    printWriter.println("            log.debug(\"mapRow CALLED.\");");
                    printWriter.println("            " + modelClassName + " " + table.getJavaObjectName() + " = new " + modelClassName + "();");
                    for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                        String commandLine = "";
                        if (SchemaHelper.getJavaType(dataModel, field).equals("String")) {
                            commandLine = "            " + table.getJavaObjectName() + ".set" + SchemaHelper.upperFirstCase(field.getJavaName()) + "(rs.getString(\""+ field.getSQLName() + "\"));";
                        }
                        if (SchemaHelper.getJavaType(dataModel, field).equals("Date")) {
                            commandLine = "            " + table.getJavaObjectName() + ".set" + SchemaHelper.upperFirstCase(field.getJavaName()) + "(rs.getDate(\""+ field.getSQLName() + "\"));";
                        }
                        if (SchemaHelper.getJavaType(dataModel, field).equals("Integer")) {
                            commandLine = "            " + table.getJavaObjectName() + ".set" + SchemaHelper.upperFirstCase(field.getJavaName()) + "(rs.getInt(\""+ field.getSQLName() + "\"));";
                        }
                        if (field.getType().toLowerCase().equals("choicelist")) {
                            commandLine = "            " + table.getJavaObjectName() + ".set" + SchemaHelper.upperFirstCase(field.getName()) + "(userListValueDao.findById(rs.getInt(\""+ field.getSQLName() + "id\")).getName());";
                        }
                        printWriter.println(commandLine);
                    }
                    printWriter.println("            log.debug(\"" + modelClassName + " mapped: \" + " + table.getJavaObjectName() +");");
                    printWriter.println("            return " + table.getJavaObjectName() + ";");
                    printWriter.println("        }");
                    printWriter.println("    }");
            
                    printWriter.close();
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: file write error.");
        }
    }
    
}
