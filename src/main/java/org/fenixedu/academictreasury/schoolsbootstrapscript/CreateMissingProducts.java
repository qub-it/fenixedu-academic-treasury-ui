package org.fenixedu.academictreasury.schoolsbootstrapscript;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.ProductGroup;
import org.fenixedu.treasury.domain.VatExemptionReason;
import org.fenixedu.treasury.domain.VatType;
import org.joda.time.format.DateTimeFormat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class CreateMissingProducts extends CustomTask {

    private static final String PAGAMENTO_EM_AVANCO = "PAGAMENTO";
    private static final String INTEREST_CODE = "INTEREST";
    private static final Map<String, String> FI_LOOKUP = Maps.newHashMap();
    private static final int NOT_APPLIED = -1;

    static {
//      FI_LOOKUP.put("FMD", "503013366");
//      FI_LOOKUP.put("FL", "502657456");
//      FI_LOOKUP.put("FF", "502659807");
//      FI_LOOKUP.put("RUL", "510739024");
      FI_LOOKUP.put("FMV", "502286326");
}
    
    @Override
    public void runTask() throws Exception {
        createMissingProducts();
    }

  private void createMissingProducts() {
      final ProductGroup TUITION_productGroup = ProductGroup.findByCode("TUITION");
      final ProductGroup EMOLUMENT_productGroup = ProductGroup.findByCode("EMOLUMENT");
      final ProductGroup OTHER_productGroup = ProductGroup.findByCode("OTHER");
      
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_1_PRESTACAO_3_CICLO_name = new LocalizedString(pt(), "1º Prestação da Propina de 3º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_1_PRESTACAO_3_CICLO", PROPINA_1_PRESTACAO_3_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_2_PRESTACAO_3_CICLO_name = new LocalizedString(pt(), "2º Prestação da Propina de 3º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_2_PRESTACAO_3_CICLO", PROPINA_2_PRESTACAO_3_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_3_PRESTACAO_3_CICLO_name = new LocalizedString(pt(), "3º Prestação da Propina de 3º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_3_PRESTACAO_3_CICLO", PROPINA_3_PRESTACAO_3_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_4_PRESTACAO_3_CICLO_name = new LocalizedString(pt(), "4º Prestação da Propina de 3º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_4_PRESTACAO_3_CICLO", PROPINA_4_PRESTACAO_3_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_5_PRESTACAO_3_CICLO_name = new LocalizedString(pt(), "5º Prestação da Propina de 3º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_5_PRESTACAO_3_CICLO", PROPINA_5_PRESTACAO_3_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_6_PRESTACAO_3_CICLO_name = new LocalizedString(pt(), "6º Prestação da Propina de 3º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_6_PRESTACAO_3_CICLO", PROPINA_6_PRESTACAO_3_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_7_PRESTACAO_3_CICLO_name = new LocalizedString(pt(), "7º Prestação da Propina de 3º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_7_PRESTACAO_3_CICLO", PROPINA_7_PRESTACAO_3_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_8_PRESTACAO_3_CICLO_name = new LocalizedString(pt(), "8º Prestação da Propina de 3º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_8_PRESTACAO_3_CICLO", PROPINA_8_PRESTACAO_3_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_9_PRESTACAO_3_CICLO_name = new LocalizedString(pt(), "9º Prestação da Propina de 3º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_9_PRESTACAO_3_CICLO", PROPINA_9_PRESTACAO_3_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_1_PRESTACAO_2_CICLO_name = new LocalizedString(pt(), "1º Prestação da Propina de 2º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_1_PRESTACAO_2_CICLO", PROPINA_1_PRESTACAO_2_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_2_PRESTACAO_2_CICLO_name = new LocalizedString(pt(), "2º Prestação da Propina de 2º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_2_PRESTACAO_2_CICLO", PROPINA_2_PRESTACAO_2_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_3_PRESTACAO_2_CICLO_name = new LocalizedString(pt(), "3º Prestação da Propina de 2º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_3_PRESTACAO_2_CICLO", PROPINA_3_PRESTACAO_2_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_4_PRESTACAO_2_CICLO_name = new LocalizedString(pt(), "4º Prestação da Propina de 2º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_4_PRESTACAO_2_CICLO", PROPINA_4_PRESTACAO_2_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_5_PRESTACAO_2_CICLO_name = new LocalizedString(pt(), "5º Prestação da Propina de 2º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_5_PRESTACAO_2_CICLO", PROPINA_5_PRESTACAO_2_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_6_PRESTACAO_2_CICLO_name = new LocalizedString(pt(), "6º Prestação da Propina de 2º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_6_PRESTACAO_2_CICLO", PROPINA_6_PRESTACAO_2_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_7_PRESTACAO_2_CICLO_name = new LocalizedString(pt(), "7º Prestação da Propina de 2º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_7_PRESTACAO_2_CICLO", PROPINA_7_PRESTACAO_2_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_8_PRESTACAO_2_CICLO_name = new LocalizedString(pt(), "8º Prestação da Propina de 2º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_8_PRESTACAO_2_CICLO", PROPINA_8_PRESTACAO_2_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
      if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) { final LocalizedString PROPINA_9_PRESTACAO_2_CICLO_name = new LocalizedString(pt(), "9º Prestação da Propina de 2º Ciclo").with(en(), ""); Product.create(TUITION_productGroup, "PROPINA_9_PRESTACAO_2_CICLO", PROPINA_9_PRESTACAO_2_CICLO_name, defaultUnitOfMeasure(), true, defaultVatType(), fromAcronymsToFinantialInstitutionList("FMV"), VatExemptionReason.findByCode("M07")); }                                                                                                 
  
  
  
  
  
  
  }
    
  
  private List<FinantialInstitution> fromAcronymsToFinantialInstitutionList(final String acronyms) {
      String[] split = acronyms.split(",");

      final List<FinantialInstitution> result = new ArrayList<FinantialInstitution>();
      for (String acronym : split) {
          if (!FI_LOOKUP.containsKey(acronym.trim())) {
              continue;
          }

          result.add(FinantialInstitution.findUniqueByFiscalCode(FI_LOOKUP.get(acronym.trim())).get());
      }

      return result;
  }

  private FinantialEntity oneOfFinantialEntity(final String acronyms) {
      return FinantialEntity.findAll()
              .filter(l -> l.getAdministrativeOffice() == AdministrativeOffice.readDegreeAdministrativeOffice()).findFirst()
              .get();
  }

  private DegreeType findDegreeTypeByCode(final String code) {
      if (code.isEmpty()) {
          return null;
      }

      for (DegreeType degreeType : Bennu.getInstance().getDegreeTypeSet()) {
          if (code.equals(degreeType.getCode())) {
              return degreeType;
          }
      }

      return null;
  }

  private Degree findDegree(final String code) {
      if (code.isEmpty()) {
          return null;
      }

      return Degree.find(code);
  }

  private BigDecimal maximumAmount(int v) {
      if (v == NOT_APPLIED) {
          return BigDecimal.ZERO;
      }

      return new BigDecimal(v);
  }

  private org.joda.time.LocalDate fixedDueDate(final String v) {
      if (v.isEmpty()) {
          return null;
      }

      return DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(v);
  }

  private org.joda.time.LocalDate parseLocalDate(final String v) {
      if (v.isEmpty()) {
          return null;
      }

      return DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(v);
  }

  private BigDecimal amount(final String v) {
      if (v.isEmpty()) {
          return null;
      }

      return new BigDecimal(v);
  }

  private Locale pt() {
      return new Locale("PT", "pt");
  }

  private Locale en() {
      return new Locale("EN", "en");
  }

  private LocalizedString defaultUnitOfMeasure() {
      return new LocalizedString(pt(), "Unidade").with(en(), "Unit");
  }

  private VatType defaultVatType() {
      return VatType.findByCode("ISE");
  }

  
  public Set<Degree> readDegreeBySigla(final String sigla) {
      final Set<Degree> result = Sets.newHashSet();
      for (final Degree degree : Bennu.getInstance().getDegreesSet()) {
          if(sigla.equals(degree.getSigla())) {
              result.add(degree);
          }
      }
      
      return result;
  }
  
  
}
