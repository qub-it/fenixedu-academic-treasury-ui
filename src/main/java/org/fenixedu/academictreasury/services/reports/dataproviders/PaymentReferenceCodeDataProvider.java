package org.fenixedu.academictreasury.services.reports.dataproviders;

import org.fenixedu.treasury.domain.paymentcodes.PaymentReferenceCode;
import org.fenixedu.treasury.services.reports.dataproviders.AbstractDataProvider;

import com.qubit.terra.docs.util.IDocumentFieldsData;
import com.qubit.terra.docs.util.IFieldsExporter;
import com.qubit.terra.docs.util.IReportDataProvider;

public class PaymentReferenceCodeDataProvider extends AbstractDataProvider implements IReportDataProvider {

    protected static final String PAYMENT_CODE_KEY = "paymentCode";

    private PaymentReferenceCode paymentCode;

    public PaymentReferenceCodeDataProvider(final PaymentReferenceCode paymentCode) {
        this.setPaymentCode(paymentCode);
        registerKey(PAYMENT_CODE_KEY, PaymentReferenceCodeDataProvider::handlePaymentCodeKey);
    }

    private static Object handlePaymentCodeKey(IReportDataProvider provider) {
        PaymentReferenceCodeDataProvider regisProvider = (PaymentReferenceCodeDataProvider) provider;
        return regisProvider.getPaymentCode();
    }

    @Override
    public void registerFieldsAndImages(IDocumentFieldsData arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void registerFieldsMetadata(IFieldsExporter arg0) {
        // TODO Auto-generated method stub

    }

    public PaymentReferenceCode getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(PaymentReferenceCode paymentCode) {
        this.paymentCode = paymentCode;
    }

}
