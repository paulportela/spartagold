public class Block implements Serializable {
    private String id;
    private String solution;
    private String minerPubKey;
    private String previousBlockID;
    private ArrayList<Transaction> transList;

    private static final int REWARDAMOUNT = 5;

    public Block(String solution, String minerId)
    {
        super();
        this.id = UUID.randomUUID().toString();
        this.solution = solution;
        BufferedReader br = new BufferedReader(new FileReader(“pubkey”));
        this.minerPubKey = br.readline();
        br.close();
        transList = new ArrayList<Transaction>();
        transList.add(new Transaction(REWARDAMOUNT, minerPubKey));
    }

    public String getPreviousBlockID() { return previousBlockID; }

    public void setPreviousBlockID(String previousBlockID) { this.previousBlockID = previousBlockID; }

    public String getMinerPubKey() { return minerPubKey; }

    public void setMinerPubKey(String minerPubKey) { this.minerPubKey = minerPubKey; }

    public String getSolution() { return solution; }

    public void setSolution(String solution) { this.solution = solution; }

    public String getId() { return id; }

    public ArrayList<Transaction> getTransactions() { return transList; }

    public void addTransaction(Transaction t) { transList.add(t); }
    @Override
    public String toString() {
        return "Block [id=" + id + ", solution=" + solution + ", minerPubKey="
        + minerId + ", previousBlockID=" + previousBlockID + ", trans="
        + trans + "]";
    }
}