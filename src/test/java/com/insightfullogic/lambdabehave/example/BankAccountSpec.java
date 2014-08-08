package com.insightfullogic.lambdabehave.example;

import com.insightfullogic.lambdabehave.JunitSuiteRunner;
import org.junit.runner.RunWith;

import static com.insightfullogic.lambdabehave.Suite.describe;
import static com.insightfullogic.lambdabehave.example.Currency.EUR;
import static com.insightfullogic.lambdabehave.example.Currency.GBP;
import static com.insightfullogic.lambdabehave.example.Currency.USD;
import static org.mockito.Mockito.*;

@RunWith(JunitSuiteRunner.class)
public class BankAccountSpec {{

    describe("A Bank Account", it -> {

        Exchange exchange = mock(Exchange.class);

        it.completesWith(() -> {
            verifyNoMoreInteractions(exchange);
            reset(exchange);
        });

        it.should("initially have a getBalance of 0", expect -> {
            expect.that(new Account(GBP, exchange).getBalance()).is(0L);
        });

        it.should("increase its balance upon a deposit", expect -> {
            Account account = new Account(GBP, exchange);

            account.deposit(30L, GBP);
            expect.that(account.getBalance()).is(30L);
        });

        it.should("decrease its balance upon a payment", expect -> {
            Account account = new Account(GBP, exchange);
            account.deposit(30L, GBP);
            account.pay(20L, GBP);

            expect.that(account.getBalance()).is(10L);
        });

        it.should("refuse to deposit a negative amount", expect -> {
            expect.exception(IllegalArgumentException.class, () -> {
                Account account = new Account(GBP, exchange);

                account.deposit(-30L, GBP);
            });
        });

        Account aggregateAccount = new Account(GBP, exchange);

        it.uses(10L, 0L, 10L)
          .and(5L, 0L, 15L)
          .and(0L, 10L, 5L)
          .toShow("that the bank balance aggregates over multiple transactions", (expect, newDeposit, newPayment, newTotal) -> {
              if (newDeposit > 0)
                aggregateAccount.deposit(newDeposit, GBP);

              if (newPayment > 0)
                  aggregateAccount.pay(newPayment, GBP);

              expect.that(aggregateAccount.getBalance()).is(newTotal);
          });

        it.should("refuse to go into overdraft", expect -> {
            expect.exception(IllegalStateException.class, () -> {
                Account account = new Account(GBP, exchange);
                account.deposit(10L, GBP);
                account.pay(20L, GBP);
            });
        });

        it.should("convert between currencies before depositing", expect -> {
            given:
            when(exchange.convert(10L, GBP, USD)).thenReturn(20L);
            when(exchange.convert(5L, GBP, EUR)).thenReturn(7L);

            // when
            Account account = new Account(GBP, exchange);
            account.deposit(10L, USD);
            account.pay(5L, EUR);

            then:
            expect.that(account.getBalance()).is(13L);
            verify(exchange).convert(10L, GBP, USD);
            verify(exchange).convert(5L, GBP, EUR);
        });

    });

}}
