package com.insightfullogic.lambdabehave.example;

public class Account {

    private final Currency currency;
    private final Exchange exchange;
    private long balance;

    public Account(Currency currency, Exchange exchange) {
        this.currency = currency;
        this.exchange = exchange;
        balance = 0;
    }

    public long getBalance() {
        return balance;
    }

    public void deposit(long amount, Currency currency) {
        validateAmount(amount);
        this.balance += convertCurrency(amount, currency);
    }

    public void pay(long amount, Currency currency) {
        validateAmount(amount);
        validateBalance(amount);
        this.balance -= convertCurrency(amount, currency);
    }

    private long convertCurrency(long amount, Currency currency) {
        if (this.currency == currency)
            return amount;

        return exchange.convert(amount, this.currency, currency);
    }

    private void validateBalance(long amount) {
        if (balance - amount < 0) {
            throw new IllegalStateException("Unable to pay amount that would leave a negative balance");
        }
    }

    private void validateAmount(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(amount + " is an invalid amount");
        }
    }

}
