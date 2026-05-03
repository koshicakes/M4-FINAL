public class Adventurer {

    private String id, name, rank, classType, password;
    private int age, hp, mp, attack, defense, xp = 0, gold = 0;
    private Party party = null;

    public Adventurer(String id, String name, int age, String rank, String classType, String password){

        if(age <= 0){
            System.out.println("Invalid age. Setting to default (18).");
            age = 18;
        }

        this.id = id;
        this.name = name;
        this.age = age;
        this.rank = rank;
        this.classType = classType;
        this.password = password;

        assignStats();
    }

    private void assignStats(){
        switch(classType){
            case "Warrior": hp=150; mp=40; attack=20; defense=18; break;
            case "Mage": hp=80; mp=150; attack=25; defense=8; break;
            case "Archer": hp=100; mp=60; attack=18; defense=12; break;
            case "Healer": hp=90; mp=140; attack=10; defense=10; break;
            case "Rogue": hp=95; mp=70; attack=22; defense=11; break;
            default: hp=100; mp=50; attack=10; defense=10;
        }
    }

    public void addXP(int xp){
        this.xp += xp;
        updateRank();
    }

    public void addGold(int gold){
        this.gold += gold;
    }

    public void updateRank(){
        if(xp >= 20000) rank = "S";
        else if(xp >= 10000) rank = "A";
        else if(xp >= 5000) rank = "B";
        else if(xp >= 2500) rank = "C";
        else if(xp >= 1000) rank = "D";
        else rank = "E";
    }

    public void showStatus(){
        System.out.println("\n========= ADVENTURER STATUS =========");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Rank: " + rank);
        System.out.println("Class: " + classType);
        System.out.println("-------------------------------------");
        System.out.println("HP: " + hp + "   MP: " + mp);
        System.out.println("ATK: " + attack + "  DEF: " + defense);
        System.out.println("-------------------------------------");
        System.out.println("XP: " + xp + "   Gold: " + gold);
        System.out.println("=====================================\n");
    }

    public String getId(){ return id; }
    public String getName(){ return name; }
    public int getAge(){ return age; }
    public String getRank(){ return rank; }
    public String getClassType(){ return classType; }
    public String exportPassword(){ return password; }
    public boolean checkPassword(String input){ return password.equals(input); }
    public Party getParty(){ return party; }
    public int getXp(){ return xp; }
    public int getGold() { return gold; }
    public void deductGold(int amount) { this.gold -= amount; }

    public void setParty(Party p){ this.party = p; }
    public boolean isInParty(){ return party != null; }
}
