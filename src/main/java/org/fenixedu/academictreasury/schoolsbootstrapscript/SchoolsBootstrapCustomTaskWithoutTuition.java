package org.fenixedu.academictreasury.schoolsbootstrapscript;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.CurricularYear;
import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeCurricularPlan;
import org.fenixedu.academic.domain.EvaluationConfiguration;
import org.fenixedu.academic.domain.EvaluationSeason;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degree.degreeCurricularPlan.DegreeCurricularPlanState;
import org.fenixedu.academic.domain.serviceRequests.AcademicServiceRequestSituationType;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestCategory;
import org.fenixedu.academic.domain.serviceRequests.ServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.AcademicServiceRequestType;
import org.fenixedu.academic.domain.serviceRequests.documentRequests.DocumentRequestType;
import org.fenixedu.academic.domain.student.RegistrationProtocol;
import org.fenixedu.academic.domain.student.StatuteType;
import org.fenixedu.academic.util.PeriodState;
import org.fenixedu.academictreasury.domain.emoluments.AcademicTax;
import org.fenixedu.academictreasury.domain.emoluments.ServiceRequestMapEntry;
import org.fenixedu.academictreasury.domain.settings.AcademicTreasurySettings;
import org.fenixedu.academictreasury.domain.tariff.AcademicTariff;
import org.fenixedu.academictreasury.domain.tuition.EctsCalculationType;
import org.fenixedu.academictreasury.domain.tuition.TuitionCalculationType;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlan;
import org.fenixedu.academictreasury.domain.tuition.TuitionPaymentPlanGroup;
import org.fenixedu.academictreasury.dto.tariff.AcademicTariffBean;
import org.fenixedu.academictreasury.dto.tariff.TuitionPaymentPlanBean;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.commons.i18n.LocalizedString;
import org.fenixedu.treasury.domain.Currency;
import org.fenixedu.treasury.domain.FinantialEntity;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.Product;
import org.fenixedu.treasury.domain.ProductGroup;
import org.fenixedu.treasury.domain.VatExemptionReason;
import org.fenixedu.treasury.domain.VatType;
import org.fenixedu.treasury.domain.exemption.TreasuryExemptionType;
import org.fenixedu.treasury.domain.settings.TreasurySettings;
import org.fenixedu.treasury.domain.tariff.DueDateCalculationType;
import org.fenixedu.treasury.domain.tariff.InterestType;
import org.joda.time.format.DateTimeFormat;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SchoolsBootstrapCustomTaskWithoutTuition extends CustomTask {

    /******************************************
     * ++++ THINGS TO DO FIRST ++++
     * 
     * 1. Migrate Finantial Institutions
     * 2. Open and make current 2015/2016
     * 3. Create Execution Degrees
     * ****************************************
     */

    private static final String PAGAMENTO_EM_AVANCO = "PAGAMENTO";
    private static final String INTEREST_CODE = "INTEREST";
    private static final Map<String, String> FI_LOOKUP = Maps.newHashMap();
    private static final int NOT_APPLIED = -1;

    private static final Map<String, String> productsMap = Maps.newHashMap();

    static {
//            FI_LOOKUP.put("FMD", "503013366");
        FI_LOOKUP.put("FL", "502657456");
//            FI_LOOKUP.put("FF", "502659807");
//            FI_LOOKUP.put("RUL", "510739024");
        //        FI_LOOKUP.put("FMV", "502286326");

        productsMap.put("PROPINA_MATRICULA", "PROPINA_MATRICULA");
        productsMap.put("PROPINA_MATRICULA_1_PRESTACAO", "PROP_MAT_1_PRESTAC");
        productsMap.put("PROPINA_MATRICULA_2_PRESTACAO", "PROP_MAT_2_PRESTAC");
        productsMap.put("PROPINA_MATRICULA_3_PRESTACAO", "PROP_MAT_3_PRESTAC");
        productsMap.put("PROPINA_MATRICULA_4_PRESTACAO", "PROP_MAT_4_PRESTAC");
        productsMap.put("PROPINA_MATRICULA_5_PRESTACAO", "PROP_MAT_5_PRESTAC");
        productsMap.put("PROPINA_MATRICULA_6_PRESTACAO", "PROP_MAT_6_PRESTAC");
        productsMap.put("PROPINA_MATRICULA_7_PRESTACAO", "PROP_MAT_7_PRESTAC");
        productsMap.put("PROPINA_MATRICULA_8_PRESTACAO", "PROP_MAT_8_PRESTAC");
        productsMap.put("PROPINA_MATRICULA_9_PRESTACAO", "PROP_MAT_9_PRESTAC");
        productsMap.put("PROPINA_UNIDADES_CURRICULARES_ISOLADAS", "PROP_UNID_CURR_ISOL");
        productsMap.put("PROPINA_UNIDADES_EXTRACURRICULARES", "PROP_UNID_EXTRACUR");
        productsMap.put("SEGURO_ESCOLAR", "SEGURO_ESCOLAR");
        productsMap.put("CARTA_CURSO", "CARTA_CURSO");
        productsMap.put("CARTA_CURSO_2_VIA", "CARTA_CURSO_2_VIA");
        productsMap.put("CARTA_TITULO_AGREGACAO", "CARTA_TIT_AGREG");
        productsMap.put("CARTA_TITULO_HABILITACAO_COORDENACAO_CIENTIFICA", "CARTA_TIT_H_C_CIEN");
        productsMap.put("CARTA_TITULO_2_VIA", "CARTA_TITULO_2_VIA");
        productsMap.put("PROCESSO_RECONHECIMENTO_GRAU", "PROC_REC_GRAU");
        productsMap.put("PROCESSO_EQUIVALENCIA_GRAU", "PROC_EQUIV_GRAU");
        productsMap.put("CERTIDAO_RECONHECIMENTO_GRAU", "CERT_REC_GRAU");
        productsMap.put("CERTIDAO_EQUIVALENCIA_GRAU", "CERT_EQUIV_GRAU");
        productsMap.put("PEDIDO_REGISTO_GRAUS_DL_341_2007", "PED_REG_G_DL3412007");
        productsMap.put("PROVAS_AVALIACAO_CAPACIDADE_M23_ADMISSAO", "PR_AV_CAP_M23_ADM");
        productsMap.put("PROVAS_AVALIACAO_CAPACIDADE_M23_RECLAMACAO", "PR_AV_CAP_M23_RECL");
        productsMap.put("CERTIDAO_REGISTO", "CERT_REGISTO");
        productsMap.put("CERTIDAO_REGISTO_2_VIA", "CERT_REGISTO_2_VIA");
        productsMap.put("SUPLEMENTO_DIPLOMA_2_VIA", "SUPL_DIPLOMA_2_VIA");
        productsMap.put("CERTIDAO_REGISTO_CURSO_POS_GRADUADO_ESPECIALIZACAO", "CERT_REG_C_P_GR_ESP");
        productsMap.put("DIPLOMA_CURSO_DOUTORAMENTO", "DIP_CURSO_DOUT");
        productsMap.put("DIPLOMA_CURSO_MESTRADO", "DIP_CURSO_MEST");
        productsMap.put("DIPLOMA_CURSO_ESPECIALIZACAO", "DIP_CURSO_ESPEC");
        productsMap.put("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO", "ADM_PROV_AC_DOUT");
        productsMap.put("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO_ART_33_DL_74_2006", "ADM_PACDA33DL742006");
        productsMap.put("ADMISSAO_PROVAS_ACADEMICAS_AGREGACAO", "ADM_P_AC_AGREG");
        productsMap.put("ADMISSAO_PROVAS_ACADEMICAS_HABILITACAO_COORDENACAO_CIENTIFICA", "ADM_P_AC_H_COORCIEN");
        productsMap.put("ADMISSAO_PROVAS_ACADEMICAS_MESTRADO", "ADM_P_AC_MESTRADO");
        productsMap.put("CERTIDAO_CONCLUSAO", "CERT_CONCLUSAO");
        productsMap.put("CERTIDAO_PROVAS_APTIDAO_PEDAGOGICA", "CERT_P_APTIDAOPEDAG");
        productsMap.put("CERTIDAO_OBTENCAO_TITULO_AGREGADO", "CERT_OBT_TIT_AGR");
        productsMap.put("CERTIFICADO_TITULO_HABILITACAO_COORDENACAO_CIENTIFICA", "CERT_TIT_H_COORCIEN");
        productsMap.put("CERTIDAO_CONCLUSAO_CURSO_MESTRADO", "CERT_CON_C_MESTRADO");
        productsMap.put("CERTIDAO_CONCLUSAO_CURSO_DOUTORAMENTO", "CERT_CON_C_DOUT");
        productsMap.put("CERTIDAO_CONCLUSAO_CURSO_ESPECIALIZACAO", "CERT_CON_C_ESPEC");
        productsMap.put("CERTIDAO_MATRICULA", "CERT_MATRICULA");
        productsMap.put("CERTIDAO_INSCRICAO", "CERT_INSCRICAO");
        productsMap.put("CERTIDAO_FREQUENCIA_EXAME", "CERT_FREQ_EXAME");
        productsMap.put("CERTIDAO_CONDUTA_ACADEMICA", "CERT_CONDUTA_ACAD");
        productsMap.put("CERTIFICADO_NARRATIVA_TEOR", "CERT_NARRATIVA_TEOR");
        productsMap.put("CERTIFICADO_AVALIACAO_CAPACIDADE_M23", "CERT_AVAL_CAP_M23");
        productsMap.put("CERTIDAO_CARGAS_HORARIAS_CONTEUDOS_PROGRAMATICOS", "CERT_C_HOR_CONTPROG");
        productsMap.put("CERTIDAO_FOTOCOPIA", "CERT_FOTOCOPIA");
        productsMap.put("PEDIDO_CREDITACAO_CONHECIMENTOS_COMPETENCIAS", "PED_CRED_CON_COMP");
        productsMap.put("PEDIDO_CREDITACAO_COMPETENCIAS_ACADEMICAS", "PED_CRED_COMP_ACAD");
        productsMap.put("PEDIDO_CREDITACAO_COMPETENCIAS_PROFISSIONAIS", "PED_CRED_COMP_PROF");
        productsMap.put("CANDIDATURA_REINGRESSO", "CAND_REINGRESSO");
        productsMap.put("CANDIDATURA_REINGRESSO_ALUNOS_ULISBOA", "CAND_REING_AULISBOA");
        productsMap.put("CANDIDATURA_TRANSFERENCIA", "CAN_TRANS");
        productsMap.put("CANDIDATURA_TRANSFERENCIA_ALUNOS_ULISBOA", "CAND_TRANS_AULISBOA");
        productsMap.put("CANDIDATURA_MUDANCA_CURSO", "CAND_MUD_CUR");
        productsMap.put("CANDIDATURA_MUDANCA_CURSO_ALUNOS_ULISBOA", "CAND_MUD_CUR_AULISB");
        productsMap.put("CANDIDATURA_MAIORES_23_ANOS", "CAND_MAI_23ANOS");
        productsMap.put("CANDIDATURA_OUTRO", "CAND_OUTRO");
        productsMap.put("CANDIDATURA_OUTRO_ALUNOS_ULISBOA", "CAND_OUT_AULISBOA");
        productsMap.put("CANDIDATURA_REGIME_LIVRE", "CAND_REGIME_LIVRE");
        productsMap.put("CANDIDATURA_UNIDADES_CURRICULARES_ISOLADAS", "CAND_UNID_CUR_ISOL");
        productsMap.put("CANDIDATURA_CURSO_APERFEICOAMENTO", "CAND_CURSO_APERF");
        productsMap.put("CANDIDATURA_CURSO_B_LEARNING", "CAND_CURSO_BLEARN");
        productsMap.put("CANDIDATURA_ESPECIALIZACAO", "CAND_ESPEC");
        productsMap.put("CANDIDATURA_MESTRADO", "CAND_MESTRADO");
        productsMap.put("CANDIDATURA_DOUTORAMENTO", "CAND_DOUTORAMENTO");
        productsMap.put("PRATICA_ATOS_FORA_PRAZO", "PRAT_ATOS_FORA_PRA");
        productsMap.put("AVERBAMENTO", "AVERBAMENTO");
        productsMap.put("TAXA_MELHORIA", "TAXA_MELHORIA");
        productsMap.put("PEDIDO_PERMUTA", "PEDIDO_PERMUTA");
        productsMap.put("VALIDACAO_PROCESSOS_ACESSO_M23_OUTRAS_INSTITUICOES", "VAL_PROC_AM23OINST");
        productsMap.put("FOTOCOPIA", "FOTOCOPIA");
        productsMap.put("TAXA_CANDIDATURA", "TAXA_CANDIDATURA");
        productsMap.put("TAXA_MATRICULA_INSCRICAO_DOUTORAMENTO", "TX_MAT_INSC_DOUT");
        productsMap.put("TAXA_MATRICULA_INSCRICAO_FORMACAO_INICIAL", "TX_MAT_INSC_FORINIC");
        productsMap.put("TAXA_ENVIO_CORREIO", "TAXA_ENVIO_CORREIO");
        productsMap.put("TAXA_DEVOLUCAO_CHEQUE", "TX_DEVOLUCAO_CHEQUE");
        productsMap.put("IMPRESSOS", "IMPRESSOS");
        productsMap.put("CANDIDATURA_CURSOS_NAO_CONFERENTES_GRAU", "CAND_CUR_N_CONF_GR");
        productsMap.put("PEDIDO_EQUIVALENCIA_CREDITACAO", "PED_EQUIV_CRED");
        productsMap.put("TAXA_MATRICULA", "TAXA_MATRICULA");
        productsMap.put("TAXA_INSCRICAO", "TAXA_INSCRICAO");
        productsMap.put("TAXA_INSCRICAO_CURRICULARES_ISOLADAS", "TX_INSC_CUR_ISOL");
        productsMap.put("TAXA_RENOVACAO_INSCRICAO", "TX_REN_INSC");
        productsMap.put("DECLARACAO_MATRICULA", "DECLARACAO_MAT");
        productsMap.put("DECLARACAO_INSCRICAO", "DECLARACAO_INSC");
        productsMap.put("PEDIDO_MUDANCA_TURMA", "PEDIDO_MUD_TURMA");
        productsMap.put("PEDIDO_MUDANCA_UNIDADE_CURRICULAR", "PED_MUD_UNID_CUR");
        productsMap.put("REVISAO_PROVAS_CAUCAO", "REV_PROVAS_CAUCAO");
        productsMap.put("PLANO_INTEGRACAO_CURRICULAR_REINGRESSO", "PLANO_INT_CUR_REIN");
        productsMap.put("TAXA_PROCESSO_ADMINISTRATIVO_APLICADO_ALUNOS_INCOMING", "TX_PR_ADM_APAINCOM");
        productsMap.put("TAXA_CANDIDATURA_CURRICULARES_ISOLADAS", "TX_CAND_CUR_ISOL");
        productsMap.put("RENOVACAO_INSCRICAO", "RENOVACAO_INSCRICAO");
        productsMap.put("CERTIDAO_APROVEITAMENTO", "CERT_APROV");
        productsMap.put("CERTIDAO_APROVEITAMENTO_ESCOLAR_TRANSICAO_ANO", "CERT_APROV_ESC_TANO");
        productsMap.put("_2_VIA_LOGBOOK", "_2_VIA_LOGBOOK");
        productsMap.put("PORTES_CORREIO_NACIONAL", "POR_CORREIO_NAC");
        productsMap.put("PORTES_CORREIO_INTERNACIONAL", "POR_CORREIO_INTER");
        productsMap.put("PROPINA_1_PRESTACAO_MESTRADO_INTEGRADO", "PROP_1_PREST_MI");
        productsMap.put("PROPINA_2_PRESTACAO_MESTRADO_INTEGRADO", "PROP_2_PREST_MI");
        productsMap.put("PROPINA_3_PRESTACAO_MESTRADO_INTEGRADO", "PROP_3_PREST_MI");
        productsMap.put("PROPINA_4_PRESTACAO_MESTRADO_INTEGRADO", "PROP_4_PREST_MI");
        productsMap.put("PROPINA_5_PRESTACAO_MESTRADO_INTEGRADO", "PROP_5_PREST_MI");
        productsMap.put("PROPINA_6_PRESTACAO_MESTRADO_INTEGRADO", "PROP_6_PREST_MI");
        productsMap.put("PROPINA_7_PRESTACAO_MESTRADO_INTEGRADO", "PROP_7_PREST_MI");
        productsMap.put("PROPINA_8_PRESTACAO_MESTRADO_INTEGRADO", "PROP_8_PREST_MI");
        productsMap.put("PROPINA_9_PRESTACAO_MESTRADO_INTEGRADO", "PROP_9_PREST_MI");
        productsMap.put("PROPINA_1_PRESTACAO_3_CICLO", "PROP_1_PREST_3_CIC");
        productsMap.put("PROPINA_2_PRESTACAO_3_CICLO", "PROP_2_PREST_3_CIC");
        productsMap.put("PROPINA_3_PRESTACAO_3_CICLO", "PROP_3_PREST_3_CIC");
        productsMap.put("PROPINA_4_PRESTACAO_3_CICLO", "PROP_4_PREST_3_CIC");
        productsMap.put("PROPINA_5_PRESTACAO_3_CICLO", "PROP_5_PREST_3_CIC");
        productsMap.put("PROPINA_6_PRESTACAO_3_CICLO", "PROP_6_PREST_3_CIC");
        productsMap.put("PROPINA_7_PRESTACAO_3_CICLO", "PROP_7_PREST_3_CIC");
        productsMap.put("PROPINA_8_PRESTACAO_3_CICLO", "PROP_8_PREST_3_CIC");
        productsMap.put("PROPINA_9_PRESTACAO_3_CICLO", "PROP_9_PREST_3_CIC");
        productsMap.put("PROPINA_1_PRESTACAO_2_CICLO", "PROP_1_PREST_2_CIC");
        productsMap.put("PROPINA_2_PRESTACAO_2_CICLO", "PROP_2_PREST_2_CIC");
        productsMap.put("PROPINA_3_PRESTACAO_2_CICLO", "PROP_3_PREST_2_CIC");
        productsMap.put("PROPINA_4_PRESTACAO_2_CICLO", "PROP_4_PREST_2_CIC");
        productsMap.put("PROPINA_5_PRESTACAO_2_CICLO", "PROP_5_PREST_2_CIC");
        productsMap.put("PROPINA_6_PRESTACAO_2_CICLO", "PROP_6_PREST_2_CIC");
        productsMap.put("PROPINA_7_PRESTACAO_2_CICLO", "PROP_7_PREST_2_CIC");
        productsMap.put("PROPINA_8_PRESTACAO_2_CICLO", "PROP_8_PREST_2_CIC");
        productsMap.put("PROPINA_9_PRESTACAO_2_CICLO", "PROP_9_PREST_2_CIC");
        productsMap.put("PROP_MAT_1_PRESTAC", "PROP_1_PREST_MI");
        productsMap.put("PROP_MAT_2_PRESTAC", "PROP_2_PREST_MI");
        productsMap.put("PROP_MAT_3_PRESTAC", "PROP_3_PREST_MI");
        productsMap.put("PROP_MAT_4_PRESTAC", "PROP_4_PREST_MI");
        productsMap.put("PROP_MAT_5_PRESTAC", "PROP_5_PREST_MI");
        productsMap.put("PROP_MAT_6_PRESTAC", "PROP_6_PREST_MI");
        productsMap.put("PROP_MAT_7_PRESTAC", "PROP_7_PREST_MI");
        productsMap.put("PROP_MAT_8_PRESTAC", "PROP_8_PREST_MI");
        productsMap.put("PROP_MAT_9_PRESTAC", "PROP_9_PREST_MI");
        productsMap.put("PROPINA_1_PRESTACAO_1_CICLO", "PROP_1_PREST_1_CIC");
        productsMap.put("PROPINA_2_PRESTACAO_1_CICLO", "PROP_2_PREST_1_CIC");
        productsMap.put("PROPINA_3_PRESTACAO_1_CICLO", "PROP_3_PREST_1_CIC");
        productsMap.put("PROPINA_4_PRESTACAO_1_CICLO", "PROP_4_PREST_1_CIC");
        productsMap.put("PROPINA_5_PRESTACAO_1_CICLO", "PROP_5_PREST_1_CIC");
        productsMap.put("PROPINA_6_PRESTACAO_1_CICLO", "PROP_6_PREST_1_CIC");
        productsMap.put("PROPINA_7_PRESTACAO_1_CICLO", "PROP_7_PREST_1_CIC");
        productsMap.put("PROPINA_8_PRESTACAO_1_CICLO", "PROP_8_PREST_1_CIC");
        productsMap.put("PROPINA_9_PRESTACAO_1_CICLO", "PROP_9_PREST_1_CIC");
        productsMap.put("PROPINA_1_PRESTACAO_POS_GRAD", "PROP_1_PREST_P_GRAD");
        productsMap.put("PROPINA_2_PRESTACAO_POS_GRAD", "PROP_2_PREST_P_GRAD");
        productsMap.put("PROPINA_3_PRESTACAO_POS_GRAD", "PROP_3_PREST_P_GRAD");
        productsMap.put("PROPINA_4_PRESTACAO_POS_GRAD", "PROP_4_PREST_P_GRAD");
        productsMap.put("PROPINA_5_PRESTACAO_POS_GRAD", "PROP_5_PREST_P_GRAD");
        productsMap.put("PROPINA_6_PRESTACAO_POS_GRAD", "PROP_6_PREST_P_GRAD");
        productsMap.put("PROPINA_7_PRESTACAO_POS_GRAD", "PROP_7_PREST_P_GRAD");
        productsMap.put("PROPINA_8_PRESTACAO_POS_GRAD", "PROP_8_PREST_P_GRAD");
        productsMap.put("PROPINA_9_PRESTACAO_POS_GRAD", "PROP_9_PREST_P_GRAD");

    }

    private static String translateProductCode(String oldCode) {
//            if(!productsMap.containsKey(oldCode)) {
//                throw new RuntimeException("invalid code");
//            }
//            
//            return productsMap.get(oldCode);

        return oldCode;
    }

    @Override
    public void runTask() throws Exception {

        getLogger().info("createMissingIngressions()");
        createMissingIngressions();

        getLogger().info("defineMappingFinantialEntityAdministrativeOffice()");
        defineMappingFinantialEntityAdministrativeOffice();

        getLogger().info("createProductForAdvancePayment()");
        createProductForAdvancePayment();

        getLogger().info("configureTreasurySettings()");
        configureTreasurySettings();

        getLogger().info("createProducts_FROM_SPREADSHEET()");
        createProducts_FROM_SPREADSHEET();

        getLogger().info("createAcademicTaxes_FROM_SPREADSHEET()");
        createAcademicTaxes_FROM_SPREADSHEET();

        getLogger().info("createTuitionPaymentPlanGroups_FROM_SPREADSHEET()");
        createTuitionPaymentPlanGroups_FROM_SPREADSHEET();

        getLogger().info("configureAcademicTreasurySettings_FROM_SPREADSHEET()");
        configureAcademicTreasurySettings_FROM_SPREADSHEET();

        getLogger().info("createServiceRequestTypesToProducts_FROM_SPREADSHEET()");
        createServiceRequestTypesToProducts_FROM_SPREADSHEET();

        getLogger().info("createExemptionTypes_FROM_SPREADSHEET()");
        createExemptionTypes_FROM_SPREADSHEET();

        getLogger().info("createEmolumentTariffs_FROM_SPREADSHEET()");
        createEmolumentTariffs_FROM_SPREADSHEET();

        changeProductsCode_FROM_SPREADSHEET();

    }

    private void changeProductsCode_FROM_SPREADSHEET() {

        if (Product.findUniqueByCode("PROPINA_MATRICULA").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA").get().setCode("PROPINA_MATRICULA");
            Product.findUniqueByCode("PROPINA_MATRICULA").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_MATRICULA_1_PRESTACAO").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA_1_PRESTACAO").get().setCode("PROP_MAT_1_PRESTAC");
            Product.findUniqueByCode("PROP_MAT_1_PRESTAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_MATRICULA_2_PRESTACAO").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA_2_PRESTACAO").get().setCode("PROP_MAT_2_PRESTAC");
            Product.findUniqueByCode("PROP_MAT_2_PRESTAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_MATRICULA_3_PRESTACAO").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA_3_PRESTACAO").get().setCode("PROP_MAT_3_PRESTAC");
            Product.findUniqueByCode("PROP_MAT_3_PRESTAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_MATRICULA_4_PRESTACAO").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA_4_PRESTACAO").get().setCode("PROP_MAT_4_PRESTAC");
            Product.findUniqueByCode("PROP_MAT_4_PRESTAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_MATRICULA_5_PRESTACAO").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA_5_PRESTACAO").get().setCode("PROP_MAT_5_PRESTAC");
            Product.findUniqueByCode("PROP_MAT_5_PRESTAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_MATRICULA_6_PRESTACAO").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA_6_PRESTACAO").get().setCode("PROP_MAT_6_PRESTAC");
            Product.findUniqueByCode("PROP_MAT_6_PRESTAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_MATRICULA_7_PRESTACAO").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA_7_PRESTACAO").get().setCode("PROP_MAT_7_PRESTAC");
            Product.findUniqueByCode("PROP_MAT_7_PRESTAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_MATRICULA_8_PRESTACAO").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA_8_PRESTACAO").get().setCode("PROP_MAT_8_PRESTAC");
            Product.findUniqueByCode("PROP_MAT_8_PRESTAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_MATRICULA_9_PRESTACAO").isPresent()) {
            Product.findUniqueByCode("PROPINA_MATRICULA_9_PRESTACAO").get().setCode("PROP_MAT_9_PRESTAC");
            Product.findUniqueByCode("PROP_MAT_9_PRESTAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_UNIDADES_CURRICULARES_ISOLADAS").isPresent()) {
            Product.findUniqueByCode("PROPINA_UNIDADES_CURRICULARES_ISOLADAS").get().setCode("PROP_UNID_CURR_ISOL");
            Product.findUniqueByCode("PROP_UNID_CURR_ISOL").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_UNIDADES_EXTRACURRICULARES").isPresent()) {
            Product.findUniqueByCode("PROPINA_UNIDADES_EXTRACURRICULARES").get().setCode("PROP_UNID_EXTRACUR");
            Product.findUniqueByCode("PROP_UNID_EXTRACUR").get().checkRules();
        }
        if (Product.findUniqueByCode("SEGURO_ESCOLAR").isPresent()) {
            Product.findUniqueByCode("SEGURO_ESCOLAR").get().setCode("SEGURO_ESCOLAR");
            Product.findUniqueByCode("SEGURO_ESCOLAR").get().checkRules();
        }
        if (Product.findUniqueByCode("CARTA_CURSO").isPresent()) {
            Product.findUniqueByCode("CARTA_CURSO").get().setCode("CARTA_CURSO");
            Product.findUniqueByCode("CARTA_CURSO").get().checkRules();
        }
        if (Product.findUniqueByCode("CARTA_CURSO_2_VIA").isPresent()) {
            Product.findUniqueByCode("CARTA_CURSO_2_VIA").get().setCode("CARTA_CURSO_2_VIA");
            Product.findUniqueByCode("CARTA_CURSO_2_VIA").get().checkRules();
        }
        if (Product.findUniqueByCode("CARTA_TITULO_AGREGACAO").isPresent()) {
            Product.findUniqueByCode("CARTA_TITULO_AGREGACAO").get().setCode("CARTA_TIT_AGREG");
            Product.findUniqueByCode("CARTA_TIT_AGREG").get().checkRules();
        }
        if (Product.findUniqueByCode("CARTA_TITULO_HABILITACAO_COORDENACAO_CIENTIFICA").isPresent()) {
            Product.findUniqueByCode("CARTA_TITULO_HABILITACAO_COORDENACAO_CIENTIFICA").get().setCode("CARTA_TIT_H_C_CIEN");
            Product.findUniqueByCode("CARTA_TIT_H_C_CIEN").get().checkRules();
        }
        if (Product.findUniqueByCode("CARTA_TITULO_2_VIA").isPresent()) {
            Product.findUniqueByCode("CARTA_TITULO_2_VIA").get().setCode("CARTA_TITULO_2_VIA");
            Product.findUniqueByCode("CARTA_TITULO_2_VIA").get().checkRules();
        }
        if (Product.findUniqueByCode("PROCESSO_RECONHECIMENTO_GRAU").isPresent()) {
            Product.findUniqueByCode("PROCESSO_RECONHECIMENTO_GRAU").get().setCode("PROC_REC_GRAU");
            Product.findUniqueByCode("PROC_REC_GRAU").get().checkRules();
        }
        if (Product.findUniqueByCode("PROCESSO_EQUIVALENCIA_GRAU").isPresent()) {
            Product.findUniqueByCode("PROCESSO_EQUIVALENCIA_GRAU").get().setCode("PROC_EQUIV_GRAU");
            Product.findUniqueByCode("PROC_EQUIV_GRAU").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_RECONHECIMENTO_GRAU").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_RECONHECIMENTO_GRAU").get().setCode("CERT_REC_GRAU");
            Product.findUniqueByCode("CERT_REC_GRAU").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_EQUIVALENCIA_GRAU").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_EQUIVALENCIA_GRAU").get().setCode("CERT_EQUIV_GRAU");
            Product.findUniqueByCode("CERT_EQUIV_GRAU").get().checkRules();
        }
        if (Product.findUniqueByCode("PEDIDO_REGISTO_GRAUS_DL_341_2007").isPresent()) {
            Product.findUniqueByCode("PEDIDO_REGISTO_GRAUS_DL_341_2007").get().setCode("PED_REG_G_DL3412007");
            Product.findUniqueByCode("PED_REG_G_DL3412007").get().checkRules();
        }
        if (Product.findUniqueByCode("PROVAS_AVALIACAO_CAPACIDADE_M23_ADMISSAO").isPresent()) {
            Product.findUniqueByCode("PROVAS_AVALIACAO_CAPACIDADE_M23_ADMISSAO").get().setCode("PR_AV_CAP_M23_ADM");
            Product.findUniqueByCode("PR_AV_CAP_M23_ADM").get().checkRules();
        }
        if (Product.findUniqueByCode("PROVAS_AVALIACAO_CAPACIDADE_M23_RECLAMACAO").isPresent()) {
            Product.findUniqueByCode("PROVAS_AVALIACAO_CAPACIDADE_M23_RECLAMACAO").get().setCode("PR_AV_CAP_M23_RECL");
            Product.findUniqueByCode("PR_AV_CAP_M23_RECL").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_REGISTO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_REGISTO").get().setCode("CERT_REGISTO");
            Product.findUniqueByCode("CERT_REGISTO").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_REGISTO_2_VIA").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_REGISTO_2_VIA").get().setCode("CERT_REGISTO_2_VIA");
            Product.findUniqueByCode("CERT_REGISTO_2_VIA").get().checkRules();
        }
        if (Product.findUniqueByCode("SUPLEMENTO_DIPLOMA_2_VIA").isPresent()) {
            Product.findUniqueByCode("SUPLEMENTO_DIPLOMA_2_VIA").get().setCode("SUPL_DIPLOMA_2_VIA");
            Product.findUniqueByCode("SUPL_DIPLOMA_2_VIA").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_REGISTO_CURSO_POS_GRADUADO_ESPECIALIZACAO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_REGISTO_CURSO_POS_GRADUADO_ESPECIALIZACAO").get().setCode("CERT_REG_C_P_GR_ESP");
            Product.findUniqueByCode("CERT_REG_C_P_GR_ESP").get().checkRules();
        }
        if (Product.findUniqueByCode("DIPLOMA_CURSO_DOUTORAMENTO").isPresent()) {
            Product.findUniqueByCode("DIPLOMA_CURSO_DOUTORAMENTO").get().setCode("DIP_CURSO_DOUT");
            Product.findUniqueByCode("DIP_CURSO_DOUT").get().checkRules();
        }
        if (Product.findUniqueByCode("DIPLOMA_CURSO_MESTRADO").isPresent()) {
            Product.findUniqueByCode("DIPLOMA_CURSO_MESTRADO").get().setCode("DIP_CURSO_MEST");
            Product.findUniqueByCode("DIP_CURSO_MEST").get().checkRules();
        }
        if (Product.findUniqueByCode("DIPLOMA_CURSO_ESPECIALIZACAO").isPresent()) {
            Product.findUniqueByCode("DIPLOMA_CURSO_ESPECIALIZACAO").get().setCode("DIP_CURSO_ESPEC");
            Product.findUniqueByCode("DIP_CURSO_ESPEC").get().checkRules();
        }
        if (Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO").isPresent()) {
            Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO").get().setCode("ADM_PROV_AC_DOUT");
            Product.findUniqueByCode("ADM_PROV_AC_DOUT").get().checkRules();
        }
        if (Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO_ART_33_DL_74_2006").isPresent()) {
            Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO_ART_33_DL_74_2006").get()
                    .setCode("ADM_PACDA33DL742006");
            Product.findUniqueByCode("ADM_PACDA33DL742006").get().checkRules();
        }
        if (Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_AGREGACAO").isPresent()) {
            Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_AGREGACAO").get().setCode("ADM_P_AC_AGREG");
            Product.findUniqueByCode("ADM_P_AC_AGREG").get().checkRules();
        }
        if (Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_HABILITACAO_COORDENACAO_CIENTIFICA").isPresent()) {
            Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_HABILITACAO_COORDENACAO_CIENTIFICA").get()
                    .setCode("ADM_P_AC_H_COORCIEN");
            Product.findUniqueByCode("ADM_P_AC_H_COORCIEN").get().checkRules();
        }
        if (Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_MESTRADO").isPresent()) {
            Product.findUniqueByCode("ADMISSAO_PROVAS_ACADEMICAS_MESTRADO").get().setCode("ADM_P_AC_MESTRADO");
            Product.findUniqueByCode("ADM_P_AC_MESTRADO").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_CONCLUSAO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_CONCLUSAO").get().setCode("CERT_CONCLUSAO");
            Product.findUniqueByCode("CERT_CONCLUSAO").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_PROVAS_APTIDAO_PEDAGOGICA").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_PROVAS_APTIDAO_PEDAGOGICA").get().setCode("CERT_P_APTIDAOPEDAG");
            Product.findUniqueByCode("CERT_P_APTIDAOPEDAG").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_OBTENCAO_TITULO_AGREGADO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_OBTENCAO_TITULO_AGREGADO").get().setCode("CERT_OBT_TIT_AGR");
            Product.findUniqueByCode("CERT_OBT_TIT_AGR").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIFICADO_TITULO_HABILITACAO_COORDENACAO_CIENTIFICA").isPresent()) {
            Product.findUniqueByCode("CERTIFICADO_TITULO_HABILITACAO_COORDENACAO_CIENTIFICA").get()
                    .setCode("CERT_TIT_H_COORCIEN");
            Product.findUniqueByCode("CERT_TIT_H_COORCIEN").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_CONCLUSAO_CURSO_MESTRADO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_CONCLUSAO_CURSO_MESTRADO").get().setCode("CERT_CON_C_MESTRADO");
            Product.findUniqueByCode("CERT_CON_C_MESTRADO").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_CONCLUSAO_CURSO_DOUTORAMENTO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_CONCLUSAO_CURSO_DOUTORAMENTO").get().setCode("CERT_CON_C_DOUT");
            Product.findUniqueByCode("CERT_CON_C_DOUT").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_CONCLUSAO_CURSO_ESPECIALIZACAO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_CONCLUSAO_CURSO_ESPECIALIZACAO").get().setCode("CERT_CON_C_ESPEC");
            Product.findUniqueByCode("CERT_CON_C_ESPEC").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_MATRICULA").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_MATRICULA").get().setCode("CERT_MATRICULA");
            Product.findUniqueByCode("CERT_MATRICULA").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_INSCRICAO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_INSCRICAO").get().setCode("CERT_INSCRICAO");
            Product.findUniqueByCode("CERT_INSCRICAO").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_FREQUENCIA_EXAME").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_FREQUENCIA_EXAME").get().setCode("CERT_FREQ_EXAME");
            Product.findUniqueByCode("CERT_FREQ_EXAME").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_CONDUTA_ACADEMICA").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_CONDUTA_ACADEMICA").get().setCode("CERT_CONDUTA_ACAD");
            Product.findUniqueByCode("CERT_CONDUTA_ACAD").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIFICADO_NARRATIVA_TEOR").isPresent()) {
            Product.findUniqueByCode("CERTIFICADO_NARRATIVA_TEOR").get().setCode("CERT_NARRATIVA_TEOR");
            Product.findUniqueByCode("CERT_NARRATIVA_TEOR").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIFICADO_AVALIACAO_CAPACIDADE_M23").isPresent()) {
            Product.findUniqueByCode("CERTIFICADO_AVALIACAO_CAPACIDADE_M23").get().setCode("CERT_AVAL_CAP_M23");
            Product.findUniqueByCode("CERT_AVAL_CAP_M23").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_CARGAS_HORARIAS_CONTEUDOS_PROGRAMATICOS").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_CARGAS_HORARIAS_CONTEUDOS_PROGRAMATICOS").get().setCode("CERT_C_HOR_CONTPROG");
            Product.findUniqueByCode("CERT_C_HOR_CONTPROG").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_FOTOCOPIA").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_FOTOCOPIA").get().setCode("CERT_FOTOCOPIA");
            Product.findUniqueByCode("CERT_FOTOCOPIA").get().checkRules();
        }
        if (Product.findUniqueByCode("PEDIDO_CREDITACAO_CONHECIMENTOS_COMPETENCIAS").isPresent()) {
            Product.findUniqueByCode("PEDIDO_CREDITACAO_CONHECIMENTOS_COMPETENCIAS").get().setCode("PED_CRED_CON_COMP");
            Product.findUniqueByCode("PED_CRED_CON_COMP").get().checkRules();
        }
        if (Product.findUniqueByCode("PEDIDO_CREDITACAO_COMPETENCIAS_ACADEMICAS").isPresent()) {
            Product.findUniqueByCode("PEDIDO_CREDITACAO_COMPETENCIAS_ACADEMICAS").get().setCode("PED_CRED_COMP_ACAD");
            Product.findUniqueByCode("PED_CRED_COMP_ACAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PEDIDO_CREDITACAO_COMPETENCIAS_PROFISSIONAIS").isPresent()) {
            Product.findUniqueByCode("PEDIDO_CREDITACAO_COMPETENCIAS_PROFISSIONAIS").get().setCode("PED_CRED_COMP_PROF");
            Product.findUniqueByCode("PED_CRED_COMP_PROF").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_REINGRESSO").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_REINGRESSO").get().setCode("CAND_REINGRESSO");
            Product.findUniqueByCode("CAND_REINGRESSO").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_REINGRESSO_ALUNOS_ULISBOA").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_REINGRESSO_ALUNOS_ULISBOA").get().setCode("CAND_REING_AULISBOA");
            Product.findUniqueByCode("CAND_REING_AULISBOA").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_TRANSFERENCIA").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_TRANSFERENCIA").get().setCode("CAN_TRANS");
            Product.findUniqueByCode("CAN_TRANS").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_TRANSFERENCIA_ALUNOS_ULISBOA").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_TRANSFERENCIA_ALUNOS_ULISBOA").get().setCode("CAND_TRANS_AULISBOA");
            Product.findUniqueByCode("CAND_TRANS_AULISBOA").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_MUDANCA_CURSO").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_MUDANCA_CURSO").get().setCode("CAND_MUD_CUR");
            Product.findUniqueByCode("CAND_MUD_CUR").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_MUDANCA_CURSO_ALUNOS_ULISBOA").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_MUDANCA_CURSO_ALUNOS_ULISBOA").get().setCode("CAND_MUD_CUR_AULISB");
            Product.findUniqueByCode("CAND_MUD_CUR_AULISB").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_MAIORES_23_ANOS").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_MAIORES_23_ANOS").get().setCode("CAND_MAI_23ANOS");
            Product.findUniqueByCode("CAND_MAI_23ANOS").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_OUTRO").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_OUTRO").get().setCode("CAND_OUTRO");
            Product.findUniqueByCode("CAND_OUTRO").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_OUTRO_ALUNOS_ULISBOA").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_OUTRO_ALUNOS_ULISBOA").get().setCode("CAND_OUT_AULISBOA");
            Product.findUniqueByCode("CAND_OUT_AULISBOA").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_REGIME_LIVRE").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_REGIME_LIVRE").get().setCode("CAND_REGIME_LIVRE");
            Product.findUniqueByCode("CAND_REGIME_LIVRE").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_UNIDADES_CURRICULARES_ISOLADAS").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_UNIDADES_CURRICULARES_ISOLADAS").get().setCode("CAND_UNID_CUR_ISOL");
            Product.findUniqueByCode("CAND_UNID_CUR_ISOL").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_CURSO_APERFEICOAMENTO").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_CURSO_APERFEICOAMENTO").get().setCode("CAND_CURSO_APERF");
            Product.findUniqueByCode("CAND_CURSO_APERF").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_CURSO_B_LEARNING").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_CURSO_B_LEARNING").get().setCode("CAND_CURSO_BLEARN");
            Product.findUniqueByCode("CAND_CURSO_BLEARN").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_ESPECIALIZACAO").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_ESPECIALIZACAO").get().setCode("CAND_ESPEC");
            Product.findUniqueByCode("CAND_ESPEC").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_MESTRADO").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_MESTRADO").get().setCode("CAND_MESTRADO");
            Product.findUniqueByCode("CAND_MESTRADO").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_DOUTORAMENTO").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_DOUTORAMENTO").get().setCode("CAND_DOUTORAMENTO");
            Product.findUniqueByCode("CAND_DOUTORAMENTO").get().checkRules();
        }
        if (Product.findUniqueByCode("PRATICA_ATOS_FORA_PRAZO").isPresent()) {
            Product.findUniqueByCode("PRATICA_ATOS_FORA_PRAZO").get().setCode("PRAT_ATOS_FORA_PRA");
            Product.findUniqueByCode("PRAT_ATOS_FORA_PRA").get().checkRules();
        }
        if (Product.findUniqueByCode("AVERBAMENTO").isPresent()) {
            Product.findUniqueByCode("AVERBAMENTO").get().setCode("AVERBAMENTO");
            Product.findUniqueByCode("AVERBAMENTO").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_MELHORIA").isPresent()) {
            Product.findUniqueByCode("TAXA_MELHORIA").get().setCode("TAXA_MELHORIA");
            Product.findUniqueByCode("TAXA_MELHORIA").get().checkRules();
        }
        if (Product.findUniqueByCode("PEDIDO_PERMUTA").isPresent()) {
            Product.findUniqueByCode("PEDIDO_PERMUTA").get().setCode("PEDIDO_PERMUTA");
            Product.findUniqueByCode("PEDIDO_PERMUTA").get().checkRules();
        }
        if (Product.findUniqueByCode("VALIDACAO_PROCESSOS_ACESSO_M23_OUTRAS_INSTITUICOES").isPresent()) {
            Product.findUniqueByCode("VALIDACAO_PROCESSOS_ACESSO_M23_OUTRAS_INSTITUICOES").get().setCode("VAL_PROC_AM23OINST");
            Product.findUniqueByCode("VAL_PROC_AM23OINST").get().checkRules();
        }
        if (Product.findUniqueByCode("FOTOCOPIA").isPresent()) {
            Product.findUniqueByCode("FOTOCOPIA").get().setCode("FOTOCOPIA");
            Product.findUniqueByCode("FOTOCOPIA").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_CANDIDATURA").isPresent()) {
            Product.findUniqueByCode("TAXA_CANDIDATURA").get().setCode("TAXA_CANDIDATURA");
            Product.findUniqueByCode("TAXA_CANDIDATURA").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_MATRICULA_INSCRICAO_DOUTORAMENTO").isPresent()) {
            Product.findUniqueByCode("TAXA_MATRICULA_INSCRICAO_DOUTORAMENTO").get().setCode("TX_MAT_INSC_DOUT");
            Product.findUniqueByCode("TX_MAT_INSC_DOUT").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_MATRICULA_INSCRICAO_FORMACAO_INICIAL").isPresent()) {
            Product.findUniqueByCode("TAXA_MATRICULA_INSCRICAO_FORMACAO_INICIAL").get().setCode("TX_MAT_INSC_FORINIC");
            Product.findUniqueByCode("TX_MAT_INSC_FORINIC").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_ENVIO_CORREIO").isPresent()) {
            Product.findUniqueByCode("TAXA_ENVIO_CORREIO").get().setCode("TAXA_ENVIO_CORREIO");
            Product.findUniqueByCode("TAXA_ENVIO_CORREIO").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_DEVOLUCAO_CHEQUE").isPresent()) {
            Product.findUniqueByCode("TAXA_DEVOLUCAO_CHEQUE").get().setCode("TX_DEVOLUCAO_CHEQUE");
            Product.findUniqueByCode("TX_DEVOLUCAO_CHEQUE").get().checkRules();
        }
        if (Product.findUniqueByCode("IMPRESSOS").isPresent()) {
            Product.findUniqueByCode("IMPRESSOS").get().setCode("IMPRESSOS");
            Product.findUniqueByCode("IMPRESSOS").get().checkRules();
        }
        if (Product.findUniqueByCode("CANDIDATURA_CURSOS_NAO_CONFERENTES_GRAU").isPresent()) {
            Product.findUniqueByCode("CANDIDATURA_CURSOS_NAO_CONFERENTES_GRAU").get().setCode("CAND_CUR_N_CONF_GR");
            Product.findUniqueByCode("CAND_CUR_N_CONF_GR").get().checkRules();
        }
        if (Product.findUniqueByCode("PEDIDO_EQUIVALENCIA_CREDITACAO").isPresent()) {
            Product.findUniqueByCode("PEDIDO_EQUIVALENCIA_CREDITACAO").get().setCode("PED_EQUIV_CRED");
            Product.findUniqueByCode("PED_EQUIV_CRED").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_MATRICULA").isPresent()) {
            Product.findUniqueByCode("TAXA_MATRICULA").get().setCode("TAXA_MATRICULA");
            Product.findUniqueByCode("TAXA_MATRICULA").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_INSCRICAO").isPresent()) {
            Product.findUniqueByCode("TAXA_INSCRICAO").get().setCode("TAXA_INSCRICAO");
            Product.findUniqueByCode("TAXA_INSCRICAO").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_INSCRICAO_CURRICULARES_ISOLADAS").isPresent()) {
            Product.findUniqueByCode("TAXA_INSCRICAO_CURRICULARES_ISOLADAS").get().setCode("TX_INSC_CUR_ISOL");
            Product.findUniqueByCode("TX_INSC_CUR_ISOL").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_RENOVACAO_INSCRICAO").isPresent()) {
            Product.findUniqueByCode("TAXA_RENOVACAO_INSCRICAO").get().setCode("TX_REN_INSC");
            Product.findUniqueByCode("TX_REN_INSC").get().checkRules();
        }
        if (Product.findUniqueByCode("DECLARACAO_MATRICULA").isPresent()) {
            Product.findUniqueByCode("DECLARACAO_MATRICULA").get().setCode("DECLARACAO_MAT");
            Product.findUniqueByCode("DECLARACAO_MAT").get().checkRules();
        }
        if (Product.findUniqueByCode("DECLARACAO_INSCRICAO").isPresent()) {
            Product.findUniqueByCode("DECLARACAO_INSCRICAO").get().setCode("DECLARACAO_INSC");
            Product.findUniqueByCode("DECLARACAO_INSC").get().checkRules();
        }
        if (Product.findUniqueByCode("PEDIDO_MUDANCA_TURMA").isPresent()) {
            Product.findUniqueByCode("PEDIDO_MUDANCA_TURMA").get().setCode("PEDIDO_MUD_TURMA");
            Product.findUniqueByCode("PEDIDO_MUD_TURMA").get().checkRules();
        }
        if (Product.findUniqueByCode("PEDIDO_MUDANCA_UNIDADE_CURRICULAR").isPresent()) {
            Product.findUniqueByCode("PEDIDO_MUDANCA_UNIDADE_CURRICULAR").get().setCode("PED_MUD_UNID_CUR");
            Product.findUniqueByCode("PED_MUD_UNID_CUR").get().checkRules();
        }
        if (Product.findUniqueByCode("REVISAO_PROVAS_CAUCAO").isPresent()) {
            Product.findUniqueByCode("REVISAO_PROVAS_CAUCAO").get().setCode("REV_PROVAS_CAUCAO");
            Product.findUniqueByCode("REV_PROVAS_CAUCAO").get().checkRules();
        }
        if (Product.findUniqueByCode("PLANO_INTEGRACAO_CURRICULAR_REINGRESSO").isPresent()) {
            Product.findUniqueByCode("PLANO_INTEGRACAO_CURRICULAR_REINGRESSO").get().setCode("PLANO_INT_CUR_REIN");
            Product.findUniqueByCode("PLANO_INT_CUR_REIN").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_PROCESSO_ADMINISTRATIVO_APLICADO_ALUNOS_INCOMING").isPresent()) {
            Product.findUniqueByCode("TAXA_PROCESSO_ADMINISTRATIVO_APLICADO_ALUNOS_INCOMING").get().setCode("TX_PR_ADM_APAINCOM");
            Product.findUniqueByCode("TX_PR_ADM_APAINCOM").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_CANDIDATURA_CURRICULARES_ISOLADAS").isPresent()) {
            Product.findUniqueByCode("TAXA_CANDIDATURA_CURRICULARES_ISOLADAS").get().setCode("TX_CAND_CUR_ISOL");
            Product.findUniqueByCode("TX_CAND_CUR_ISOL").get().checkRules();
        }
        if (Product.findUniqueByCode("RENOVACAO_INSCRICAO").isPresent()) {
            Product.findUniqueByCode("RENOVACAO_INSCRICAO").get().setCode("RENOVACAO_INSCRICAO");
            Product.findUniqueByCode("RENOVACAO_INSCRICAO").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_APROVEITAMENTO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_APROVEITAMENTO").get().setCode("CERT_APROV");
            Product.findUniqueByCode("CERT_APROV").get().checkRules();
        }
        if (Product.findUniqueByCode("CERTIDAO_APROVEITAMENTO_ESCOLAR_TRANSICAO_ANO").isPresent()) {
            Product.findUniqueByCode("CERTIDAO_APROVEITAMENTO_ESCOLAR_TRANSICAO_ANO").get().setCode("CERT_APROV_ESC_TANO");
            Product.findUniqueByCode("CERT_APROV_ESC_TANO").get().checkRules();
        }
        if (Product.findUniqueByCode("_2_VIA_LOGBOOK").isPresent()) {
            Product.findUniqueByCode("_2_VIA_LOGBOOK").get().setCode("_2_VIA_LOGBOOK");
            Product.findUniqueByCode("_2_VIA_LOGBOOK").get().checkRules();
        }
        if (Product.findUniqueByCode("PORTES_CORREIO_NACIONAL").isPresent()) {
            Product.findUniqueByCode("PORTES_CORREIO_NACIONAL").get().setCode("POR_CORREIO_NAC");
            Product.findUniqueByCode("POR_CORREIO_NAC").get().checkRules();
        }
        if (Product.findUniqueByCode("PORTES_CORREIO_INTERNACIONAL").isPresent()) {
            Product.findUniqueByCode("PORTES_CORREIO_INTERNACIONAL").get().setCode("POR_CORREIO_INTER");
            Product.findUniqueByCode("POR_CORREIO_INTER").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_1_PRESTACAO_MESTRADO_INTEGRADO").isPresent()) {
            Product.findUniqueByCode("PROPINA_1_PRESTACAO_MESTRADO_INTEGRADO").get().setCode("PROP_1_PREST_MI");
            Product.findUniqueByCode("PROP_1_PREST_MI").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_2_PRESTACAO_MESTRADO_INTEGRADO").isPresent()) {
            Product.findUniqueByCode("PROPINA_2_PRESTACAO_MESTRADO_INTEGRADO").get().setCode("PROP_2_PREST_MI");
            Product.findUniqueByCode("PROP_2_PREST_MI").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_3_PRESTACAO_MESTRADO_INTEGRADO").isPresent()) {
            Product.findUniqueByCode("PROPINA_3_PRESTACAO_MESTRADO_INTEGRADO").get().setCode("PROP_3_PREST_MI");
            Product.findUniqueByCode("PROP_3_PREST_MI").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_4_PRESTACAO_MESTRADO_INTEGRADO").isPresent()) {
            Product.findUniqueByCode("PROPINA_4_PRESTACAO_MESTRADO_INTEGRADO").get().setCode("PROP_4_PREST_MI");
            Product.findUniqueByCode("PROP_4_PREST_MI").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_5_PRESTACAO_MESTRADO_INTEGRADO").isPresent()) {
            Product.findUniqueByCode("PROPINA_5_PRESTACAO_MESTRADO_INTEGRADO").get().setCode("PROP_5_PREST_MI");
            Product.findUniqueByCode("PROP_5_PREST_MI").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_6_PRESTACAO_MESTRADO_INTEGRADO").isPresent()) {
            Product.findUniqueByCode("PROPINA_6_PRESTACAO_MESTRADO_INTEGRADO").get().setCode("PROP_6_PREST_MI");
            Product.findUniqueByCode("PROP_6_PREST_MI").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_7_PRESTACAO_MESTRADO_INTEGRADO").isPresent()) {
            Product.findUniqueByCode("PROPINA_7_PRESTACAO_MESTRADO_INTEGRADO").get().setCode("PROP_7_PREST_MI");
            Product.findUniqueByCode("PROP_7_PREST_MI").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_8_PRESTACAO_MESTRADO_INTEGRADO").isPresent()) {
            Product.findUniqueByCode("PROPINA_8_PRESTACAO_MESTRADO_INTEGRADO").get().setCode("PROP_8_PREST_MI");
            Product.findUniqueByCode("PROP_8_PREST_MI").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_9_PRESTACAO_MESTRADO_INTEGRADO").isPresent()) {
            Product.findUniqueByCode("PROPINA_9_PRESTACAO_MESTRADO_INTEGRADO").get().setCode("PROP_9_PREST_MI");
            Product.findUniqueByCode("PROP_9_PREST_MI").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_1_PRESTACAO_3_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_1_PRESTACAO_3_CICLO").get().setCode("PROP_1_PREST_3_CIC");
            Product.findUniqueByCode("PROP_1_PREST_3_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_2_PRESTACAO_3_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_2_PRESTACAO_3_CICLO").get().setCode("PROP_2_PREST_3_CIC");
            Product.findUniqueByCode("PROP_2_PREST_3_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_3_PRESTACAO_3_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_3_PRESTACAO_3_CICLO").get().setCode("PROP_3_PREST_3_CIC");
            Product.findUniqueByCode("PROP_3_PREST_3_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_4_PRESTACAO_3_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_4_PRESTACAO_3_CICLO").get().setCode("PROP_4_PREST_3_CIC");
            Product.findUniqueByCode("PROP_4_PREST_3_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_5_PRESTACAO_3_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_5_PRESTACAO_3_CICLO").get().setCode("PROP_5_PREST_3_CIC");
            Product.findUniqueByCode("PROP_5_PREST_3_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_6_PRESTACAO_3_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_6_PRESTACAO_3_CICLO").get().setCode("PROP_6_PREST_3_CIC");
            Product.findUniqueByCode("PROP_6_PREST_3_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_7_PRESTACAO_3_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_7_PRESTACAO_3_CICLO").get().setCode("PROP_7_PREST_3_CIC");
            Product.findUniqueByCode("PROP_7_PREST_3_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_8_PRESTACAO_3_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_8_PRESTACAO_3_CICLO").get().setCode("PROP_8_PREST_3_CIC");
            Product.findUniqueByCode("PROP_8_PREST_3_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_9_PRESTACAO_3_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_9_PRESTACAO_3_CICLO").get().setCode("PROP_9_PREST_3_CIC");
            Product.findUniqueByCode("PROP_9_PREST_3_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_1_PRESTACAO_2_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_1_PRESTACAO_2_CICLO").get().setCode("PROP_1_PREST_2_CIC");
            Product.findUniqueByCode("PROP_1_PREST_2_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_2_PRESTACAO_2_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_2_PRESTACAO_2_CICLO").get().setCode("PROP_2_PREST_2_CIC");
            Product.findUniqueByCode("PROP_2_PREST_2_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_3_PRESTACAO_2_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_3_PRESTACAO_2_CICLO").get().setCode("PROP_3_PREST_2_CIC");
            Product.findUniqueByCode("PROP_3_PREST_2_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_4_PRESTACAO_2_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_4_PRESTACAO_2_CICLO").get().setCode("PROP_4_PREST_2_CIC");
            Product.findUniqueByCode("PROP_4_PREST_2_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_5_PRESTACAO_2_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_5_PRESTACAO_2_CICLO").get().setCode("PROP_5_PREST_2_CIC");
            Product.findUniqueByCode("PROP_5_PREST_2_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_6_PRESTACAO_2_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_6_PRESTACAO_2_CICLO").get().setCode("PROP_6_PREST_2_CIC");
            Product.findUniqueByCode("PROP_6_PREST_2_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_7_PRESTACAO_2_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_7_PRESTACAO_2_CICLO").get().setCode("PROP_7_PREST_2_CIC");
            Product.findUniqueByCode("PROP_7_PREST_2_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_8_PRESTACAO_2_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_8_PRESTACAO_2_CICLO").get().setCode("PROP_8_PREST_2_CIC");
            Product.findUniqueByCode("PROP_8_PREST_2_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_9_PRESTACAO_2_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_9_PRESTACAO_2_CICLO").get().setCode("PROP_9_PREST_2_CIC");
            Product.findUniqueByCode("PROP_9_PREST_2_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_1_PRESTACAO_POS_GRAD").isPresent()) {
            Product.findUniqueByCode("PROPINA_1_PRESTACAO_POS_GRAD").get().setCode("PROP_1_PREST_P_GRAD");
            Product.findUniqueByCode("PROP_1_PREST_P_GRAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_2_PRESTACAO_POS_GRAD").isPresent()) {
            Product.findUniqueByCode("PROPINA_2_PRESTACAO_POS_GRAD").get().setCode("PROP_2_PREST_P_GRAD");
            Product.findUniqueByCode("PROP_2_PREST_P_GRAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_3_PRESTACAO_POS_GRAD").isPresent()) {
            Product.findUniqueByCode("PROPINA_3_PRESTACAO_POS_GRAD").get().setCode("PROP_3_PREST_P_GRAD");
            Product.findUniqueByCode("PROP_3_PREST_P_GRAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_4_PRESTACAO_POS_GRAD").isPresent()) {
            Product.findUniqueByCode("PROPINA_4_PRESTACAO_POS_GRAD").get().setCode("PROP_4_PREST_P_GRAD");
            Product.findUniqueByCode("PROP_4_PREST_P_GRAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_5_PRESTACAO_POS_GRAD").isPresent()) {
            Product.findUniqueByCode("PROPINA_5_PRESTACAO_POS_GRAD").get().setCode("PROP_5_PREST_P_GRAD");
            Product.findUniqueByCode("PROP_5_PREST_P_GRAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_6_PRESTACAO_POS_GRAD").isPresent()) {
            Product.findUniqueByCode("PROPINA_6_PRESTACAO_POS_GRAD").get().setCode("PROP_6_PREST_P_GRAD");
            Product.findUniqueByCode("PROP_6_PREST_P_GRAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_7_PRESTACAO_POS_GRAD").isPresent()) {
            Product.findUniqueByCode("PROPINA_7_PRESTACAO_POS_GRAD").get().setCode("PROP_7_PREST_P_GRAD");
            Product.findUniqueByCode("PROP_7_PREST_P_GRAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_8_PRESTACAO_POS_GRAD").isPresent()) {
            Product.findUniqueByCode("PROPINA_8_PRESTACAO_POS_GRAD").get().setCode("PROP_8_PREST_P_GRAD");
            Product.findUniqueByCode("PROP_8_PREST_P_GRAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_9_PRESTACAO_POS_GRAD").isPresent()) {
            Product.findUniqueByCode("PROPINA_9_PRESTACAO_POS_GRAD").get().setCode("PROP_9_PREST_P_GRAD");
            Product.findUniqueByCode("PROP_9_PREST_P_GRAD").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_1_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_1_PRESTACAO_1_CICLO").get().setCode("PROP_1_PREST_1_CIC");
            Product.findUniqueByCode("PROP_1_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_2_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_2_PRESTACAO_1_CICLO").get().setCode("PROP_2_PREST_1_CIC");
            Product.findUniqueByCode("PROP_2_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_3_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_3_PRESTACAO_1_CICLO").get().setCode("PROP_3_PREST_1_CIC");
            Product.findUniqueByCode("PROP_3_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_4_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_4_PRESTACAO_1_CICLO").get().setCode("PROP_4_PREST_1_CIC");
            Product.findUniqueByCode("PROP_4_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_5_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_5_PRESTACAO_1_CICLO").get().setCode("PROP_5_PREST_1_CIC");
            Product.findUniqueByCode("PROP_5_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_6_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_6_PRESTACAO_1_CICLO").get().setCode("PROP_6_PREST_1_CIC");
            Product.findUniqueByCode("PROP_6_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_7_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_7_PRESTACAO_1_CICLO").get().setCode("PROP_7_PREST_1_CIC");
            Product.findUniqueByCode("PROP_7_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_8_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_8_PRESTACAO_1_CICLO").get().setCode("PROP_8_PREST_1_CIC");
            Product.findUniqueByCode("PROP_8_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_9_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_9_PRESTACAO_1_CICLO").get().setCode("PROP_9_PREST_1_CIC");
            Product.findUniqueByCode("PROP_9_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("PROPINA_9_PRESTACAO_1_CICLO").isPresent()) {
            Product.findUniqueByCode("PROPINA_9_PRESTACAO_1_CICLO").get().setCode("PROP_9_PREST_1_CIC");
            Product.findUniqueByCode("PROP_9_PREST_1_CIC").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_INSCRICAO_CURSOS_LIVRES").isPresent()) {
            Product.findUniqueByCode("TAXA_INSCRICAO_CURSOS_LIVRES").get().setCode("TX_INSC_CUR_LIVRES");
            Product.findUniqueByCode("TX_INSC_CUR_LIVRES").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_ANUAL_REGISTO_PROGRAMA_POS_DOUTORAMENTO").isPresent()) {
            Product.findUniqueByCode("TAXA_ANUAL_REGISTO_PROGRAMA_POS_DOUTORAMENTO").get().setCode("TX_ANL_R_PROG_P_DOUT");
            Product.findUniqueByCode("TX_ANL_R_PROG_P_DOUT").get().checkRules();
        }
        if (Product.findUniqueByCode("TAXA_REGISTO_INVESTIGADOR_VISITANTE_CURTA_DURACAO").isPresent()) {
            Product.findUniqueByCode("TAXA_REGISTO_INVESTIGADOR_VISITANTE_CURTA_DURACAO").get().setCode("TX_REG_INV_VIS_C_DUR");
            Product.findUniqueByCode("TX_REG_INV_VIS_C_DUR").get().checkRules();
        }

    }

    private void open2015_2016() {
        ExecutionYear.readExecutionYearByName("2014/2015").getFirstExecutionPeriod().getNextExecutionPeriod()
                .setState(PeriodState.OPEN);
        ExecutionYear.readExecutionYearByName("2015/2016").setState(PeriodState.OPEN);
        ExecutionYear.readExecutionYearByName("2015/2016").getFirstExecutionPeriod().setState(PeriodState.CURRENT);
    }

    private void createExtracurricularTariffs() {

        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            TuitionPaymentPlanBean tuitionPaymentPlanBean =
                    new TuitionPaymentPlanBean(TuitionPaymentPlanGroup.findUniqueDefaultGroupForExtracurricular().get()
                            .getCurrentProduct(), TuitionPaymentPlanGroup.findUniqueDefaultGroupForExtracurricular().get(),
                            oneOfFinantialEntity("FF"), defaultExecutionYear());
            tuitionPaymentPlanBean
                    .setDegreeCurricularPlans(readDegreeCurricularPlans("BOLONHA_INTEGRATED_MASTER_DEGREE", "", ""));
            tuitionPaymentPlanBean.setDefaultPaymentPlan(false);
            tuitionPaymentPlanBean.setRegistrationProtocol(registrationProtocol(""));
            tuitionPaymentPlanBean.setIngression(ingression(""));
            tuitionPaymentPlanBean.setCustomized(false);
            tuitionPaymentPlanBean.setName("");
            tuitionPaymentPlanBean.setWithLaboratorialClasses(false);
            tuitionPaymentPlanBean.setBeginDate(defaultExecutionYear().getBeginLocalDate());
            tuitionPaymentPlanBean.setEndDate(null);
            tuitionPaymentPlanBean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            tuitionPaymentPlanBean.setFixedDueDate(fixedDueDate(""));
            tuitionPaymentPlanBean.setNumberOfDaysAfterCreationForDueDate(0);
            tuitionPaymentPlanBean.setApplyInterests(true);
            tuitionPaymentPlanBean.setInterestType(InterestType.GLOBAL_RATE);
            tuitionPaymentPlanBean.setApplyInFirstWorkday(true);
            tuitionPaymentPlanBean.setTuitionInstallmentProduct(TuitionPaymentPlanGroup
                    .findUniqueDefaultGroupForExtracurricular().get().getCurrentProduct());
            tuitionPaymentPlanBean.setTuitionCalculationType(TuitionCalculationType.ECTS);
            tuitionPaymentPlanBean.setFixedAmount(amount("15"));
            tuitionPaymentPlanBean.setEctsCalculationType(ectsCalculationType("FIXED_AMOUNT"));
            tuitionPaymentPlanBean.setFactor(amount(""));
            tuitionPaymentPlanBean.setTotalEctsOrUnits(amount(""));
            tuitionPaymentPlanBean.setAcademicalActBlockingOn(true);
            tuitionPaymentPlanBean.addInstallment();
            TuitionPaymentPlan.create(tuitionPaymentPlanBean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            TuitionPaymentPlanBean tuitionPaymentPlanBean =
                    new TuitionPaymentPlanBean(TuitionPaymentPlanGroup.findUniqueDefaultGroupForExtracurricular().get()
                            .getCurrentProduct(), TuitionPaymentPlanGroup.findUniqueDefaultGroupForExtracurricular().get(),
                            oneOfFinantialEntity("FF"), defaultExecutionYear());
            tuitionPaymentPlanBean.setDegreeCurricularPlans(readDegreeCurricularPlans("BOLONHA_MASTER_DEGREE", "", ""));
            tuitionPaymentPlanBean.setDefaultPaymentPlan(false);
            tuitionPaymentPlanBean.setRegistrationProtocol(registrationProtocol(""));
            tuitionPaymentPlanBean.setIngression(ingression(""));
            tuitionPaymentPlanBean.setCustomized(false);
            tuitionPaymentPlanBean.setName("");
            tuitionPaymentPlanBean.setWithLaboratorialClasses(false);
            tuitionPaymentPlanBean.setBeginDate(defaultExecutionYear().getBeginLocalDate());
            tuitionPaymentPlanBean.setEndDate(null);
            tuitionPaymentPlanBean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            tuitionPaymentPlanBean.setFixedDueDate(fixedDueDate(""));
            tuitionPaymentPlanBean.setNumberOfDaysAfterCreationForDueDate(0);
            tuitionPaymentPlanBean.setApplyInterests(true);
            tuitionPaymentPlanBean.setInterestType(InterestType.GLOBAL_RATE);
            tuitionPaymentPlanBean.setApplyInFirstWorkday(true);
            tuitionPaymentPlanBean.setTuitionInstallmentProduct(TuitionPaymentPlanGroup
                    .findUniqueDefaultGroupForExtracurricular().get().getCurrentProduct());
            tuitionPaymentPlanBean.setTuitionCalculationType(TuitionCalculationType.ECTS);
            tuitionPaymentPlanBean.setFixedAmount(amount("35"));
            tuitionPaymentPlanBean.setEctsCalculationType(ectsCalculationType("FIXED_AMOUNT"));
            tuitionPaymentPlanBean.setFactor(amount(""));
            tuitionPaymentPlanBean.setTotalEctsOrUnits(amount(""));
            tuitionPaymentPlanBean.setAcademicalActBlockingOn(true);
            tuitionPaymentPlanBean.addInstallment();
            TuitionPaymentPlan.create(tuitionPaymentPlanBean);
        }

    }

    private void createMissingEvaluationSeasons() {

        if (EvaluationSeason.readNormalSeason() == null) {
            EvaluationSeason season =
                    new EvaluationSeason(new LocalizedString(pt(), "NORMAL"), new LocalizedString(pt(), "Normal"), true, false,
                            false, false);
            season.setCode("NORMAL");
        }

        if (EvaluationSeason.readImprovementSeason() == null) {
            EvaluationSeason season =
                    new EvaluationSeason(new LocalizedString(pt(), "MELHORIA"), new LocalizedString(pt(), "Melhoria"), false,
                            true, false, false);
            season.setCode("MELHORIA");
        }

        if (EvaluationSeason.readSpecialSeason() == null) {
            EvaluationSeason season =
                    new EvaluationSeason(new LocalizedString(pt(), "EPOCA_ESPECIAL"),
                            new LocalizedString(pt(), "Época Especial"), false, false, false, true);
            season.setCode("EPOCA_ESPECIAL");
        }

        EvaluationConfiguration.getInstance().setDefaultEvaluationSeason(EvaluationSeason.readNormalSeason());
    }

    private void createMissingIngressions() {

        if (!IngressionType.findIngressionTypeByCode("ESTUDANTE_INTERNACIONAL").isPresent()) {
            IngressionType.createIngressionType("ESTUDANTE_INTERNACIONAL", new LocalizedString(pt(), "Estudante Internacional"));
        }

        if (!IngressionType.findIngressionTypeByCode("ALUNOS_ULISBOA").isPresent()) {
            IngressionType.createIngressionType("ALUNOS_ULISBOA", new LocalizedString(pt(), "Alunos da Universidade de Lisboa"));
        }

        if (!IngressionType.findIngressionTypeByCode("ALUNOS_EXTERNOS_ULISBOA").isPresent()) {
            IngressionType.createIngressionType("ALUNOS_EXTERNOS_ULISBOA", new LocalizedString(pt(),
                    "Alunos Externos a Universidade de Lisboa"));
        }
    }

    private EctsCalculationType ectsCalculationType(String value) {
        if (value.trim().isEmpty()) {
            return null;
        }

        return EctsCalculationType.valueOf(value);
    }

    private StatuteType statuteType(final String value) {
        if (value.trim().isEmpty()) {
            return null;
        }

        for (final StatuteType statuteType : Bennu.getInstance().getStatuteTypesSet()) {
            if (statuteType.getCode().equals(value.trim())) {
                return statuteType;
            }
        }

        return null;
    }

    private CurricularYear curricularYear(String value) {
        if (value.trim().isEmpty()) {
            return null;
        }

        return CurricularYear.readByYear(Integer.valueOf(value));
    }

    private IngressionType ingression(String value) {
        if (value.trim().isEmpty()) {
            return null;
        }

        return IngressionType.findIngressionTypeByCode(value.trim()).get();
    }

    private RegistrationProtocol registrationProtocol(final String value) {
        if (value.trim().isEmpty()) {
            return null;
        }

        for (final RegistrationProtocol registrationProtocol : Bennu.getInstance().getRegistrationProtocolsSet()) {
            if (registrationProtocol.getCode().equals(value.trim())) {
                return registrationProtocol;
            }
        }

        return null;
    }

    private Set<DegreeCurricularPlan> readDegreeCurricularPlans(final String degreeTypeValues, final String degreeCodes,
            final String degreesToExcludeCode) {
        if (!degreeCodes.trim().isEmpty()) {

            final Set<DegreeCurricularPlan> result = Sets.newHashSet();

            for (final String code : degreeCodes.split(",")) {
                if (code.trim().isEmpty()) {
                    continue;
                }

                result.addAll(readDegreeBySigla(code.trim()).stream().map(l -> l.getDegreeCurricularPlansSet())
                        .reduce((a, c) -> Sets.union(a, c)).orElse(Sets.newHashSet()));
            }

            return result.stream()
                    .filter(t -> ExecutionDegree.getByDegreeCurricularPlanAndExecutionYear(t, defaultExecutionYear()) != null)
                    .collect(Collectors.toSet());
        }

        final Set<DegreeCurricularPlan> result =
                Sets.newHashSet(DegreeCurricularPlan.readByDegreeTypeAndState(new Predicate<DegreeType>() {

                    @Override
                    public boolean test(DegreeType t) {
                        return degreeTypes(degreeTypeValues).contains(t);
                    }
                }, DegreeCurricularPlanState.ACTIVE));

        if (!degreesToExcludeCode.trim().isEmpty()) {
            for (String dcpToExclude : degreesToExcludeCode.split(",")) {
                result.removeAll(Degree.readBySigla(dcpToExclude.trim()).getDegreeCurricularPlansSet());
            }
        }

        return result.stream()
                .filter(t -> ExecutionDegree.getByDegreeCurricularPlanAndExecutionYear(t, defaultExecutionYear()) != null)
                .collect(Collectors.toSet());
    }

    private Set<DegreeType> degreeTypes(final String degreeTypeValues) {
        final Set<DegreeType> result = Sets.newHashSet();
        for (final String degreeType : degreeTypeValues.split(",")) {
            if (degreeType.trim().isEmpty()) {
                continue;
            }

            result.add(findDegreeTypeByCode(degreeType.trim()));
        }

        return result;
    }

    private ExecutionYear defaultExecutionYear() {
        return ExecutionYear.readCurrentExecutionYear();
    }

    private void defineMappingFinantialEntityAdministrativeOffice() {
        FinantialInstitution.findUniqueByFiscalCode(FI_LOOKUP.entrySet().iterator().next().getValue()).get()
                .getFinantialEntitiesSet().iterator().next()
                .setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
    }

    private void createExemptionTypes_FROM_SPREADSHEET() {

        if (!fromAcronymsToFinantialInstitutionList("FMD, FF, FL, FMV, RUL").isEmpty()) {
            TreasuryExemptionType
                    .create("ISENCAO_DOUTORAMENTO_DOCENTE",
                            new LocalizedString(pt(),
                                    "Isenção em Propina de Doutoramento para docentes abrangidos pelo Artigo  4.º, n.º 4, do Decreto--Lei n.º 216/92")
                                    .with(en(), ""), new BigDecimal(100), true);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FF, FMV").isEmpty()) {
            TreasuryExemptionType
                    .create("ISENCAO_DOUTORAMENTO_NAO_DOCENTE",
                            new LocalizedString(pt(),
                                    "Docentes da FMDUL não abrangidos pelo Artigo 4.º, n.º 4, do Decreto -Lei n.º 216/92 e Funcionários não docentes da FMDUL")
                                    .with(en(), ""), new BigDecimal(70), true);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FF, FL, FMV, RUL").isEmpty()) {
            TreasuryExemptionType.create("ISENCAO_IRS_ADSE_SS_MIL_PASSES_SOCIAIS", new LocalizedString(pt(),
                    "IRS, ADSE, Segurança Social, prestações familiares, militares, passes sociais e bolsas").with(en(), ""),
                    new BigDecimal(100), true);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            TreasuryExemptionType.create("PREMIO_ESCOLAR", new LocalizedString(pt(), "Prémio Escolar").with(en(), ""),
                    new BigDecimal(100), true);
        }
    }

    private void createDefaultProductGroups() {
        if (ProductGroup.findByCode("TUITION") == null) {
            ProductGroup.create("TUITION", new LocalizedString(pt(), "Propina").with(en(), "Tuition"));
        }

        if (ProductGroup.findByCode("EMOLUMENT") == null) {
            ProductGroup.create("EMOLUMENT", new LocalizedString(pt(), "Emolumento").with(en(), "Emolument"));
        }

        if (ProductGroup.findByCode("OTHER") == null) {
            ProductGroup.create("OTHER", new LocalizedString(pt(), "Outro").with(en(), "Other"));
        }
    }

    private void configureTreasurySettings() {
        TreasurySettings.getInstance().edit(Currency.findByCode("EUR"), Product.findUniqueByCode(INTEREST_CODE).get(),
                Product.findUniqueByCode(PAGAMENTO_EM_AVANCO).get());
    }

    private void createProductForInterest() {
        if (Product.findUniqueByCode(INTEREST_CODE).isPresent()) {
            return;
        }

        final ProductGroup productGroup = ProductGroup.findByCode("OTHER");
        LocalizedString productName = new LocalizedString(pt(), "Juro").with(en(), "Interest");
        Product.create(productGroup, INTEREST_CODE, productName, defaultUnitOfMeasure(), true, false, VatType.findByCode("ISE"),
                FinantialInstitution.findAll().collect(Collectors.toList()), VatExemptionReason.findByCode("M1"));

    }

    private void createProductForAdvancePayment() {
        if (Product.findUniqueByCode(PAGAMENTO_EM_AVANCO).isPresent()) {
            return;
        }

        final ProductGroup productGroup = ProductGroup.findByCode("OTHER");
        LocalizedString productName = new LocalizedString(pt(), "Pagamento em avanço").with(en(), "Advanced payment");
        Product.create(productGroup, PAGAMENTO_EM_AVANCO, productName, defaultUnitOfMeasure(), true, false, VatType.findByCode("ISE"),
                FinantialInstitution.findAll().collect(Collectors.toList()), null);
    }

    private void createEmolumentTariffs_FROM_SPREADSHEET() {

        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CARTA_CURSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(100));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CARTA_CURSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_INTEGRATED_MASTER_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(125));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CARTA_CURSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("MASTER_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(125));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CARTA_CURSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("PHD"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(175));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CARTA_CURSO_2_VIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CARTA_TITULO_AGREGACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(200));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CARTA_TITULO_HABILITACAO_COORDENACAO_CIENTIFICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(200));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CARTA_TITULO_2_VIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PROCESSO_RECONHECIMENTO_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV-DEPREC, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PROCESSO_EQUIVALENCIA_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV-DEPREC, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PROCESSO_RECONHECIMENTO_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_INTEGRATED_MASTER_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(550));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV-DEPREC, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PROCESSO_EQUIVALENCIA_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV-DEPREC, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_INTEGRATED_MASTER_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(550));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PROCESSO_RECONHECIMENTO_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_PHD"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(600));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV-DEPREC, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PROCESSO_EQUIVALENCIA_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV-DEPREC, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_PHD"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(600));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_RECONHECIMENTO_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_EQUIVALENCIA_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV-DEPREC, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_REGISTO_GRAUS_DL_341_2007")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV-DEPREC, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(26.7));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV-DEPREC, FL, FF, RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("PROVAS_AVALIACAO_CAPACIDADE_M23_ADMISSAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV-DEPREC, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV-DEPREC, FL, FF, RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("PROVAS_AVALIACAO_CAPACIDADE_M23_RECLAMACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV-DEPREC, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(30));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_REGISTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(38));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_REGISTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_INTEGRATED_MASTER_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(38));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_REGISTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_PHD"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(38));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_REGISTO_2_VIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(25));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV, FL, FF, RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("SUPLEMENTO_DIPLOMA_2_VIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV, FL, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(25));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FF, RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("PEDIDO_CREDITACAO_COMPETENCIAS_ACADEMICAS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(40));
            bean.setUnitsForBase(6);
            bean.setUnitAmount(new BigDecimal(5));
            bean.setMaximumAmount(maximumAmount(250));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FF, RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("PEDIDO_CREDITACAO_COMPETENCIAS_PROFISSIONAIS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FF, RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(40));
            bean.setUnitsForBase(6);
            bean.setUnitAmount(new BigDecimal(5));
            bean.setMaximumAmount(maximumAmount(250));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("SEGURO_ESCOLAR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(6));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD-DEPREC").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD-DEPREC");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_REGISTO_CURSO_POS_GRADUADO_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(38));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO_ART_33_DL_74_2006"))
                            .get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(2500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_AGREGACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(600));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(
                            translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_HABILITACAO_COORDENACAO_CIENTIFICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(600));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(180));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONCLUSAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_PROVAS_APTIDAO_PEDAGOGICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_OBTENCAO_TITULO_AGREGADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_FREQUENCIA_EXAME")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_APROVEITAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_APROVEITAMENTO_ESCOLAR_TRANSICAO_ANO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONDUTA_ACADEMICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIFICADO_NARRATIVA_TEOR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIFICADO_AVALIACAO_CAPACIDADE_M23")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_CARGAS_HORARIAS_CONTEUDOS_PROGRAMATICOS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(7.5));
            bean.setMaximumAmount(maximumAmount(150));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_FOTOCOPIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(5));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_REINGRESSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_TRANSFERENCIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MUDANCA_CURSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MAIORES_23_ANOS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_OUTRO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_REGIME_LIVRE")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(100));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_CURSO_APERFEICOAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_CURSO_B_LEARNING")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(250));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(250));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(250));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(250));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PRATICA_ATOS_FORA_PRAZO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(4));
            bean.setMaximumAmount(maximumAmount(120));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("AVERBAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(3));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MELHORIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(30));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_PERMUTA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("VALIDACAO_PROCESSOS_ACESSO_M23_OUTRAS_INSTITUICOES")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("FOTOCOPIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("IMPRESSOS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(0.15));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_CANDIDATURA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(100));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MATRICULA_INSCRICAO_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(100));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("TAXA_MATRICULA_INSCRICAO_FORMACAO_INICIAL")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_ENVIO_CORREIO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(3));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_DEVOLUCAO_CHEQUE")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(35));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(55));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(55));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CANDIDATURA_CURSOS_NAO_CONFERENTES_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(55));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_REINGRESSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_TRANSFERENCIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MUDANCA_CURSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MAIORES_23_ANOS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_OUTRO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CANDIDATURA_UNIDADES_CURRICULARES_ISOLADAS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_EQUIVALENCIA_CREDITACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(25));
            bean.setMaximumAmount(maximumAmount(125));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("RENOVACAO_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_INSCRICAO_CURRICULARES_ISOLADAS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_INSCRICAO_CURSOS_LIVRES")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(325));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO_ART_33_DL_74_2006"))
                            .get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(2500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_AGREGACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(541));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(
                            translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_HABILITACAO_COORDENACAO_CIENTIFICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(163));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(163));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONCLUSAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_PROVAS_APTIDAO_PEDAGOGICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_OBTENCAO_TITULO_AGREGADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_FREQUENCIA_EXAME")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONDUTA_ACADEMICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIFICADO_NARRATIVA_TEOR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_APROVEITAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_CARGAS_HORARIAS_CONTEUDOS_PROGRAMATICOS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(7.5));
            bean.setMaximumAmount(maximumAmount(150));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_REGISTO_CURSO_POS_GRADUADO_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(38));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DECLARACAO_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DECLARACAO_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PRATICA_ATOS_FORA_PRAZO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(120));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("FOTOCOPIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MELHORIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_MUDANCA_TURMA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(7.5));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_MUDANCA_UNIDADE_CURRICULAR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(7.5));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("REVISAO_PROVAS_CAUCAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("PLANO_INTEGRACAO_CURRICULAR_REINGRESSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("TAXA_PROCESSO_ADMINISTRATIVO_APLICADO_ALUNOS_INCOMING")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("TAXA_ANUAL_REGISTO_PROGRAMA_POS_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(120));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("TAXA_REGISTO_INVESTIGADOR_VISITANTE_CURTA_DURACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL-DEPREC").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("SEGURO_ESCOLAR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FL-DEPREC");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(1.15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_INSCRICAO_CURRICULARES_ISOLADAS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(25));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("TAXA_CANDIDATURA_CURRICULARES_ISOLADAS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MAIORES_23_ANOS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_OUTRO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(75));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(75));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CANDIDATURA_CURSOS_NAO_CONFERENTES_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(50));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("RENOVACAO_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_INTEGRATED_MASTER_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_INTEGRATED_MASTER_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(25));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_MASTER_DEGREE"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(75));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode("BOLONHA_PHD"));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(75));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO_ART_33_DL_74_2006"))
                            .get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(2500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_AGREGACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(600));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(
                            translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_HABILITACAO_COORDENACAO_CIENTIFICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(600));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(180));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_REGISTO_CURSO_POS_GRADUADO_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(38));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONCLUSAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_PROVAS_APTIDAO_PEDAGOGICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_OBTENCAO_TITULO_AGREGADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_APROVEITAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_APROVEITAMENTO_ESCOLAR_TRANSICAO_ANO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_FREQUENCIA_EXAME")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONDUTA_ACADEMICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(50));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIFICADO_NARRATIVA_TEOR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIFICADO_AVALIACAO_CAPACIDADE_M23")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_CARGAS_HORARIAS_CONTEUDOS_PROGRAMATICOS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(7.5));
            bean.setMaximumAmount(maximumAmount(150));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_FOTOCOPIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(5));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PRATICA_ATOS_FORA_PRAZO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(4));
            bean.setMaximumAmount(maximumAmount(120));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("AVERBAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(3));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MELHORIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_PERMUTA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("VALIDACAO_PROCESSOS_ACESSO_M23_OUTRAS_INSTITUICOES")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("FOTOCOPIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(1));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_MUDANCA_TURMA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_MUDANCA_UNIDADE_CURRICULAR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("REVISAO_PROVAS_CAUCAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("PLANO_INTEGRACAO_CURRICULAR_REINGRESSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(50));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PORTES_CORREIO_NACIONAL")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(1.85));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PORTES_CORREIO_INTERNACIONAL")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(5));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("SEGURO_ESCOLAR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FF");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(1.28));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(25));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(50));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("SEGURO_ESCOLAR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(1.28));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONCLUSAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_OBTENCAO_TITULO_AGREGADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIFICADO_TITULO_HABILITACAO_COORDENACAO_CIENTIFICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONCLUSAO_CURSO_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONCLUSAO_CURSO_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_PROVAS_APTIDAO_PEDAGOGICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_CONCLUSAO_CURSO_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(5));
            bean.setMaximumAmount(maximumAmount(60));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_FREQUENCIA_EXAME")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(5));
            bean.setMaximumAmount(maximumAmount(60));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONDUTA_ACADEMICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(5));
            bean.setMaximumAmount(maximumAmount(60));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIFICADO_NARRATIVA_TEOR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_CARGAS_HORARIAS_CONTEUDOS_PROGRAMATICOS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(7.5));
            bean.setMaximumAmount(maximumAmount(150));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_FOTOCOPIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(5));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_AGREGACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(600));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(
                            translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_HABILITACAO_COORDENACAO_CIENTIFICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(600));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO_ART_33_DL_74_2006"))
                            .get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(2500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(550));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(150));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("PROVAS_AVALIACAO_CAPACIDADE_M23_ADMISSAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(140));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PROCESSO_EQUIVALENCIA_GRAU")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(400));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("PEDIDO_CREDITACAO_COMPETENCIAS_ACADEMICAS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(25));
            bean.setMaximumAmount(maximumAmount(125));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("PEDIDO_CREDITACAO_COMPETENCIAS_PROFISSIONAIS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(150));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(100));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(100));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV-DEPREC").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_REINGRESSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV-DEPREC");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(140));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_REINGRESSO_ALUNOS_ULISBOA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(50));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_TRANSFERENCIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(140));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CANDIDATURA_TRANSFERENCIA_ALUNOS_ULISBOA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(50));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MUDANCA_CURSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(140));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CANDIDATURA_MUDANCA_CURSO_ALUNOS_ULISBOA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(50));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_OUTRO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(140));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_OUTRO_ALUNOS_ULISBOA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(50));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FMV").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CANDIDATURA_UNIDADES_CURRICULARES_ISOLADAS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMD, FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(50));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PRATICA_ATOS_FORA_PRAZO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(4));
            bean.setMaximumAmount(maximumAmount(120));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("AVERBAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(3));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MELHORIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("REVISAO_PROVAS_CAUCAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_PERMUTA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("_2_VIA_LOGBOOK")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("FOTOCOPIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PORTES_CORREIO_NACIONAL")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(1.4));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMV").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PORTES_CORREIO_INTERNACIONAL")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("FMV");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(4));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("SEGURO_ESCOLAR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(1.28));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_MESTRADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("DIPLOMA_CURSO_ESPECIALIZACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(80));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO_ART_33_DL_74_2006"))
                            .get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(2500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_DOUTORAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(500));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("ADMISSAO_PROVAS_ACADEMICAS_AGREGACAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(600));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONCLUSAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_OBTENCAO_TITULO_AGREGADO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIFICADO_TITULO_HABILITACAO_COORDENACAO_CIENTIFICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_MATRICULA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_INSCRICAO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_FREQUENCIA_EXAME")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(5));
            bean.setMaximumAmount(maximumAmount(60));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_CONDUTA_ACADEMICA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(5));
            bean.setMaximumAmount(maximumAmount(60));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIFICADO_NARRATIVA_TEOR")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(10));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIFICADO_AVALIACAO_CAPACIDADE_M23")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("CERTIDAO_CARGAS_HORARIAS_CONTEUDOS_PROGRAMATICOS")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(7.5));
            bean.setMaximumAmount(maximumAmount(150));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CERTIDAO_FOTOCOPIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(5));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_REINGRESSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_TRANSFERENCIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("CANDIDATURA_MUDANCA_CURSO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PRATICA_ATOS_FORA_PRAZO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(4));
            bean.setMaximumAmount(maximumAmount(120));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("AVERBAMENTO")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(3));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(100));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("TAXA_MELHORIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(15));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product =
                    Product.findUniqueByCode(translateProductCode("VALIDACAO_PROCESSOS_ACESSO_M23_OUTRAS_INSTITUICOES")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(60));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("PEDIDO_PERMUTA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(20));
            bean.setUnitsForBase(1);
            bean.setUnitAmount(new BigDecimal(0));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }
        if (!fromAcronymsToFinantialInstitutionList("RUL").isEmpty()) {
            final Product product = Product.findUniqueByCode(translateProductCode("FOTOCOPIA")).get();
            final FinantialEntity finantialEntity = oneOfFinantialEntity("RUL");
            final AcademicTariffBean bean = new AcademicTariffBean();
            bean.setAdministrativeOffice(AdministrativeOffice.readDegreeAdministrativeOffice());
            bean.setBeginDate(parseLocalDate("1/07/2015"));
            bean.setDegreeType(findDegreeTypeByCode(""));
            bean.setDegree(findDegree(""));
            bean.setBaseAmount(new BigDecimal(0));
            bean.setUnitsForBase(0);
            bean.setUnitAmount(new BigDecimal(1));
            bean.setMaximumAmount(maximumAmount(-1));
            bean.setDueDateCalculationType(DueDateCalculationType.DAYS_AFTER_CREATION);
            bean.setFixedDueDate(fixedDueDate(""));
            bean.setNumberOfDaysAfterCreationForDueDate(0);
            bean.setUrgencyRate(new BigDecimal(0));
            bean.setLanguageTranslationRate(new BigDecimal(0));
            AcademicTariff.create(finantialEntity, product, bean);
        }

    }

    private void createServiceRequestTypesToProducts_FROM_SPREADSHEET() {

        if (!fromAcronymsToFinantialInstitutionList("FMD, FF, FMV, FL, RUL").isEmpty()) {
            ServiceRequestMapEntry.create(
                    Product.findUniqueByCode(translateProductCode("CARTA_CURSO")).get(),
                    serviceRequestType("DIPLOMA_REQUEST", academicServiceRequestType("DIPLOMA_REQUEST"),
                            documentRequestType("DIPLOMA_REQUEST")), AcademicServiceRequestSituationType.NEW);
        }
    }

    private ServiceRequestType serviceRequestType(final String code, final AcademicServiceRequestType academicServiceRequestType,
            final DocumentRequestType documentRequestType) {
        if (!Strings.isNullOrEmpty(code)) {
            if (ServiceRequestType.findUniqueByCode(code).isPresent()) {
                return ServiceRequestType.findUniqueByCode(code).get();
            }
        }

        if (academicServiceRequestType != null) {
            if (ServiceRequestType.findUnique(academicServiceRequestType, documentRequestType) != null) {
                return ServiceRequestType.findUnique(academicServiceRequestType, documentRequestType);
            }
        }

        return null;
    }

    private DocumentRequestType documentRequestType(final String value) {
        if (Strings.isNullOrEmpty(value.trim())) {
            return null;
        }

        return DocumentRequestType.valueOf(value);
    }

    private AcademicServiceRequestType academicServiceRequestType(final String value) {
        if (Strings.isNullOrEmpty(value.trim())) {
            return null;
        }

        return AcademicServiceRequestType.valueOf(value);
    }

    private ServiceRequestType createServiceRequestType(final String code,
            final AcademicServiceRequestType academicServiceRequestType, final DocumentRequestType documentRequestType,
            final String namePT, final String nameEN, final ServiceRequestCategory category, final boolean active,
            final boolean payable, final boolean detailed, final boolean numberOfUnits, final String numberOfUnitsDesignationPT,
            final boolean urgent) {

        if (!Strings.isNullOrEmpty(code)) {
            if (ServiceRequestType.findUniqueByCode(code).isPresent()) {
                return ServiceRequestType.findUniqueByCode(code).get();
            }
        }

        if (academicServiceRequestType != null) {
            if (ServiceRequestType.findUnique(academicServiceRequestType, documentRequestType) != null) {
                return ServiceRequestType.findUnique(academicServiceRequestType, documentRequestType);
            }
        }

        final ServiceRequestType createdServiceRequestType;
        final LocalizedString name = new LocalizedString(pt(), namePT).with(en(), nameEN);
        if (academicServiceRequestType != null) {
            createdServiceRequestType =
                    ServiceRequestType.createLegacy(code, name, true, academicServiceRequestType, documentRequestType, payable,
                            Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, category);
        } else {
            createdServiceRequestType =
                    ServiceRequestType.create(code, name, true, payable, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, category);
        }

        if (numberOfUnits) {
            createdServiceRequestType.edit(createdServiceRequestType.getCode(), createdServiceRequestType.getName(),
                    createdServiceRequestType.getActive(), createdServiceRequestType.getPayable(),
                    createdServiceRequestType.getNotifyUponConclusion(), createdServiceRequestType.getPrintable(),
                    createdServiceRequestType.getRequestedOnline(), createdServiceRequestType.getServiceRequestCategory(),
                    new LocalizedString(pt(), numberOfUnitsDesignationPT));
        }

        return createdServiceRequestType;
    }

    private void createAcademicTaxes_FROM_SPREADSHEET() {

        if (!fromAcronymsToFinantialInstitutionList("FMD, FF, FL, FMV, RUL").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("SEGURO_ESCOLAR").get(), true, true, true, true);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD, FF, FL, FMV, RUL").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("TAXA_MELHORIA").get(), true, true, true, false);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("TAXA_INSCRICAO").get(), true, true, true, true);
        }
        if (!fromAcronymsToFinantialInstitutionList("FL, FMV").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("TAXA_INSCRICAO").get(), true, false, true, true);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("TAXA_CANDIDATURA").get(), true, true, false, false);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF, FL, FMV").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("TAXA_MATRICULA").get(), true, true, false, true);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("TAXA_MATRICULA").get(), true, true, true, true);
        }
        if (!fromAcronymsToFinantialInstitutionList("FF, FL").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("RENOVACAO_INSCRICAO").get(), true, false, true, true);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("TAXA_MATRICULA_INSCRICAO_DOUTORAMENTO").get(), true, true, true, false);
        }
        if (!fromAcronymsToFinantialInstitutionList("FMD").isEmpty()) {
            AcademicTax.create(Product.findUniqueByCode("TAXA_MATRICULA_INSCRICAO_FORMACAO_INICIAL").get(), true, true, true,
                    false);
        }

    }

    private void configureAcademicTreasurySettings_FROM_SPREADSHEET() {
        if (!fromAcronymsToFinantialInstitutionList("FF, FL, FMD, FMV, RUL").isEmpty()) {
            AcademicTreasurySettings.getInstance().edit(ProductGroup.findByCode("EMOLUMENT"), ProductGroup.findByCode("TUITION"),
                    AcademicTax.findUnique(Product.findUniqueByCode("TAXA_MELHORIA").get()).get(), true, false);
        }
    }

    private void createTuitionPaymentPlanGroups_FROM_SPREADSHEET() {

        final Product REGISTRATION_TUITION_product = Product.findUniqueByCode("PROPINA_MATRICULA").get();
        final Product STANDALONE_TUITION_product = Product.findUniqueByCode("PROPINA_UNIDADES_CURRICULARES_ISOLADAS").get();
        final Product EXTRACURRICULAR_TUITION_product = Product.findUniqueByCode("PROPINA_UNIDADES_EXTRACURRICULARES").get();

        final LocalizedString REGISTRATION_TUITION_name =
                new LocalizedString(pt(), "Propina de Matrícula").with(en(), "Registration Tuition");
        final LocalizedString STANDALONE_TUITION_name =
                new LocalizedString(pt(), "Propina em Unidades Curriculares Isoladas").with(en(), "Standalone Tuition");
        final LocalizedString EXTRACURRICULAR_TUITION_name =
                new LocalizedString(pt(), "Propina em Unidades Extracurriculares").with(en(), "Extracurricular Tuition");

        TuitionPaymentPlanGroup.create("REGISTRATION_TUITION", REGISTRATION_TUITION_name, true, false, false,
                REGISTRATION_TUITION_product);
        TuitionPaymentPlanGroup.create("STANDALONE_TUITION", STANDALONE_TUITION_name, false, true, false,
                STANDALONE_TUITION_product);
        TuitionPaymentPlanGroup.create("EXTRACURRICULAR_TUITION", EXTRACURRICULAR_TUITION_name, false, false, true,
                EXTRACURRICULAR_TUITION_product);
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
            if (sigla.equals(degree.getSigla())) {
                result.add(degree);
            }
        }

        return result;
    }

    private void createProducts_FROM_SPREADSHEET() {

    }

    private void addProductsThatBlocksAcademicalActs_FROM_SPREADSHEET() {

    }

}
