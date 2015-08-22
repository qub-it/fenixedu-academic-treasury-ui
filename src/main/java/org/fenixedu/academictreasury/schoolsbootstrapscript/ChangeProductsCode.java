package org.fenixedu.academictreasury.schoolsbootstrapscript;

import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.Product;

public class ChangeProductsCode extends CustomTask {

    @Override
    public void runTask() throws Exception {
        changeProductsCode_FROM_SPREADSHEET();
    }

    
    private void changeProductsCode_FROM_SPREADSHEET() {    
        if(Product.findUniqueByCode("PROPINA_MATRICULA_1_PRESTACAO").isPresent()) {Product.findUniqueByCode("PROPINA_MATRICULA_1_PRESTACAO").get().setCode("PROPINA_1_PRESTACAO_MESTRADO_INTEGRADO"); Product.findUniqueByCode("PROPINA_1_PRESTACAO_MESTRADO_INTEGRADO").get().checkRules(); }                                                                                                  
        if(Product.findUniqueByCode("PROPINA_MATRICULA_2_PRESTACAO").isPresent()) {Product.findUniqueByCode("PROPINA_MATRICULA_2_PRESTACAO").get().setCode("PROPINA_2_PRESTACAO_MESTRADO_INTEGRADO"); Product.findUniqueByCode("PROPINA_2_PRESTACAO_MESTRADO_INTEGRADO").get().checkRules(); }                                                                                                  
        if(Product.findUniqueByCode("PROPINA_MATRICULA_3_PRESTACAO").isPresent()) {Product.findUniqueByCode("PROPINA_MATRICULA_3_PRESTACAO").get().setCode("PROPINA_3_PRESTACAO_MESTRADO_INTEGRADO"); Product.findUniqueByCode("PROPINA_3_PRESTACAO_MESTRADO_INTEGRADO").get().checkRules(); }                                                                                                  
        if(Product.findUniqueByCode("PROPINA_MATRICULA_4_PRESTACAO").isPresent()) {Product.findUniqueByCode("PROPINA_MATRICULA_4_PRESTACAO").get().setCode("PROPINA_4_PRESTACAO_MESTRADO_INTEGRADO"); Product.findUniqueByCode("PROPINA_4_PRESTACAO_MESTRADO_INTEGRADO").get().checkRules(); }                                                                                                  
        if(Product.findUniqueByCode("PROPINA_MATRICULA_5_PRESTACAO").isPresent()) {Product.findUniqueByCode("PROPINA_MATRICULA_5_PRESTACAO").get().setCode("PROPINA_5_PRESTACAO_MESTRADO_INTEGRADO"); Product.findUniqueByCode("PROPINA_5_PRESTACAO_MESTRADO_INTEGRADO").get().checkRules(); }                                                                                                  
        if(Product.findUniqueByCode("PROPINA_MATRICULA_6_PRESTACAO").isPresent()) {Product.findUniqueByCode("PROPINA_MATRICULA_6_PRESTACAO").get().setCode("PROPINA_6_PRESTACAO_MESTRADO_INTEGRADO"); Product.findUniqueByCode("PROPINA_6_PRESTACAO_MESTRADO_INTEGRADO").get().checkRules(); }                                                                                                  
        if(Product.findUniqueByCode("PROPINA_MATRICULA_7_PRESTACAO").isPresent()) {Product.findUniqueByCode("PROPINA_MATRICULA_7_PRESTACAO").get().setCode("PROPINA_7_PRESTACAO_MESTRADO_INTEGRADO"); Product.findUniqueByCode("PROPINA_7_PRESTACAO_MESTRADO_INTEGRADO").get().checkRules(); }                                                                                                  
        if(Product.findUniqueByCode("PROPINA_MATRICULA_8_PRESTACAO").isPresent()) {Product.findUniqueByCode("PROPINA_MATRICULA_8_PRESTACAO").get().setCode("PROPINA_8_PRESTACAO_MESTRADO_INTEGRADO"); Product.findUniqueByCode("PROPINA_8_PRESTACAO_MESTRADO_INTEGRADO").get().checkRules(); }                                                                                                  
        if(Product.findUniqueByCode("PROPINA_MATRICULA_9_PRESTACAO").isPresent()) {Product.findUniqueByCode("PROPINA_MATRICULA_9_PRESTACAO").get().setCode("PROPINA_9_PRESTACAO_MESTRADO_INTEGRADO"); Product.findUniqueByCode("PROPINA_9_PRESTACAO_MESTRADO_INTEGRADO").get().checkRules(); }                                                                                                  
    
    
    }
    
}
