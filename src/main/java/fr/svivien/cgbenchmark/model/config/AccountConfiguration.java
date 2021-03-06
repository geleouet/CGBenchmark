package fr.svivien.cgbenchmark.model.config;

import fr.egaetan.cgbench.model.leaderboard.User;

public class AccountConfiguration {

	private String accountName;
	private String accountCookie;
	private String accountIde;
	private String accountLogin;
	private String accountPassword;
	private int userId;
	private User user;

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public void setAccountLogin(String accountLogin) {
		this.accountLogin = accountLogin;
	}

	public void setAccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}


    public void setAccountIde(String accountIde) {
        this.accountIde = accountIde;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public String getAccountLogin() {
        return accountLogin;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountCookie() {
        return accountCookie;
    }

    public String getAccountIde() {
        return accountIde;
    }

    public void setAccountCookie(String accountCookie) {
        this.accountCookie = accountCookie;
    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
