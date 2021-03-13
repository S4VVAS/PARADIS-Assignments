//Java Assignment 2 by Savvas Giortsis sagi2536
package assignment2;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank {
	// I changed the objects stored in the list to AccountObjects, specified in the
	// bellow inner class.
	private final List<AccountObject> accounts = new ArrayList<AccountObject>();
	private ReentrantReadWriteLock listLock = new ReentrantReadWriteLock();
	// Instance methods.

	int newAccount(int balance) {
		int accountId;

		listLock.writeLock().lock();
		// Lock the accounts list prior to taking accountId so there is no chance of 2
		// threads reading the same size, same id.
		accountId = accounts.size();
		accounts.add(new AccountObject(new Account(accountId, balance), new ReentrantReadWriteLock()));
		listLock.writeLock().unlock();

		return accountId;
	}

	int getAccountBalance(int accountId) {
		AccountObject account = getAcc(accountId);

		
		account.lock.readLock().lock(); // Prior to getting the balance i lock this account to make sure the latest
										// updated balance is returned
		int balance = account.account.getBalance();
		account.lock.readLock().unlock();

		return balance;
	}

	void runOperation(Operation operation) {
		AccountObject account = getAcc(operation.getAccountId());
		Boolean held = account.lock.isWriteLockedByCurrentThread(); //If part of transaction, the lock is already held by the current thread
		
		if(!held) //If the lock has already been taken, it can't be re-taken
			account.lock.writeLock().lock(); // Here i use a write lock to make sure that all writing from this account has
											// stopped prior to changes in balance, and that no more reads or writes can
											// occur simultaneously
		int balance = account.account.getBalance(); // I make sure that the lock has been taken prior to this get, or
													// that i don't use a read lock so that another thread doesn't read
													// simultaneously as the current thread, as read locks can be
													// acquired from multiple threads.
		balance = balance + operation.getAmount();
		account.account.setBalance(balance);
		if(!held) //If the lock is taken, this is part of a transaction, no need to unlock the lock, as the transaction may not be finished
			account.lock.writeLock().unlock(); // Unlocks write lock after changes have been made
	}

	void runTransaction(Transaction transaction) {
		List<Operation> currentOperations = transaction.getOperations();
		HashSet<AccountObject> transactionAccounts = new HashSet<AccountObject>(); // Cannot contain duplicates
		
		// In order to get all the locks that the operations need, i will first need to
		// loop through all operations to extract and lock the associated accounts using
		// write locks.
		// Since there is a chance (especially when releasing all the locks) that an
		// unlock can affect another thread, as we are looping though the operations,
		// where many may contain the same account, unlocking the lock from another
		// thread and making the account vulnerable
		for (Operation operation : currentOperations) {
			AccountObject acc = getAcc(operation.getAccountId());
			if(!acc.lock.isWriteLockedByCurrentThread()) //If the lock has already been taken, it can't be re-taken
				acc.lock.writeLock().lock(); //Write lock the current account
			transactionAccounts.add(acc); //Add the locked account to transactionAccounts
		}
		for (Operation operation : currentOperations)
			runOperation(operation);
		for (AccountObject acc : transactionAccounts) //Here we loop through all locked accounts and release their write locks
			acc.lock.writeLock().unlock();

	}
	
	private AccountObject getAcc(int accountId) { //Gets the associated AccountObject for the given accountId
		listLock.readLock().lock();
		AccountObject acc = accounts.get(accountId);
		listLock.readLock().unlock();
		return acc;
	}

	private class AccountObject {
		// This class allows for containing an account and a ReentrantReadWriteLock,
		// linked to each other
		final Account account;
		final ReentrantReadWriteLock lock;

		public AccountObject(Account account, ReentrantReadWriteLock lock) {
			this.lock = lock;
			this.account = account;
		}
	}

}
