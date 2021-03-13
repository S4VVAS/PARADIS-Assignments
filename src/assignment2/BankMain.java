package assignment2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BankMain {

    static int numberOfOperations = 100;
    static ArrayList<Operation> operations = new ArrayList<>();
    static ArrayList<Transaction> transactions = new ArrayList<>();
    static int numberOfTransactions = 100;
    static int accountID = 0;

    public static void initOperations(Bank bank) {
        for (int i = 0; i < numberOfOperations; i++) {
            int account = accountID;
            int balance = 1;
            operations.add(new Operation(bank, account, balance));
        }
    }

    public static void initTransactions(Bank bank){
        for(int i = 0; i < numberOfTransactions; i++){
            Transaction aTransaction = new Transaction(bank);
            for(Operation o : operations)
                aTransaction.add(o);
            transactions.add(aTransaction);
        }
    }

    public static void main(String[] args) {
        //TESTING

        //create bank
        Bank bank = new Bank();

        //create account
        bank.newAccount(0);

        System.out.println("Account balance: " + bank.getAccountBalance(accountID));

        //create operations
        initOperations(bank);

        //create transactions
        initTransactions(bank);

        //create threads
        int nrThreads = numberOfTransactions;
        Thread[] threads = new Thread[nrThreads];
        for(int i = 0; i < nrThreads; i++)
            threads[i] = new Thread(transactions.get(i));

        for(int i = 0; i < nrThreads; i++)
            threads[i].start();

        try{
            for(int i = 0; i < nrThreads; i++)
                threads[i].join();
        }catch(Exception e){
            System.out.println(e);
        }

        System.out.println("Account balance: " + bank.getAccountBalance(accountID));

    }
}

