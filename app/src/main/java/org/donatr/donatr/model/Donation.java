package org.donatr.donatr.model;

import java.io.Serializable;

/**
 * Created by mikkkarner on 21/11/15.
 */
public class Donation implements Serializable{

    private String charity;
    private String amount;

    public Donation(String charity, String amount) {
        this.charity = charity;
        this.amount = amount;
    }

    public String getCharity() {
        return charity;
    }

    public void setCharity(String charity) {
        this.charity = charity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
