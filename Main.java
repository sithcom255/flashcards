package flashcards;

import java.io.*;
import java.util.*;

public class Main {
    boolean saveOnExit = false;
    String SaveOnName;
    boolean LoadOnOpen = false;
    String loadOnOpen;
    Scanner scanner = new Scanner(System.in);
    Map<String, String> slovicka = new TreeMap<>();
    Map<String, String> slovickareverse = new TreeMap<>();
    File logg = new File("./" + "log.txt");
    Map<String, Integer> errors = new TreeMap<>();
    int loaded = 0;

    public static void main(String[] args) {

        Main ok = new Main();
        ok.talkingWithConsole(args);

    }

    public void talkingWithConsole(String[] args) {
        boolean x = false;
        if (args.length == 2) {
            if (args[0].equals("-import")) {
                LoadOnOpen = true;
                loadOnOpen = args[1];
            } else if (args[0].equals("-export")) {
                saveOnExit = true;
                SaveOnName = args[1];
            }
        } else if (args.length == 4) {
            if (args[0].equals("-import")) {
                LoadOnOpen = true;
                loadOnOpen = args[1];
            } else if (args[0].equals("-export")) {
                saveOnExit = true;
                SaveOnName = args[1];
            }
            if (args[2].equals("-import")) {
                LoadOnOpen = true;
                loadOnOpen = args[3];
            } else if (args[2].equals("-export")) {
                saveOnExit = true;
                SaveOnName = args[3];
            }
        }
        if (LoadOnOpen) {
            String name = loadOnOpen;
            File file = new File("./" + name);

            if (!file.exists()) {
                print("File not found.");
                return;
            }
            try (Scanner reader = new Scanner(file)) {
                while (reader.hasNextLine()) {
                    String[] value = new String[2];
                    String line = reader.nextLine();

                    value = line.split("\t", 3);
                    String key = value[0];
                    String curak = value[1];
                    String chyba = value[2];

                    if (slovicka.containsKey(key)) {

                        slovickareverse.remove(slovicka.get(key));
                        slovicka.remove(key);
                    }
                    slovicka.put(key, curak);
                    slovickareverse.put(curak, key);
                    loaded++;

                    try {
                        int chybax = Integer.parseInt(value[2].trim());

                        errors.remove(key);
                        errors.put(key, chybax);
                    } catch (Exception e) {

                    }
                }

            } catch (Exception e) {
                print("" + e);
            }
            print(loaded + " cards have been loaded.");
        }
        do {


            print("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String input = logInput(scanner.nextLine());
            switch (input.toLowerCase()) {
                case "add":
                    add();
                    break;
                case "remove":
                    remove();
                    break;
                case "import":
                    importing();
                    break;
                case "export":
                    export();
                    break;
                case "ask":
                    ask();
                    break;
                case "log":
                    log();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
            }
            x = input.equals("exit");
        } while (!x);

        print("Bye bye!");
        if (saveOnExit) {
            String name = SaveOnName;
            File file = new File(name);
            try (PrintWriter printWriter = new PrintWriter(file)) {
                for (Map.Entry<String, String> entry : slovicka.entrySet()) {
                    printWriter.println(entry.getKey() + "\t" + entry.getValue() + "\t" + errors.get(entry.getKey())); // prints a string
                }
            } catch (IOException e) {
                print("An exception occurs: " + e.getMessage());
            }
            print(slovicka.size() + " cards have been saved.");
        }
    }

    public void add() {

        print("The card:");
        String fronty = logInput(scanner.nextLine());
        if (slovicka.containsKey(fronty)) {
            print("The card \"" + fronty + "\" already exists.");
            return;
        }
        print("The definition of the card:");
        String curak = logInput(scanner.nextLine());
        if (slovicka.containsValue(curak)) {
            print("The definition \"" + curak + "\" already exists.");
            return;
        }
        slovicka.put(fronty, curak);
        slovickareverse.put(curak, fronty);
        print("The pair (\"" + fronty + ":" + curak + "\") has been added.");
        return;
    }

    public void remove() {
        print("The card:");
        String removable = logInput(scanner.nextLine());
        if (slovicka.containsKey(removable)) {

            slovickareverse.remove(slovicka.get(removable));
            slovicka.remove(removable);
            errors.remove(removable);
            print("The card has been removed.");
        } else {
            print("Can't remove \"" + removable + "\": there is no such card.");
        }
    }

    public void importing() {
        print("File name:");
        String name = logInput(scanner.nextLine());
        File file = new File("./" + name);

        if (!file.exists()) {
            print("File not found.");
            return;
        }
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String[] value = new String[2];
                String line = reader.nextLine();

                value = line.split("\t", 3);
                String key = value[0];
                String curak = value[1];
                String chyba = value[2];

                if (slovicka.containsKey(key)) {

                    slovickareverse.remove(slovicka.get(key));
                    slovicka.remove(key);
                }
                slovicka.put(key, curak);
                slovickareverse.put(curak, key);
                loaded++;

                try {
                    int chybax = Integer.parseInt(value[2].trim());

                    errors.remove(key);
                    errors.put(key, chybax);
                } catch (Exception e) {

                }
            }

        } catch (Exception e) {
            print("" + e);
        }
        print(loaded + " cards have been loaded.");


    }

    public void export() {
        print("File name:");
        String name = logInput(scanner.nextLine());
        File file = new File(name);
        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (Map.Entry<String, String> entry : slovicka.entrySet()) {
                printWriter.println(entry.getKey() + "\t" + entry.getValue() + "\t" + errors.get(entry.getKey())); // prints a string
            }
        } catch (IOException e) {
            print("An exception occurs: " + e.getMessage());
        }
        print(slovicka.size() + " cards have been saved.");
    }

    public void ask() {
        print("How many times to ask?");
        int count = Integer.parseInt(logInput(scanner.nextLine()));
        Random generator = new Random();
        Object[] values = slovicka.keySet().toArray();

        for (int i = 0; i < count; i++) {
            String randomValue = (String) values[generator.nextInt(values.length)];
            print("Print the definition of \"" + randomValue + "\":");
            String whatever = logInput(scanner.nextLine());
            guess(randomValue, slovicka.get(randomValue), whatever);
        }
    }

    public void guess(String key, String value, String whatever) {
        if (whatever.equals(value)) {
            print("Correct answer.");
        } else if (slovickareverse.containsKey(whatever)) {
            print("Wrong answer, The correct one is \"" + value + "\", you've just written the definition of \"" + slovickareverse.get(whatever) + "\"");
            if (errors.containsKey(key)) {
                int x = errors.get(key) + 1;
                errors.put(key, x);
            } else {
                errors.put(key, 1);
            }
        } else {
            print("Wrong answer. The correct one is \"" + value + "\".");
            if (errors.containsKey(key)) {
                int x = errors.get(key) + 1;
                errors.put(key, x);
            } else {
                errors.put(key, 1);
            }
        }
    }

    public void hardestCard() {
        ArrayList<String> konec = getHardestCard();
        int number = konec.size();
        String[] toUse = new String[1];
        toUse[0] = "";
        if (errors.isEmpty()) {
            print("There are no cards with errors.");
        } else if (konec.size() == 1) {
            print("The hardest card is \"" + konec.get(0) + "\". You have " + errors.get(konec.get(0)) + " errors answering it.");
        } else {
            for (int i = 0; i < number - 1; i++) {
                String con = "\"" + konec.get(i) + "\", ";
                toUse[0] = toUse[0].concat(con);
            }
            print("The hardest cards are " + toUse[0] + "\"" + konec.get(number - 1) + "\". You have " + errors.get(konec.get(0)) + " errors answering them.");

        }
    }

    public ArrayList<String> getHardestCard() {
        ArrayList<String> hardest = new ArrayList<>();
        int max = 0;
        for (Map.Entry<String, Integer> entry : errors.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                hardest.clear();
                hardest.add(entry.getKey());
            } else if (entry.getValue() == max) {
                hardest.add(entry.getKey());
            } else {
            }
        }
        return hardest;
    }

    public void log() {
        print("File name:");
        String name = logInput(scanner.nextLine());
        File file = new File("../" + name);
        makeALog(name);
        print("The log has been saved.");

    }

    public void makeALog(String name) {
        {
            //this will close the resources automatically
            //even if an exception rises
            try (FileReader fr = new FileReader("./log.txt");
                 FileWriter fw = new FileWriter("./" + name)) {
                int c = fr.read();
                while (c != -1) {
                    fw.write(c);
                    c = fr.read();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void print(String str) {
        System.out.println(str);
        try {
            FileWriter fileWriter = new FileWriter(logg);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(str);

        } catch (IOException e) {
            System.out.println("An exception occurs: " + e.getMessage());
        }
    }

    public String logInput(String str) {
        try {
            FileWriter fileWriter = new FileWriter(logg);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(str);

        } catch (IOException e) {
            System.out.println("An exception occurs: " + e.getMessage());
        }
        return str;
    }

    public void resetStats() {
        errors.clear();
        print("Card statistics has been reset.");
    }
}