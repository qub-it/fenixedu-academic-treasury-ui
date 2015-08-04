package org.fenixedu.academictreasury.services.reports;

import java.io.File;
import java.util.Set;

import com.qubit.terra.docs.core.IDocumentTemplate;
import com.qubit.terra.docs.core.IDocumentTemplateService;

public class DocumentPrinterConfiguration implements IDocumentTemplateService {

    @Override
    public String getFontsPath() {
        if (System.getProperty("os.name").startsWith("Windows")) {
            String path = System.getenv("WINDIR");
            File fontDirectory = new File(path, "Fonts");
            return fontDirectory.getAbsolutePath();
        } else {
            //HACK: THIS SHOULD BE VIA PROPERTY ??!?!?!?!
            return "/usr/share/fonts";
        }
    }

    @Override
    public boolean isOpenOfficeConverting() {
        return true;
    }

    @Override
    public Set<? extends IDocumentTemplate> readActiveDocuments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<? extends IDocumentTemplate> readAllDocuments() {
        // TODO Auto-generated method stub
        return null;
    }

}
