package com.freetonleague.core.service.financeUnit.implementations;

import com.freetonleague.core.domain.dto.NotificationDto;
import com.freetonleague.core.domain.enums.AccountStatusType;
import com.freetonleague.core.domain.enums.AccountTransactionStatusType;
import com.freetonleague.core.domain.enums.NotificationType;
import com.freetonleague.core.domain.model.Account;
import com.freetonleague.core.domain.model.AccountTransaction;
import com.freetonleague.core.service.NotificationService;
import com.freetonleague.core.service.financeUnit.FinanceEventService;
import com.freetonleague.core.service.financeUnit.FinancialUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class FinanceEventServiceImpl implements FinanceEventService {

    private final NotificationService notificationService;
    private final FinancialUnitService financialUnitService;

    /**
     * Process transaction status changing
     */
    @Override
    public void processAccountStatusChange(Account account, AccountStatusType newAccountStatusType) {
        log.debug("^Not implement to process account event in processAccountStatusChange");
    }

    /**
     * Process transaction status changing
     */
    @Override
    public void processTransactionStatusChange(AccountTransaction accountTransaction, AccountTransactionStatusType newAccountTransactionStatusType) {
        log.debug("^ try to process transaction.guid {} status change to {}", accountTransaction.getGUID(), newAccountTransactionStatusType);
        //notify user about balance amount change
//        this.sendSourceTransactionNotification(accountTransaction);
        this.composeTargetTransactionNotification(accountTransaction);
    }

    private void sendSourceTransactionNotification(AccountTransaction accountTransaction) {
        Account sourceAccount = accountTransaction.getSourceAccount();
        if (isNull(sourceAccount)) {
            log.debug("^ Source account is NULL. No need to send notification");
            return;
        }
        if (!sourceAccount.getHolder().getHolderType().isUser()) {
            log.debug("^ Source account not belong to User. No need to send notification");
            return;
        }
        NotificationDto notification;
        try {
            notification = NotificationDto.builder()
                    .leagueId(sourceAccount.getHolder().getHolderExternalGUID())
                    .message(String.format("The withdraw transaction from your account '%s' of amount '%s' with purpose '%s - %s' was successfully completed",
                            sourceAccount.getGUID(), accountTransaction.getAmount(),
                            accountTransaction.getTransactionType(), accountTransaction.getTransactionTemplateType()))
                    .title("Withdraw transaction")
                    .type(NotificationType.SYSTEM)
                    .build();
        } catch (NullPointerException exc) {
            log.error("^ error while compose transactional Notification to source account {}. Check stack trace {}",
                    sourceAccount, exc);
            return;
        }
        notificationService.sendNotification(notification);
    }

    private void composeTargetTransactionNotification(AccountTransaction accountTransaction) {
        Account targetAccount = accountTransaction.getTargetAccount();
        if (isNull(targetAccount)) {
            log.debug("^ Target account is NULL. No need to send notification");
            return;
        }
        if (!targetAccount.getHolder().getHolderType().isUser()) {
            log.debug("^ Target account not belong to User. No need to send notification");
            return;
        }
        NotificationDto notification;
        try {
            notification = NotificationDto.builder()
                    .leagueId(targetAccount.getHolder().getHolderExternalGUID())
                    .message(String.format("Ваш на счет пришло %s TON", accountTransaction.getAmount()))
                    .title("Пополнение")
                    .type(NotificationType.SYSTEM)
                    .build();
        } catch (NullPointerException exc) {
            log.error("^ error while compose transactional Notification to target account {}. Check stack trace {}",
                    targetAccount, exc);
            return;
        }
        notificationService.sendNotification(notification);
    }
}
