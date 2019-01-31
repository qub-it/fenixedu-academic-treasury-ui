package org.fenixedu.academictreasury.schoolsbootstrapscript;

import java.math.BigDecimal;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.scheduler.custom.CustomTask;
import org.fenixedu.treasury.domain.FinantialInstitution;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCodeStateType;
import org.fenixedu.treasury.domain.paymentcodes.pool.PaymentCodePool;
import org.fenixedu.treasury.util.TreasuryConstants;
import org.joda.time.LocalDate;

import com.google.common.base.Strings;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class CreateAdvancePaymentReferenceCodes extends CustomTask {

    private static final String CODE_FILLER = "0";
    private static final int NUM_CONTROL_DIGITS = 2;
    private static final int NUM_SEQUENTIAL_NUMBERS = 7;
    private static final int LENGTH_REFERENCE_CODE = 9;

    @Override
    public void runTask() throws Exception {
        final FinantialInstitution finantialInstitution = FinantialInstitution.findAll().findFirst().get();
        final PaymentCodePool paymentCodePool = FenixFramework.getDomainObject("1128610031206402");
//        PaymentReferenceCode.findAll().sorted(SequentialPaymentCodeGenerator.COMPARATOR_BY_PAYMENT_SEQUENTIAL_DIGITS).forEach(p -> taskLog(p.getReferenceCode()));
//        
//        PaymentReferenceCode max = Collections.max(PaymentReferenceCode.findAll().collect(Collectors.toSet()), SequentialPaymentCodeGenerator.COMPARATOR_BY_PAYMENT_SEQUENTIAL_DIGITS);
//        taskLog("%s\n", max.getReferenceCode());
        
        for(long i = 7200000; i < 7349999; i++) {
            if(i % 10000 == 0) {
                taskLog("%d\n", i);
            }
            
            generateNewCodeFor(paymentCodePool.getMaxAmount(),
                    paymentCodePool.getValidFrom(), paymentCodePool.getValidTo(), false, paymentCodePool, i);
        }
        
    }

    private void generateNewCodeFor(BigDecimal amount, LocalDate validFrom, LocalDate validTo,
            boolean useFixedAmount, PaymentCodePool referenceCodePool, long nextSequentialNumber) {

        String sequentialNumberPadded =
                StringUtils.leftPad(String.valueOf(nextSequentialNumber), NUM_SEQUENTIAL_NUMBERS, CODE_FILLER);
        String controDigitsPadded =
                StringUtils.leftPad(String.valueOf(new Random().nextInt(99)), NUM_CONTROL_DIGITS, CODE_FILLER);

        String referenceCodeString = sequentialNumberPadded + controDigitsPadded;

        BigDecimal minAmount = referenceCodePool.getMinAmount();
        BigDecimal maxAmount = referenceCodePool.getMaxAmount();
        if (useFixedAmount) {
            minAmount = amount;
            maxAmount = amount;
        } else {
            //Correct max amount if needed
            if (TreasuryConstants.isGreaterThan(amount, maxAmount)) {
                maxAmount = amount;
            }
        }

        PaymentReferenceCode newPaymentReference =
                create(referenceCodeString, validFrom, validTo, PaymentReferenceCodeStateType.UNUSED,
                        referenceCodePool, minAmount, maxAmount);

        newPaymentReference.setPayableAmount(amount);
    }
    
    
    @Atomic
    public PaymentReferenceCode create(final String referenceCode, final LocalDate beginDate, final LocalDate endDate,
            final PaymentReferenceCodeStateType state, PaymentCodePool pool, BigDecimal minAmount, BigDecimal maxAmount) {
        PaymentReferenceCode paymentReferenceCode = new PaymentReferenceCode();
        init(paymentReferenceCode, referenceCode, beginDate, endDate, state, pool, minAmount, maxAmount);
        return paymentReferenceCode;
    }
    
    
    protected void init(PaymentReferenceCode code, final String referenceCode, final LocalDate beginDate, final LocalDate endDate,
            final PaymentReferenceCodeStateType state, PaymentCodePool pool, BigDecimal minAmount, BigDecimal maxAmount) {
        code.setReferenceCode(Strings.padStart(referenceCode, LENGTH_REFERENCE_CODE, '0'));
        code.setBeginDate(beginDate);
        code.setEndDate(endDate);
        code.setState(state);
        code.setPaymentCodePool(pool);
        code.setMinAmount(minAmount);
        code.setMaxAmount(maxAmount);
        checkRules(code);
    }
    
    private void checkRules(PaymentReferenceCode code) {
        if (code.getMinAmount() == null) {
            code.setMinAmount(BigDecimal.ZERO);
        }
        if (code.getMaxAmount() == null) {
            code.setMaxAmount(BigDecimal.ZERO);
        }
    }
    
}
