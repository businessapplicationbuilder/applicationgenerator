package hu.applicationgenerator.main;

import hu.applicationgenerator.helper.IOHelper;
import hu.applicationgenerator.helper.PropertyHelper;
import hu.applicationgenerator.writer.JavaSpringMVCWriter;
import hu.applicationgenerator.writer.OracleWriter;
import hu.applicationgenerator.preparator.SchemaTablePreparator;
import hu.applicationgenerator.validator.SchemaTypeChecker;
import hu.applicationgenerator.validator.SchemaChoiceListChecker;
import hu.applicationgenerator.validator.SchemaTableChecker;
import hu.applicationgenerator.DataModel;
import java.nio.file.Files;
import java.io.File;

public class applicationgenerator {

    private static DataModel unmarshallXMLToDataModel() throws javax.xml.bind.JAXBException {
        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(DataModel.class.getPackage().getName());
        javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
        return (DataModel) unmarshaller.unmarshal(new java.io.File("c:\\Project\\SIApplication\\intarchia\\datamodel.xml")); //NOI18N
    }
    
    public static void main(String[] args) {

        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER);
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src");
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main");
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java");
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu");
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu\\"+PropertyHelper.APPLICATIONNAME);
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu\\"+PropertyHelper.APPLICATIONNAME+"\\model");
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu\\"+PropertyHelper.APPLICATIONNAME+"\\dao");
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu\\"+PropertyHelper.APPLICATIONNAME+"\\service");
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu\\"+PropertyHelper.APPLICATIONNAME+"\\controller");
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu\\"+PropertyHelper.APPLICATIONNAME+"\\helper");
        IOHelper.createDirectory(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\sql");
        
        File source = new File("c:\\Project\\SIApplication\\Samples\\DBSchemaCreator\\SecurityHelper.java");
        File dest = new File(PropertyHelper.APPLICATIONROOTFOLDER + "\\src\\main\\java\\hu\\"+PropertyHelper.APPLICATIONNAME+"\\helper\\SecurityHelper.java");        
        try {
        Files.copy(source.toPath(),dest.toPath());
            DataModel dataModel;
            dataModel = unmarshallXMLToDataModel();
            SchemaTypeChecker.DoCheck(dataModel);
            SchemaChoiceListChecker.DoCheck(dataModel);
            SchemaTableChecker.DoCheck(dataModel);
            SchemaTablePreparator.Prepare(dataModel);
            OracleWriter oracleWriter = new OracleWriter();
            oracleWriter.writeSchema(dataModel);
        } catch (javax.xml.bind.JAXBException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            System.out.println("Hiba");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        try {
            DataModel dataModel;
            dataModel = unmarshallXMLToDataModel();
            SchemaTablePreparator.Prepare(dataModel);
            JavaSpringMVCWriter.writeModelFiles(dataModel);
            JavaSpringMVCWriter.writeDaoFiles(dataModel);
        } catch (javax.xml.bind.JAXBException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            System.out.println("Hiba");
        }

    }
    
}
