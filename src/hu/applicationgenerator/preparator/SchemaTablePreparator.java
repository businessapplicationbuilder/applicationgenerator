package hu.applicationgenerator.preparator;

import hu.applicationgenerator.helper.SchemaHelper;
import static hu.applicationgenerator.helper.SchemaHelper.getSQLType;
import hu.applicationgenerator.DataModel;

public class SchemaTablePreparator {

    public static void addDefaultTables(DataModel dataModel) {
        DataModel.Tables.Table table = new DataModel.Tables.Table();

        //FLEXIBLEATTRIBUTE

        table.setName("flexibleattribute");
        table.setDescription("Entitásokhoz tetszőlegesen rögzíthető attribútumok.");
        table.setHistory("yes");
        table.setLabel("Rugalmas attribútumok");
        DataModel.Tables.Table.Fields fields = new DataModel.Tables.Table.Fields();
        table.setFields(fields);
        //Ha van olyan tábla, amelyik igényel flexibilis attribútumot, akkor a felületen is meg kell jelennie az attribútumoknak,
        //egyébként pedig nem kell felületre kivezetni a rugalmas attribútum konfigurációt.
        if (SchemaHelper.hasSchemaFlexibleAttribute(dataModel)) {
            table.setVisible("yes");
        } else {
            table.setVisible("no");
        }
        DataModel.Tables.Table.Fields.Field field = new DataModel.Tables.Table.Fields.Field();
        field.setName("type");
        field.setDescription("Attribútum adattípusa.");
        field.setReference("userlistvalue");
        field.setType("number");
        field.setNullable("no");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Adattípus");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("identifier");
        field.setDescription("Attribútum egyedi azonosítója, neve.");
        field.setSearchable("yes");
        field.setType("middlestring");
        field.setNullable("no");
        field.setUnique("yes");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Név");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("label");
        field.setDescription("Felhasználói felületen megjelenő címke.");
        field.setType("middlestring");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Felületen megjelenő címke");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("entity");
        field.setDescription("Melyik entitáshoz rögzíthető az adott attribútum.");
        field.setReference("userlistvalue");
        field.setSearchable("yes");
        field.setType("number");
        field.setNullable("no");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Kapcsolódó entitás");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("userlist");
        field.setDescription("Választólista típus esetén az elemeket tartalmazó lista.");
        field.setReference("userlist");
        field.setType("number");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Választólista neve");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("ismandatory");
        field.setDescription("Adott entitásnál kötelező-e kitölteni az attribútumot értékkel.");
        field.setType("check");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Kötelező");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("status");
        field.setDescription("Rugalmas attribútum megjelenítési, érvényességi státusza.");
        field.setType("check");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Státusz");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("readonly");
        field.setDescription("Csak olvashatók-e az attribútum értékei.");
        field.setType("check");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Csak olvasható");
        table.getFields().getField().add(field);
        dataModel.getTables().getTable().add(table);

        //FLEXIBLEATTRIBUTEVALUE

        table = new DataModel.Tables.Table();
        table.setName("flexibleattributevalue");
        table.setDescription("Entitásokhoz tetszőlegesen rögzíthető attribútum példányok által felvett értékek.");
        table.setHistory("yes");
        table.setLabel("Rugalmas attribútum értékek");
        table.setVisible("no"); //Önállóan nem jelenítjük meg a tábla tartalmát
        fields = new DataModel.Tables.Table.Fields();
        table.setFields(fields);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("flexibleattribute");
        field.setDescription("Attribútum definíciója.");
        field.setType("number");
        field.setReference("flexibleattribute");
        field.setNullable("no");
        field.setVisible("no");
        field.setEditable("no");
        field.setLabel("Attribútum definíciója");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("valuestring");
        field.setDescription("Attribútum példány által felvett érték, ha az szöveges típusú.");
        field.setType("longstring");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("<<Rugalmas attribútum>>");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("valuenumber");
        field.setDescription("Attribútum példány által felvett érték, ha az szám típusú.");
        field.setType("number");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("<<Rugalmas attribútum>>");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("valuedate");
        field.setDescription("Attribútum példány által felvett érték, ha az dátum típusú.");
        field.setType("date");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("<<Rugalmas attribútum>>");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("valuelistitem");
        field.setDescription("Attribútum példány által felvett érték, ha az választólista típusú.");
        field.setType("number");
        field.setReference("userlistvalue");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("<<Rugalmas attribútum>>");
        table.getFields().getField().add(field);
        for (DataModel.Tables.Table table2: dataModel.getTables().getTable()) {
            if ((table2.getFlexibleattribute() != null) && (table2.getFlexibleattribute().equals("yes"))) {
                field = new DataModel.Tables.Table.Fields.Field();
                field.setName(table2.getName());
                field.setDescription("Kapcsolódó " + table.getName() + " entitás");
                field.setType("number");
                field.setReference(table2.getName());
                field.setVisible("no");
                field.setEditable("no");
                field.setLabel("Rugalmas attribútum kapcsolata a " + table2.getName() + " entitással");
                table.getFields().getField().add(field);
            }
        }
        dataModel.getTables().getTable().add(table);

        //USERLIST

        table = new DataModel.Tables.Table();
        table.setName("userlist");
        table.setDescription("Az alkalmazásban karbantartott választólisták fejadatai.");
        table.setHistory("yes");
        table.setLabel("Választólisták");
        table.setVisible("yes");
        fields = new DataModel.Tables.Table.Fields();
        table.setFields(fields);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("name");
        field.setDescription("Választólista neve.");
        field.setSearchable("yes");
        field.setType("middlestring");
        field.setNullable("no");
        field.setUnique("yes");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Név");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("description");
        field.setDescription("Választólista leírása, szöveges jellemzése.");
        field.setType("longstring");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Leírás");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("type");
        field.setDescription("Lista típusa felhasználás szerint: F=flexibilis attribútum, S=Rendszer működéséhez elengedhetetlen.");
        field.setType("check");
        field.setNullable("no");
        field.setVisible("yes");
        field.setEditable("no");
        field.setLabel("Típus");
        table.getFields().getField().add(field);
        dataModel.getTables().getTable().add(0, table);

        //USERLISTVALUE

        table = new DataModel.Tables.Table();
        table.setName("userlistvalue");
        table.setDescription("Az alkalmazásban karbantartott választólisták elemei.");
        table.setHistory("yes");
        table.setLabel("Választólista elemek");
        table.setVisible("no");
        table.setMemberof("userlist");
        fields = new DataModel.Tables.Table.Fields();
        table.setFields(fields);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("name");
        field.setDescription("Listaelem neve.");
        field.setType("middlestring");
        field.setNullable("no");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Név");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("description");
        field.setDescription("Listaelem leírása, szöveges jellemzése.");
        field.setType("longstring");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Leírás");
        table.getFields().getField().add(field);
        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("position");
        field.setDescription("Listaelem listán belüli pozíciója.");
        field.setType("number");
        field.setVisible("yes");
        field.setEditable("yes");
        field.setLabel("Pozíció");
        field.setNullable("no");
        table.getFields().getField().add(field);
/*        field = new DataModel.Tables.Table.Fields.Field();
        field.setName("userlist");
        field.setDescription("Lista fejrekord.");
        field.setType("number");
        field.setNullable("no");
        field.setVisible("no");
        field.setEditable("no");
        field.setReference("userlist");
        field.setLabel("Lista fejrekord");
        table.getFields().getField().add(field);*/
        dataModel.getTables().getTable().add(1,table);
    }
    
    
    private static void completeTableFields(DataModel dataModel) {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() == null) {
                DataModel.Tables.Table.Fields fields = new DataModel.Tables.Table.Fields();
                table.setFields(fields);
            }
            DataModel.Tables.Table.Fields.Field field = new DataModel.Tables.Table.Fields.Field();
            field.setName("creationDate");
            field.setNullable("no");
            field.setDescription("Rekord létrehozásának időpontja.");
            field.setType("date");
            field.setVisible("yes");
            field.setEditable("no");
            field.setLabel("Létrehozás dátuma");
            table.getFields().getField().add(field);
            field = new DataModel.Tables.Table.Fields.Field();
            field.setName("createdBy");
            field.setNullable("no");
            field.setDescription("Rekordot létrehozó felhasználó neve.");
            field.setType("middlestring");
            field.setVisible("yes");
            field.setEditable("no");
            field.setLabel("Létrehozó");
            table.getFields().getField().add(field);
            field = new DataModel.Tables.Table.Fields.Field();
            field.setName("modificationDate");
            field.setDescription("Rekord módosításának időpontja.");
            field.setType("date");
            field.setVisible("yes");
            field.setEditable("no");
            field.setLabel("Módosítás dátuma");
            table.getFields().getField().add(field);
            field = new DataModel.Tables.Table.Fields.Field();
            field.setName("modifiedBy");
            field.setDescription("Rekordot módosító felhasználó neve.");
            field.setType("middlestring");
            field.setVisible("yes");
            field.setEditable("no");
            field.setLabel("Módosító");
            table.getFields().getField().add(field);
            //Primary key előállítás
            field = new DataModel.Tables.Table.Fields.Field();
            field.setName(table.getName() + "Id");
            field.setDescription("Rekord elsődleges belső azonosítója.");
            field.setType("number");
            field.setPrimarykey("yes");
            field.setVisible("no");
            field.setEditable("no");
            field.setLabel("Belső azonosító");
            table.getFields().getField().add(field);
            //Tartalmazás kapcsolat (1:n) külső kulcs mező generálása
            if (table.getMemberof() != null) {
                field = new DataModel.Tables.Table.Fields.Field();
                field.setName(SchemaHelper.getTableNameFromMemberOf(table));
                field.setDescription("Kapcsolódó " + SchemaHelper.getTableNameFromMemberOf(table) + " rekord.");
                field.setType("number");
                field.setNullable("no");
                field.setReference(table.getMemberof());
                field.setVisible("no");
                field.setEditable("no");
                field.setSearchable("yes");
                field.setLabel("Külső kulcs a tartalmazó rekordra.");
                table.getFields().getField().add(field);
            }
            //MTM kapcsolat (m:n) külső kulcs mezők generálása
            if (table.getMtm() != null) {
                field = new DataModel.Tables.Table.Fields.Field();
                field.setName(SchemaHelper.getFirstTableNameFromMtm(table));
                field.setDescription("Kapcsolódó " + SchemaHelper.getFirstTableNameFromMtm(table) + " rekord.");
                field.setType("number");
                field.setNullable("no");
                field.setReference(SchemaHelper.getFirstTableNameFromMtm(table));
                field.setVisible("no");
                field.setEditable("no");
                field.setSearchable("yes");
                field.setLabel("Külső kulcs az mtm kapcsolat első rekordjára.");
                table.getFields().getField().add(field);
                field = new DataModel.Tables.Table.Fields.Field();
                field.setName(SchemaHelper.getSecondTableNameFromMtm(table));
                field.setDescription("Kapcsolódó " + SchemaHelper.getSecondTableNameFromMtm(table) + " rekord.");
                field.setType("number");
                field.setNullable("no");
                field.setReference(SchemaHelper.getSecondTableNameFromMtm(table));
                field.setVisible("no");
                field.setEditable("no");
                field.setSearchable("yes");
                field.setLabel("Külső kulcs az mtm kapcsolat második rekordjára.");
                table.getFields().getField().add(field);
            }
            //Külső kulcs kapcsolat mezők nevének kiegészítése az Id utótaggal
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field2: table.getFields().getField()) {
                    field2.setJavaName(field2.getName());
                    if (field2.getReference() != null) {
                        if (field2.getListname() != null) {
                            field2.setJavaName(field2.getName());
                        } else {
                            field2.setName(field2.getName() + "Id");
                            field2.setJavaName(field2.getName());
                        }
                    }
                }
            }
        }
    
    }
    
    private static void completeAttributes(DataModel dataModel) {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getHistory() == null) {
                table.setHistory("no");
            }
            if (table.getVisible() == null) {
                table.setVisible("no");
            }
            if (table.getFlexibleattribute() == null) {
                table.setFlexibleattribute("no");
            }
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if (field.getEditable() == null) {
                        field.setEditable("no");
                    }
                    if (field.getNullable() == null) {
                        field.setNullable("yes");
                    }
                    if (field.getSearchable() == null) {
                        field.setSearchable("no");
                    }
                    if (field.getSearchablestandalone() == null) {
                        field.setSearchablestandalone("no");
                    }
                    if (field.getUnique() == null) {
                        field.setUnique("no");
                    }
                    if (field.getVisible() == null) {
                        field.setVisible("no");
                    }
                }
            }
        }
    }
    
    private static void setSearchFields(DataModel dataModel) {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if ((field.getSearchable() != null) && (field.getSearchable().toLowerCase().equals("yes"))) {
                        if (getSQLType(dataModel, field).contains("varchar")) {
                            field.setSearchfield("s_" + field.getName());
                        } else {
                            field.setSearchfield(field.getName());
                        }
                    }
                }
            }
        }
    }
    
    private static void addReferenceToChoiceListFields(DataModel dataModel) {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if (field.getType().equals("choicelist")) {
                        field.setReference("userlistvalue");
                    }
                }
            }
        }
    }
 

    private static void prepareAttributes(DataModel dataModel) {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            
            //Az Oracle azonosító case insensitive, így minden kicsi lesz.
            table.setSQLName(table.getName().toLowerCase());
            //Java entitás/osztály név nagy betűvel kezdődik, míg mező/objektum név kis betűvel.
            table.setJavaClassName(SchemaHelper.upperFirstCase(table.getName()));
            table.setJavaObjectName(SchemaHelper.lowerFirstCase(table.getName()));
            
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {

                    //Az Oracle azonosító case insensitive, így minden kicsi lesz.
                    field.setSQLName(field.getName().toLowerCase()); 
                    //Java mező/változó név kis betűvel kezdődik.
                    field.setJavaName(SchemaHelper.lowerFirstCase(field.getName())); 

                    //Az Oralce típusban benne szerepel a hossza is zárójelben, amennyiben értelmezett.
                    field.setSQLType(SchemaHelper.getSQLType(dataModel, field).toLowerCase()); 
                    //Java típus esetén nincs variálás a kis-nagy betűkkel, mert a típus lehet elemi típus is, ami kisbetűs.
                    field.setJavaType(SchemaHelper.getJavaType(dataModel, field)); 

                    //Java-ban le kell programozni a hosszellenőrzést, míg Oracle ezt adatbázis motorból megoldja, így
                    //nincs szükség küldön az Oracle mezőhossz definiálására. 
                    field.setJavaLength(SchemaHelper.getTypeByName(dataModel, field.getType()).getLength()); 

                    //Az adott mezú megjelölése, ha az entitás tartalmazás (1:n) kapcsolat megteremtését szolgáló mező.
                    if ((table.getMemberof() != null) && (field.getName().toLowerCase().equals(SchemaHelper.getTableNameFromMemberOf(table).toLowerCase() + "id"))) {
                        field.setMemberOfReference("yes");
                    } else {
                        field.setMemberOfReference("no");
                    }
                    
                }
            }
        }
        
        
    }
    
    public static void Prepare(DataModel dataModel) {
        addDefaultTables(dataModel);
        addReferenceToChoiceListFields(dataModel);
        completeTableFields(dataModel);
        //completeAttributes(dataModel);
        prepareAttributes(dataModel);
        setSearchFields(dataModel);
    }
    
}
