package fr.nomadesduballon.avisstades.reviews;

import fr.nomadesduballon.avisstades.users.internal.AccountLookup;

/**
 * Checks that the author of a review is allowed to publish it.
 */
public class ReviewAuthorPolicy {

	private final AccountLookup accountLookup = new AccountLookup();

	public boolean canPublish(String authorId) {
		return accountLookup.isVerified(authorId);
	}

}
