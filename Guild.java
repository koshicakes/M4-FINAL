    import java.util.*;
    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.Statement;

    public class Guild {

        private ArrayList<Adventurer> adventurers = new ArrayList<>();
        private ArrayList<Quest> quests = new ArrayList<>();

        public String generateAdventurerId(){
            return "A" + String.format("%03d", adventurers.size() + 1);
        }

        public String generateQuestId(){
            return "Q" + String.format("%03d", quests.size() + 1);
        }

        public void loadAdventurers(){
            try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM adventurers")) {
                
                while(rs.next()){
                    Adventurer a = new Adventurer(
                        rs.getString("id"), rs.getString("name"),
                        rs.getInt("age"), rs.getString("rank"), 
                        rs.getString("classType"), rs.getString("password")
                    );
                    a.addXP(rs.getInt("xp"));
                    a.addGold(rs.getInt("gold"));
                    adventurers.add(a);
                }
            } catch(Exception e) {
                System.out.println("Error loading adventurers from Database.");
            }
        }

        public void saveAdventurers(){
            try (Connection conn = DatabaseConnection.getConnection()) {
                for(Adventurer a: adventurers){
                    String sql = "INSERT OR REPLACE INTO adventurers (id, name, age, rank, classType, password, xp, gold) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, a.getId());
                    pstmt.setString(2, a.getName());
                    pstmt.setInt(3, a.getAge());
                    pstmt.setString(4, a.getRank());
                    pstmt.setString(5, a.getClassType());
                    pstmt.setString(6, a.exportPassword());
                    pstmt.setInt(7, a.getXp());
                    pstmt.setInt(8, a.getGold());
                    pstmt.executeUpdate();
                }
            } catch(Exception e) {
                System.out.println("Error saving adventurers to Database.");
            }
        }

        public void loadQuests(){
            try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM quests")) {
                
                while(rs.next()){
                    quests.add(new Quest(
                        rs.getString("id"),
                        rs.getString("name"), 
                        rs.getInt("xp"),
                        rs.getInt("gold"), 
                        rs.getString("requiredRank"),
                        rs.getString("status")
                    ));
                }
            } catch(Exception e) {
                System.out.println("Error loading quests from Database.");
            }
        }

        public void saveQuests(){
            try (Connection conn = DatabaseConnection.getConnection()) {
                for(Quest q: quests){
                    String sql = "INSERT OR REPLACE INTO quests (id, name, xp, gold, requiredRank, status) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, q.getId());
                    pstmt.setString(2, q.getName());
                    pstmt.setInt(3, q.getXp());
                    pstmt.setInt(4, q.getGold());
                    pstmt.setString(5, q.getRequiredRank());
                    pstmt.setString(6, q.getStatus());
                    pstmt.executeUpdate();
                }
            } catch(Exception e) {
                System.out.println("Error saving quests to Database.");
            }
        }

        public void register(String name,int age,String classType,String password){

            String id = generateAdventurerId();

            adventurers.add(new Adventurer(id,name,age,"E",classType,password));
            saveAdventurers();

            System.out.println("Adventurer successfully registered.");
            System.out.println("Your ID: " + id);
        }

        public Adventurer findAdv(String id){
            for(Adventurer a: adventurers){
                if(a.getId().equals(id)) return a;
            }
            return null;
        }

        public void removeAdventurer(String id){

            Iterator<Adventurer> it = adventurers.iterator();

            while(it.hasNext()){
                Adventurer a = it.next();

                if(a.getId().equals(id)){

                    if(a.isInParty()){
                        Party p = a.getParty();

                        if(a.equals(p.getLeader())){
                            for(Adventurer m : p.getMembers()){
                                m.setParty(null);
                            }
                            System.out.println("Party disbanded.");
                        } else {
                            p.removeMember(p.getLeader(), a);
                        }
                    }

                    it.remove();
                    saveAdventurers();

                    System.out.println("Operation completed successfully.");
                    return;
                }
            }

            System.out.println("Adventurer not found.");
        }

        public void searchAdventurer(String id){

            Adventurer a = findAdv(id);

            if(a == null){
                System.out.println("Not found.");
                return;
            }

            a.showStatus();
            System.out.println("Party: " + (a.isInParty() ? a.getParty().getId() : "None"));
        }

        public void addQuest(String name,int xp,int gold,String rank){

            String id = generateQuestId();

            quests.add(new Quest(id,name,xp,gold,rank));
            saveQuests();

            System.out.println("Quest added. ID: " + id);
        }

        public Quest findQuest(String id){
            for(Quest q: quests){
                if(q.getId().equals(id)) return q;
            }
            return null;
        }

        public void showQuests(){

            System.out.println("\n========== QUEST BOARD ==========");

            for(Quest q: quests){
                System.out.printf("%-5s | %-15s | %-10s | %-10s\n",
                    q.getId(), q.getName(), q.getRequiredRank(), q.getStatus());
            }

            System.out.println("=================================\n");
        }

        private int getRankValue(String r){
            switch(r){
                case "S": return 6;
                case "A": return 5;
                case "B": return 4;
                case "C": return 3;
                case "D": return 2;
                default: return 1;
            }
        }

        public void showAdventurers(){

            ArrayList<Adventurer> sorted = new ArrayList<>(adventurers);

            sorted.sort((a,b) -> getRankValue(b.getRank()) - getRankValue(a.getRank()));

            System.out.println("\n========== ADVENTURERS ==========");

            for(Adventurer a: sorted){
                System.out.printf("%s | %s | %s | %s\n",
                    a.getId(), a.getName(), a.getRank(), a.getClassType());
            }

            System.out.println("=================================\n");
        }
    }
