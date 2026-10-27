package fr.nomadesduballon.avisstades.users.internal;

/**
 * Internal lookup of user accounts, not part of the public API of the users module.
 */
public class AccountLookup {

	public boolean isVerified(String accountId) {
		return accountId != null && !accountId.isBlank();
	}

}
