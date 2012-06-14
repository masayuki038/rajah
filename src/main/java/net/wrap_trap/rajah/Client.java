package net.wrap_trap.rajah;

public class Client {

    private Database database;
    private Object[] args;

    public Client(Database database, Object[] args) {
        this.database = database;
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public Database getDatabase() {
        return database;
    }

}
