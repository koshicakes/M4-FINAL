public class Quest {

    private String id, name, status;
    private int xp, gold;
    private Adventurer assigned;
    private Party assignedParty;

    private String requiredRank; 

    // ================= CONSTRUCTOR =================

    public Quest(String id, String name, int xp, int gold, String requiredRank){
        this(id, name, xp, gold, requiredRank, "Available");
    }

    public Quest(String id, String name, int xp, int gold, String requiredRank, String status){
        this.id = id;
        this.name = name;
        this.xp = xp;
        this.gold = gold;
        this.requiredRank = requiredRank;
        this.status = status == null ? "Available" : status;
        this.assigned = null;
        this.assignedParty = null;
    }

    // ================= RANK LOGIC =================

    private int rankValue(String r){
        switch(r){
            case "S": return 6;
            case "A": return 5;
            case "B": return 4;
            case "C": return 3;
            case "D": return 2;
            default: return 1; // E
        }
    }

    // ================= ASSIGN QUEST =================

    public void assign(Adventurer adv){
        assign(adv, adv.isInParty() && adv.getParty().getLeader().equals(adv));
    }

    public void assign(Adventurer adv, boolean useParty){

        if(status.equals("Completed")){
            System.out.println("Quest already assigned or completed.");
            return;
        }

        if(status.equals("Ongoing")){
            System.out.println("Quest already assigned or completed.");
            return;
        }

        if(useParty){
            if(!adv.isInParty()){
                System.out.println("You are not in a party.");
                return;
            }

            Party party = adv.getParty();
            if(!party.getLeader().equals(adv)){
                System.out.println("Only the party leader can accept a party quest.");
                return;
            }

            for(Adventurer member : party.getMembers()){
                if(rankValue(member.getRank()) < rankValue(requiredRank)){
                    System.out.println("All party members must meet the rank requirement.");
                    return;
                }
            }

            assignedParty = party;
            status = "Ongoing";
            System.out.println("Quest assigned to party " + party.getId() + " successfully.");
            return;
        }

        if(rankValue(adv.getRank()) < rankValue(requiredRank)){
            System.out.println("Your rank is too low for this quest.");
            return;
        }

        assigned = adv;
        status = "Ongoing";

        System.out.println("Quest assigned successfully.");
    }

    // ================= COMPLETE QUEST =================

    public void complete(Adventurer actor){

        if(!status.equals("Ongoing")){
            System.out.println("Quest is not active.");
            return;
        }

        if(assignedParty != null){
            if(actor.getParty() == null || !actor.getParty().equals(assignedParty)){
                System.out.println("Only party members may complete this party quest.");
                return;
            }

            int members = assignedParty.getMembers().size();
            int xpShare = xp / members;
            int goldShare = gold / members;
            int xpRemainder = xp % members;
            int goldRemainder = gold % members;

            for(Adventurer member : assignedParty.getMembers()){
                member.addXP(xpShare);
                member.addGold(goldShare);
            }

            if(xpRemainder > 0){
                assignedParty.getLeader().addXP(xpRemainder);
            }
            if(goldRemainder > 0){
                assignedParty.getLeader().addGold(goldRemainder);
            }

            status = "Completed";

            System.out.println("Quest completed by party " + assignedParty.getId() + "!");
            System.out.println("Each member receives +" + xpShare + " XP and +" + goldShare + " Gold.");

            if(xpRemainder > 0 || goldRemainder > 0){
                System.out.println("Leader bonus remainder: +" + xpRemainder + " XP, +" + goldRemainder + " Gold.");
            }
            return;
        }

        if(assigned == null){
            System.out.println("No adventurer assigned.");
            return;
        }

        if(!assigned.equals(actor)){
            System.out.println("Only the assigned adventurer may complete this quest.");
            return;
        }

        assigned.addXP(xp);
        assigned.addGold(gold);

        status = "Completed";

        System.out.println("Quest completed!");
        System.out.println("Rewards: +" + xp + " XP, +" + gold + " Gold");
    }

    // ================= GETTERS =================

    public String getId(){ return id; }
    public String getName(){ return name; }
    public String getStatus(){ return status; }
    public int getXp(){ return xp; }
    public int getGold(){ return gold; }
    public String getRequiredRank(){ return requiredRank; }
}