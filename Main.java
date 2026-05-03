import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static Guild guild = new Guild();
    
    static ArrayList<Party> parties = new ArrayList<>();

    static String generatePartyId(){
        return "P" + String.format("%03d", parties.size() + 1);
    }

    static void saveParties(){
        try (Connection conn = DatabaseConnection.getConnection()) {
            for(Party p: parties){
                StringBuilder memberIds = new StringBuilder();
                for(Adventurer a: p.getMembers()){
                    memberIds.append(a.getId()).append(",");
                }

                String sql = "INSERT OR REPLACE INTO parties (id, leader_id, member_ids) VALUES (?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, p.getId());
                pstmt.setString(2, p.getLeader().getId());
                pstmt.setString(3, memberIds.toString());
                pstmt.executeUpdate();
            }
        } catch(Exception e) {
            System.out.println("Error saving parties to Database.");
        }
    }

    static void loadParties(){
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM parties")) {
             
            while(rs.next()){
                String id = rs.getString("id");
                String leaderId = rs.getString("leader_id");
                String memberIdsStr = rs.getString("member_ids");

                Adventurer leader = guild.findAdv(leaderId);
                if(leader == null) continue;

                Party p = new Party(id, leader);

                if(memberIdsStr != null && !memberIdsStr.isEmpty()) {
                    String[] mIds = memberIdsStr.split(",");
                    for(String mId : mIds){
                        if (!mId.isEmpty() && !mId.equals(leaderId)) {
                            Adventurer m = guild.findAdv(mId);
                            if(m != null) p.addMember(m);
                        }
                    }
                }
                parties.add(p);
            }
        } catch(Exception e) {
            System.out.println("Error loading parties from Database.");
        }
    }

    static boolean checkAdminLogin(String u, String p){
        return u.equals("gm_4non") && p.equals("4non123");
    }

    public static void main(String[] args){

       DatabaseConnection.initializeDatabase();
       guild.loadAdventurers();
       guild.loadQuests();
       loadParties();

        int choice;

        do{
            System.out.println("\n========== GUILD SYSTEM ==========");
            System.out.println("[1] Register");
            System.out.println("[2] Login");
            System.out.println("[3] Exit");
            System.out.println("==================================");

            while(true){
                System.out.print("Enter choice: ");

                if(sc.hasNextInt()){
                    choice = sc.nextInt();
                    sc.nextLine();

                    if(choice >= 1 && choice <= 3) break;
                } else {
                    sc.nextLine();
                }

                System.out.println("Invalid choice. Try again.");
            }

            switch(choice){
                case 1: register(); break;
                case 2: login(); break;
                case 3: System.out.println("Exiting system..."); break;
            }

        }while(choice != 3);
    }

    static void register(){ 
        System.out.print("Name: ");
        String name = sc.nextLine();
        while(name == null || name.isEmpty()){
            System.out.println("Name cannot be empty. Please enter a valid name.");
            System.out.print("Name: ");
            name = sc.nextLine();
        }


        int age;
        while(true){
            System.out.print("Age: ");

            if(sc.hasNextInt()){
                age = sc.nextInt();
                sc.nextLine();

                if(age > 15) break;
            } else {
                sc.nextLine();
            }

            System.out.println("Invalid age input.");
        }

        String classType = "";

        while(true){
            System.out.println("\nSelect Class:");
            System.out.println("[1] Warrior");
            System.out.println("[2] Mage");
            System.out.println("[3] Archer");
            System.out.println("[4] Healer");
            System.out.println("[5] Rogue");

            System.out.print("Choice: ");

            if(sc.hasNextInt()){
                int c = sc.nextInt();
                sc.nextLine();

                switch(c){
                    case 1: classType="Warrior"; break;
                    case 2: classType="Mage"; break;
                    case 3: classType="Archer"; break;
                    case 4: classType="Healer"; break;
                    case 5: classType="Rogue"; break;
                    default:
                        System.out.println("Invalid choice.");
                        continue;
                }
                break;
            } else {
                sc.nextLine();
                System.out.println("Invalid input.");
            }
        }

        System.out.print("Password: ");
        String p = sc.nextLine();

        guild.register(name, age, classType, p);
    }

    static void login(){

        int t;

        while(true){
            System.out.println("\n[1] Guild Master");
            System.out.println("[2] Adventurer");
            System.out.print("Choice: ");

            if(sc.hasNextInt()){
                t = sc.nextInt();
                sc.nextLine();

                if(t==1 || t==2) break;
            } else {
                sc.nextLine();
            }

            System.out.println("Invalid input.");
        }

        if(t==1){
            System.out.print("Username: ");
            String u = sc.nextLine();

            System.out.print("Password: ");
            String p = sc.nextLine();

           if(checkAdminLogin(u, p)){
                adminMenu();
            } else {
                System.out.println("Invalid login.");
            }
        }
        else{
            System.out.print("ID: ");
            String id = sc.nextLine();
            
            System.out.print("Password: ");
            String p = sc.nextLine();

            Adventurer a = guild.findAdv(id);

            if(a != null && a.checkPassword(p)){
                adventurerMenu(a);
            }else if(a == null){
                System.out.println("Account does not exist.");
            }else {
                System.out.println("Invalid username or password.");
            }

        }
    }

    static void adminMenu(){

        int c;
        System.out.println("Login Successful! Welcome, Guild Master!");
        do{
            System.out.println("\n===== GUILD MASTER MENU =====");
            System.out.println("[1] Add Quest");
            System.out.println("[2] View Quests");
            System.out.println("[3] View Adventurers");
            System.out.println("[4] View Parties");
            System.out.println("[5] Remove Adventurer");
            System.out.println("[6] Search Adventurer");
            System.out.println("[7] View Income Statement");
            System.out.println("[8] Logout");
            
            while(true){
                System.out.print("Choice: ");

                if(sc.hasNextInt()){
                    c = sc.nextInt();
                    sc.nextLine();

                    if(c>=1 && c<=8) break;
                } else {
                    sc.nextLine();
                }

                System.out.println("Invalid.");
            }

            switch(c){
                case 1:
                    System.out.print("Quest Name: ");
                    String n = sc.nextLine();

                    int xp, gold;

                    while(true){
                        System.out.print("XP: ");
                        if(sc.hasNextInt()){
                            xp = sc.nextInt(); break;
                        } else sc.nextLine();
                    }

                    while(true){
                        System.out.print("Gold: ");
                        if(sc.hasNextInt()){
                            gold = sc.nextInt(); sc.nextLine(); break;
                        } else sc.nextLine();
                    }

                    System.out.print("Required Rank (E/D/C/B/A/S): ");
                    String rank = sc.nextLine();

                    guild.addQuest(n, xp, gold, rank);
                    break;

                case 2: guild.showQuests(); break;
                case 3: guild.showAdventurers(); break;
                case 4: showParties(); break;

                case 5:
                    System.out.print("Enter Adventurer ID to remove: ");
                    String rid = sc.nextLine();

                    guild.removeAdventurer(rid);
                    break;

                case 6:
                    System.out.print("Enter Adventurer ID: ");
                    String sid = sc.nextLine();

                    if(sid == null || sid.isEmpty()){
                        System.out.println("Adventurer not found.");
                        break;
                        
                    }
                    guild.searchAdventurer(sid);
                    break;

                case 7:
                    DatabaseConnection.printIncomeStatement();
                    break;
            }

        }while(c!=8);
    }

    static void adventurerMenu(Adventurer a){

        int c;

        do{
            System.out.println("\n===== ADVENTURER MENU =====");
            System.out.println("[1] View Status");
            System.out.println("[2] View Quests");
            System.out.println("[3] Accept Quest");
            System.out.println("[4] Complete Quest");
            System.out.println("[5] Party Menu");
            System.out.println("[6] Reserve Facility");
            System.out.println("[7] Logout");

            while(true){
                System.out.print("Choice: ");

                if(sc.hasNextInt()){
                    c = sc.nextInt();
                    sc.nextLine();

                    if(c>=1 && c<=7) break;
                } else {
                    sc.nextLine();
                }

                System.out.println("Invalid choice.");
            }

            switch(c){

                case 1:
                    a.showStatus();
                    break;

                case 2:
                    guild.showQuests();
                    break;

                case 3:
                    System.out.print("Quest ID: ");
                    Quest q = guild.findQuest(sc.nextLine());

                    if(q != null) {
                        if(a.isInParty() && a.getParty().getLeader().equals(a)){
                            System.out.println("You are the party leader. Choose how to accept this quest:");
                            System.out.println("[1] Accept as party");
                            System.out.println("[2] Accept solo");

                            int acceptChoice;
                            while(true){
                                System.out.print("Choice: ");
                                if(sc.hasNextInt()){
                                    acceptChoice = sc.nextInt();
                                    sc.nextLine();
                                    if(acceptChoice == 1 || acceptChoice == 2) break;
                                } else {
                                    sc.nextLine();
                                }
                                System.out.println("Invalid choice.");
                            }

                            q.assign(a, acceptChoice == 1);
                        } else {
                            q.assign(a);
                        }
                        guild.saveQuests();
                    }
                    else System.out.println("Quest not found.");
                    break;

                case 4:
                    System.out.print("Quest ID: ");
                    Quest qc = guild.findQuest(sc.nextLine());

                    if(qc != null) {
                        qc.complete(a);
                        guild.saveQuests();
                        guild.saveAdventurers();
                    }
                    else System.out.println("Quest not found.");
                    break;

                case 5:
                    partyMenu(a);
                    break;
                
                case 6:
                    System.out.println("\n--- Facility Reservation ---");
                    System.out.println("[1] Training Arena (Cost: 500 Gold)");
                    System.out.println("[2] VIP Tavern (Cost: 200 Gold)");
                    System.out.println("[3] Training with Guild Master (Cost: 100000 Gold)");
                    System.out.print("Choice: ");
                    int facChoice = sc.nextInt();
                    sc.nextLine();

                    if (facChoice == 1 || facChoice == 2 || facChoice == 3) {
                        String facility;
                        double cost;

                        switch (facChoice) {
                            case 1:
                                facility = "Training Arena";
                                cost = 500.0;
                                break;
                            case 2:
                                facility = "VIP Tavern";
                                cost = 200.0;
                                break;
                            case 3:
                                facility = "Training with Guild Master";
                                cost = 100000.0;
                                
                                break;
                            default:
                                facility = "";
                                cost = 0.0;
                        }

                        FacilityPayment payment = new FacilityPayment(cost, a, facility, guild);
                        payment.processInvoice();
                    } else {
                        System.out.println("Invalid facility choice.");
                    }
                    break;
            }

        }while(c != 7);
    }

    static void partyMenu(Adventurer a){

        int c;

        do{
            System.out.println("\n===== PARTY MENU =====");
            System.out.println("[1] Create Party");
            System.out.println("[2] Join Party");
            System.out.println("[3] Show Party");
            System.out.println("[4] Back");

            System.out.print("Choice: ");
            c = sc.nextInt(); sc.nextLine();

            switch(c){

                case 1:
                    if(a.isInParty()){
                        System.out.println("Already in party.");
                        break;
                    }

                    Party p = new Party(generatePartyId(), a);
                    parties.add(p);
                    saveParties();

                    System.out.println("Party created.");
                    break;

                case 2:
                    for(Party pt: parties){
                        System.out.println(pt.getId() + " Leader: " + pt.getLeader().getName());
                    }

                    System.out.print("Enter Party ID: ");
                    String id = sc.nextLine();

                    for(Party pt: parties){
                        if(pt.getId().equals(id)){
                            pt.addMember(a);
                            saveParties();
                            return;
                        }
                    }

                    System.out.println("Not found.");
                    break;

                case 3:
                    if(a.isInParty()) a.getParty().showStatus();
                    else System.out.println("Not in party.");
                    break;
            }

        }while(c != 4);
    }

    static void showParties(){

        System.out.println("\n========== PARTY LIST ==========");

        if(parties.isEmpty()){
            System.out.println("No parties available.");
            System.out.println("================================");
            return;
        }

        for(Party p: parties){
            System.out.printf("ID: %-5s | Leader: %-10s | Members: %d/%d\n",
                    p.getId(),
                    p.getLeader().getName(),
                    p.getSize(),
                    p.getMaxSize());
        }

        System.out.println("================================\n");
    }
}
