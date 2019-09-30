package com.example.banksimulation;

public class userProfileReturned {
    String name,email,phone_no;
    long balance,account_no;

    public String getEmail() {
        return email;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public long getAccount_no() {
        return account_no;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public void setAccount_no(long account_no) {
        this.account_no = account_no;
    }

    public String getName() {
        return name;
    }

    public long getBalance() {
        return balance;
    }
}
