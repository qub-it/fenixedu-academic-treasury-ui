package org.fenixedu.academictreasury.domain.reports;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.fenixedu.treasury.domain.debt.DebtAccount;
import org.fenixedu.treasury.domain.document.InvoiceEntry;
import org.fenixedu.treasury.domain.document.PaymentEntry;
import org.fenixedu.treasury.domain.document.ReimbursementEntry;
import org.fenixedu.treasury.domain.document.SettlementEntry;

public class ErrorsLog {
    final StringBuilder sb = new StringBuilder();

    public void addError(final InvoiceEntry entry, final Exception e) {
        synchronized (this) {
            final String oid = entry.getExternalId();
            final String documentNumber =
                    entry.getFinantialDocument() != null ? entry.getFinantialDocument().getUiDocumentNumber() : "";
            final String description = entry.getDescription();

            sb.append(String.format("[%s/%s] - '%s'\n", oid, documentNumber, description));
            sb.append(ExceptionUtils.getFullStackTrace(e)).append("\n\n");
        }
    }

    public void addError(final SettlementEntry entry, final Exception e) {
        synchronized (this) {
            final String oid = entry.getExternalId();
            final String documentNumber =
                    entry.getFinantialDocument() != null ? entry.getFinantialDocument().getUiDocumentNumber() : "";
            final String description = entry.getDescription() != null ? entry.getDescription() : "";

            sb.append(String.format("[%s/%s] - '%s'\n", oid, documentNumber, description));
            sb.append(ExceptionUtils.getFullStackTrace(e)).append("\n\n");
        }
    }

    public void addError(final PaymentEntry entry, final Exception e) {
        synchronized (this) {
            final String oid = entry.getExternalId();
            final String documentNumber =
                    entry.getSettlementNote() != null ? entry.getSettlementNote().getUiDocumentNumber() : "";

            sb.append(String.format("[%s/%s]\n", oid, documentNumber));
            sb.append(ExceptionUtils.getFullStackTrace(e)).append("\n\n");

        }
    }

    public void addError(final ReimbursementEntry entry, final Exception e) {
        synchronized (this) {
            final String oid = entry.getExternalId();
            final String documentNumber =
                    entry.getSettlementNote() != null ? entry.getSettlementNote().getUiDocumentNumber() : "";

            sb.append(String.format("[%s/%s]\n", oid, documentNumber));
            sb.append(ExceptionUtils.getFullStackTrace(e)).append("\n\n");
        }
    }

    public void addError(final DebtAccount debtAccount, final Exception e) {
        synchronized (this) {
            final String oid = debtAccount.getExternalId();
            final String identification = debtAccount.getCustomer().getIdentificationNumber();
            final String name = debtAccount.getCustomer().getName();

            sb.append(String.format("[%s/%s] - %s\n", oid, identification, name));
            sb.append(ExceptionUtils.getFullStackTrace(e)).append("\n\n");
        }
    }
    
    public String getLog() {
        return sb.toString();
    }

}