package hu.applicationgenerator.validator;

//A séma <choiceList> elemének, és alatta lévő elemeninek és azokra mutató referenciáinak ellenőrzése

import hu.applicationgenerator.helper.SchemaHelper;
import hu.applicationgenerator.helper.StringHelper;
import hu.applicationgenerator.DataModel;

public class SchemaChoiceListChecker {
    
    //A választólisták ellenőrzése:
    //- duplikációk
    //- név attribútum kötelezőség
    private static void checkChoceListNames(DataModel dataModel) throws Exception {
        int i=0;
        for (DataModel.ChoiceLists.ChoiceList choiceList: dataModel.getChoiceLists().getChoiceList()) {
            if (choiceList.getName() == null) {
                throw new Exception("There is a choiceList which has not name attribute");
            }
            if (choiceList.getElements() == null) {
                throw new Exception(choiceList.getName() + " has no any element.");
            }
            int j=0;
            for (DataModel.ChoiceLists.ChoiceList choiceList2: dataModel.getChoiceLists().getChoiceList()) {
                if ((choiceList2.getName().toLowerCase().equals(choiceList.getName().toLowerCase())) && (j>i)) {
                    throw new Exception("There is duplicated choiceList with same name: " + choiceList.getName());
                }
                j++;
            }
            i++;
        }
    }

    //A választólista elemek ellenőrzése:
    //- duplikációk név és pozíció szerint
    //- név attribútum kötelezőség
    private static void checkChoiceListElements(DataModel dataModel) throws Exception {
        for (DataModel.ChoiceLists.ChoiceList choiceList: dataModel.getChoiceLists().getChoiceList()) {
            if (choiceList.getElements() != null) {
                int i=0;
                for (DataModel.ChoiceLists.ChoiceList.Elements.Element element: choiceList.getElements().getElement()) {
                    int j=0;
                    if (element.getName() == null) {
                        throw new Exception("There is an element without name attribute in " + choiceList.getName() + " choicelist.");
                    }
                    if (element.getPosition() == null) {
                        throw new Exception(choiceList.getName() + "." + element.getName() + " choicelist element ha nos any position attribute.");
                    }
                    for (DataModel.ChoiceLists.ChoiceList.Elements.Element element2: choiceList.getElements().getElement()) {
                        if ((element2.getName().toLowerCase().equals(element.getName().toLowerCase())) && (j>i)) {
                            throw new Exception("There is duplicated element with same name: " + choiceList.getName() + "." + element.getName());
                        }
                        if ((element2.getPosition() == element.getPosition()) && (j>i)) {
                            throw new Exception("The following elements has same position in " + choiceList.getName() + " list: " + element.getName() + ", " + element2.getName());
                        }
                        j++;
                    }
                    i++;
                }
            }
        }
    }
    
    //A választólista típus hivatkozások ellenőrzése, hogy valós választólistákra hivatkoznak-e
    private static void checkValidChoiceListReferences(DataModel dataModel) throws Exception {
        for (DataModel.Tables.Table table: dataModel.getTables().getTable()) {
            if (table.getFields() != null) {
                for (DataModel.Tables.Table.Fields.Field field: table.getFields().getField()) {
                    if (field.getListname() != null) {
                        if (SchemaHelper.getChoiceListByName(dataModel, field.getListname()) == null) {
                           throw new Exception(table.getName() + "." + field.getName() + " field has invalid choiceLista name: " + field.getListname());
                        }
                    }
                }
            }
        }
    }
        
    public static void DoCheck(DataModel dataModel) throws Exception {
        checkChoceListNames(dataModel);
        checkChoiceListElements(dataModel);
        checkValidChoiceListReferences(dataModel);
    }
    
}
