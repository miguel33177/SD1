package edu.ufp.inf.sd.rabbitmqservices.project.producer;

public class TokenRing {
    private int numClients;
    private int holder;

    public TokenRing(int numberClients) {
        this.numClients = numberClients;
        this.holder = 0;
    }

    public void passToken() {
        this.holder++;
        if (this.holder >= this.numClients) {
            this.holder = 0;
        }

    }

    public int getHolder() {
        return this.holder;
    }
}
