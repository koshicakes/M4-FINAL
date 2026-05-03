import java.util.*;

public class Party {

    private String id;
    private Adventurer leader;
    private ArrayList<Adventurer> members = new ArrayList<>();
    private int maxSize = 4;

    public Party(String id, Adventurer leader){
        this.id = id;
        this.leader = leader;
        members.add(leader);
        leader.setParty(this);
    }

    public String getId(){ return id; }
    public Adventurer getLeader(){ return leader; }
    public int getSize(){ return members.size(); }
    public int getMaxSize(){ return maxSize; }

    public boolean isFull(){
        return members.size() >= maxSize;
    }

    public ArrayList<Adventurer> getMembers(){
        return new ArrayList<>(members);
    }

    public void addMember(Adventurer adv){

        if(isFull()){
            System.out.println("Party is full.");
            return;
        }

        if(adv.isInParty()){
            System.out.println("Already in a party.");
            return;
        }

        members.add(adv);
        adv.setParty(this);

        System.out.println("Member added.");
    }

    public void removeMember(Adventurer requester, Adventurer target){

        if(!requester.equals(leader)){
            System.out.println("Only leader can remove.");
            return;
        }

        if(target.equals(leader)){
            System.out.println("Leader cannot remove self.");
            return;
        }

        if(!members.contains(target)){
            System.out.println("Not found.");
            return;
        }

        members.remove(target);
        target.setParty(null);

        System.out.println("Removed.");
    }

    public void showStatus(){

        System.out.println("\n========== PARTY ==========");
        System.out.println("ID: " + id);
        System.out.println("Leader: " + leader.getName());
        System.out.println("Members: " + members.size() + "/" + maxSize);
        System.out.println("---------------------------");

        for(Adventurer a: members){
            String role = a.equals(leader) ? "Leader" : "Member";

            System.out.printf("- %-10s (%-7s | Rank %s) [%s]\n",
                    a.getName(),
                    a.getClassType(),
                    a.getRank(),
                    role);
        }

        System.out.println("===========================\n");
    }
}